package org.example.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName: MqttMessageHandler
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/6/18 17:59
 **/
//@Service
@Slf4j
public class MqttMessageHandler {
    @ServiceActivator(inputChannel = MqttSubscriberConfig.CHANNEL_NAME_IN)
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = Objects.requireNonNull(message.getHeaders().get("mqtt_receivedTopic")).toString();
        String msg = message.getPayload().toString();
        log.info("\n--------------------START-------------------\n" +
                "接收到订阅消息:\ntopic:" + topic + "\nmessage:" + msg +
                "\n---------------------END--------------------");

    }
}
