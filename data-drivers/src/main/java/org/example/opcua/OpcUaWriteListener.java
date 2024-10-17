package org.example.opcua;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.DpValueWrite;
import org.example.entity.DpValueWriteResult;
import org.example.rabbitmq.RabbitConfig;
import org.example.rabbitmq.RabbitmqService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author: xiaoliyu
 * @description: TODO
 * @dateTime: 2024/10/17 14:25
 **/
@Component
@Slf4j
public class OpcUaWriteListener {

    private final RabbitmqService rabbitmqService;
    private final OpcUaWrite opcUaWrite;

    @Autowired
    public OpcUaWriteListener(RabbitmqService rabbitmqService, OpcUaWrite opcUaWrite) {
        this.rabbitmqService = rabbitmqService;
        this.opcUaWrite = opcUaWrite;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_WRITE)
    public void receiveWrite(Message message) {
        log.info("...RabbitMQ Received DataPoint Write: {}", new String(message.getBody()));
        DpValueWriteResult dpValueWriteResult = new DpValueWriteResult();
        CompletableFuture.supplyAsync(() -> {
            DpValueWrite dpValueWrite = JSON.parseObject(message.getBody(), DpValueWrite.class);
            dpValueWriteResult.setMark(dpValueWrite.getMark());
            return opcUaWrite.writeNodeValue(dpValueWrite.getDpName(), dpValueWrite.getValue());
        }).whenComplete((aBoolean, throwable) -> {
            if (aBoolean == null) {
                dpValueWriteResult.setErr(throwable.getMessage());
            } else {
                dpValueWriteResult.setIsGood(aBoolean);
            }
            rabbitmqService.sendMessage(RabbitConfig.QUEUE_WRITE_RETURN, JSON.toJSONString(dpValueWriteResult));
        });
    }
}
