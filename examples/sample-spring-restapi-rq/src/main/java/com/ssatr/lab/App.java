package com.ssatr.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application class for SSATR Lab RabbitMQ REST API Sample.
 * This Spring Boot application provides REST endpoints for message publishing
 * and retrieval using RabbitMQ.
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        System.out.println("Starting SSATR Lab RabbitMQ REST API Application...");
        SpringApplication.run(App.class, args);
        System.out.println("Application started successfully!");
        System.out.println("API available at: http://localhost:8080/api/messages");
    }
}
