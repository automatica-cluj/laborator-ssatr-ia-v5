package com.ssatr.lab;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Message entity representing a message that can be sent/received via RabbitMQ.
 * Simple POJO with basic message properties.
 */
public class Message {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private String content;
    private String timestamp;

    public Message() {
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public Message(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public Message(String id, String content) {
        this.id = id;
        this.content = content;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
