# SSATR IA 2025

Repository pentru laboratoarele materiei **Sisteme Software Avansate în Timp Real** (SSATR IA), Anul 2, Masterul de Inginerie Aplicațiilor, Universitatea Tehnică din Cluj-Napoca (UTCN).

## Structură Laboratoare

### [Laborator 1 - Event-Driven Architecture & Spring Boot](lab-1/readme.md)

**Obiective:**
- Implementare aplicații event-driven folosind un mini Event Framework
- Introducere în Spring Boot și Spring IoC
- Pattern-uri de design (Observer)
- Git workflow și best practices

**Exerciții:**
1. Event-Driven Application - scenarii: monitorizare prezențe, comenzi restaurant, gestionare coadă, acces evenimente
2. Spring Boot Introduction - customizare aplicație senzori

### [Laborator 2 - Message Brokers & RabbitMQ](lab-2/readme.md)

**Obiective:**
- Comunicare asincronă între componente distribuite
- Utilizare RabbitMQ (exchanges, queues, routing)
- Gestionare priorități și pattern-uri de messaging (fanout, direct, topic)
- Arhitecturi de timp real pentru aplicații IoT

**Exerciții:**
1. Sistem IoT cu Arhitectură de Timp Real - senzori prioritizați și controleri stratificați
2. Sistem de Livrare cu Distribuție și Confirmare Comenzi - pattern fanout și notificări

### [Laborator 3 - Kafka & Baze de Date Relaționale](lab-3/readme.md)

**Obiective:**
- Lucrul cu Apache Kafka pentru streaming de date în timp real
- Persistența datelor în baze de date relaționale (PostgreSQL)
- Comparație între abordări low-level (JDBC, Kafka API) și high-level (Spring Data JPA, Spring Kafka)
- Dezvoltare aplicații web cu Spring Boot și Thymeleaf
- Arhitecturi event-driven distribuite

**Exerciții:**
1. Testare Simulator Drone (low-level) - JDBC manual, Kafka Producer/Consumer API direct, management thread-uri
2. Testare Aplicație Web Management Drone - Spring Boot, JPA, REST API, interfață web cu Thymeleaf și Leaflet.js
3. Implementarea a Sistemului Mission Control 

## Resurse

- **Documentație:** Fiecare laborator are ghid detaliat de lucru
- **Cărți recomandate:**
  - "Spring in Action" (disponibilă pe Teams)
  - "Thinking in Java" by Bruce Eckel
- **Repository curs:** https://github.com/automatica-cluj/curs-ssatr-ia-v5

## Cerințe Tehnice

- **Java:** JDK 17 sau mai nou
- **Git:** Pentru version control
- **IDE:** IntelliJ IDEA (recomandat) sau NetBeans 24+
- **Docker:** Pentru servicii (RabbitMQ, baze de date, etc)
- **Maven:** Pentru build management

