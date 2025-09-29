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
    private SensorReadingService sensorReadingService;

    public static void main(String[] args) {
        System.out.println("Starting SSATR Lab Sample Application...");
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== SSATR Lab - IoT Sensor Demo ===");

        // Display all available sensors
        System.out.println("\nAvailable sensors:");
        sensorReadingService.getAllSensors().forEach(sensor ->
            System.out.println("- " + sensor.getSensorId() + " (" + sensor.getSensorType() + ")"));

        // Initialize all sensors
        sensorReadingService.initializeAllSensors();

        // Display status of all sensors
        System.out.println("\nSensor status after initialization:");
        sensorReadingService.getAllSensors().forEach(sensor ->
            System.out.println(sensorReadingService.getSensorStatus(sensor.getSensorId())));

        // Perform temperature readings on the first sensor
        String firstSensorId = sensorReadingService.getAllSensors().get(0).getSensorId();
        System.out.println("\nPerforming temperature readings on " + firstSensorId + ":");
        for (int i = 1; i <= 5; i++) {
            try {
                Thread.sleep(1000); // Wait 1 second between readings
                double temperature = sensorReadingService.readValue(firstSensorId);
                System.out.println("Reading " + i + ": " + temperature + "°C");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Display final status of all sensors
        System.out.println("\nFinal sensor status:");
        sensorReadingService.getAllSensors().forEach(sensor ->
            System.out.println(sensorReadingService.getSensorStatus(sensor.getSensorId())));

        // Shutdown all sensors
        sensorReadingService.shutdownAllSensors();

        System.out.println("\nApplication completed successfully!");
    }
}
