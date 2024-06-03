package org.example.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: RabbitConfig
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 13:45
 **/
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_READ = "DataPoint-Exchange-Read";
    public static final String QUEUE_READ = "DataPoint-Queue-Read";
    public static final String QUEUE_WRITE = "DataPoint-Queue-Write";
    public static final String QUEUE_WRITE_RETURN = "DataPoint-Queue-WriteReturn";
    private static final int QUEUE_TTL = 5000;

    private static final Map<String, Object> args = new HashMap<>();

    static {
        args.put("x-message-ttl", QUEUE_TTL);
    }

    //read
    @Bean
    public FanoutExchange subExchangeRead() {
        return new FanoutExchange(EXCHANGE_READ);
    }

    @Bean
    public Queue subQueueRead() {
        return new Queue(QUEUE_READ, true, false, false, args);
    }

    @Bean
    public Binding BindingRead() {
        return BindingBuilder.bind(subQueueRead()).to(subExchangeRead());
    }

    //write
    @Bean
    public Queue subQueueWrite() {
        return new Queue(QUEUE_WRITE, true, false, false, args);
    }

    @Bean
    public Queue subQueueWriteReturn() {
        return new Queue(QUEUE_WRITE_RETURN, true, false, false, args);
    }
}
