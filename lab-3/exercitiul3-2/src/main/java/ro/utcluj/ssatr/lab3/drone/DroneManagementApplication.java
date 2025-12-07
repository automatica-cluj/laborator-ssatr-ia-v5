package ro.utcluj.ssatr.lab3.drone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application pentru Drone Management Dashboard.
 */
@SpringBootApplication
public class DroneManagementApplication {

    public static void main(String[] args) {
         //Option 1: Uncomment to use custom profile (remote server)
         SpringApplication app = new SpringApplication(DroneManagementApplication.class);
         app.setAdditionalProfiles("custom");
         app.run(args);

        // Option 2: Default - uses application.properties (local/current config)
        //SpringApplication.run(DroneManagementApplication.class, args);
    }
}
