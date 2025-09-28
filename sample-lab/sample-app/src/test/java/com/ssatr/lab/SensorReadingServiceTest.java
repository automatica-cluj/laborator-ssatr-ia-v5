package com.ssatr.lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SensorReadingService class
 */
@SpringBootTest
public class SensorReadingServiceTest {

    private SensorReadingService sensorReadingService;

    @BeforeEach
    void setUp() {
        sensorReadingService = new SensorReadingService();
    }

    @Test
    void testServiceInitialization() {
        // Service should initialize with a default sensor
        List<Sensor> sensors = sensorReadingService.getAllSensors();
        assertEquals(1, sensors.size());
        assertEquals("TEMP_001", sensors.get(0).getSensorId());
    }

    @Test
    void testAddSensor() {
        Sensor newSensor = new Sensor("TEMP_002", "Custom Temperature Sensor");
        sensorReadingService.addSensor(newSensor);

        List<Sensor> sensors = sensorReadingService.getAllSensors();
        assertEquals(2, sensors.size());

        Optional<Sensor> foundSensor = sensorReadingService.getSensorById("TEMP_002");
        assertTrue(foundSensor.isPresent());
        assertEquals("Custom Temperature Sensor", foundSensor.get().getSensorType());
    }

    @Test
    void testGetSensorById() {
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEMP_001");
        assertTrue(sensor.isPresent());
        assertEquals("TEMP_001", sensor.get().getSensorId());

        Optional<Sensor> nonExistentSensor = sensorReadingService.getSensorById("NON_EXISTENT");
        assertFalse(nonExistentSensor.isPresent());
    }

    @Test
    void testInitializeSensor() {
        // Initially sensor should be inactive
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEMP_001");
        assertTrue(sensor.isPresent());
        assertFalse(sensor.get().isActive());

        // Initialize the sensor
        sensorReadingService.initializeSensor("TEMP_001");
        assertTrue(sensor.get().isActive());
    }

    @Test
    void testInitializeNonExistentSensor() {
        assertThrows(IllegalArgumentException.class, () -> {
            sensorReadingService.initializeSensor("NON_EXISTENT");
        });
    }

    @Test
    void testShutdownSensor() {
        // Initialize and then shutdown
        sensorReadingService.initializeSensor("TEMP_001");
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEMP_001");
        assertTrue(sensor.get().isActive());

        sensorReadingService.shutdownSensor("TEMP_001");
        assertFalse(sensor.get().isActive());
    }

    @Test
    void testShutdownNonExistentSensor() {
        assertThrows(IllegalArgumentException.class, () -> {
            sensorReadingService.shutdownSensor("NON_EXISTENT");
        });
    }

    @Test
    void testReadValue() {
        sensorReadingService.initializeSensor("TEMP_001");
        double temperature = sensorReadingService.readValue("TEMP_001");

        // Temperature should be within a reasonable range (±5 from 20.0)
        assertTrue(temperature >= 15.0 && temperature <= 25.0);

        // Value should be updated in the sensor
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEMP_001");
        assertTrue(sensor.isPresent());
        assertEquals(temperature, sensor.get().getValue());
    }

    @Test
    void testReadValueFromInactiveSensor() {
        // Sensor is inactive by default
        assertThrows(IllegalStateException.class, () -> {
            sensorReadingService.readValue("TEMP_001");
        });
    }

    @Test
    void testReadValueFromNonExistentSensor() {
        assertThrows(IllegalArgumentException.class, () -> {
            sensorReadingService.readValue("NON_EXISTENT");
        });
    }

    @Test
    void testGetSensorStatus() {
        sensorReadingService.initializeSensor("TEMP_001");
        String status = sensorReadingService.getSensorStatus("TEMP_001");

        assertNotNull(status);
        assertTrue(status.contains("TEMP_001"));
        assertTrue(status.contains("Temperature Sensor"));
        assertTrue(status.contains("Active: true"));
    }

    @Test
    void testGetStatusOfNonExistentSensor() {
        assertThrows(IllegalArgumentException.class, () -> {
            sensorReadingService.getSensorStatus("NON_EXISTENT");
        });
    }

    @Test
    void testInitializeAllSensors() {
        // Add another sensor
        Sensor sensor2 = new Sensor("TEMP_002", "Second Sensor");
        sensorReadingService.addSensor(sensor2);

        // Initially both should be inactive
        List<Sensor> sensors = sensorReadingService.getAllSensors();
        sensors.forEach(sensor -> assertFalse(sensor.isActive()));

        // Initialize all sensors
        sensorReadingService.initializeAllSensors();

        // Now all should be active
        sensors.forEach(sensor -> assertTrue(sensor.isActive()));
    }

    @Test
    void testShutdownAllSensors() {
        // Add another sensor and initialize all
        Sensor sensor2 = new Sensor("TEMP_002", "Second Sensor");
        sensorReadingService.addSensor(sensor2);
        sensorReadingService.initializeAllSensors();

        // All should be active
        List<Sensor> sensors = sensorReadingService.getAllSensors();
        sensors.forEach(sensor -> assertTrue(sensor.isActive()));

        // Shutdown all sensors
        sensorReadingService.shutdownAllSensors();

        // Now all should be inactive
        sensors.forEach(sensor -> assertFalse(sensor.isActive()));
    }
}