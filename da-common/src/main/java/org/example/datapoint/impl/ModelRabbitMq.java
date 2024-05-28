package org.example.datapoint.impl;

import org.example.annotation.SendItemType;
import org.example.constant.DpConstant;
import org.example.datapoint.DpValueItem;
import org.example.datapoint.SendDpValue;
import org.example.rabbitmq.RabbitConfig;
import org.example.rabbitmq.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ModelRabbitMq
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:11
 **/
@Component
@SendItemType(DpConstant.SEND_MODEL_RABBITMQ)
public class ModelRabbitMq extends SendDpValue {

    @Autowired
    private RabbitmqService rabbitmqService;

    @Override
    public void send(DpValueItem dpValueItem) {
        rabbitmqService.sendMessage(RabbitConfig.EXCHANGE_READ, (dpValueItem.getDpName().split(":"))[0], dpValueItem.getValue());
    }
}
