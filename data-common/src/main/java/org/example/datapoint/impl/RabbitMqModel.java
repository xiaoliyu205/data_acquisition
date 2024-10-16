package org.example.datapoint.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.annotation.SendItemType;
import org.example.constant.DpConstant;
import org.example.datapoint.SendDpValue;
import org.example.entity.DpValueRead;
import org.example.rabbitmq.RabbitConfig;
import org.example.rabbitmq.RabbitmqService;
import org.example.redis.RedisCache;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ModelRabbitMq
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:11
 **/
@Component
@SendItemType(DpConstant.SEND_MODEL_RABBITMQ)
public class RabbitMqModel extends SendDpValue {

    private final RabbitmqService rabbitmqService;

    public RabbitMqModel(RedisCache redisCache, RabbitmqService rabbitmqService) {
        super(redisCache);
        this.rabbitmqService = rabbitmqService;
    }

    @Override
    public void send(DpValueRead dpValueRead) {
        rabbitmqService.sendMessage(RabbitConfig.EXCHANGE_READ, (dpValueRead.getDpName().split(":"))[0], JSON.toJSONString(dpValueRead, SerializerFeature.WriteDateUseDateFormat));
    }
}
