## Project Overview

This project is a distributed drone management system built with Java. It consists of two main modules: a drone simulator and a web-based management dashboard. The system uses Apache Kafka for asynchronous communication between the two modules and PostgreSQL for data persistence.

### Modules

1.  **`exercitiul3-1`**: A low-level drone simulator that:
    *   Simulates drone behavior, including movement, battery drain, and status changes.
    *   Publishes telemetry data (position, battery level, etc.) to a Kafka topic.
    *   Listens for commands (e.g., takeoff, land) from a Kafka topic and reacts accordingly.
    *   Uses plain JDBC for database interactions and the low-level Kafka client library for messaging.

2.  **`exercitiul3-2`**: A high-level web application for drone management that:
    *   Provides a web-based dashboard for monitoring and controlling drones.
    *   Consumes telemetry data from the Kafka topic and stores it in a PostgreSQL database.
    *   Sends commands to drones via a Kafka topic.
    *   Built with Spring Boot, using Spring Data JPA for database access and Spring Kafka for Kafka integration.
    *   Features a REST API for programmatic access to drone data and control.
    *   Includes a live map that visualizes drone positions in real-time.

### Technologies

*   **Java 17**
*   **Maven**
*   **Apache Kafka**: For real-time data streaming between the simulator and the web application.
*   **PostgreSQL**: For storing drone information and telemetry data.
*   **Spring Boot**: For the web application backend.
*   **Spring Data JPA**: For simplifying database access.
*   **Spring Kafka**: For simplifying Kafka integration in the Spring application.
*   **Thymeleaf**: For templating the web pages.
*   **Leaflet.js**: For the interactive map.
*   **Docker**: For running the required services (Kafka, PostgreSQL).

### Architecture

The system follows an event-driven architecture. The drone simulator and the web application are decoupled through Kafka.

1.  **Drone Simulator (`exercitiul3-1`)**:
    *   On startup, it registers drones in the PostgreSQL database.
    *   It periodically publishes telemetry data to the `drone-telemetry` Kafka topic.
    *   It subscribes to the `drone-commands` Kafka topic and executes commands received from the web application.

2.  **Web Application (`exercitiul3-2`)**:
    *   It subscribes to the `drone-telemetry` topic, consumes the telemetry data, and persists it to the PostgreSQL database.
    *   It provides a user interface for viewing drone status and telemetry.
    *   Users can send commands to drones through the web interface. These commands are published to the `drone-commands` Kafka topic.

## Building and Running

### Prerequisites

*   Java 17
*   Maven 3.6+
*   Docker and Docker Compose

### 1. Start Services

Start the required services (PostgreSQL, Kafka, etc.) using Docker Compose:

```bash
cd resources
docker-compose up -d
```

### 2. Build the Project

Build both modules using Maven:

```bash
mvn clean install
```

### 3. Run the Applications

#### Run the Drone Simulator (`exercitiul3-1`)

```bash
cd exercitiul3-1
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain"
```

#### Run the Web Application (`exercitiul3-2`)

```bash
cd exercitiul3-2
mvn spring-boot:run
```

The web application will be available at `http://localhost:8080`.

## Development Conventions

*   The project is split into two modules, `exercitiul3-1` and `exercitiul3-2`, representing the low-level simulator and the high-level web application, respectively.
*   The simulator uses low-level APIs (JDBC, Kafka clients) for educational purposes, while the web application uses Spring Boot for productivity.
*   Communication between the two modules is done asynchronously through Kafka topics (`drone-telemetry` and `drone-commands`).
*   Database schema is managed through `schema.sql` and `schema-simple.sql` in the `resources` directory.
*   The web application uses a standard three-layer architecture (Controller, Service, Repository).
*   The front-end is built with Thymeleaf and JavaScript, with Leaflet.js for the map visualization.
