# Sample Lab - Spring Boot IoT Application Template

This is a template Spring Boot console application for SSATR IA students. It demonstrates basic IoT sensor functionality using Java and Spring Boot framework.

## Project Structure

```
sample-lab/
└── sample-app/
    ├── pom.xml                 # Maven configuration with Spring Boot
    └── src/
        ├── main/java/com/ssatr/lab/
        │   ├── App.java        # Main application entry point
        │   └── Sensor.java     # IoT sensor simulation class
        └── test/java/com/ssatr/lab/
            ├── AppTest.java    # Application integration tests
            └── SensorTest.java # Unit tests for Sensor class
```

## Features

- **Spring Boot Console Application**: No web interface, runs as a console application
- **IoT Sensor Simulation**: Temperature sensor with random readings
- **Dependency Injection**: Uses Spring's @Component and @Autowired annotations
- **Unit Testing**: JUnit 5 tests for both application and sensor functionality
- **Maven Build System**: Standard Maven project structure with Spring Boot parent

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Building the Application

```bash
cd sample-lab/sample-app
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Running the Application

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/sample-app-1.0-SNAPSHOT.jar
```

## Application Output

When you run the application, you should see output similar to:

```
=== SSATR Lab - IoT Sensor Demo ===
Sensor TEMP_001 (Temperature Sensor) initialized successfully

Sensor ID: TEMP_001, Type: Temperature Sensor, Active: true, Last Temperature: 20.00°C

Performing temperature readings:
Reading 1: 22.14°C
Reading 2: 18.24°C
Reading 3: 22.65°C
Reading 4: 19.01°C
Reading 5: 22.97°C

Final sensor status:
Sensor ID: TEMP_001, Type: Temperature Sensor, Active: true, Last Temperature: 22.97°C
Sensor TEMP_001 shut down

Application completed successfully!
```

## Key Classes

### App.java
- Main entry point with `@SpringBootApplication` annotation
- Implements `CommandLineRunner` for console application behavior
- Uses dependency injection to access the Sensor component
- Demonstrates initialization, usage, and cleanup of the sensor

### Sensor.java
- Spring `@Component` for dependency injection
- Simulates a temperature sensor with basic functionality:
  - Initialize/shutdown operations
  - Temperature reading with random variation
  - Status reporting
  - Error handling for inactive sensor

## Extending the Template

Students can extend this template by:

1. **Adding new sensor types**: Create classes similar to `Sensor.java`
2. **Implementing new functionality**: Add methods to existing classes
3. **Creating service layers**: Add `@Service` annotated classes for business logic
4. **Adding configuration**: Use `@ConfigurationProperties` for external configuration
5. **Enhancing tests**: Add more comprehensive test coverage

## Spring Boot Features Used

- **Auto-configuration**: Minimal configuration required
- **Dependency Injection**: Using `@Component` and `@Autowired`
- **Command Line Runner**: For console application execution
- **Testing**: Spring Boot test framework with JUnit 5

## Maven Dependencies

- `spring-boot-starter`: Core Spring Boot functionality
- `spring-boot-starter-test`: Testing framework including JUnit 5, Mockito, AssertJ

This template provides a solid foundation for IoT applications and can be easily extended for more complex scenarios.