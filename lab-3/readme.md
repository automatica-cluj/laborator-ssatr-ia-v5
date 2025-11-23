# Laborator 3 - Sistem de Management Drone


## Notă importantă despre abordare

La fel ca și în laboratoarele precedente, **nu se dorește implementarea unor soluții "production ready"**. Focusul acestui laborator este pe **înțelegerea mecanismelor de lucru cu sisteme de streaming (Kafka) și baze de date relaționale** în contextul unei aplicații distribuite de management drone.

Aspecte pe care să vă concentrați:
- Înțelegerea conceptelor **Kafka** (topics, producers, consumers, partitions)
- Lucrul **low-level** cu biblioteci (Kafka API, JDBC) vs abstractizare (Spring)
- Modelarea datelor în **baze de date relaționale** și scrierea query-urilor SQL
- **Event-driven architecture** și streaming de date în timp real
- Integrarea componentelor: Kafka + PostgreSQL + Spring Boot
- Dezvoltarea unei interfețe web simple cu **Thymeleaf**

Nu este necesar să implementați:
- Algoritmi complecși de control drone sau navigație
- Validări exhaustive sau gestionare complexă a erorilor
- Interfețe grafice foarte elaborate (hărți reale, grafice complexe)
- Mecanisme de securitate sau autentificare
- Optimizări avansate de performanță
- Logică de business complexă

Scopul este să experimentați și să înțelegeți cum funcționează sistemele event-driven cu Kafka și persistența datelor în baze de date relaționale.
---

## Introducere

Acest laborator demonstrează construirea unui **sistem distribuit de management pentru drone** folosind:
- **Apache Kafka** - pentru comunicarea asincronă între componente
- **PostgreSQL** - pentru persistența datelor
- **Spring Boot** - pentru aplicația web
- **Java low-level APIs** - pentru simulatorul de drone

Veți învăța:
- ✅ Cum funcționează Kafka (producers, consumers, topics)
- ✅ Diferența dintre abordarea low-level (JDBC, Kafka API) și high-level (Spring Boot)
- ✅ Comunicarea event-driven
- ✅ Integrarea multiple componente într-un sistem distribuit

---

## Arhitectura Generală

### Fluxul de Date

1. **User** → Aplicația Web (Ex 3-2) → Trimite comandă "TAKE_OFF" către Kafka
2. **Kafka** → Distribuie comanda către toate componentele interesate
3. **DroneSimulator** (Ex 3-1) → Primește comanda și simulează zborul
4. **DroneSimulator** → Generează telemetrie (poziție GPS, baterie, etc.) → Trimite către Kafka
5. **Aplicația Web** (Ex 3-2) → Primește telemetria → Salvează în PostgreSQL
6. **User** → Vizualizează drone-urile pe hartă în timp real

---

## Pregătirea Mediului

### Cerințe Software

- **Java 17** sau superior
- **Maven 3.6+**
- **Docker** și **Docker Compose**
- **Git**
- Un browser modern (Chrome, Firefox, Edge)

### Pasul 1: Pornirea Serviciilor Docker

Toate serviciile (PostgreSQL, Kafka, Zookeeper) sunt configurate în `docker-compose.yml`.

```bash
# Navigați în directorul resources
cd lab-3/resources

# Porniți toate serviciile
docker-compose up -d

# Verificați că toate serviciile rulează
docker ps
```

Ar trebui să vedeți 4 containere pornite:
- `postgres` - PostgreSQL database (port 5432)
- `kafka` - Apache Kafka broker (port 9092)
- `zookeeper` - Coordonator pentru Kafka (port 2181)
- `kafka-ui` - Interfață web pentru Kafka (port 8090)

### Pasul 2: Verificarea Serviciilor

#### PostgreSQL
```bash
# Conectați-vă la PostgreSQL
docker exec -it postgres psql -U postgres -d dronedb

# Verificați tabelele
\dt

# Ieșire
\q
```

#### Kafka UI
Deschideți browserul la: http://localhost:8090

Veți vedea interfața Kafka UI unde puteți:
- Vizualiza topics existente
- Monitoriza mesajele
- Verifica consumatorii

---

## Exercițiul 3-1: Simulatorul de Drone

### Scopul Exercițiului

Acest exercițiu demonstrează **lucrul low-level** cu:
- JDBC manual pentru acces la baza de date
- Kafka Producer/Consumer API direct
- Gestionarea manuală a conexiunilor și thread-urilor

### Structura Proiectului

```
exercitiul3-1/
├── src/main/java/ro/utcluj/ssatr/lab3/
│   ├── model/
│   │   ├── DroneCommand.java          # Model pentru comenzi
│   │   └── TelemetryData.java         # Model pentru telemetrie
│   ├── simulator/
│   │   ├── DroneSimulator.java        # Simulează o dronă
│   │   └── DroneSimulatorMain.java    # Punctul de intrare
│   ├── dispatcher/
│   │   └── CommandDispatcher.java     # Trimite comenzi manual
│   ├── processor/
│   │   └── TelemetryProcessor.java    # Consumă telemetrie
│   ├── analytics/
│   │   └── AnalyticsReporter.java     # Rapoarte și statistici
│   └── utils/
│       ├── KafkaUtils.java            # Configurare Kafka
│       └── DatabaseUtils.java         # Configurare JDBC
└── pom.xml
```

### Componentele Principale

#### 1. DroneSimulator
- **Rol**: Simulează comportamentul unei drone
- **Funcții**:
  - Generează telemetrie periodic (GPS, baterie, temperatură)
  - Ascultă comenzi din Kafka (TAKE_OFF, LAND, RETURN_HOME)
  - Actualizează starea internă bazat pe comenzi

#### 2. CommandDispatcher
- **Rol**: Interfață consolă pentru trimiterea comenzilor
- **Funcții**:
  - Trimite comenzi către drone prin Kafka
  - Comenzi suportate: TAKEOFF, LAND, GOTO, RTH, EMERGENCY

#### 3. TelemetryProcessor
- **Rol**: Consumă telemetria din Kafka
- **Funcții**:
  - Citește mesaje din topic `drone-telemetry`
  - Salvează date în PostgreSQL folosind JDBC
  - Afișează telemetrie în consolă

### Compilare și Rulare

Exercițiul 3-1 are **3 componente principale** care pot fi rulate independent:

#### Compilare (pentru toate componentele)

```bash
# Navigați în directorul exercițiului
cd lab-3/exercitiul3-1

# Compilare
mvn clean package
```

#### Componentă 1: DroneSimulator (OBLIGATORIU)

**Rol**: Simulează drone-uri care generează telemetrie și răspund la comenzi.

```bash
# Rulare cu Maven
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain"

# SAU rulare directă cu Java
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar \
     ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain
```

**Output așteptat**:
```
[INFO] Starting Drone Simulator Application
[INFO] Drone DRONE-002 registered in database (name: Foxtrot, model: DJI Mavic 3 Pro)
[INFO] Drone DRONE-002 initialized at (46.775, 23.625)
[INFO] Drone DRONE-006 registered in database (name: Delta, model: Skydio 2+)
[INFO] Drone DRONE-006 initialized at (46.765, 23.618)
[INFO] Starting drone DRONE-002 simulation (telemetry every 3 seconds)
[INFO] Drone DRONE-002 started listening for commands on topic drone-commands
[INFO] Starting drone DRONE-006 simulation (telemetry every 3 seconds)
[INFO] Drone DRONE-006 started listening for commands on topic drone-commands
[INFO] Telemetry sent for drone DRONE-002 to partition 0 at offset 1
```

Lăsați acest terminal deschis - simulatorul rulează continuu.

#### Componentă 2: TelemetryProcessor (OPȚIONAL)

**Rol**: Consumă telemetrie din Kafka și o salvează în PostgreSQL.

**NOTĂ**: Această componentă este **opțională** pentru Exercițiul 3-1 deoarece Exercițiul 3-2 (aplicația Spring Boot) deja consumă și salvează telemetria. Folosiți-o doar dacă doriți să testați Exercițiul 3-1 independent, **fără** Exercițiul 3-2.

```bash
# Într-un terminal NOU
cd lab-3/exercitiul3-1

# Rulare TelemetryProcessor
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.processor.TelemetryProcessor"

# SAU
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar \
     ro.utcluj.ssatr.lab3.processor.TelemetryProcessor
```

**Output așteptat**:
```
[INFO] TelemetryProcessor initialized and subscribed to topic: drone-telemetry
[INFO] Starting telemetry processing...
[INFO] Received telemetry: DRONE-002 at (46.7751, 23.6251), battery: 100.0%
[INFO] Telemetry saved to database for drone: DRONE-002
```

Lăsați acest terminal deschis dacă doriți să monitorizați telemetria.

#### Componentă 3: CommandDispatcher (OPȚIONAL)

**Rol**: Interfață interactivă în consolă pentru trimiterea comenzilor către drone din linia de comandă.

**NOTĂ**: Această componentă este **opțională** - puteți trimite comenzi și din aplicația web (Exercițiul 3-2). Folosiți CommandDispatcher dacă preferați să controlați drone-urile din terminal.

```bash
# Într-un terminal NOU
cd lab-3/exercitiul3-1

# Rulare CommandDispatcher
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.dispatcher.CommandDispatcher"

# SAU
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar \
     ro.utcluj.ssatr.lab3.dispatcher.CommandDispatcher
```

**Output așteptat**:
```
=== Drone Command Dispatcher ===
Available commands:
  TAKEOFF <drone_id>                    - Take off drone
  LAND <drone_id>                       - Land drone
  GOTO <drone_id> <lat> <lon> <alt>     - Go to waypoint
  RTH <drone_id>                        - Return to home
  EMERGENCY <drone_id>                  - Emergency land
  HELP                                  - Show this menu
  EXIT                                  - Exit
================================

> _
```

**Exemple de comenzi**:
```bash
> TAKEOFF DRONE-002
[INFO] Command TAKE_OFF sent to drone DRONE-002 (partition: 0, offset: 5)

> GOTO DRONE-006 46.78 23.64 150
[INFO] Command GOTO_WAYPOINT sent to drone DRONE-006 (partition: 0, offset: 6)

> LAND DRONE-002
[INFO] Command LAND sent to drone DRONE-002 (partition: 0, offset: 7)

> EXIT
```

### Rezumat: Ce Componente Trebuie Să Rulați?

| Componentă | Obligatoriu? | Când să o folosiți |
|------------|-------------|-------------------|
| **DroneSimulator** | ✅ DA | ÎNTOTDEAUNA - generează drone și telemetrie |
| **TelemetryProcessor** | ❌ NU | Doar dacă testați Ex 3-1 fără Ex 3-2 |
| **CommandDispatcher** | ❌ NU | Doar dacă preferați comenzi din terminal |

### Scenarii Tipice de Rulare

#### Scenariu A: Sistem Complet

```bash
# Terminal 1: Docker services
cd lab-3/resources
docker-compose up -d

# Terminal 2: DroneSimulator (Ex 3-1)
cd lab-3/exercitiul3-1
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain"

# Terminal 3: Aplicația Spring Boot (Ex 3-2)
cd lab-3/exercitiul3-2
mvn spring-boot:run

# Browser: Deschideți http://localhost:8080
```

În acest scenariu:
- ✅ DroneSimulator generează telemetrie
- ✅ Aplicația Spring Boot consumă telemetria și o salvează
- ✅ Aplicația Spring Boot trimite comenzi
- ✅ Vedeți totul pe hartă în browser

#### Scenariu B: Doar Exercițiul 3-1 (Pentru testare low-level)

```bash
# Terminal 1: Docker services
cd lab-3/resources
docker-compose up -d

# Terminal 2: DroneSimulator
cd lab-3/exercitiul3-1
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain"

# Terminal 3: TelemetryProcessor (salvează în DB)
cd lab-3/exercitiul3-1
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.processor.TelemetryProcessor"

# Terminal 4: CommandDispatcher (trimite comenzi)
cd lab-3/exercitiul3-1
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.dispatcher.CommandDispatcher"
```

În acest scenariu:
- ✅ Totul funcționează în Ex 3-1 low-level
- ✅ Vedeți cum funcționează JDBC și Kafka API direct
- ❌ Nu aveți interfață web

#### Scenariu C: Testare Rapidă (Doar telemetrie în Kafka)

```bash
# Terminal 1: Docker services
cd lab-3/resources
docker-compose up -d

# Terminal 2: DroneSimulator
cd lab-3/exercitiul3-1
mvn exec:java -Dexec.mainClass="ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain"

# Terminal 3: Monitorizare Kafka
docker exec -it kafka kafka-console-consumer.sh \
  --topic drone-telemetry \
  --from-beginning \
  --bootstrap-server localhost:9092
```

În acest scenariu:
- ✅ Verificați rapid că telemetria se publică
- ✅ Util pentru debugging
- ❌ Datele nu se salvează în DB

### Ce Face Simulatorul?

Când porniți `DroneSimulatorMain`, acesta:

1. **Creează 2 drone simulate**:
   
   - `DRONE-002` - poziție inițială (46.775, 23.625) - Cluj-Napoca
   - `DRONE-006` - poziție inițială (46.765, 23.618)
   
2. **Înregistrează fiecare dronă în PostgreSQL**:
   - Verifică dacă drona există în tabela `drones`
   - Dacă NU există → inserează înregistrare nouă cu:
     - ID (DRONE-002, DRONE-006)
     - Nume generat automat (Alpha, Bravo, Charlie, etc.)
     - Model aleator (DJI Mavic 3 Pro, DJI Phantom 4 Pro, etc.)
     - Status: IDLE
     - Battery: 100%
   - Dacă există deja → actualizează status și battery level
   ```
   [INFO] Drone DRONE-002 registered in database (name: Foxtrot, model: DJI Mavic 3 Pro)
   [INFO] Drone DRONE-002 initialized at (46.775, 23.625)
   ```

3. **Generează telemetrie la fiecare 3 secunde**:
   ```
   [INFO] Telemetry sent for drone DRONE-002 to partition 0 at offset 42
   ```

4. **Ascultă comenzi din Kafka**:
   - Dacă primiți o comandă TAKE_OFF → statusul devine FLYING
   - Dacă primiți o comandă LAND → statusul devine LANDED
   - Simulează mișcare GPS când zboară

4. **Publică telemetrie în Kafka**:
   - Topic: `drone-telemetry`
   - Format JSON:
   ```json
   {
     "droneId": "DRONE-001",
     "timestamp": 1700000000000,
     "latitude": 46.7712,
     "longitude": 23.6236,
     "altitude": 125.5,
     "speed": 12.3,
     "heading": 45,
     "batteryLevel": 85.2,
     "status": "FLYING",
     "temperature": 28.5,
     "vibration": 0.023
   }
   ```

---

## Exercițiul 3-2: Aplicația Web de Management

### Scopul Exercițiului

Acest exercițiu demonstrează **abordarea high-level** folosind Spring Boot:
- Spring Data JPA (ORM) în loc de JDBC manual
- Spring Kafka cu annotations (@KafkaListener)
- Spring MVC cu Thymeleaf pentru interfața web
- REST API pentru frontend

### Structura Proiectului

```
exercitiul3-2/
├── src/main/java/ro/utcluj/ssatr/lab3/drone/
│   ├── model/                    # JPA Entities
│   │   ├── Drone.java
│   │   ├── Mission.java
│   │   ├── Waypoint.java
│   │   └── TelemetrySnapshot.java
│   ├── repository/               # Spring Data JPA
│   │   ├── DroneRepository.java
│   │   ├── MissionRepository.java
│   │   └── TelemetryRepository.java
│   ├── service/                  # Business Logic
│   │   ├── DroneService.java
│   │   ├── MissionService.java
│   │   └── TelemetryService.java
│   ├── controller/               # Web + REST
│   │   ├── WebController.java
│   │   ├── DroneRestController.java
│   │   └── MissionRestController.java
│   ├── kafka/                    # Kafka Integration
│   │   ├── TelemetryKafkaListener.java
│   │   └── CommandKafkaProducer.java
│   └── dto/
│       └── DronePositionDTO.java
├── src/main/resources/
│   ├── templates/                # Thymeleaf HTML
│   │   ├── index.html           # Dashboard
│   │   ├── drones.html          # Lista drone
│   │   ├── drone-detail.html   # Detalii dronă
│   │   └── monitor.html         # Hartă live
│   ├── static/css/
│   │   └── style.css
│   └── application.properties   # Configurare
└── pom.xml
```

### Componentele Principale

#### 1. Model Layer (JPA Entities)

**Drone.java** - Entity pentru tabela `drones`
```java
@Entity
@Table(name = "drones")
public class Drone {
    @Id
    private String id;
    private String name;
    private String model;

    @Enumerated(EnumType.STRING)
    private DroneStatus status;

    private BigDecimal batteryLevel;
    private Long lastSeen;

    @OneToMany(mappedBy = "drone")
    private List<Mission> missions;
}
```

**TelemetrySnapshot.java** - Entity pentru `telemetry_logs`
```java
@Entity
@Table(name = "telemetry_logs")
public class TelemetrySnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;

    private Long timestamp;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;
    // ... alte câmpuri
}
```

#### 2. Repository Layer (Spring Data JPA)

Spring Data generează automat implementările metodelor CRUD:

```java
public interface DroneRepository extends JpaRepository<Drone, String> {
    // Spring generează automat implementarea bazat pe nume metodă
    List<Drone> findByStatus(DroneStatus status);

    // Query custom folosind JPQL
    @Query("SELECT d FROM Drone d WHERE d.batteryLevel < :threshold")
    List<Drone> findLowBatteryDrones(@Param("threshold") BigDecimal threshold);
}
```

#### 3. Service Layer (Business Logic)

```java
@Service
@Transactional
public class DroneService {
    @Autowired
    private DroneRepository droneRepository;

    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    public List<Drone> getActiveDrones() {
        return droneRepository.findByStatus(DroneStatus.FLYING);
    }
}
```

#### 4. Kafka Integration

**TelemetryKafkaListener** - Consumă telemetrie
```java
@Component
public class TelemetryKafkaListener {
    @KafkaListener(topics = "drone-telemetry", groupId = "drone-dashboard-group")
    public void listenTelemetry(String message) {
        // Parse JSON
        TelemetryDTO dto = gson.fromJson(message, TelemetryDTO.class);

        // Salvează în baza de date
        telemetryService.saveTelemetry(dto);

        // Actualizează statusul dronei
        telemetryService.updateDroneFromTelemetry(dto.getDroneId(), snapshot);
    }
}
```

**CommandKafkaProducer** - Trimite comenzi
```java
@Component
public class CommandKafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendTakeOffCommand(String droneId) {
        Map<String, Object> params = new HashMap<>();
        params.put("target_altitude", 100.0);
        sendCommand(droneId, "TAKE_OFF", params);
    }
}
```

#### 5. Controllers

**WebController** - Pagini HTML (Thymeleaf)
```java
@Controller
public class WebController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalDrones", droneService.getAllDrones().size());
        return "index";
    }

    @GetMapping("/drones/{id}")
    public String droneDetail(@PathVariable String id, Model model) {
        model.addAttribute("drone", droneService.getDroneById(id));
        return "drone-detail";
    }
}
```

**DroneRestController** - REST API
```java
@RestController
@RequestMapping("/api/drones")
public class DroneRestController {
    @GetMapping
    public List<Drone> getAllDrones() {
        return droneService.getAllDrones();
    }

    @PostMapping("/{id}/takeoff")
    public ResponseEntity<String> takeoff(@PathVariable String id) {
        commandKafkaProducer.sendTakeOffCommand(id);
        return ResponseEntity.ok("Command sent");
    }
}
```

### Compilare și Rulare

```bash
# Navigați în directorul exercițiului
cd lab-3/exercitiul3-2

# Compilare
mvn clean package

# Rulare aplicație
mvn spring-boot:run

# SAU rulați JAR-ul direct
java -jar target/lab3-exercitiul2-1.0-SNAPSHOT.jar
```

### Accesarea Aplicației

Deschideți browserul la: **http://localhost:8080**

#### Pagini Disponibile:

1. **Dashboard** (`/`)
   - Statistici generale: Total drone, Active, Baterie scăzută, Misiuni active
   - Quick actions: Acțiuni rapide

2. **Lista Drone** (`/drones`)
   - Tabel cu toate drone-urile
   - Butoane pentru comenzi (Take Off, Land)
   - Link către detalii

3. **Detalii Dronă** (`/drones/{id}`)
   - Informații complete despre dronă
   - Statistici (baterie, status, etc.)
   - Panou comenzi (Take Off, Land, Return Home)
   - Ultimul snapshot telemetric
   - Misiuni asociate
   - Istoric telemetrie (ultimele 50 înregistrări)

4. **Live Monitor** (`/monitor`)
   - **Hartă interactivă** cu Leaflet.js
   - Toate drone-urile vizualizate în timp real
   - Markere colorate după status (verde=FLYING, albastru=IDLE, roșu=EMERGENCY)
   - Click pe marker pentru detalii
   - Actualizare automată la fiecare 2 secunde

---

## Integrarea Sistemelor

### Cum Funcționează Împreună?

```
┌─────────────────────────────────────────────────────────────────┐
│                    SCENARIUL COMPLET                             │
└─────────────────────────────────────────────────────────────────┘

1. USER (Browser) → Click buton "Take Off" în aplicația web
   ↓
2. WebController → DroneRestController.takeoff()
   ↓
3. CommandKafkaProducer → Publică în Kafka topic "drone-commands"
   {
     "commandId": "uuid-123",
     "droneId": "DRONE-001",
     "commandType": "TAKE_OFF",
     "timestamp": 1700000000000,
     "parameters": {"target_altitude": 100}
   }
   ↓
4. Kafka → Distribuie mesajul către toți consumatorii
   ↓
5. DroneSimulator (Ex 3-1) → Primește comanda
   ↓
6. DroneSimulator → Execută: status = "FLYING", altitude = 100
   ↓
7. DroneSimulator → Generează telemetrie și publică în "drone-telemetry"
   {
     "droneId": "DRONE-001",
     "timestamp": 1700000003000,
     "latitude": 46.7712,
     "longitude": 23.6236,
     "altitude": 100.0,
     "status": "FLYING",
     "batteryLevel": 98.5,
     ...
   }
   ↓
8. TelemetryKafkaListener (Ex 3-2) → Primește telemetria
   ↓
9. TelemetryService → Salvează în PostgreSQL (telemetry_logs + actualizează drones)
   ↓
10. Browser (Live Monitor) → Poll la 2 secunde → Fetch /api/drones/positions
   ↓
11. DroneRestController → Returnează poziții actualizate
   ↓
12. Leaflet.js → Actualizează markerul pe hartă
   ↓
13. USER → Vede drona mișcându-se pe hartă în timp real! 🎉
```

### Pași pentru Integrare Completă

#### Pasul 1: Porniți Docker
```bash
cd lab-3/resources
docker-compose up -d
```

#### Pasul 2: Porniți Simulatorul (Exercițiul 3-1)
```bash
cd lab-3/exercitiul3-1
mvn spring-boot:run
# SAU
java -cp target/lab3-exercitiul1-1.0-SNAPSHOT.jar \
     ro.utcluj.ssatr.lab3.simulator.DroneSimulatorMain
```

Veți vedea:
```
[INFO] Starting drone DRONE-001 simulation
[INFO] Starting drone DRONE-002 simulation
[INFO] Drone DRONE-001 started listening for commands on topic drone-commands
[INFO] Telemetry sent for drone DRONE-001...
```

#### Pasul 3: Porniți Aplicația Web (Exercițiul 3-2)
```bash
cd lab-3/exercitiul3-2
mvn spring-boot:run
```

Veți vedea:
```
[INFO] Started DroneManagementApplication in 5.234 seconds
[INFO] Received telemetry message: {"droneId":"DRONE-001",...}
[INFO] Telemetry saved for drone: DRONE-001
```

#### Pasul 4: Testați Integrarea

1. **Deschideți browserul**: http://localhost:8080

2. **Dashboard** - Verificați statisticile:
   - Ar trebui să vedeți 2 drone (DRONE-001, DRONE-002)
   - Status: IDLE inițial

3. **Lista Drone** (`/drones`):
   - Click pe "Take Off" pentru DRONE-001
   - Observați în log-urile simulatorului:
     ```
     [INFO] Drone DRONE-001 received command: TAKE_OFF
     [INFO] Drone DRONE-001 taking off
     ```

4. **Live Monitor** (`/monitor`):
   - Veți vedea 2 markere pe hartă (Cluj-Napoca)
   - După comanda Take Off, markerul DRONE-001 devine verde (FLYING)
   - Poziția se actualizează la fiecare 2 secunde

5. **Detalii Dronă** (`/drones/DRONE-001`):
   - Vedeți telemetria în timp real
   - Bateria scade când zboară
   - Tabelul de istoric se populează

### Monitorizarea Sistemului

#### Kafka UI
http://localhost:8090
- Topics → `drone-telemetry` → Vedeți mesajele
- Topics → `drone-commands` → Vedeți comenzile trimise
- Consumers → `drone-dashboard-group` → Verificați offset-ul

#### PostgreSQL
```bash
docker exec -it postgres psql -U postgres -d dronedb

-- Verificare drone
SELECT * FROM drones;

-- Telemetrie recentă
SELECT drone_id, status, battery_level, altitude,
       to_timestamp(timestamp/1000) as time
FROM telemetry_logs
ORDER BY timestamp DESC
LIMIT 20;

-- Număr înregistrări per dronă
SELECT drone_id, COUNT(*) as records
FROM telemetry_logs
GROUP BY drone_id;
```

---

## Intrebări Frecvente

### Q1: Cum adaug o nouă dronă în sistem?

**Opțiunea 1 - Modificați simulatorul (RECOMANDAT)**:
```java
// În DroneSimulatorMain.java
// Adăugați o linie nouă:
drones.add(new DroneSimulator("DRONE-003", 46.79, 23.64, 3));

// Drona va fi înregistrată AUTOMAT în baza de date la pornire!
```

Când porniți simulatorul, veți vedea:
```
[INFO] Drone DRONE-003 registered in database (name: Charlie, model: Autel EVO II)
```

**Opțiunea 2 - Prin REST API** (din aplicația web):

```bash
curl -X POST http://localhost:8080/api/drones \
  -H "Content-Type: application/json" \
  -d '{
    "id": "DRONE-007",
    "name": "Manual Drone",
    "model": "DJI Phantom 4",
    "status": "IDLE",
    "batteryLevel": 100.0
  }'
```

Optiunea a doua NU porneste si simulatorul acesteia. Deci orice comanda trimisa catre aceasta drona adaugata utilizand REST API nu va fi interceptata si executata.

### Q2: Unde se stochează poziția curentă a unei drone?

Poziția **NU** este în tabela `drones`. Este în `telemetry_logs`.

Pentru poziția curentă, se ia **ultima înregistrare de telemetrie**:
```sql
SELECT latitude, longitude, altitude
FROM telemetry_logs
WHERE drone_id = 'DRONE-001'
ORDER BY timestamp DESC
LIMIT 1;
```

În cod (Spring):
```java
TelemetrySnapshot latest = telemetryService.getLatestTelemetry("DRONE-001");
BigDecimal lat = latest.getLatitude();
BigDecimal lon = latest.getLongitude();
```

### Q3: Cum funcționează actualizarea în timp real pe hartă?

**Polling simplu** (implementat):
```javascript
// Fiecare 2 secunde
setInterval(function() {
    fetch('/api/drones/positions')
        .then(response => response.json())
        .then(drones => {
            // Actualizează markere pe hartă
            drones.forEach(drone => updateMarker(drone));
        });
}, 2000);
```

Pentru upgrade la **Server-Sent Events** (mai eficient), vezi comentariile din `monitor.html`.

### Q4: Cum modific frecvența telemetriei?

**În simulator**:
```java
// DroneSimulatorMain.java
// Parametrul 3 = interval în secunde
DroneSimulator drone1 = new DroneSimulator("DRONE-001", 46.77, 23.62, 3);
//                                                                      ↑
//                                                          Schimbă acest număr
```

**În aplicația web** (polling):
```javascript
// monitor.html
setInterval(pollDronePositions, 2000);  // 2000ms = 2 secunde
//                               ↑
//                      Schimbă acest număr
```

### Q5: Cum trimit comenzi personalizate?

**Pasul 1** - Adaugă tipul comenzii în simulator:
```java
// DroneSimulator.java - în processCommand()
case "HOVER":
    hover();
    break;
```

**Pasul 2** - Adaugă metoda în CommandKafkaProducer:
```java
public void sendHoverCommand(String droneId) {
    sendCommand(droneId, "HOVER", null);
}
```

**Pasul 3** - Adaugă endpoint în REST controller:

```java
@PostMapping("/{id}/hover")
public ResponseEntity<String> hover(@PathVariable String id) {
    commandKafkaProducer.sendHoverCommand(id);
    return ResponseEntity.ok("Hover command sent");
}
```

### Q6: Diferența dintre Exercițiul 3-1 și 3-2?

| Aspect | Exercițiul 3-1 | Exercițiul 3-2 |
|--------|---------------|---------------|
| **Scop** | Învățare low-level | Productivitate |
| **Database** | JDBC manual, SQL queries | Spring Data JPA, auto-generated |
| **Kafka** | KafkaProducer/Consumer API | @KafkaListener, KafkaTemplate |
| **Configurare** | Programmatic (Properties) | application.properties |
| **UI** | Console/Terminal | Web Browser (Thymeleaf) |
| **REST API** | Nu | Da |
| **Cod** | Mai mult (~500 linii) | Mai puțin (~300 linii) |
| **Control** | Control total | Abstractizat de Spring |
| **Complexitate** | Mai complex | Mai simplu |
| **Timp dezvoltare** | Mai lung | Mai scurt |

**Concluzie**: Ex 3-1 vă învață **cum funcționează lucrurile intern**. Ex 3-2 vă arată **cum se dezvoltă profesionist** folosind framework-uri.

---

## Resurse Suplimentare

### Documentație Oficială
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Leaflet.js Documentation](https://leafletjs.com/)

### Tutoriale Recomandate
- [Baeldung - Spring Kafka](https://www.baeldung.com/spring-kafka)
- [Baeldung - Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)
- [Kafka: The Definitive Guide](https://www.confluent.io/resources/kafka-the-definitive-guide/)

### Tools Utile
- **Postman** - Pentru testarea REST API
- **DBeaver** - Client PostgreSQL GUI
- **IntelliJ IDEA** - IDE recomandat
- **Docker Desktop** - Gestionare containere

---

