package org.example.opcua;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.example.constant.RedisKeyPrefix;
import org.example.datapoint.DpValueItem;
import org.example.entity.OpcUaAddrInfo;
import org.example.rabbitmq.RabbitConfig;
import org.example.redis.RedisCache;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: OpcUaWrite
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/28 10:46
 **/
@Slf4j
@Component
public class OpcUaWrite {

    @Autowired
    private RedisCache redisCache;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,
            20,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @RabbitListener(queues = RabbitConfig.QUEUE_WRITE)
    public void receiveWrite(Message message) throws Exception {
        log.info("...RabbitMQ Received DataPoint Write: {}", message);
        executor.execute(() -> {
            String driverName = message.getMessageProperties().getReceivedRoutingKey();

            DpValueItem dpValueItem = JSON.parseObject(message.getBody(), DpValueItem.class);

            set(dpValueItem.getDpName(), dpValueItem.getValue());
        });
    }


    public void set(String dpName, String value)  {
        String info = redisCache.get(RedisKeyPrefix.NODE_CONFIG + dpName);
        OpcUaAddrInfo opcUaInfo = JSON.parseObject(info, OpcUaAddrInfo.class);

        System.out.println(opcUaInfo.getUrl());

        OpcUaClient client = OpcUaDriver.getOpcUaClient(opcUaInfo.getUrl());
        System.out.println(client);

        writeNodeValue(client, opcUaInfo, value);
    }

    private static void writeNodeValue(OpcUaClient client, OpcUaAddrInfo info, String value) {
        //节点
        NodeId nodeId = new NodeId(info.getNamespaceIndex(), info.getAddress());
        //创建数据对象,此处的数据对象一定要定义类型，不然会出现类型错误，导致无法写入
        DataValue nowValue = new DataValue(new Variant(value), null, null);
        //写入节点数据
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        System.out.println("结果：" + statusCode.isGood());
    }
}
