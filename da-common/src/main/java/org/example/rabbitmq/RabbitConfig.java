package org.example.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: RabbitConfig
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 13:45
 **/
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_READ = "DataPoint-Exchange-Read";
    public static final String EXCHANGE_WRITE = "DataPoint-Exchange-Write";
    public static final String QUEUE_READ = "DataPoint-Queue-Read";
    public static final String QUEUE_WRITE = "DataPoint-Queue-Write";

    @Bean
    public Queue subQueueRead() {
        return new Queue(QUEUE_READ, true);
    }

    @Bean
    public Queue subQueueWrite() {
        return new Queue(QUEUE_WRITE, true);
    }

    @Bean
    public FanoutExchange subExchangeRead() {
        return new FanoutExchange(EXCHANGE_READ);
    }

    @Bean
    public FanoutExchange subExchangeWrite() {
        return new FanoutExchange(EXCHANGE_WRITE);
    }

    @Bean
    public Binding BindingRead() {
        return BindingBuilder.bind(subQueueRead()).to(subExchangeRead());
    }

    @Bean
    public Binding BindingWrite() {
        return BindingBuilder.bind(subQueueWrite()).to(subExchangeWrite());
    }
}
