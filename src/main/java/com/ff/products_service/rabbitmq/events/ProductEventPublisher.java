package com.ff.products_service.rabbitmq.events;

import com.ff.products_service.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(ProductEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRODUCT_EXCHANGE,
                RabbitMQConfig.PRODUCT_ROUTING,
                event
        );
    }
}
