package com.ssatr.lab;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration class.
 * Configures two separate queues:
 * - API queue: Used by REST endpoints for publishing/consuming messages
 * - Processing queue: Monitored by the listener for background processing
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue.api}")
    private String apiQueueName;

    @Value("${app.rabbitmq.queue.processing}")
    private String processingQueueName;

    /**
     * Define the API queue bean.
     * Used by REST API endpoints to publish and retrieve messages.
     */
    @Bean
    public Queue apiQueue() {
        return new Queue(apiQueueName, true);
    }

    /**
     * Define the processing queue bean.
     * Monitored by MessageListener for background processing.
     */
    @Bean
    public Queue processingQueue() {
        return new Queue(processingQueueName, true);
    }

    /**
     * Message converter for JSON serialization/deserialization.
     * Allows sending and receiving messages as JSON objects.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configure RabbitTemplate to use JSON message converter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
