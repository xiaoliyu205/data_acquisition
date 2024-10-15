package org.example.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author: xiaoliyu
 * @description: TODO
 * @dateTime: 2024/10/15 17:50
 **/
@Service
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
