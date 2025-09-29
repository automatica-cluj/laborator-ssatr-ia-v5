package com.ssatr.lab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SensorReadingService class
 */

@SpringBootTest(
        properties = {
                "app.sensors.count=2",
                "app.sensors.prefix=TEST_",
                "app.sensors.type=Test Sensor"
        }
)
public class SensorReadingServiceTest {

    @Autowired
    private SensorReadingService sensorReadingService;

    @Test
    void testGetSensorById() {
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEST_001");
        assertTrue(sensor.isPresent());
        assertEquals("TEST_001", sensor.get().getSensorId());

        Optional<Sensor> nonExistentSensor = sensorReadingService.getSensorById("NON_EXISTENT");
        assertFalse(nonExistentSensor.isPresent());
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
        sensorReadingService.initializeSensor("TEST_001");
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEST_001");
        assertTrue(sensor.get().isActive());

        sensorReadingService.shutdownSensor("TEST_001");
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
        sensorReadingService.initializeSensor("TEST_001");
        double temperature = sensorReadingService.readValue("TEST_001");

        // Temperature should be within a reasonable range (±5 from 20.0)
        assertTrue(temperature >= 15.0 && temperature <= 25.0);

        // Value should be updated in the sensor
        Optional<Sensor> sensor = sensorReadingService.getSensorById("TEST_001");
        assertTrue(sensor.isPresent());
        assertEquals(temperature, sensor.get().getValue());
    }

    @Test
    void testReadValueFromNonExistentSensor() {
        assertThrows(IllegalArgumentException.class, () -> {
            sensorReadingService.readValue("NON_EXISTENT");
        });
    }

    @Test
    void testGetSensorStatus() {
        sensorReadingService.initializeSensor("TEST_001");
        String status = sensorReadingService.getSensorStatus("TEST_001");

        assertNotNull(status);
        assertTrue(status.contains("TEST_001"));
        assertTrue(status.contains("Test Sensor"));
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
        // Add another sensor to the existing 2
        Sensor sensor3 = new Sensor("EXTRA_001", "Extra Sensor");
        sensorReadingService.addSensor(sensor3);

        // Initially all should be inactive
        List<Sensor> sensors = sensorReadingService.getAllSensors();
        assertEquals(3, sensors.size()); // 2 from config + 1 added
        sensors.forEach(sensor -> assertFalse(sensor.isActive()));

        // Initialize all sensors
        sensorReadingService.initializeAllSensors();

        // Now all should be active
        sensors.forEach(sensor -> assertTrue(sensor.isActive()));
    }


}