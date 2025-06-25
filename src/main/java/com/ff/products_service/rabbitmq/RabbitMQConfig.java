package com.ff.products_service.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PRODUCT_EXCHANGE = "product.exchange";
    public static final String PRODUCT_QUEUE    = "product.queue";
    public static final String PRODUCT_ROUTING  = "product.created";

    @Bean TopicExchange productExchange() { return new TopicExchange(PRODUCT_EXCHANGE); }
    @Bean Queue productQueue() { return new Queue(PRODUCT_QUEUE); }
    @Bean Binding productBinding() {
        return BindingBuilder.bind(productQueue()).to(productExchange()).with(PRODUCT_ROUTING);
    }
}

