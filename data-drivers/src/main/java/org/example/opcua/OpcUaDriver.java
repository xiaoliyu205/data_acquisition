package org.example.opcua;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscriptionManager;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.example.constant.RedisKeyPrefix;
import org.example.datapoint.SendDpValue;
import org.example.datapoint.SendItemFactory;
import org.example.entity.DpValueRead;
import org.example.entity.OpcUaAddrInfo;
import org.example.entity.OpcUaAddress;
import org.example.mapper.OpcUaAddressMapper;
import org.example.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: OpcUaDriver
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 23:20
 **/
@Slf4j
@Component
public class OpcUaDriver implements ApplicationRunner {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private OpcUaAddressMapper opcUaAddressMapper;

    private final Map<String, List<NodeId>> urlNodeMap = new HashMap<>();

    private final Map<String, OpcUaAddress> urlMap = new HashMap<>();

    private final Double defaultSamplingInterval = 500D;

    @Value("${sendItem.model}")
    private String sendModel;

    private SendDpValue sendDpValue;

    private static final Map<String, OpcUaClient> urlClient = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {

        sendDpValue = SendItemFactory.getSendDpValue(sendModel);

        List<OpcUaAddress> opcUaAddressList = opcUaAddressMapper.selectList(new LambdaQueryWrapper<>());

        Set<String> keys = redisCache.keys(RedisKeyPrefix.DELETE_STR + "*");
        redisCache.delete(keys);

        opcUaAddressList.forEach(e -> {
            //设置在缓存里
            redisCache.set(RedisKeyPrefix.ADDRESS_CONFIG + e.getUrl() + ":" + e.getAddress(), e.getDataPointType() + ":" + e.getDataPoint());
            redisCache.set(RedisKeyPrefix.NODE_CONFIG + e.getDataPointType() + ":" + e.getDataPoint(), JSON.toJSONString(new OpcUaAddrInfo(e.getUrl(), e.getAddress(), e.getNamespaceIndex())));
            if (!urlMap.containsKey(e.getUrl())) {
                urlMap.put(e.getUrl(), e);
                urlNodeMap.put(e.getUrl(), new ArrayList<>());
            }
            urlNodeMap.get(e.getUrl()).add(new NodeId(e.getNamespaceIndex(), e.getAddress()));
        });

        //subscribe
        if (!urlMap.isEmpty()) {
            urlMap.forEach((url, opcUaAddress) -> {
                new Thread(() -> {
                    urlClient.put(url, start(url, opcUaAddress.getUserName(), opcUaAddress.getPassword(), urlNodeMap.get(url)));
                }).start();
            });
        }
    }

    private String getDpNameByUrlAndNode(String url, String nodeName) {
        return redisCache.get(RedisKeyPrefix.ADDRESS_CONFIG + url + ":" + nodeName);
    }

    public static OpcUaClient getOpcUaClient(String url) {
        return urlClient.get(url);
    }

    /**
     * 监听数据变化后的操作
     */
    private void doAfterSubscriptionChange(ManagedDataItem item, DataValue value) {
        String nodeName = item.getReadValueId().getNodeId().getIdentifier().toString();
        String url = item.getClient().getConfig().getApplicationUri();
        if (value.getValue() == null || value.getValue().getValue() == null) {
            log.warn("...OpcUa Received null: {}", nodeName);
            return;
        }
        String dpValue = value.getValue().getValue().toString();
        String dpName = getDpNameByUrlAndNode(url, nodeName);

        log.info("...OpcUa Received {}: {}", dpName, dpValue);
        assert value.getSourceTime() != null;
        sendDpValue.execute(new DpValueRead(dpName, dpValue, value.getSourceTime().getJavaDate()));
    }

    private OpcUaClient start(String url, String userName, String password, List<NodeId> subscriptionNodeList) {
        // 开启连接
        try {
            OpcUaClient opcUaClient = createClient(url, userName, password);
            opcUaClient.connect().get();

            // 订阅
            if (!subscriptionNodeList.isEmpty()) {
                new Thread(() -> {
                    final CountDownLatch eventLatch = new CountDownLatch(1);

                    // 添加订阅监听器，用于处理断线重连后的订阅问题
                    OpcUaSubscriptionManager subscriptionManager = opcUaClient.getSubscriptionManager();
                    subscriptionManager.addSubscriptionListener(new CustomSubscriptionListener(opcUaClient, subscriptionNodeList));
                    // 批量订阅
                    managedSubscriptionEvent(opcUaClient, subscriptionNodeList);

                    //持续监听
                    try {
                        eventLatch.await();
                    } catch (InterruptedException e) {
                        log.info("{} 订阅入口线程退出", getClass().getSimpleName(), e);
                    }
                    log.info("{} 下的节点订阅执行完成", getClass().getSimpleName());
                }).start();
            }
            return opcUaClient;
        } catch (Exception e) {
            log.error("{} start failed, url {}", getClass().getSimpleName(), url, e);
            return null;
        }
    }

    /**
     * 创建OPC UA客户端
     */
    private OpcUaClient createClient(String url, String userName, String password) throws Exception {
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        return OpcUaClient.create(url,
                endpoints ->
                        endpoints.stream()
                                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                .findFirst(),
                configBuilder ->
                        configBuilder
                                .setApplicationName(LocalizedText.english("milo opc-ua client"))
                                .setApplicationUri(url)
                                .setIdentityProvider(userName.isEmpty() ? new AnonymousProvider() : new UsernameProvider(userName, password))
                                .setRequestTimeout(UInteger.valueOf(5000))
                                .build()
        );
    }

    /**
     * 批量订阅
     */
    private void managedSubscriptionEvent(OpcUaClient client, List<NodeId> nodeList) {
        try {
            if (CollectionUtils.isEmpty(nodeList)) {
                return;
            }

            ManagedSubscription subscription = ManagedSubscription.create(client);
            if (defaultSamplingInterval > 0) {
                subscription.setDefaultSamplingInterval(defaultSamplingInterval);
            }

            List<ManagedDataItem> dataItemList = subscription.createDataItems(nodeList);
            for (ManagedDataItem managedDataItem : dataItemList) {
                String nodeName = managedDataItem.getReadValueId().getNodeId().toString();
                if (managedDataItem.getStatusCode().isGood()) {
                    log.info("{} item created success for nodeId {}", getClass().getSimpleName(), nodeName);
                } else {
                    log.error("{} item created failed for nodeId {}, status {}", getClass().getSimpleName(), nodeName, managedDataItem.getStatusCode());
                }
                managedDataItem.addDataValueListener(this::doAfterSubscriptionChange);
            }
            log.info("{} subscriptions finish...", getClass().getSimpleName());
        } catch (Exception e) {
            log.error("{} 批量订阅数据节点发生异常", getClass().getSimpleName(), e);
        }
    }

    /**
     * 自定义订阅监听
     */
    private class CustomSubscriptionListener implements UaSubscriptionManager.SubscriptionListener {

        private final OpcUaClient client;
        private final List<NodeId> nodeIds;

        CustomSubscriptionListener(OpcUaClient client, List<NodeId> nodeIds) {
            this.client = client;
            this.nodeIds = nodeIds;
        }

        public void onKeepAlive(UaSubscription subscription, DateTime publishTime) {
        }

        public void onStatusChanged(UaSubscription subscription, StatusCode status) {
            log.info("onStatusChanged : {}", subscription);
        }

        public void onPublishFailure(UaException exception) {
            log.info("onPublishFailure : {}", exception);
        }

        public void onNotificationDataLost(UaSubscription subscription) {
            log.info("onNotificationDataLost : {}", subscription);
        }

        /**
         * 重连时 尝试恢复之前的订阅失败时 会调用此方法
         */
        public void onSubscriptionTransferFailed(UaSubscription uaSubscription, StatusCode statusCode) {
            log.info("恢复订阅失败 需要重新订阅");
            //删除老订阅，创建新订阅
            client.getSubscriptionManager().deleteSubscription(uaSubscription.getSubscriptionId());
            //在回调方法中重新订阅
            managedSubscriptionEvent(client, nodeIds);
        }
    }
}
