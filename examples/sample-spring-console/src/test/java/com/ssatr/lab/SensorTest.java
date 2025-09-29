package com.ssatr.lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Sensor entity class
 */
public class SensorTest {

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        sensor = new Sensor();
    }

    @Test
    void testSensorDefaultInitialization() {
        // Test default constructor initialization
        assertEquals("TEMP_001", sensor.getSensorId());
        assertEquals("Temperature Sensor", sensor.getSensorType());
        assertEquals(20.0, sensor.getValue());
        assertFalse(sensor.isActive()); // Should be inactive by default
    }

    @Test
    void testCustomSensorInitialization() {
        // Test custom constructor
        Sensor customSensor = new Sensor("TEMP_002", "Custom Temperature Sensor");
        assertEquals("TEMP_002", customSensor.getSensorId());
        assertEquals("Custom Temperature Sensor", customSensor.getSensorType());
        assertEquals(20.0, customSensor.getValue());
        assertFalse(customSensor.isActive()); // Should be inactive by default
    }

    @Test
    void testSettersAndGetters() {
        sensor.setSensorId("TEST_001");
        sensor.setSensorType("Test Sensor");
        sensor.setValue(25.5);
        sensor.setActive(true);

        assertEquals("TEST_001", sensor.getSensorId());
        assertEquals("Test Sensor", sensor.getSensorType());
        assertEquals(25.5, sensor.getValue());
        assertTrue(sensor.isActive());
    }

    @Test
    void testSensorActivation() {
        // Initially inactive
        assertFalse(sensor.isActive());

        // Activate sensor
        sensor.setActive(true);
        assertTrue(sensor.isActive());

        // Deactivate sensor
        sensor.setActive(false);
        assertFalse(sensor.isActive());
    }

    @Test
    void testValueSetting() {
        // Test setting various temperature values
        sensor.setValue(15.75);
        assertEquals(15.75, sensor.getValue());

        sensor.setValue(-10.0);
        assertEquals(-10.0, sensor.getValue());

        sensor.setValue(100.25);
        assertEquals(100.25, sensor.getValue());
    }
}