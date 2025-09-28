package com.ssatr.lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Sensor class
 */
@SpringBootTest
public class SensorTest {

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        sensor = new Sensor();
    }

    @Test
    void testSensorInitialization() {
        // Test default constructor initialization
        assertEquals("TEMP_001", sensor.getSensorId());
        assertEquals("Temperature Sensor", sensor.getSensorType());
        assertTrue(sensor.isActive());
    }

    @Test
    void testCustomSensorInitialization() {
        // Test custom constructor
        Sensor customSensor = new Sensor("TEMP_002", "Custom Temperature Sensor");
        assertEquals("TEMP_002", customSensor.getSensorId());
        assertEquals("Custom Temperature Sensor", customSensor.getSensorType());
        assertTrue(customSensor.isActive());
    }

    @Test
    void testSensorInitialize() {
        sensor.initialize();
        assertTrue(sensor.isActive());
    }

    @Test
    void testSensorShutdown() {
        sensor.shutdown();
        assertFalse(sensor.isActive());
    }

    @Test
    void testTemperatureReading() {
        sensor.initialize();
        double temperature = sensor.readTemperature();
        
        // Temperature should be within a reasonable range (±5 from 20.0)
        assertTrue(temperature >= 15.0 && temperature <= 25.0);
    }

    @Test
    void testTemperatureReadingWhenInactive() {
        sensor.shutdown();
        
        assertThrows(IllegalStateException.class, () -> {
            sensor.readTemperature();
        });
    }

    @Test
    void testGetStatus() {
        sensor.initialize();
        String status = sensor.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("TEMP_001"));
        assertTrue(status.contains("Temperature Sensor"));
        assertTrue(status.contains("Active: true"));
    }

    @Test
    void testSettersAndGetters() {
        sensor.setSensorId("TEST_001");
        sensor.setSensorType("Test Sensor");
        
        assertEquals("TEST_001", sensor.getSensorId());
        assertEquals("Test Sensor", sensor.getSensorType());
    }
}