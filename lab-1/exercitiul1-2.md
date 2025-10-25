# Spring Console Application Requirements

## Instructions

In this laboratory, you will work with the **sample-spring-console** application to understand Spring Boot configuration, dependency injection, and property management. You will modify the existing sensor simulation system and extend it with new functionality.

## Getting Started

### Step 1: Setup and Initial Testing

1. **Download** the `sample-spring-console` project and copy it in your lab repository
2. **Import** the project into your IDE
3. **Run** the application (check readme from project folder for detailed instructions)
4. **Observe** the default behavior and console output
   - How many sensors are created by default
   - What type of sensors are running
   - The naming convention used
   - The frequency of sensor readings

### Step 2: Understand the Current Architecture

Before making changes, analyze the existing code structure:

- Examine the `Sensor` entity class
- Review the `SensorReadingService` implementation
- Study the `application.properties` file
- Understand how dependency injection works in the application

## Laboratory Tasks

### Task 1: Change Number of Sensors

**Objective:** Modify the application to create a different number of sensors.

------

### Task 2: Change Sensor Naming Convention

**Objective:** Customize how sensors are named and described.

**Instructions:**

1. In the `application.properties` file, add the following properties:

   ```properties
   app.sensors.prefix=HUMIDITY_app.sensors.type=Humidity Sensor
   ```

------

### Task 3: Add New Sensor Types

**Objective:** Extend the application to support multiple types of sensors with different characteristics.



## Bonus Challenges (Optional)

1. **Add Sensor Status Monitoring:**
   - Implement sensor health checks
   - Add status indicators (ACTIVE, INACTIVE, ERROR)
2. **Create REST Endpoints:**
   - Add web controllers to expose sensor data via HTTP
   - Implement endpoints for sensor configuration
3. **Add Data Persistence:**
   - Store sensor readings in a database
   - Implement historical data retrieval
4. **External Configuration:**
   - Load sensor configurations from external JSON/YAML files
   - Implement hot-reload functionality

