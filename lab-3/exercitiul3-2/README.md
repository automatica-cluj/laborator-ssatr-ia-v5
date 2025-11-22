# Exercițiul 3-2: Drone Management Dashboard (Spring Boot)

Aplicație schelet Spring Boot pentru managementul flotei de drone cu interfață web (Thymeleaf).

## Structură Aplicație

```
src/main/java/ro/utcluj/ssatr/lab3/drone/
├── DroneManagementApplication.java     # Main Spring Boot app
├── model/                              # JPA Entities
│   ├── Drone.java                     # TODO: Creați entity
│   ├── Mission.java                   # TODO: Creați entity
│   ├── Waypoint.java                  # TODO: Creați entity
│   └── TelemetrySnapshot.java         # TODO: Creați entity
├── repository/                         # Spring Data JPA Repositories
│   ├── DroneRepository.java           # TODO: Creați repository
│   ├── MissionRepository.java         # TODO: Creați repository
│   └── TelemetryRepository.java       # TODO: Creați repository
├── service/                            # Business Logic
│   ├── DroneService.java              # TODO: Creați service
│   ├── MissionService.java            # TODO: Creați service
│   └── TelemetryService.java          # TODO: Creați service
├── controller/                         # Controllers
│   ├── WebController.java             # TODO: Thymeleaf pages
│   ├── DroneRestController.java       # TODO: REST API
│   └── MissionRestController.java     # TODO: REST API
├── kafka/                              # Kafka Integration
│   ├── TelemetryKafkaListener.java    # TODO: @KafkaListener
│   └── CommandKafkaProducer.java      # TODO: KafkaTemplate
├── config/                             # Configuration
│   ├── KafkaConfig.java               # TODO: Kafka config
│   └── WebSocketConfig.java           # TODO: WebSocket config
└── dto/                                # Data Transfer Objects
    ├── TelemetryDTO.java              # TODO: Creați DTO
    └── DroneCommandDTO.java           # TODO: Creați DTO

src/main/resources/
├── application.properties              # ✓ Configurat
├── templates/                          # Thymeleaf templates
│   ├── index.html                     # TODO: Dashboard
│   ├── drones.html                    # TODO: Lista drone
│   └── drone-detail.html              # TODO: Detalii dronă
└── static/
    ├── css/style.css                  # TODO: Custom CSS
    └── js/dashboard.js                # TODO: JavaScript
```

## Arhitectura Aplicației

### Layered Architecture (Spring Boot)

```
┌─────────────────────────────────────────────────┐
│             Browser (Client)                     │
│  Thymeleaf Pages + JavaScript + WebSocket        │
└────────────────┬────────────────────────────────┘
                 │ HTTP / WebSocket
                 ▼
┌─────────────────────────────────────────────────┐
│         Controllers Layer                        │
│  - WebController (Thymeleaf views)              │
│  - DroneRestController (REST API)               │
│  - MissionRestController (REST API)             │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│           Service Layer                          │
│  - DroneService (business logic)                │
│  - MissionService (business logic)              │
│  - TelemetryService (business logic)            │
└─────────┬──────────────────────┬────────────────┘
          │                      │
          ▼                      ▼
┌──────────────────┐   ┌──────────────────────────┐
│  Repository Layer│   │   Kafka Integration      │
│  (Spring Data)   │   │  - TelemetryListener     │
│  - DroneRepo     │   │  - CommandProducer       │
│  - MissionRepo   │   └──────────┬───────────────┘
│  - TelemetryRepo │              │
└────────┬─────────┘              │
         │                        │
         ▼                        ▼
┌──────────────────┐      ┌──────────────┐
│   PostgreSQL     │      │    Kafka     │
└──────────────────┘      └──────────────┘
```

## Pași de Implementare

### Pas 1: Crearea Entities (JPA)

Creați entities în `model/` pe baza schemei PostgreSQL:
- `Drone.java` - @Entity pentru tabela `drones`
- `Mission.java` - @Entity pentru tabela `missions`
- `Waypoint.java` - @Entity pentru tabela `waypoints`
- `TelemetrySnapshot.java` - @Entity pentru `telemetry_logs`

**Exemplu:**
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

    private Double batteryLevel;
    private Long lastSeen;

    @OneToMany(mappedBy = "drone")
    private List<Mission> missions;

    // Getters, setters, constructors
}
```

### Pas 2: Crearea Repositories (Spring Data JPA)

Creați repositories în `repository/`:
```java
public interface DroneRepository extends JpaRepository<Drone, String> {
    List<Drone> findByStatus(DroneStatus status);

    @Query("SELECT d FROM Drone d WHERE d.batteryLevel < :threshold")
    List<Drone> findLowBatteryDrones(@Param("threshold") Double threshold);
}
```

### Pas 3: Crearea Services

Creați services în `service/` pentru business logic:
```java
@Service
public class DroneService {
    @Autowired
    private DroneRepository droneRepository;

    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    public Drone getDroneById(String id) {
        return droneRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Drone not found"));
    }

    // TODO: Implement more methods
}
```

### Pas 4: Crearea Controllers

**WebController (Thymeleaf):**
```java
@Controller
public class WebController {
    @Autowired
    private DroneService droneService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "index";
    }

    @GetMapping("/drones")
    public String drones(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "drones";
    }
}
```

**REST Controller:**
```java
@RestController
@RequestMapping("/api/drones")
public class DroneRestController {
    @Autowired
    private DroneService droneService;

    @GetMapping
    public List<Drone> getAllDrones() {
        return droneService.getAllDrones();
    }

    @GetMapping("/{id}")
    public Drone getDrone(@PathVariable String id) {
        return droneService.getDroneById(id);
    }

    @PostMapping("/{id}/takeoff")
    public ResponseEntity<String> takeoff(@PathVariable String id) {
        // TODO: Send command via Kafka
        return ResponseEntity.ok("Command sent");
    }
}
```

### Pas 5: Kafka Integration

**Listener (Consumer):**
```java
@Component
public class TelemetryKafkaListener {
    @Autowired
    private TelemetryService telemetryService;

    @KafkaListener(topics = "drone-telemetry", groupId = "drone-dashboard-group")
    public void listenTelemetry(String message) {
        // Parse JSON
        TelemetryDTO dto = new Gson().fromJson(message, TelemetryDTO.class);

        // Save to database
        telemetryService.saveTelemetry(dto);

        // TODO: Broadcast to WebSocket clients
    }
}
```

**Producer:**
```java
@Component
public class CommandKafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendCommand(DroneCommandDTO command) {
        String json = new Gson().toJson(command);
        kafkaTemplate.send("drone-commands", command.getDroneId(), json);
    }
}
```

### Pas 6: Thymeleaf Templates

**index.html (Dashboard):**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Drone Management Dashboard</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <h1>Drone Fleet Dashboard</h1>

    <div class="stats">
        <div class="stat-card">
            <h3>Total Drones</h3>
            <p th:text="${drones.size()}">0</p>
        </div>
        <!-- TODO: Add more statistics -->
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>Battery</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="drone : ${drones}">
                <td th:text="${drone.id}">DRONE-001</td>
                <td th:text="${drone.name}">Alpha</td>
                <td th:text="${drone.status}">FLYING</td>
                <td th:text="${drone.batteryLevel} + '%'">85%</td>
                <td>
                    <a th:href="@{/drones/{id}(id=${drone.id})}">Details</a>
                </td>
            </tr>
        </tbody>
    </table>
</body>
</html>
```

## Build și Rulare

### 1. Pornire servicii (Docker)
```bash
cd ../resources
docker-compose up -d
```

### 2. Build aplicație
```bash
mvn clean package
```

### 3. Rulare aplicație
```bash
mvn spring-boot:run
```

sau:

```bash
java -jar target/lab3-exercitiul2-1.0-SNAPSHOT.jar
```

### 4. Acces aplicație

Deschideți browser la: **http://localhost:8080**

## Endpoints REST API

- `GET /api/drones` - Lista toate drone
- `GET /api/drones/{id}` - Detalii dronă
- `POST /api/drones` - Creare dronă nouă
- `POST /api/drones/{id}/takeoff` - Comandă decolare
- `POST /api/drones/{id}/land` - Comandă aterizare
- `GET /api/missions` - Lista misiuni
- `POST /api/missions` - Creare misiune nouă
- `POST /api/missions/{id}/start` - Start misiune

## Cerințe de Implementare

### Minime (obligatorii)
- [ ] Entities pentru Drone, Mission, Waypoint, TelemetrySnapshot
- [ ] Repositories cu Spring Data JPA
- [ ] Services pentru business logic
- [ ] WebController pentru pagini Thymeleaf
- [ ] REST Controllers pentru API
- [ ] Kafka listener pentru telemetrie
- [ ] Kafka producer pentru comenzi
- [ ] Template HTML pentru dashboard
- [ ] Template HTML pentru lista drone

### Extinse (opționale)
- [ ] WebSocket pentru update-uri real-time
- [ ] CRUD complet pentru misiuni
- [ ] Pagină detalii dronă cu istoric telemetrie
- [ ] Filtrare și sortare în liste
- [ ] Grafice pentru battery level (Chart.js)
- [ ] Validări și error handling
- [ ] Paginare pentru rezultate mari
- [ ] Export CSV pentru rapoarte

## Comparație cu Exercițiul 3-1

| Aspect | Ex 3-1 (Low-Level) | Ex 3-2 (Spring Boot) |
|--------|-------------------|----------------------|
| Database | JDBC manual | Spring Data JPA |
| Kafka | Producer/Consumer API | @KafkaListener, KafkaTemplate |
| Config | Programatic | application.properties |
| UI | Console | Web (Thymeleaf) |
| REST API | Nu | Da |
| WebSocket | Nu | Da (opțional) |
| Cod | Mai mult, control total | Mai puțin, abstractizat |

## Note pentru Studenți

- **Înțelegere vs Eficiență:** Exercițiul 3-1 vă învață cum funcționează lucrurile la nivel low. Exercițiul 3-2 vă arată cum să le folosiți eficient în producție.
- **Spring Magic:** Observați cum annotations (@Entity, @Repository, @Service, @Controller) reduc dramatic cantitatea de cod.
- **Abstractizare:** Spring Data JPA face query-uri pentru voi. Spring Kafka gestionează consumatori automat.
- **Best Practices:** Arhitectura layered (Controller → Service → Repository) este standard în industrie.

## Debugging

### Verificare conectare DB
```bash
# Check logs
tail -f logs/spring.log

# Query database
docker exec -it postgres psql -U postgres -d dronedb
SELECT * FROM drones;
```

### Verificare Kafka
```bash
# Monitor telemetrie
docker exec -it kafka kafka-console-consumer.sh \
  --topic drone-telemetry \
  --from-beginning \
  --bootstrap-server localhost:9092
```

## Resurse

- **Spring Boot Docs:** https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
- **Spring Data JPA:** https://docs.spring.io/spring-data/jpa/reference/
- **Thymeleaf:** https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html
- **Baeldung Spring Tutorials:** https://www.baeldung.com/spring-boot

Succes!
