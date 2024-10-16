package org.example.datapoint.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.annotation.SendItemType;
import org.example.constant.DpConstant;
import org.example.datapoint.SendDpValue;
import org.example.entity.DpValueItem;
import org.example.mqtt.SendToMqtt;
import org.example.redis.RedisCache;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ModelMqtt
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:35
 **/
@Component
@SendItemType(DpConstant.SEND_MODEL_MQTT)
public class MqttModel extends SendDpValue {

    private final SendToMqtt sendToMqtt;

    public MqttModel(RedisCache redisCache, SendToMqtt sendToMqtt) {
        super(redisCache);
        this.sendToMqtt = sendToMqtt;
    }

    @Override
    public void send(DpValueItem dpValueItem) {
        sendToMqtt.sendToMqtt("DataPoint-Read/" + (dpValueItem.getDpName().split(":"))[0], 2, JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat));
    }
}
