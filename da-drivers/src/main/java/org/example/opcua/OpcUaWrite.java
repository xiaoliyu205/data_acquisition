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
import org.example.exception.InvalidDpNameException;
import org.example.exception.WriteFailException;
import org.example.rabbitmq.RabbitConfig;
import org.example.rabbitmq.RabbitmqService;
import org.example.redis.RedisCache;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;

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

    @Autowired
    private RabbitmqService rabbitmqService;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,
            20,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @RabbitListener(queues = RabbitConfig.QUEUE_WRITE)
    public void receiveWrite(Message message) {
        log.info("...RabbitMQ Received DataPoint Write: {}", new String(message.getBody()));
        CompletableFuture<Boolean> isGood = CompletableFuture.supplyAsync(() -> {
            try {
                DpValueItem dpValueItem = com.alibaba.fastjson.JSON.parseObject(message.getBody(), DpValueItem.class);
                return writeNodeValue(dpValueItem.getDpName(), dpValueItem.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
        isGood.thenAccept((status) -> {
            System.out.println("111");
            rabbitmqService.sendMessage(RabbitConfig.QUEUE_WRITE_RETURN, status.toString());
        });
        isGood.exceptionally((e) -> {
            System.out.println("222");
            rabbitmqService.sendMessage(RabbitConfig.QUEUE_WRITE_RETURN, "e.toString()");
            return null;
        });
    }


    private Boolean writeNodeValue(String dpName, String value) {
        String info = redisCache.get(RedisKeyPrefix.NODE_CONFIG + dpName);
        if (info.isEmpty()) {
            throw new InvalidDpNameException("Not found redis key: " + dpName);
        }
        OpcUaAddrInfo opcUaInfo = JSON.parseObject(info, OpcUaAddrInfo.class);
        OpcUaClient client = OpcUaDriver.getOpcUaClient(opcUaInfo.getUrl());
        if (Objects.isNull(client)) {
            throw new InvalidDpNameException("Not found OpcUa client, url is : " + opcUaInfo.getUrl() + dpName);
        }
        NodeId nodeId = new NodeId(opcUaInfo.getNamespaceIndex(), opcUaInfo.getAddress());
        DataValue nowValue = new DataValue(new Variant(value), null, null);
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        if (statusCode.isGood()) {
            return true;
        } else {
            throw new WriteFailException("Write is fail, Status code is : " + statusCode);
        }
    }
}
