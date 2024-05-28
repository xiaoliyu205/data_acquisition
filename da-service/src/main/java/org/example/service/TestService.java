package org.example.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.datapoint.DpValueItem;
import org.example.rabbitmq.RabbitConfig;
import org.example.rabbitmq.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName: TestService
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/28 14:19
 **/
@Component
public class TestService {

    @Autowired
    private RabbitmqService rabbitmqService;

    public void setDpValueItem(String key, String value) {
        DpValueItem dpValueItem = new DpValueItem(key, value);
        rabbitmqService.sendMessage(RabbitConfig.EXCHANGE_WRITE, "opcua", JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat));
    }

}
