package com.ssatr.lab;

import org.springframework.stereotype.Component;

/**
 * Sample Sensor class that simulates a basic sensor with temperature readings.
 * This class demonstrates basic IoT sensor functionality.
 */
@Component
public class Sensor {
    
    private String sensorId;
    private String sensorType;
    private double temperature;
    private boolean isActive;
    
    public Sensor() {
        this.sensorId = "TEMP_001";
        this.sensorType = "Temperature Sensor";
        this.temperature = 20.0; // Initial temperature in Celsius
        this.isActive = true;
    }
    
    public Sensor(String sensorId, String sensorType) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.temperature = 20.0;
        this.isActive = true;
    }
    
    /**
     * Simulates reading temperature from the sensor
     * @return current temperature reading
     */
    public double readTemperature() {
        if (!isActive) {
            throw new IllegalStateException("Sensor is not active");
        }
        
        // Simulate temperature fluctuation (±5 degrees from base temperature)
        double variation = (Math.random() - 0.5) * 10;
        this.temperature = 20.0 + variation;
        
        return Math.round(this.temperature * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    /**
     * Initialize the sensor
     */
    public void initialize() {
        this.isActive = true;
        System.out.println("Sensor " + sensorId + " (" + sensorType + ") initialized successfully");
    }
    
    /**
     * Shutdown the sensor
     */
    public void shutdown() {
        this.isActive = false;
        System.out.println("Sensor " + sensorId + " shut down");
    }
    
    /**
     * Get sensor status information
     * @return status string
     */
    public String getStatus() {
        return String.format("Sensor ID: %s, Type: %s, Active: %s, Last Temperature: %.2f°C", 
                sensorId, sensorType, isActive, temperature);
    }
    
    // Getters and setters
    public String getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public String getSensorType() {
        return sensorType;
    }
    
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public boolean isActive() {
        return isActive;
    }
}