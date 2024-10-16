package org.example.datapoint.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.annotation.SendItemType;
import org.example.constant.DpConstant;
import org.example.datapoint.SendDpValue;
import org.example.entity.DpValueItem;
import org.example.mqtt.SendToMqtt;
import org.example.rabbitmq.RabbitConfig;
import org.example.rabbitmq.RabbitmqService;
import org.example.redis.RedisCache;
import org.springframework.stereotype.Component;

/**
 * @author: xiaoliyu
 * @description: TODO
 * @dateTime: 2024/10/15 18:10
 **/
@Component
@SendItemType(DpConstant.SEND_MODEL_ALL)
public class AllModel extends SendDpValue {

    private final SendToMqtt sendToMqtt;
    private final RabbitmqService rabbitmqService;

    public AllModel(RedisCache redisCache, SendToMqtt sendToMqtt, RabbitmqService rabbitmqService) {
        super(redisCache);
        this.sendToMqtt = sendToMqtt;
        this.rabbitmqService = rabbitmqService;
    }

    @Override
    public void send(DpValueItem dpValueItem) {
        sendToMqtt.sendToMqtt("DataPoint-Read/" + (dpValueItem.getDpName().split(":"))[0], 2, JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat));
        rabbitmqService.sendMessage(RabbitConfig.EXCHANGE_READ, (dpValueItem.getDpName().split(":"))[0], JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat));
    }
}
