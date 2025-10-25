package com.ssatr.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Message listener that monitors the RabbitMQ queue and processes messages automatically.
 * Uses @RabbitListener to consume messages in the background.
 */
@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    /**
     * Listen for messages on the processing queue.
     * This method is invoked automatically when a message is published to the processing queue.
     * Processes messages asynchronously in the background.
     *
     * Note: This listener monitors the PROCESSING queue, not the API queue.
     * Messages must be published to the processing queue to be processed here.
     *
     * @param message The message received from the processing queue
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.processing}")
    public void processMessage(Message message) {
        logger.info("=== Message Received from Processing Queue ===");
        logger.info("ID: {}", message.getId());
        logger.info("Content: {}", message.getContent());
        logger.info("Timestamp: {}", message.getTimestamp());
        logger.info("Processing message: {}", message.getContent());

        // Simulate some processing work
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Message processing interrupted", e);
        }

        logger.info("Message processed successfully: {}", message.getId());
        logger.info("================================================");
    }
}
