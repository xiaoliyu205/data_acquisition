package org.example.opcua;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.example.constant.RedisKeyPrefix;
import org.example.entity.DpValueWrite;
import org.example.entity.DpValueWriteReturn;
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

    @RabbitListener(queues = RabbitConfig.QUEUE_WRITE)
    public void receiveWrite(Message message) {
        log.info("...RabbitMQ Received DataPoint Write: {}", new String(message.getBody()));
        DpValueWriteReturn dpValueWriteReturn = new DpValueWriteReturn();
        CompletableFuture.supplyAsync(() -> {
            DpValueWrite dpValueWrite = JSON.parseObject(message.getBody(), DpValueWrite.class);
            dpValueWriteReturn.setMark(dpValueWrite.getMark());
            return writeNodeValue(dpValueWrite.getDpName(), dpValueWrite.getValue());
        }).whenComplete((aBoolean, throwable) -> {
            if (aBoolean == null) {
                dpValueWriteReturn.setErr(throwable.getMessage());
            } else {
                dpValueWriteReturn.setIsGood(aBoolean);
            }
            rabbitmqService.sendMessage(RabbitConfig.QUEUE_WRITE_RETURN, JSON.toJSONString(dpValueWriteReturn));
        });
    }

    private Boolean writeNodeValue(String dpName, String value) {
        String info = redisCache.get(RedisKeyPrefix.NODE_CONFIG + dpName);
        if (Objects.isNull(info)) {
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
