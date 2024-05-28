package org.example.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RabbitConsumer
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 14:08
 **/
@Component
@Slf4j
public class RabbitConsumer {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,
            20,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @RabbitListener(queues = RabbitConfig.QUEUE_READ)
    public void receive(Message message) {
        log.info("...RabbitMQ Received: {}", message);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    //log.info("finish..." + JSON.parseObject(message.getBody(), DpValueItem.class).toString() );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
       // executor.execute(new DpChangeDriver(message));
    }
}
