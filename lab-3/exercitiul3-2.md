# Exercițiul 3-2: Dashboard Management cu Spring Boot și Thymeleaf

## Introducere

Scopul acestui exercițiu este de a implementa o aplicație web full-stack pentru managementul flotei de drone folosind **Spring Boot** și abstractizările oferite de ecosistemul Spring. Veți folosi **Spring Kafka** pentru messaging, **Spring Data JPA** pentru persistență, și **Thymeleaf** pentru interfața web.

## Obiective de învățare

- Înțelegerea abstractizărilor oferite de **Spring Framework**
- Folosirea **Spring Kafka** cu annotations (`@KafkaListener`, `KafkaTemplate`)
- Lucrul cu **Spring Data JPA** și entities
- **ORM vs SQL direct** - avantaje și dezavantaje
- Dezvoltare interfață web cu **Thymeleaf**
- **REST API** pentru operații CRUD
- **WebSocket** pentru update-uri real-time în UI
- Arhitectură **layered**: Controller → Service → Repository

## Descrierea Sistemului

Veți implementa o aplicație web care oferă:
- **Dashboard** cu vizualizare stare flotă
- **Management drone** (CRUD operations)
- **Planificare misiuni** cu waypoints
- **Monitorizare telemetrie** în timp real
- **Istoric și rapoarte**

### Arhitectura

```
┌──────────────────────────────────────────────────────────┐
│                     Browser (Client)                      │
│                                                           │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐         │
│  │ Dashboard  │  │ Drone Mgmt │  │  Missions  │         │
│  │  (HTML)    │  │   (HTML)   │  │   (HTML)   │         │
│  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘         │
│         │                │                │               │
│         │ WebSocket      │ HTTP           │ HTTP          │
│         │ (telemetry)    │ (REST API)     │ (REST API)    │
└─────────┼────────────────┼────────────────┼───────────────┘
          │                │                │
          ▼                ▼                ▼
┌──────────────────────────────────────────────────────────┐
│              Spring Boot Application                      │
│                                                           │
│  ┌─────────────────────────────────────────────────┐    │
│  │              Controllers Layer                   │    │
│  │  - WebController (Thymeleaf)                    │    │
│  │  - DroneRestController (REST API)               │    │
│  │  - MissionRestController (REST API)             │    │
│  │  - WebSocketController (Real-time updates)      │    │
│  └───────────────────┬─────────────────────────────┘    │
│                      │                                    │
│  ┌───────────────────▼─────────────────────────────┐    │
│  │              Service Layer                       │    │
│  │  - DroneService                                  │    │
│  │  - MissionService                                │    │
│  │  - TelemetryService                              │    │
│  └───────────────────┬─────────────────────────────┘    │
│                      │                                    │
│  ┌───────────────────▼─────────────────────────────┐    │
│  │         Repository Layer (Spring Data JPA)       │    │
│  │  - DroneRepository                               │    │
│  │  - MissionRepository                             │    │
│  │  - TelemetryRepository                           │    │
│  │  - WaypointRepository                            │    │
│  └───────────────────┬─────────────────────────────┘    │
│                      │                                    │
│  ┌───────────────────▼─────────────────────────────┐    │
│  │            Kafka Integration                     │    │
│  │  - TelemetryKafkaListener (@KafkaListener)      │    │
│  │  - CommandKafkaProducer (KafkaTemplate)         │    │
│  └──────────────────────────────────────────────────┘   │
│                                                           │
└─────┬──────────────────────────────────────────┬─────────┘
      │                                          │
      ▼                                          ▼
┌──────────────┐                        ┌──────────────────┐
│    Kafka     │                        │   PostgreSQL     │
│              │                        │                  │
│  Topics:     │                        │  Tables:         │
│  - drone-    │                        │  - drones        │
│    telemetry │                        │  - missions      │
│  - drone-    │                        │  - waypoints     │
│    commands  │                        │  - telemetry     │
└──────────────┘                        └──────────────────┘
```

## Structura Aplicației Spring Boot

```
exercitiul3-2/
├── pom.xml
└── src/main/
    ├── java/ro/utcluj/ssatr/lab3/drone/
    │   ├── DroneManagementApplication.java      # Main Spring Boot
    │   ├── controller/
    │   │   ├── WebController.java               # Thymeleaf pages
    │   │   ├── DroneRestController.java         # REST API drones
    │   │   ├── MissionRestController.java       # REST API missions
    │   │   └── TelemetryWebSocketController.java# WebSocket
    │   ├── service/
    │   │   ├── DroneService.java
    │   │   ├── MissionService.java
    │   │   └── TelemetryService.java
    │   ├── repository/
    │   │   ├── DroneRepository.java
    │   │   ├── MissionRepository.java
    │   │   ├── WaypointRepository.java
    │   │   └── TelemetryRepository.java
    │   ├── model/
    │   │   ├── Drone.java                       # JPA Entity
    │   │   ├── Mission.java                     # JPA Entity
    │   │   ├── Waypoint.java                    # JPA Entity
    │   │   ├── TelemetrySnapshot.java           # JPA Entity
    │   │   └── DroneStatus.java                 # Enum
    │   ├── kafka/
    │   │   ├── TelemetryKafkaListener.java      # @KafkaListener
    │   │   └── CommandKafkaProducer.java        # KafkaTemplate
    │   ├── config/
    │   │   ├── KafkaConfig.java                 # Kafka configuration
    │   │   └── WebSocketConfig.java             # WebSocket configuration
    │   └── dto/
    │       ├── TelemetryDTO.java
    │       ├── DroneCommandDTO.java
    │       └── MissionDTO.java
    └── resources/
        ├── application.properties               # Spring configuration
        ├── templates/                           # Thymeleaf templates
        │   ├── index.html                       # Dashboard
        │   ├── drones.html                      # Lista drone
        │   ├── drone-detail.html                # Detalii dronă
        │   ├── missions.html                    # Lista misiuni
        │   ├── mission-create.html              # Creare misiune
        │   └── telemetry-monitor.html           # Monitor real-time
        └── static/
            ├── css/
            │   └── style.css                    # Custom CSS
            └── js/
                ├── dashboard.js                 # Dashboard logic
                └── telemetry-websocket.js       # WebSocket client
```

## Componente de Implementat

### 1. Entities (JPA)

#### Drone Entity

```java
@Entity
@Table(name = "drones")
public class Drone {
    @Id
    private String id;

    private String name;
    private String model;

    @Enumerated(EnumType.STRING)
    private DroneStatus status; // IDLE, FLYING, CHARGING, EMERGENCY, LANDED

    private Double batteryLevel;
    private Long lastSeen;

    @OneToMany(mappedBy = "drone", cascade = CascadeType.ALL)
    private List<Mission> missions;

    // TODO: Add getters, setters, constructors
}
```

#### Mission Entity

```java
@Entity
@Table(name = "missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;

    @Enumerated(EnumType.STRING)
    private MissionStatus status; // PLANNED, ACTIVE, COMPLETED, FAILED

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    private List<Waypoint> waypoints;

    private Long startTime;
    private Long endTime;

    // TODO: Add getters, setters
}
```

#### Waypoint Entity

```java
@Entity
@Table(name = "waypoints")
public class Waypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private Integer sequenceNumber;
    private Double latitude;
    private Double longitude;
    private Double altitude;

    private Boolean reached;

    // TODO: Add getters, setters
}
```

#### TelemetrySnapshot Entity

```java
@Entity
@Table(name = "telemetry_logs")
public class TelemetrySnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;

    private Long timestamp;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;
    private Integer heading;
    private Double batteryLevel;
    private Double temperature;
    private Double vibration;

    @Enumerated(EnumType.STRING)
    private DroneStatus status;

    // TODO: Add getters, setters
}
```

### 2. Repositories (Spring Data JPA)

```java
public interface DroneRepository extends JpaRepository<Drone, String> {
    // Spring Data generează automat implementarea

    List<Drone> findByStatus(DroneStatus status);

    @Query("SELECT d FROM Drone d WHERE d.batteryLevel < :threshold")
    List<Drone> findLowBatteryDrones(@Param("threshold") Double threshold);

    // TODO: Adăugați query-uri custom
}

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByDroneId(String droneId);
    List<Mission> findByStatus(MissionStatus status);

    // TODO: Query pentru misiuni active
}

public interface TelemetryRepository extends JpaRepository<TelemetrySnapshot, Long> {
    List<TelemetrySnapshot> findByDroneIdOrderByTimestampDesc(String droneId, Pageable pageable);

    @Query("SELECT t FROM TelemetrySnapshot t WHERE t.drone.id = :droneId AND t.timestamp > :since ORDER BY t.timestamp DESC")
    List<TelemetrySnapshot> findRecentTelemetry(@Param("droneId") String droneId, @Param("since") Long since);

    // TODO: Adăugați query pentru statistici
}
```

### 3. Services

#### DroneService

```java
@Service
public class DroneService {
    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private CommandKafkaProducer commandProducer;

    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    public Drone getDroneById(String id) {
        return droneRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Drone not found: " + id));
    }

    public Drone createDrone(Drone drone) {
        // TODO: Validate and save
        return droneRepository.save(drone);
    }

    public void sendTakeOffCommand(String droneId) {
        // TODO: Create command and send via Kafka
        DroneCommandDTO command = new DroneCommandDTO();
        command.setDroneId(droneId);
        command.setCommandType("TAKE_OFF");
        commandProducer.sendCommand(command);
    }

    public void sendLandCommand(String droneId) {
        // TODO: Similar to takeoff
    }

    // TODO: Implement other methods
}
```

#### MissionService

```java
@Service
public class MissionService {
    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    @Autowired
    private DroneRepository droneRepository;

    public Mission createMission(MissionDTO missionDTO) {
        // TODO: Create mission with waypoints
        Mission mission = new Mission();
        mission.setName(missionDTO.getName());
        mission.setDescription(missionDTO.getDescription());

        Drone drone = droneRepository.findById(missionDTO.getDroneId())
            .orElseThrow(() -> new RuntimeException("Drone not found"));
        mission.setDrone(drone);
        mission.setStatus(MissionStatus.PLANNED);

        // Add waypoints
        List<Waypoint> waypoints = new ArrayList<>();
        for (int i = 0; i < missionDTO.getWaypoints().size(); i++) {
            Waypoint wp = new Waypoint();
            wp.setMission(mission);
            wp.setSequenceNumber(i);
            // Set coordinates from DTO
            waypoints.add(wp);
        }
        mission.setWaypoints(waypoints);

        return missionRepository.save(mission);
    }

    public List<Mission> getActiveMissions() {
        return missionRepository.findByStatus(MissionStatus.ACTIVE);
    }

    // TODO: Start mission, complete mission, etc.
}
```

### 4. Kafka Integration

#### TelemetryKafkaListener

```java
@Component
public class TelemetryKafkaListener {

    @Autowired
    private TelemetryService telemetryService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For WebSocket

    @KafkaListener(topics = "drone-telemetry", groupId = "drone-dashboard-group")
    public void listenTelemetry(String message) {
        // TODO: Parse JSON message
        TelemetryDTO telemetryDTO = parseJson(message);

        // Save to database
        telemetryService.saveTelemetry(telemetryDTO);

        // Broadcast to WebSocket clients
        messagingTemplate.convertAndSend("/topic/telemetry", telemetryDTO);

        // Update drone last seen and status
        telemetryService.updateDroneStatus(telemetryDTO.getDroneId(), telemetryDTO);
    }

    private TelemetryDTO parseJson(String json) {
        // TODO: Use Jackson or Gson
        return new Gson().fromJson(json, TelemetryDTO.class);
    }
}
```

#### CommandKafkaProducer

```java
@Component
public class CommandKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String COMMAND_TOPIC = "drone-commands";

    public void sendCommand(DroneCommandDTO command) {
        // TODO: Serialize to JSON and send
        String json = new Gson().toJson(command);
        kafkaTemplate.send(COMMAND_TOPIC, command.getDroneId(), json);
    }
}
```

### 5. REST Controllers

#### DroneRestController

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

    @PostMapping
    public Drone createDrone(@RequestBody Drone drone) {
        return droneService.createDrone(drone);
    }

    @PostMapping("/{id}/takeoff")
    public ResponseEntity<String> takeoff(@PathVariable String id) {
        droneService.sendTakeOffCommand(id);
        return ResponseEntity.ok("Takeoff command sent");
    }

    @PostMapping("/{id}/land")
    public ResponseEntity<String> land(@PathVariable String id) {
        droneService.sendLandCommand(id);
        return ResponseEntity.ok("Land command sent");
    }

    // TODO: Add more endpoints
}
```

#### MissionRestController

```java
@RestController
@RequestMapping("/api/missions")
public class MissionRestController {

    @Autowired
    private MissionService missionService;

    @GetMapping
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }

    @GetMapping("/{id}")
    public Mission getMission(@PathVariable Long id) {
        return missionService.getMissionById(id);
    }

    @PostMapping
    public Mission createMission(@RequestBody MissionDTO missionDTO) {
        return missionService.createMission(missionDTO);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startMission(@PathVariable Long id) {
        missionService.startMission(id);
        return ResponseEntity.ok("Mission started");
    }

    // TODO: Add more endpoints
}
```

### 6. Web Controller (Thymeleaf)

```java
@Controller
public class WebController {

    @Autowired
    private DroneService droneService;

    @Autowired
    private MissionService missionService;

    @GetMapping("/")
    public String index(Model model) {
        // Dashboard
        model.addAttribute("totalDrones", droneService.getAllDrones().size());
        model.addAttribute("activeDrones", droneService.getActiveDrones().size());
        model.addAttribute("lowBatteryDrones", droneService.getLowBatteryDrones().size());
        return "index";
    }

    @GetMapping("/drones")
    public String drones(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "drones";
    }

    @GetMapping("/drones/{id}")
    public String droneDetail(@PathVariable String id, Model model) {
        model.addAttribute("drone", droneService.getDroneById(id));
        model.addAttribute("recentTelemetry", droneService.getRecentTelemetry(id, 50));
        return "drone-detail";
    }

    @GetMapping("/missions")
    public String missions(Model model) {
        model.addAttribute("missions", missionService.getAllMissions());
        return "missions";
    }

    @GetMapping("/missions/create")
    public String createMission(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "mission-create";
    }

    @GetMapping("/monitor")
    public String telemetryMonitor(Model model) {
        return "telemetry-monitor";
    }
}
```

### 7. WebSocket Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-telemetry").withSockJS();
    }
}
```

### 8. Thymeleaf Templates

#### index.html (Dashboard)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Drone Management Dashboard</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <nav>
        <a th:href="@{/}">Dashboard</a>
        <a th:href="@{/drones}">Drones</a>
        <a th:href="@{/missions}">Missions</a>
        <a th:href="@{/monitor}">Live Monitor</a>
    </nav>

    <div class="dashboard">
        <h1>Drone Fleet Dashboard</h1>

        <div class="stats">
            <div class="stat-card">
                <h3>Total Drones</h3>
                <p class="stat-value" th:text="${totalDrones}">0</p>
            </div>

            <div class="stat-card">
                <h3>Active Drones</h3>
                <p class="stat-value" th:text="${activeDrones}">0</p>
            </div>

            <div class="stat-card alert">
                <h3>Low Battery</h3>
                <p class="stat-value" th:text="${lowBatteryDrones}">0</p>
            </div>
        </div>

        <!-- TODO: Add charts, recent activity, etc. -->
    </div>

    <script th:src="@{/js/dashboard.js}"></script>
</body>
</html>
```

#### drones.html (Lista Drone)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Drones - Fleet Management</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <nav th:replace="~{fragments/nav :: nav}"></nav>

    <div class="container">
        <h1>Drone Fleet</h1>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Model</th>
                    <th>Status</th>
                    <th>Battery</th>
                    <th>Last Seen</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="drone : ${drones}">
                    <td th:text="${drone.id}">DRONE-001</td>
                    <td th:text="${drone.name}">Alpha</td>
                    <td th:text="${drone.model}">DJI Mavic</td>
                    <td>
                        <span class="status-badge"
                              th:classappend="${drone.status}"
                              th:text="${drone.status}">FLYING</span>
                    </td>
                    <td>
                        <div class="battery-bar">
                            <div class="battery-fill"
                                 th:style="'width: ' + ${drone.batteryLevel} + '%'"></div>
                        </div>
                        <span th:text="${drone.batteryLevel} + '%'">85%</span>
                    </td>
                    <td th:text="${#dates.format(drone.lastSeen, 'HH:mm:ss')}">12:34:56</td>
                    <td>
                        <a th:href="@{/drones/{id}(id=${drone.id})}" class="btn">Details</a>
                        <button onclick="takeoff(this.dataset.droneId)"
                                th:data-drone-id="${drone.id}"
                                class="btn btn-success">Take Off</button>
                        <button onclick="land(this.dataset.droneId)"
                                th:data-drone-id="${drone.id}"
                                class="btn btn-danger">Land</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <script>
        function takeoff(droneId) {
            fetch(`/api/drones/${droneId}/takeoff`, { method: 'POST' })
                .then(response => response.text())
                .then(data => alert(data));
        }

        function land(droneId) {
            fetch(`/api/drones/${droneId}/land`, { method: 'POST' })
                .then(response => response.text())
                .then(data => alert(data));
        }
    </script>
</body>
</html>
```

#### telemetry-monitor.html (WebSocket Real-time)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Live Telemetry Monitor</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
</head>
<body>
    <nav th:replace="~{fragments/nav :: nav}"></nav>

    <div class="container">
        <h1>Live Telemetry Monitor</h1>

        <div id="telemetry-feed">
            <!-- Telemetry data will be appended here via WebSocket -->
        </div>
    </div>

    <script>
        var socket = new SockJS('/ws-telemetry');
        var stompClient = Stomp.over(socket);

        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);

            stompClient.subscribe('/topic/telemetry', function(message) {
                var telemetry = JSON.parse(message.body);
                displayTelemetry(telemetry);
            });
        });

        function displayTelemetry(telemetry) {
            var feed = document.getElementById('telemetry-feed');
            var item = document.createElement('div');
            item.className = 'telemetry-item';
            item.innerHTML = `
                <strong>${telemetry.droneId}</strong> -
                Battery: ${telemetry.batteryLevel}% |
                Alt: ${telemetry.altitude}m |
                Speed: ${telemetry.speed} m/s |
                ${new Date(telemetry.timestamp).toLocaleTimeString()}
            `;
            feed.insertBefore(item, feed.firstChild);

            // Keep only last 20 items
            while (feed.children.length > 20) {
                feed.removeChild(feed.lastChild);
            }
        }
    </script>
</body>
</html>
```

## application.properties

```properties
# Application
spring.application.name=drone-management-system

# Server
server.port=8080

# PostgreSQL DataSource
spring.datasource.url=jdbc:postgresql://localhost:5432/dronedb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=drone-dashboard-group
spring.kafka.consumer.auto-offset-reset=latest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Logging
logging.level.ro.utcluj.ssatr.lab3=DEBUG
logging.level.org.springframework.kafka=INFO
```

## Dependințe Maven (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Spring Kafka -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>

    <!-- WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JSON Processing -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
    </dependency>

    <!-- Dev Tools (optional, for hot reload) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Lombok (optional, for reducing boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## Cerințe de Implementare

### Cerințe Minime (obligatorii)

1. **Dashboard:**
   - Afișare statistici flotă (total, active, low battery)
   - Listă drone cu status și baterie

2. **Management Drone:**
   - Listare toate drone
   - Detalii dronă (istoric telemetrie)
   - Comenzi: Take Off, Land

3. **Kafka Integration:**
   - Listener pentru telemetrie (salvare în DB)
   - Producer pentru comenzi

4. **Spring Data JPA:**
   - Entities pentru Drone, Mission, Waypoint, Telemetry
   - Repositories cu query methods

### Cerințe Extinse (opționale)

5. **Management Misiuni:**
   - CRUD misiuni cu waypoints
   - Start/stop misiune
   - Vizualizare progres

6. **WebSocket Real-time:**
   - Monitor telemetrie live
   - Update status drone în UI

7. **REST API complet:**
   - Endpoints pentru toate operațiile
   - DTO-uri pentru request/response

8. **UI îmbunătățit:**
   - Grafice (battery level over time)
   - Hartă simplificată (coordonate text sau canvas)
   - Filtrare și sortare

## Instrucțiuni de Rulare

### 1. Pornire servicii (Docker Compose)

```bash
cd lab-3/resources
docker-compose up -d
```

### 2. Build aplicație

```bash
cd lab-3/exercitiul3-2
mvn clean package
```

### 3. Rulare aplicație Spring Boot

```bash
mvn spring-boot:run
```

Sau:

```bash
java -jar target/exercitiul3-2.jar
```

### 4. Acces aplicație web

Deschideți browser la: http://localhost:8080

## Livrabile

1. **Cod sursă complet** în `lab-3/exercitiul3-2/`
2. **README.md** cu:
   - Arhitectura aplicației
   - Screenshot-uri interfață web
   - Endpoints REST API
   - Instrucțiuni build și rulare
3. **Demonstrație funcționalitate:**
   - Video/screenshots cu dashboard
   - Telemetrie real-time
   - Trimitere comenzi

## Testare și Validare

Verificați că:
- [ ] Aplicația pornește fără erori
- [ ] Dashboard afișează statistici corecte
- [ ] Listarea drone funcționează
- [ ] Comenzile se trimit în Kafka
- [ ] Telemetria este salvată în DB
- [ ] WebSocket funcționează (dacă implementat)
- [ ] CRUD misiuni funcționează (dacă implementat)

## Comparație cu Exercițiul 3-1

| Aspect | Exercițiul 3-1 | Exercițiul 3-2 |
|--------|---------------|---------------|
| Kafka | KafkaProducer/Consumer manual | @KafkaListener, KafkaTemplate |
| Database | JDBC + SQL | Spring Data JPA + Entities |
| Configuration | Programatic (Properties) | application.properties |
| UI | Console | Web (Thymeleaf) |
| REST API | Nu | Da |
| WebSocket | Nu | Da |
| Code lines | Mai multe | Mai puține (Spring magic) |

## Resurse Utile

- **Spring Boot Docs:** https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
- **Spring Kafka:** https://docs.spring.io/spring-kafka/reference/
- **Spring Data JPA:** https://docs.spring.io/spring-data/jpa/reference/
- **Thymeleaf:** https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html
- **WebSocket with Spring:** https://spring.io/guides/gs/messaging-stomp-websocket/

Succes!
