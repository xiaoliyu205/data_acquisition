package org.example.datapoint.impl;

import org.example.annotation.SendItemType;
import org.example.constant.DpConstant;
import org.example.datapoint.SendDpValue;
import org.example.entity.DpValueRead;
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
public class ModelMqtt extends SendDpValue {
    public ModelMqtt(RedisCache redisCache) {
        super(redisCache);
    }

    @Override
    public void send(DpValueRead dpValueRead) {
        System.out.println("mqtt" + dpValueRead);
    }
}
