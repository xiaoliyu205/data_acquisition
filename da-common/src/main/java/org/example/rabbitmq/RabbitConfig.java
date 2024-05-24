package org.example.rabbitmq;

import org.example.constant.DpConstant;
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

    @Bean
    public Queue subQueue() {
        return new Queue(DpConstant.DPT_PREDIX + "*");
    }

    @Bean
    public FanoutExchange subExchange() {
        return new FanoutExchange(DpConstant.EXCHANGE);
    }

    @Bean
    public Binding Binding() {
        return BindingBuilder.bind(subQueue()).to(subExchange());
    }

}
