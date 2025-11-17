# Exercițiul 3-1: Sistem Low-Level de Telemetrie Drone (Kafka + JDBC)

## Introducere

Scopul acestui exercițiu este de a lucra **low-level** cu API-urile Kafka și JDBC pentru a înțelege în profunzime mecanismele de streaming și persistență a datelor. Veți implementa un sistem de colectare telemetrie de la drone și procesarea acestor date fără abstractizări de tip Spring.

## Obiective de învățare

- Înțelegerea conceptelor Kafka: **topics, producers, consumers, partitions, offsets**
- Lucrul direct cu **Kafka Producer API** și **Consumer API**
- Conectare la PostgreSQL cu **JDBC** și executarea query-urilor SQL
- **Connection pooling** și gestionarea tranzacțiilor manual
- Serializare/deserializare **JSON** pentru mesaje Kafka
- Procesare **event-driven** și gestionarea erorilor

## Descrierea Sistemului

Sistemul simulează o flotă de drone care emit telemetrie în timp real. Datele sunt publicate în Kafka topics, procesate de consumatori, și persistate în PostgreSQL pentru analiză ulterioară.

### Arhitectura

```
┌─────────────┐        ┌─────────────────┐        ┌──────────────────┐
│   Drone 1   │───────▶│                 │───────▶│                  │
│  Simulator  │        │  Kafka Topic    │        │   Telemetry      │
└─────────────┘        │ "drone-telemetry"│       │   Processor      │
                       │                 │        │   (Consumer)     │
┌─────────────┐        │                 │        └────────┬─────────┘
│   Drone 2   │───────▶│                 │                 │
│  Simulator  │        └─────────────────┘                 │
└─────────────┘                                            │ JDBC
                                                           ▼
┌─────────────┐        ┌─────────────────┐        ┌──────────────────┐
│  Command    │◀───────│  Kafka Topic    │        │   PostgreSQL     │
│ Dispatcher  │        │ "drone-commands"│        │                  │
│ (Producer)  │        └─────────────────┘        │  - drones        │
└─────────────┘                                   │  - telemetry     │
                                                  │  - missions      │
                                                  │  - events        │
                       ┌─────────────────┐        └──────────────────┘
                       │   Analytics     │                 ▲
                       │    Reporter     │─────────────────┘
                       │  (SQL Queries)  │        JDBC
                       └─────────────────┘
```

## Componente ale Sistemului

### 1. Drone Simulator (Kafka Producer)

**Responsabilități:**
- Simulează 3-5 drone care emit telemetrie periodic (1-5 secunde)
- Publică mesaje în topic-ul Kafka `drone-telemetry`
- Fiecare dronă are ID unic și status (IDLE, FLYING, CHARGING, EMERGENCY, LANDED)

**Date emise (format JSON):**
```json
{
  "droneId": "DRONE-001",
  "timestamp": 1699876543210,
  "latitude": 46.7712,
  "longitude": 23.6236,
  "altitude": 125.5,
  "speed": 15.3,
  "heading": 270,
  "batteryLevel": 78.5,
  "status": "FLYING",
  "temperature": 22.3,
  "vibration": 0.02
}
```

**Cerințe de implementare:**
- Folosiți **KafkaProducer** API direct (fără Spring)
- Configurați producer properties programatic:
  ```java
  Properties props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  ```
- Serializați obiectele în JSON (folosiți Gson sau Jackson)
- Simulați mișcarea drone-lor (schimbați GPS coordinates, altitude)
- Simulați descărcarea bateriei în timp
- Generați random events (low battery, temperature warnings)

**Clasa schelet:**
```java
public class DroneSimulator {
    private final KafkaProducer<String, String> producer;
    private final String droneId;
    private DroneState state;

    public DroneSimulator(String droneId) {
        // TODO: Initialize Kafka producer
        // TODO: Initialize drone state
    }

    public void start() {
        // TODO: Start telemetry emission loop
    }

    private void emitTelemetry() {
        // TODO: Generate telemetry data
        // TODO: Serialize to JSON
        // TODO: Send to Kafka
    }
}
```

### 2. Telemetry Processor (Kafka Consumer)

**Responsabilități:**
- Consumă mesaje din topic-ul `drone-telemetry`
- Validează și parsează datele primite
- Detectează anomalii (baterie < 20%, temperatură > 45°C)
- Persistă telemetria în PostgreSQL (tabelul `telemetry_logs`)
- Publică evenimente în topic-ul `drone-events` când detectează probleme

**Cerințe de implementare:**
- Folosiți **KafkaConsumer** API direct
- Configurați consumer properties și consumer group:
  ```java
  Properties props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("group.id", "telemetry-processor-group");
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  props.put("enable.auto.commit", "false"); // Manual commit
  ```
- Poll mesaje și procesați-le:
  ```java
  ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
  for (ConsumerRecord<String, String> record : records) {
      // Process record
  }
  consumer.commitSync(); // Manual commit după procesare
  ```
- Conectați-vă la PostgreSQL cu JDBC:
  ```java
  Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/dronedb",
      "postgres",
      "postgres"
  );
  ```
- Folosiți **PreparedStatement** pentru insert-uri:
  ```java
  String sql = "INSERT INTO telemetry_logs (drone_id, timestamp, latitude, longitude, ...) VALUES (?, ?, ?, ?, ...)";
  PreparedStatement pstmt = conn.prepareStatement(sql);
  pstmt.setString(1, droneId);
  // ...
  pstmt.executeUpdate();
  ```
- Gestionați tranzacții manual:
  ```java
  conn.setAutoCommit(false);
  try {
      // Execute queries
      conn.commit();
  } catch (Exception e) {
      conn.rollback();
  }
  ```

**Clasa schelet:**
```java
public class TelemetryProcessor {
    private final KafkaConsumer<String, String> consumer;
    private Connection dbConnection;

    public TelemetryProcessor() {
        // TODO: Initialize Kafka consumer
        // TODO: Initialize DB connection
        // TODO: Subscribe to topic
    }

    public void start() {
        // TODO: Start consumption loop
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                processTelemetry(record.value());
            }
            consumer.commitSync();
        }
    }

    private void processTelemetry(String jsonData) {
        // TODO: Parse JSON
        // TODO: Validate data
        // TODO: Check for anomalies
        // TODO: Persist to database
    }

    private void persistToDatabase(TelemetryData data) throws SQLException {
        // TODO: Insert into telemetry_logs table
    }
}
```

### 3. Command Dispatcher (Kafka Producer)

**Responsabilități:**
- Trimite comenzi către drone prin topic-ul `drone-commands`
- Comenzi disponibile: TAKE_OFF, LAND, GOTO_WAYPOINT, RETURN_HOME, EMERGENCY_LAND
- Poate fi controlat prin interfață consolă sau programatic

**Format mesaj comandă (JSON):**
```json
{
  "commandId": "CMD-12345",
  "droneId": "DRONE-001",
  "commandType": "GOTO_WAYPOINT",
  "timestamp": 1699876543210,
  "parameters": {
    "latitude": 46.7800,
    "longitude": 23.6300,
    "altitude": 100.0
  }
}
```

**Cerințe de implementare:**
- Producer Kafka pentru topic `drone-commands`
- Interfață consolă simplă pentru introducere comenzi:
  ```
  Available commands:
  1. TAKE_OFF <drone_id>
  2. LAND <drone_id>
  3. GOTO <drone_id> <lat> <lon> <alt>
  4. RETURN_HOME <drone_id>
  5. EMERGENCY_LAND <drone_id>
  Enter command:
  ```

**Clasa schelet:**
```java
public class CommandDispatcher {
    private final KafkaProducer<String, String> producer;

    public CommandDispatcher() {
        // TODO: Initialize Kafka producer
    }

    public void sendCommand(DroneCommand command) {
        // TODO: Serialize command to JSON
        // TODO: Send to Kafka topic "drone-commands"
    }

    public void startConsoleInterface() {
        // TODO: Read commands from console
        // TODO: Parse and send commands
    }
}
```

### 4. Analytics Reporter (JDBC + SQL)

**Responsabilități:**
- Generează rapoarte din baza de date PostgreSQL
- Query-uri SQL complexe pentru analiză

**Rapoarte de implementat:**

**A. Raport telemetrie pe dronă:**
```sql
SELECT
    drone_id,
    COUNT(*) as total_records,
    AVG(battery_level) as avg_battery,
    AVG(altitude) as avg_altitude,
    MAX(speed) as max_speed,
    MIN(timestamp) as first_seen,
    MAX(timestamp) as last_seen
FROM telemetry_logs
GROUP BY drone_id
ORDER BY drone_id;
```

**B. Detectare evenimente critice:**
```sql
SELECT
    drone_id,
    timestamp,
    battery_level,
    temperature,
    status
FROM telemetry_logs
WHERE battery_level < 20 OR temperature > 45
ORDER BY timestamp DESC
LIMIT 50;
```

**C. Istoric traiectorie dronă:**
```sql
SELECT
    timestamp,
    latitude,
    longitude,
    altitude,
    speed,
    heading
FROM telemetry_logs
WHERE drone_id = ?
ORDER BY timestamp DESC
LIMIT 100;
```

**D. Statistici pe intervale de timp:**
```sql
SELECT
    date_trunc('hour', to_timestamp(timestamp/1000)) as hour,
    drone_id,
    COUNT(*) as records_count,
    AVG(battery_level) as avg_battery,
    AVG(speed) as avg_speed
FROM telemetry_logs
WHERE timestamp > (extract(epoch from NOW() - interval '24 hours') * 1000)
GROUP BY hour, drone_id
ORDER BY hour DESC, drone_id;
```

**Cerințe de implementare:**
- Conectare JDBC la PostgreSQL
- Executare query-uri cu **Statement** sau **PreparedStatement**
- Formatare output în consolă (tabel text)
- Export rezultate în CSV (opțional)

**Clasa schelet:**
```java
public class AnalyticsReporter {
    private Connection dbConnection;

    public AnalyticsReporter() {
        // TODO: Initialize DB connection
    }

    public void generateDroneStatistics() throws SQLException {
        // TODO: Execute SQL query
        // TODO: Process ResultSet
        // TODO: Print formatted output
    }

    public void generateCriticalEvents() throws SQLException {
        // TODO: Query critical events
        // TODO: Display results
    }

    public void generateDroneTrajectory(String droneId) throws SQLException {
        // TODO: Query trajectory
        // TODO: Display GPS points
    }
}
```

## Schema Bază de Date (PostgreSQL)

Vedeți fișierul `resources/schema.sql` pentru schema completă.

**Tabele principale:**

### Tabelul `drones`
```sql
CREATE TABLE drones (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    model VARCHAR(100),
    status VARCHAR(20),
    battery_level DECIMAL(5,2),
    last_seen BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tabelul `telemetry_logs`
```sql
CREATE TABLE telemetry_logs (
    id SERIAL PRIMARY KEY,
    drone_id VARCHAR(50) REFERENCES drones(id),
    timestamp BIGINT NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    altitude DECIMAL(8,2),
    speed DECIMAL(6,2),
    heading INTEGER,
    battery_level DECIMAL(5,2),
    temperature DECIMAL(5,2),
    vibration DECIMAL(6,4),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_telemetry_drone_timestamp ON telemetry_logs(drone_id, timestamp);
CREATE INDEX idx_telemetry_timestamp ON telemetry_logs(timestamp);
```

### Tabelul `drone_events`
```sql
CREATE TABLE drone_events (
    id SERIAL PRIMARY KEY,
    drone_id VARCHAR(50) REFERENCES drones(id),
    event_type VARCHAR(50),
    severity VARCHAR(20),
    message TEXT,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Kafka Topics

Creați următoarele topics:

```bash
# Telemetrie de la drone
docker exec -it kafka kafka-topics.sh --create \
  --topic drone-telemetry \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Comenzi către drone
docker exec -it kafka kafka-topics.sh --create \
  --topic drone-commands \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Evenimente critice
docker exec -it kafka kafka-topics.sh --create \
  --topic drone-events \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1

# Listare topics
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

## Dependințe Maven (pom.xml)

```xml
<dependencies>
    <!-- Kafka Client -->
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>3.6.0</version>
    </dependency>

    <!-- PostgreSQL JDBC Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>

    <!-- JSON Processing (Gson) -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>

    <!-- Logging (SLF4J + Logback) -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
    </dependency>
</dependencies>
```

## Cerințe de Implementare

### Cerințe Minime (obligatorii)

1. **Drone Simulator:**
   - Simulează minim 3 drone
   - Emit telemetrie la 2-5 secunde
   - Date GPS, baterie, status

2. **Telemetry Processor:**
   - Consumă mesaje din Kafka
   - Persistă în PostgreSQL
   - Detectează baterie < 20%

3. **Command Dispatcher:**
   - Trimite comenzi TAKE_OFF și LAND
   - Interfață consolă simplă

4. **Analytics Reporter:**
   - Raport statistici pe dronă
   - Raport evenimente critice

### Cerințe Extinse (opționale, pentru nota maximă)

5. **Drone Simulator avansat:**
   - Consumă comenzi din `drone-commands` și reacționează (schimbă status, mișcă dronă)
   - Simulează misiuni auto (decolare, mers pe waypoints, aterizare)

6. **Telemetry Processor avansat:**
   - Connection pooling (HikariCP)
   - Procesare batch (inserturi multiple)
   - Publicare evenimente în `drone-events` topic

7. **Analytics Reporter avansat:**
   - Export CSV
   - Grafic text ASCII pentru battery level în timp
   - Query-uri cu JOIN-uri complexe

8. **Gestionare erori:**
   - Retry logic pentru Kafka și DB
   - Logging structured (JSON)
   - Graceful shutdown

## Instrucțiuni de Rulare

### 1. Pornire servicii (Docker Compose)

```bash
cd lab-3/resources
docker-compose up -d
```

### 2. Creare topics Kafka

```bash
# Rulați scriptul de creare topics (sau manual comenzile de mai sus)
```

### 3. Inițializare bază de date

```bash
# Conectați-vă la PostgreSQL
docker exec -it postgres psql -U postgres -d dronedb

# Rulați schema.sql
\i /path/to/schema.sql
```

Sau programatic în aplicație:
```java
// Load and execute schema.sql from resources
```

### 4. Compilare și rulare

```bash
cd lab-3/exercitiul3-1
mvn clean package

# Terminal 1: Start drone simulators
java -cp target/exercitiul3-1.jar ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain

# Terminal 2: Start telemetry processor
java -cp target/exercitiul3-1.jar ro.utcluj.ssatr.lab3.processor.TelemetryProcessorMain

# Terminal 3: Start command dispatcher
java -cp target/exercitiul3-1.jar ro.utcluj.ssatr.lab3.dispatcher.CommandDispatcherMain

# Terminal 4: Run analytics reporter
java -cp target/exercitiul3-1.jar ro.utcluj.ssatr.lab3.analytics.AnalyticsReporterMain
```

## Livrabile

1. **Cod sursă complet** în `lab-3/exercitiul3-1/`
2. **README.md** cu:
   - Descrierea implementării
   - Instrucțiuni de build și rulare
   - Screenshot-uri cu output console
   - Query-uri SQL folosite
3. **Documentație:**
   - Explicarea arhitecturii
   - Diagramă flux de date (opțional)
   - Probleme întâmpinate și soluții

## Testare și Validare

Verificați că:
- [ ] Drone-urile emit telemetrie în Kafka (verificați cu kafka-console-consumer)
- [ ] Telemetria apare în PostgreSQL (verificați cu query SELECT)
- [ ] Comenzile sunt trimise și primite de drone
- [ ] Rapoartele SQL returnează date corecte
- [ ] Aplicațiile se opresc graceful (Ctrl+C)

## Resurse Utile

- **Kafka Producer API:** https://kafka.apache.org/36/javadoc/org/apache/kafka/clients/producer/KafkaProducer.html
- **Kafka Consumer API:** https://kafka.apache.org/36/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html
- **JDBC Tutorial:** https://docs.oracle.com/javase/tutorial/jdbc/basics/
- **PostgreSQL JDBC:** https://jdbc.postgresql.org/documentation/
- **Gson User Guide:** https://github.com/google/gson/blob/master/UserGuide.md

## Tips & Tricks

1. **Debugging Kafka:**
   ```bash
   # Monitor mesaje în timp real
   docker exec -it kafka kafka-console-consumer.sh \
     --topic drone-telemetry \
     --from-beginning \
     --bootstrap-server localhost:9092
   ```

2. **Debugging PostgreSQL:**
   ```bash
   # Conectare psql
   docker exec -it postgres psql -U postgres -d dronedb

   # Query-uri utile
   SELECT COUNT(*) FROM telemetry_logs;
   SELECT * FROM telemetry_logs ORDER BY timestamp DESC LIMIT 10;
   ```

3. **Connection Pooling (opțional):**
   Pentru performanță mai bună, folosiți HikariCP:
   ```xml
   <dependency>
       <groupId>com.zaxxer</groupId>
       <artifactId>HikariCP</artifactId>
       <version>5.0.1</version>
   </dependency>
   ```

4. **Graceful Shutdown:**
   ```java
   Runtime.getRuntime().addShutdownHook(new Thread(() -> {
       System.out.println("Shutting down gracefully...");
       producer.close();
       consumer.close();
       dbConnection.close();
   }));
   ```

Succes!
