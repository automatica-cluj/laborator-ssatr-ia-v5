# Laborator 3 - Sistem de Management Flotă de Drone cu Kafka și Baze de Date

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

## Descrierea Sistemului

Veți implementa un **Sistem de Management și Monitorizare Flotă de Drone** care:

- Colectează telemetrie în timp real de la drone (GPS, baterie, senzori)
- Procesează stream-uri de date cu Kafka
- Persistă date în PostgreSQL pentru analiză și raportare
- Oferă o interfață web pentru monitorizare și control
- Trimite comenzi către drone

## Resurse și Pregătire

### Instalare Kafka, Zookeeper și PostgreSQL

Pentru acest laborator este necesară instalarea:
- **Apache Kafka** - message broker pentru streaming
- **Zookeeper** - coordonare cluster Kafka
- **PostgreSQL** - bază de date relațională

#### Opțiunea recomandată: Docker Compose

În directorul `lab-3/resources/` găsiți un fișier `docker-compose.yml` care pornește toate serviciile necesare:

```bash
cd lab-3/resources/

# Pornire servicii
docker-compose up -d

# Verificare status
docker-compose ps

# Oprire servicii
docker-compose down

# Oprire și ștergere volume-uri (date)
docker-compose down -v
```

**Porturile folosite:**
- Kafka: `localhost:9092`
- Zookeeper: `localhost:2181`
- PostgreSQL: `localhost:5432` (user: `postgres`, password: `postgres`, database: `dronedb`)

### Exemple de referință

Pentru a vă ajuta în rezolvarea exercițiilor:

1. **Exemplu Kafka** din repository-ul cursului:
   - https://github.com/automatica-cluj/curs-ssatr-ia-v5

2. **Exemple locale** - în repository-ul curent:
   - `examples/` - exemple Spring Boot existente

### Resurse de studiu

- **Apache Kafka Documentation:** https://kafka.apache.org/documentation/
- **Kafka Quickstart:** https://kafka.apache.org/quickstart
- **Spring for Apache Kafka:** https://docs.spring.io/spring-kafka/reference/
- **Spring Data JPA:** https://docs.spring.io/spring-data/jpa/reference/
- **Thymeleaf Documentation:** https://www.thymeleaf.org/documentation.html
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/

## Workflow Git - Pași generali

Acest workflow este similar cu cel din laboratoarele anterioare. Pentru detalii complete despre lucrul cu Git, consultați [Ghidul Laborator 1](../lab-1/readme.md).

### 1. Actualizare repository local

```bash
# Asigurați-vă că sunteți pe main și aveți ultima versiune
git checkout main
git pull origin main
```

### 2. Creare Issue

1. Accesați secțiunea `Issues` din repository
2. Click pe `New Issue`
3. Completați:
   - **Title:** De exemplu: "Laborator 3 - Exercițiul 1"
   - **Description:** Descriere scurtă a cerințelor
   - **Labels:** (opțional) "enhancement"
4. Click pe `Create issue`

### 3. Creare Branch din interfața web

1. Din Issue, click pe `Create a branch`
2. Denumire sugerată: `lab3-<descriere-scurta>` (ex: `lab3-drone-telemetry`)
3. **Lăsați bifat:** "Checkout locally" pentru a vedea comenzile necesare
4. Click pe `Create branch`

### 4. Checkout branch local

```bash
# Aduceți toate branch-urile de pe remote
git fetch origin

# Comutați pe branch-ul de lucru
git checkout <nume-branch>

# Verificați că sunteți pe branch-ul corect
git branch
```

### 5. Rezolvare exercițiu

Lucrați la rezolvarea exercițiului în folderul `lab-3/`.

### 6. Commit și Push

```bash
# Adăugați toate fișierele modificate
git add .

# Creați un commit cu un mesaj descriptiv
git commit -m "Rezolvare parțială/completă laborator 3 - exercițiul X"

# Trimiteți modificările pe remote
git push
```

**Notă:** Puteți face multiple commit-uri pe parcursul lucrului.

### 7. Pull Request

1. Accesați repository-ul pe GitHub
2. Selectați branch-ul de lucru
3. Click pe `Compare & pull request`
4. Configurați:
   - **Base:** `main`
   - **Compare:** `<branch-ul-vostru>`
5. Adăugați comentarii relevante
6. (Opțional) Assignați reviewer pentru feedback
7. Click pe `Create pull request`

### 8. Review și Merge

1. Așteptați eventualul review
2. Dacă totul este OK, click pe `Merge pull request`
3. Confirmați merge-ul
4. (Opțional) `Delete branch` pentru a șterge branch-ul de pe remote

### 9. Actualizare repository local

```bash
# Comutați înapoi pe main
git checkout main

# Aduceți ultimele modificări
git pull origin main

# Ștergeți branch-ul local (opțional)
git branch -d <nume-branch>
```

## Exercițiile Laboratorului 3

### Exercițiul 1 - Sistem Low-Level de Telemetrie Drone (Kafka + JDBC)

Implementare sistem de colectare și procesare telemetrie folosind **Kafka Producer/Consumer API** (pure Java, fără Spring) și **JDBC** pentru persistență în PostgreSQL.

**Detalii complete:** [exercitiul3-1.md](exercitiul3-1.md)

**Componente principale:**
- Drone Simulator - simulează multiple drone care emit telemetrie
- Telemetry Processor - consumă și procesează date din Kafka
- Command Dispatcher - trimite comenzi către drone
- Analytics Reporter - generează rapoarte SQL
- Kafka Broker + PostgreSQL

**Obiective de învățare:**
- Lucrul low-level cu Kafka Producer și Consumer API
- JDBC și SQL pentru operații CRUD
- Connection pooling și transaction management
- Serializare/deserializare JSON
- Gestionarea offset-urilor Kafka

### Exercițiul 2 - Dashboard Management cu Spring Boot și Thymeleaf

Implementare aplicație web full-stack pentru managementul flotei de drone folosind **Spring Boot**, **Spring Kafka**, **Spring Data JPA** și **Thymeleaf**.

**Detalii complete:** [exercitiul3-2.md](exercitiul3-2.md)

**Componente principale:**
- Dashboard web cu Thymeleaf
- Management drone și misiuni
- Streaming telemetrie real-time (WebSocket)
- REST API pentru operații CRUD
- Integrare Spring Kafka și Spring Data JPA

**Obiective de învățare:**
- Abstractizarea oferită de Spring Framework
- ORM vs SQL direct (JPA entities, repositories)
- Dezvoltare interfață web cu Thymeleaf
- WebSocket pentru update-uri real-time
- Arhitectură layered (Controller, Service, Repository)

## Timp total estimat

**6-8 ore** pentru finalizarea completă a ambelor exerciții.

## Structura fișierelor

```
lab-3/
├── readme.md                           # Acest fișier
├── exercitiul3-1.md                    # Detalii Exercițiul 1
├── exercitiul3-2.md                    # Detalii Exercițiul 2
├── resources/
│   ├── docker-compose.yml              # Kafka + Zookeeper + PostgreSQL
│   └── schema.sql                      # Schema inițială baza de date
├── exercitiul3-1/                      # Aplicație schelet Ex. 1
│   ├── pom.xml
│   └── src/
│       └── main/java/ro/utcluj/ssatr/lab3/
│           ├── simulator/              # Drone simulator
│           ├── processor/              # Telemetry processor
│           ├── dispatcher/             # Command dispatcher
│           ├── analytics/              # SQL reporter
│           ├── model/                  # Data models
│           └── utils/                  # Utilities (DB, Kafka config)
└── exercitiul3-2/                      # Aplicație schelet Ex. 2
    ├── pom.xml
    └── src/
        └── main/
            ├── java/ro/utcluj/ssatr/lab3/drone/
            │   ├── controller/         # Spring MVC controllers
            │   ├── service/            # Business logic
            │   ├── repository/         # Spring Data repositories
            │   ├── model/              # JPA entities
            │   ├── kafka/              # Kafka listeners/producers
            │   └── config/             # Spring configuration
            └── resources/
                ├── templates/          # Thymeleaf templates
                ├── static/             # CSS, JS, images
                └── application.properties
```

## Întrebări frecvente

**Î: Trebuie să creez un branch separat pentru fiecare exercițiu?**
R: Puteți lucra cu un singur branch per laborator sau câte un branch per exercițiu, după preferință.

**Î: Este obligatoriu să folosesc Docker pentru Kafka și PostgreSQL?**
R: Nu este obligatoriu, dar este foarte recomandat pentru o configurare rapidă și consistentă.

**Î: Pot folosi alte limbaje de programare decât Java?**
R: Exercițiile sunt concepute pentru Java, dar dacă doriți să experimentați cu Python sau alt limbaj, asigurați-vă că implementați conceptele cerute.

**Î: Cum verific dacă Kafka funcționează?**
R: Puteți folosi comenzi din container:
```bash
# Liste topics
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Creare topic de test
docker exec -it kafka kafka-topics.sh --create --topic test --bootstrap-server localhost:9092

# Producer console
docker exec -it kafka kafka-console-producer.sh --topic test --bootstrap-server localhost:9092

# Consumer console
docker exec -it kafka kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server localhost:9092
```

**Î: Cum mă conectez la PostgreSQL?**
R: Folosiți un client SQL (DBeaver, pgAdmin, IntelliJ Database Tool) cu:
- Host: `localhost`
- Port: `5432`
- Database: `dronedb`
- User: `postgres`
- Password: `postgres`

**Î: Trebuie să implementez o hartă reală pentru drone?**
R: Nu, puteți afișa doar coordonatele text sau o reprezentare simplificată. Focusul este pe Kafka și baze de date, nu pe UI.

## Diferența dintre Exercițiul 1 și 2

| Aspect | Exercițiul 1 (Low-Level) | Exercițiul 2 (Spring Boot) |
|--------|-------------------------|----------------------------|
| **Kafka** | Producer/Consumer API direct | Spring Kafka (`@KafkaListener`) |
| **Database** | JDBC + SQL manual | Spring Data JPA + Entities |
| **Tranzacții** | Manual (begin, commit, rollback) | Declarativ (`@Transactional`) |
| **Configuration** | Programatic (Properties) | application.properties + annotations |
| **UI** | Console/CLI | Web (Thymeleaf) + REST API |
| **Complexitate** | Mai low-level, control total | Abstractizare, mai rapid de dezvoltat |
| **Învățare** | Înțelegerea mecanismelor interne | Eficiență și best practices Spring |

Ambele exerciții sunt importante: primul vă învață fundamentele, al doilea vă arată cum să le folosiți eficient în producție.

## Resurse suplimentare

- **Apache Kafka:** https://kafka.apache.org/
- **Spring Boot:** https://spring.io/projects/spring-boot
- **PostgreSQL Tutorial:** https://www.postgresqltutorial.com/
- **Thymeleaf Tutorial:** https://www.baeldung.com/thymeleaf-in-spring-mvc
- **Event-Driven Architecture:** https://martinfowler.com/articles/201701-event-driven.html
- **CQRS Pattern:** https://martinfowler.com/bliki/CQRS.html

## Note finale

Acest laborator combină concepte importante pentru sistemele distribuite moderne:
- **Streaming** (Kafka) pentru procesare evenimente în timp real
- **Persistență** (PostgreSQL) pentru stocare și analiză istorică
- **Event-driven architecture** pentru decuplare componente
- **Full-stack development** cu Spring Boot și Thymeleaf

Concentrați-vă pe înțelegerea fluxurilor de date și a modului în care componentele interacționează. Succces!
