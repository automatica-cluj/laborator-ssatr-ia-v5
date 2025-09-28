package com.ssatr.lab;

/**
 * Sensor entity class that represents a sensor with its properties.
 * This is a pure data entity without business logic.
 */
public class Sensor {

    private String sensorId;
    private String sensorType;
    private double value;
    private boolean isActive;

    public Sensor() {
        this.sensorId = "TEMP_001";
        this.sensorType = "Temperature Sensor";
        this.value = 20.0; // Initial temperature in Celsius
        this.isActive = false;
    }

    public Sensor(String sensorId, String sensorType) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.value = 20.0;
        this.isActive = false;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}