package org.example.datapoint.impl;

import org.example.annotation.SendItemType;
import org.example.constant.DpConstant;
import org.example.datapoint.DpValueItem;
import org.example.datapoint.SendDpValue;
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
    @Override
    public void send(DpValueItem dpValueItem) {
        System.out.println("mqtt" + dpValueItem);
    }
}
