package com.ssatr.lab;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing message publishing and consumption with RabbitMQ.
 * Works exclusively with the API queue for REST endpoint operations.
 */
@Service
public class MessageService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.queue.api}")
    private String apiQueueName;

    public MessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish a message to the API queue.
     * Automatically generates a unique ID for the message.
     *
     * Note: This publishes to the API queue, not the processing queue.
     * Messages won't be processed by MessageListener unless moved to processing queue.
     *
     * @param content The message content to publish
     * @return The published message with generated ID
     */
    public Message publishMessage(String content) {
        Message message = new Message(UUID.randomUUID().toString(), content);
        rabbitTemplate.convertAndSend(apiQueueName, message);
        return message;
    }

    /**
     * Read all available messages from the API queue.
     * This method consumes messages (removes them from the queue).
     * Non-blocking - returns immediately if no messages are available.
     *
     * @return List of messages retrieved from the API queue
     */
    public List<Message> readAllMessages() {
        List<Message> messages = new ArrayList<>();
        Message message;

        // Receive messages until no more are available
        // receiveAndConvert returns null when queue is empty
        while ((message = (Message) rabbitTemplate.receiveAndConvert(apiQueueName, 1000)) != null) {
            messages.add(message);
        }

        return messages;
    }
}
