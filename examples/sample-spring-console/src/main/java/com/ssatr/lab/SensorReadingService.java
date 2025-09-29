package com.ssatr.lab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SensorReadingService {

    private final List<Sensor> sensors;

    @Value("${app.sensors.count:1}")
    private int sensorCount;

    @Value("${app.sensors.prefix:TEMP_}")
    private String sensorPrefix;

    @Value("${app.sensors.type:Temperature Sensor}")
    private String sensorType;

    public SensorReadingService() {
        this.sensors = new ArrayList<>();
    }

    @PostConstruct
    private void initializeSensors() {
        for (int i = 1; i <= sensorCount; i++) {
            String sensorId = sensorPrefix + String.format("%03d", i);
            addSensor(new Sensor(sensorId, sensorType));
        }
        System.out.println("Initialized " + sensorCount + " sensors");
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public Optional<Sensor> getSensorById(String sensorId) {
        return sensors.stream()
                .filter(sensor -> sensor.getSensorId().equals(sensorId))
                .findFirst();
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors);
    }

    public void initializeSensor(String sensorId) {
        Optional<Sensor> sensor = getSensorById(sensorId);
        if (sensor.isPresent()) {
            sensor.get().setActive(true);
            System.out.println("Sensor " + sensorId + " (" + sensor.get().getSensorType() + ") initialized successfully");
        } else {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " not found");
        }
    }

    public void shutdownSensor(String sensorId) {
        Optional<Sensor> sensor = getSensorById(sensorId);
        if (sensor.isPresent()) {
            sensor.get().setActive(false);
            System.out.println("Sensor " + sensorId + " shut down");
        } else {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " not found");
        }
    }

    public double readValue(String sensorId) {
        Optional<Sensor> sensor = getSensorById(sensorId);
        if (sensor.isEmpty()) {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " not found");
        }

        Sensor s = sensor.get();
        if (!s.isActive()) {
            throw new IllegalStateException("Sensor " + sensorId + " is not active");
        }

        // Simulate temperature fluctuation (±5 degrees from base temperature)
        double variation = (Math.random() - 0.5) * 10;
        double newValue = 20.0 + variation;
        s.setValue(Math.round(newValue * 100.0) / 100.0); // Round to 2 decimal places

        return s.getValue();
    }

    public String getSensorStatus(String sensorId) {
        Optional<Sensor> sensor = getSensorById(sensorId);
        if (sensor.isEmpty()) {
            throw new IllegalArgumentException("Sensor with ID " + sensorId + " not found");
        }

        Sensor s = sensor.get();
        return String.format("Sensor ID: %s, Type: %s, Active: %s, Last Temperature: %.2f°C",
                s.getSensorId(), s.getSensorType(), s.isActive(), s.getValue());
    }

    public void initializeAllSensors() {
        for (Sensor sensor : sensors) {
            sensor.setActive(true);
            System.out.println("Sensor " + sensor.getSensorId() + " (" + sensor.getSensorType() + ") initialized successfully");
        }
    }

    public void shutdownAllSensors() {
        for (Sensor sensor : sensors) {
            sensor.setActive(false);
            System.out.println("Sensor " + sensor.getSensorId() + " shut down");
        }
    }
}