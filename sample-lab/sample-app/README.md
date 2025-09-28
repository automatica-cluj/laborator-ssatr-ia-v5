# SSATR Lab - Spring Boot IoT Sensor Simulation

A simple Spring Boot console application that demonstrates IoT sensor simulation. This project showcases basic Spring Boot concepts including dependency injection, service layer architecture, configuration properties, and testing.

## 🎯 Learning Objectives

This application demonstrates:
- **Spring Boot Console Application** using `CommandLineRunner`
- **Dependency Injection** with `@Service` and `@Autowired`
- **Configuration Properties** with `@Value` annotations
- **Service Layer Pattern** separating data entities from business logic
- **Unit Testing** with JUnit 5 and Spring Boot Test
- **Maven** project structure and dependencies

## 🏗️ Application Architecture

```
📁 com.ssatr.lab
├── App.java                    # Main application class (CommandLineRunner)
├── Sensor.java                 # Data entity representing a sensor
├── SensorReadingService.java   # Service managing sensor operations
└── 📁 test/
    ├── AppTest.java                    # Integration tests
    ├── SensorTest.java                 # Entity tests
    └── SensorReadingServiceTest.java   # Service tests
```

### Key Components

1. **Sensor Entity** - Pure data class holding sensor properties
2. **SensorReadingService** - Business logic for managing multiple sensors
3. **App** - Main application demonstrating sensor operations

## ⚙️ Configuration

The application reads configuration from `src/main/resources/application.properties`:

```properties
# Number of sensors to simulate
app.sensors.count=3

# Sensor configuration
app.sensors.prefix=TEMP_
app.sensors.type=Temperature Sensor
```

This creates 3 sensors with IDs: `TEMP_001`, `TEMP_002`, `TEMP_003`

## 🚀 How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Compile the project:**
   ```bash
   mvn compile
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Alternative - Run as JAR:**
   ```bash
   mvn package
   java -jar target/sample-app-1.0-SNAPSHOT.jar
   ```

### Expected Output
```
Starting SSATR Lab Sample Application...
Initialized 3 sensors

=== SSATR Lab - IoT Sensor Demo ===

Available sensors:
- TEMP_001 (Temperature Sensor)
- TEMP_002 (Temperature Sensor)
- TEMP_003 (Temperature Sensor)

Sensor TEMP_001 (Temperature Sensor) initialized successfully
Sensor TEMP_002 (Temperature Sensor) initialized successfully
Sensor TEMP_003 (Temperature Sensor) initialized successfully

...temperature readings...

Application completed successfully!
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=SensorTest
mvn test -Dtest=SensorReadingServiceTest
```

### Run Single Test Method
```bash
mvn test -Dtest=SensorTest#testSensorDefaultInitialization
```

### Test Coverage
- **SensorTest** - Tests data entity properties and getters/setters
- **SensorReadingServiceTest** - Tests service operations like initialization, readings, and sensor management
- **AppTest** - Integration tests ensuring Spring context loads properly

## 🎓 Spring Boot Concepts Demonstrated

### 1. CommandLineRunner
```java
@SpringBootApplication
public class App implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // Application logic here
    }
}
```

### 2. Dependency Injection
```java
@Service
public class SensorReadingService { ... }

@SpringBootApplication
public class App {
    @Autowired
    private SensorReadingService sensorReadingService;
}
```

### 3. Configuration Properties
```java
@Value("${app.sensors.count:1}")
private int sensorCount;
```

### 4. Post-Construction Initialization
```java
@PostConstruct
private void initializeSensors() {
    // Initialize sensors based on configuration
}
```

## 🔧 Customization

### Change Number of Sensors
Edit `application.properties`:
```properties
app.sensors.count=5
```

### Change Sensor Naming
```properties
app.sensors.prefix=HUMIDITY_
app.sensors.type=Humidity Sensor
```

### Add New Sensor Types
1. Extend the `Sensor` entity with new properties
2. Update `SensorReadingService` to handle different sensor types
3. Modify configuration properties as needed

## 📚 Key Learning Points

1. **Separation of Concerns**: Entities hold data, Services contain business logic
2. **Spring Configuration**: Use properties files for configurable values
3. **Dependency Injection**: Let Spring manage object creation and wiring
4. **Testing Strategy**: Test entities, services, and integration separately
5. **Maven Structure**: Follow standard Maven directory layout
