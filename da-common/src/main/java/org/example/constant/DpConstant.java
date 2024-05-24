package org.example.constant;

import org.springframework.stereotype.Component;

/**
 * @ClassName: DptConstant
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 15:59
 **/
@Component
public class DpConstant {

    public static final String EXCHANGE = "DataPoint-Exchange";

    public static final String DPT_PREDIX = "Dpt:";

    public static final String SEND_MODEL_RABBITMQ = "rabbitmq";

    public static final String SEND_MODEL_MQTT = "mqtt";
}
