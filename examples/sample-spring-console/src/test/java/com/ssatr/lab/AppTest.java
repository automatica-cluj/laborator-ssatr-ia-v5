package com.ssatr.lab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the main Application class
 */
@SpringBootTest
@SpringJUnitConfig
public class AppTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context loads successfully
        assertTrue(true);
    }

    @Test
    void testMainMethodExecution() {
        // Test that main method can be called without throwing exceptions
        assertDoesNotThrow(() -> {
            App.main(new String[]{});
        });
    }
}
