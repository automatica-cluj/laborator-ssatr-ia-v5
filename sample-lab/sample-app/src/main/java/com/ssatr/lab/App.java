package com.ssatr.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application class for SSATR Lab Sample Application.
 * This is a Spring Boot console application that demonstrates basic IoT sensor functionality.
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    @Autowired
    private Sensor sensor;

    public static void main(String[] args) {
        System.out.println("Starting SSATR Lab Sample Application...");
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== SSATR Lab - IoT Sensor Demo ===");
        
        // Initialize the sensor
        sensor.initialize();
        
        // Display sensor information
        System.out.println("\n" + sensor.getStatus());
        
        // Perform some temperature readings
        System.out.println("\nPerforming temperature readings:");
        for (int i = 1; i <= 5; i++) {
            try {
                Thread.sleep(1000); // Wait 1 second between readings
                double temperature = sensor.readTemperature();
                System.out.println("Reading " + i + ": " + temperature + "°C");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Display final sensor status
        System.out.println("\nFinal sensor status:");
        System.out.println(sensor.getStatus());
        
        // Shutdown the sensor
        sensor.shutdown();
        
        System.out.println("\nApplication completed successfully!");
    }
}
