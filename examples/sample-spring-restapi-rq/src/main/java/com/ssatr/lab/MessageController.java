package com.ssatr.lab;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for message operations.
 * Provides endpoints to publish and retrieve messages from RabbitMQ.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Publish a message to the RabbitMQ queue.
     * POST /api/messages
     *
     * Request body example:
     * {
     *   "content": "Hello, RabbitMQ!"
     * }
     *
     * @param request Map containing the message content
     * @return The published message with generated ID and timestamp
     */
    @PostMapping
    public ResponseEntity<Message> publishMessage(@RequestBody Map<String, String> request) {
        String content = request.get("content");

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Message message = messageService.publishMessage(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * Read all available messages from the RabbitMQ queue.
     * GET /api/messages
     *
     * This endpoint consumes (removes) messages from the queue.
     * Returns an empty list if no messages are available.
     *
     * @return List of messages from the queue
     */
    @GetMapping
    public ResponseEntity<List<Message>> readMessages() {
        List<Message> messages = messageService.readAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Health check endpoint to verify the service is running.
     * GET /api/messages/health
     *
     * @return Simple status message
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "RabbitMQ Message Service"
        ));
    }
}
