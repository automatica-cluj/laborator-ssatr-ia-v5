# Exercițiul 3-1: Sistem de Telemetrie Drone (Low-Level)

Aplicație schelet pentru sistemul de telemetrie drone folosind Kafka și JDBC low-level.

## Structură Aplicație

```
src/main/java/ro/utcluj/ssatr/lab3/
├── model/
│   ├── TelemetryData.java          # Model telemetrie
│   └── DroneCommand.java           # Model comandă
├── utils/
│   ├── DatabaseUtils.java          # Connection pooling (HikariCP)
│   └── KafkaUtils.java             # Configurații Kafka
├── simulator/
│   ├── DroneSimulator.java         # Simulează drone individual
│   └── DroneSimulatorMain.java     # Pornește multiple drone
├── processor/
│   └── TelemetryProcessor.java     # Consumă și procesează telemetrie
├── dispatcher/
│   └── CommandDispatcher.java      # Trimite comenzi prin Kafka
└── analytics/
    └── AnalyticsReporter.java      # Rapoarte SQL
```

## Build și Rulare

### 1. Pornire servicii (Docker Compose)

```bash
cd ../resources
docker-compose up -d
```

### 2. Build aplicație

```bash
mvn clean package
```

### 3. Rulare componente

Deschideți 4 terminale:

**Terminal 1 - Drone Simulators:**
```bash
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain
```

**Terminal 2 - Telemetry Processor:**
```bash
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar ro.utcluj.ssatr.lab3.processor.TelemetryProcessor
```

**Terminal 3 - Command Dispatcher:**
```bash
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar ro.utcluj.ssatr.lab3.dispatcher.CommandDispatcher
```

**Terminal 4 - Analytics Reporter:**
```bash
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar ro.utcluj.ssatr.lab3.analytics.AnalyticsReporter
```

## Funcționalități Implementate

### Simulator (✓ Funcțional)
- ✓ Simulează 5 drone cu telemetrie periodic
- ✓ Emit date GPS, baterie, senzori în Kafka
- ✓ Simulează descărcare baterie
- ✓ Detectează low battery și EMERGENCY mode
- ⚠ TODO: Consumă comenzi din Kafka și reacționează
- ⚠ TODO: Simulare misiuni complete

### Telemetry Processor (✓ Funcțional)
- ✓ Consumă telemetrie din Kafka topic
- ✓ Persistă în PostgreSQL (JDBC)
- ✓ Actualizează status drone
- ✓ Detectare anomalii simple (low battery, high temp)
- ⚠ TODO: Publicare evenimente în topic "drone-events"
- ⚠ TODO: Batch processing pentru performanță

### Command Dispatcher (✓ Funcțional)
- ✓ Interfață consolă interactivă
- ✓ Comenzi: TAKEOFF, LAND, GOTO, RTH, EMERGENCY
- ✓ Publicare în Kafka topic "drone-commands"
- ⚠ TODO: Validare comenzi
- ⚠ TODO: Verificare stare dronă înainte de comandă

### Analytics Reporter (✓ Funcțional)
- ✓ Raport statistici per dronă
- ✓ Raport evenimente critice
- ✓ Raport traiectorie dronă
- ✓ Raport statistici pe ore
- ⚠ TODO: Export CSV
- ⚠ TODO: Grafice ASCII pentru battery level

## Cerințe de Extindere (TODO pentru Studenți)

### Obligatorii
1. [ ] Drone simulator consumă comenzi din Kafka și reacționează
2. [ ] Telemetry processor publică evenimente în topic "drone-events"
3. [ ] Toate rapoartele SQL funcționează corect
4. [ ] Documentație completă în acest README

### Opționale (pentru nota maximă)
5. [ ] Simulare misiuni complete (decolare, waypoints, aterizare)
6. [ ] Batch processing în TelemetryProcessor
7. [ ] Export CSV în AnalyticsReporter
8. [ ] Grafice ASCII pentru battery level
9. [ ] Retry logic și error handling avansat
10. [ ] Connection pooling optimizat

## Testare

### Verificare Kafka
```bash
# Monitorizare telemetrie
docker exec -it kafka kafka-console-consumer.sh \
  --topic drone-telemetry \
  --from-beginning \
  --bootstrap-server localhost:9092

# Monitorizare comenzi
docker exec -it kafka kafka-console-consumer.sh \
  --topic drone-commands \
  --from-beginning \
  --bootstrap-server localhost:9092
```

### Verificare PostgreSQL
```bash
# Conectare PostgreSQL
docker exec -it postgres psql -U postgres -d dronedb

# Query-uri test
SELECT COUNT(*) FROM telemetry_logs;
SELECT * FROM drones;
SELECT * FROM telemetry_logs ORDER BY timestamp DESC LIMIT 10;
```

## Note pentru Studenți

- **Focus pe înțelegere:** Această aplicație este un schelet funcțional. Scopul este să înțelegeți cum funcționează Kafka și JDBC la nivel low.
- **Extinderi:** Adăugați funcționalități pe baza TODO-urilor din cod.
- **Comparație:** După ce finalizați acest exercițiu, comparați cu Exercițiul 3-2 (Spring Boot) pentru a înțelege avantajele abstractizării.

## Probleme Cunoscute

- Drone-urile nu răspund la comenzi (TODO: implementare consumer în DroneSimulator)
- Evenimente nu sunt publicate în topic "drone-events" (TODO: implementare în TelemetryProcessor)
- Lipsa retry logic pentru erori temporare

Acestea sunt intenționate și trebuie implementate de studenți!
