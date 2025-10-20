# Laborator 1 - Ghid de lucru

## Resurse și Pregătire

### 1. Instalare unelte necesare

Verificați că aveți instalate următoarele instrumente:

#### Java
```bash
java --version
```

#### Git
```bash
git --version
```

**Important:** Trimiteți screenshot-uri cu rezultatele acestor comenzi pentru confirmare.

#### Medii de dezvoltare recomandate
- **IntelliJ IDEA** - recomandat pentru lucrul cu Spring
- **NetBeans** (versiunea 24 sau mai nouă) - util pentru crearea interfețelor grafice în mod vizual

#### Docker (opțional, dar recomandat)
Veți folosi Docker pentru a rula servicii precum baze de date și message brokeri în laboratorele viitoare.

### 2. Configurare GitHub

#### Creare cont
Dacă nu aveți deja un cont GitHub, creați unul. Dacă aveți deja, folosiți contul existent.

#### Activare GitHub Student Developer Pack
Accesați [GitHub Student Developer Pack](https://education.github.com/pack) pentru acces gratuit la GitHub Copilot și alte facilități.

## Inițializare Repository

### 1. Creare repository privat

1. Accesați GitHub și creați un repository nou
2. **Convenție de denumire:** `ssatr-lab-<NumePrenume>` (ex: `siadp-lab-PopescuIon`)
3. Setări:
   - **Visibility:** Private
   - Bifați: "Add a README file"
   - Adăugați `.gitignore` pentru Java
4. Click pe "Create repository"

### 2. Adăugare colaborator

1. Accesați `Settings` → `Collaborators`
2. Click pe `Add people`
3. Adăugați utilizatorul: **`automatica-cluj`**

### 3. Completare formular

Completați formularul de înscriere cu:
- Informații despre facultatea absolvită
- Link-ul către repository-ul creat

## Workflow Git - Pași generali

Acest workflow se va repeta la fiecare laborator:

### 1. Clonare repository (o singură dată la început)

```bash
# Selectați un folder de lucru
cd path/to/your/workspace

# Clonați repository-ul
git clone <URL-repository>

# Intrați în folderul clonат
cd siadp-lab-<NumePrenume>
```

### 2. Creare Issue

1. Accesați secțiunea `Issues` din repository
2. Click pe `New Issue`
3. Completați:
   - **Title:** De exemplu: "Laborator 1 - Exercițiul 1"
   - **Description:** Descriere scurtă a cerințelor
   - **Labels:** (opțional) "enhancement"
4. Click pe `Create issue`

### 3. Creare Branch din interfața web

1. Din Issue, click pe `Create a branch`
2. Denumire sugerată: `<numar-lab>-<descriere-scurta>` (ex: `1-event-framework`)
3. **Lăsați bifat:** "Checkout locally" pentru a vedea comenzile necesare
4. Click pe `Create branch`

### 4. Actualizare și checkout branch local

```bash
# Aduceți toate branch-urile de pe remote
git fetch origin

# Afișați toate branch-urile (opțional)
git branch -a

# Comutați pe branch-ul de lucru
git checkout <nume-branch>

# Verificați că sunteți pe branch-ul corect (va apărea cu verde)
git branch
```

### 5. Rezolvare exercițiu

Lucrați la rezolvarea exercițiului în folderul corespunzător.

### 6. Commit și Push

```bash
# Adăugați toate fișierele modificate
git add .

# Creați un commit cu un mesaj descriptiv
git commit -m "Rezolvare parțială/completă laborator X - exercițiul Y"

# Trimiteți modificările pe remote
git push
```

**Notă:** Puteți face multiple commit-uri pe parcursul lucrului.

### 7. Pull Request

1. Accesați repository-ul pe GitHub
2. Selectați branch-ul de lucru
3. Click pe `Compare & pull request` (sau manual din `Pull requests` → `New pull request`)
4. Configurați:
   - **Base:** `main`
   - **Compare:** `<branch-ul-vostru>`
5. Adăugați comentarii relevante
6. (Opțional) Assignați `automatica-cluj` ca reviewer pentru feedback Copilot
7. Click pe `Create pull request`

### 8. Review și Merge

1. Așteptați eventualul review (dacă ați activat Copilot review)
2. Dacă totul este OK, click pe `Merge pull request`
3. Confirmați merge-ul
4. (Opțional) `Delete branch` pentru a șterge branch-ul de pe remote

### 9. Actualizare repository local

```bash
# Comutați înapoi pe main
git checkout main

# Aduceți ultimele modificări
git pull origin main

# Ștergeți branch-ul local (opțional, dar recomandat)
git branch -d <nume-branch>
```

## Exercițiul 1 - Event-Driven Application

### Obiective

- Alegerea unui scenariu de aplicație
- Identificarea tipurilor de evenimente
- Implementarea unui schelet de aplicație folosind un mini Event Framework

### Scenarii disponibile

Alegeți **unul** dintre scenariile următoare:

1. **Sistem de monitorizare prezențe**
   - Profesor generează QR code pentru curs (conține: locație, timp început, expirare)
   - Studenți scanează QR code pentru confirmare prezență
   - Sistem previne scanări duplicate
   - Profesor vizualizează prezențele în timp real

2. **Sistem gestionare comenzi restaurant**
   - Client scanează QR code și selectează comanda
   - Comanda ajunge la bucătărie
   - Chelner preia comanda finalizată
   - Sistem notifică client când comanda este gata

3. **Sistem management coadă (queue management)**
   - Client scanează QR code și selectează serviciul dorit
   - Sistem generează număr în coadă virtuală
   - Afișare lungime coadă și timp estimat de așteptare
   - Notificare când vine rândul clientului

4. **Sistem gestionare acces eveniment**
   - Eveniment cu capacitate maximă
   - Gestionare intrări spectatori
   - Monitorizare ocupare în timp real
   - Blocare acces la capacitate maximă

### Exemple de evenimente

Pentru scenariul 1 (prezențe), exemple de evenimente:
- `StudentScannedQRCode` - student scanează codul
- `QRCodeExpired` - codul expiră
- `AttendanceConfirmed` - prezența este confirmată
- `DuplicateScanAttempt` - tentativă de scanare duplicată

### Sample Event Framework

#### Structura framework-ului

Repository: `lab1-25/examples/SampleEventFramework`

**Componente principale:**

1. **Event.java** (clasă abstractă generică)
```java
public abstract class Event {
    private String eventId;
    private long timestamp;
    private Object data;
    
    // Getters, constructors, toString
}
```

2. **EventListener.java** (interfață)
```java
public interface EventListener {
    boolean canHandle(Event event);
    void onEvent(Event event);
}
```

3. **EventPublisher.java**
   - Gestionează lista de listeners
   - Metodă `addListener(EventListener listener)`
   - Metodă `publishEvent(Event event)` - distribuie evenimentele către listeners interesați

#### Exemple de evenimente concrete

**StatusChangeEvent.java**
```java
public class StatusChangeEvent extends Event {
    public StatusChangeEvent(String eventId, Object data) {
        super(eventId, "STATUS_CHANGE", data);
    }
}
```

**UserActionEvent.java**
```java
public class UserActionEvent extends Event {
    private String action;
    
    public UserActionEvent(String eventId, String action, Object data) {
        super(eventId, "USER_ACTION", data);
        this.action = action;
    }
    
    public String getAction() { return action; }
}
```

#### Exemple de listeners

**LoggerListener.java** - interceptează toate evenimentele
```java
public class LoggerListener implements EventListener {
    @Override
    public boolean canHandle(Event event) {
        return true; // acceptă orice tip de event
    }
    
    @Override
    public void onEvent(Event event) {
        System.out.println("Event logged: " + event);
    }
}
```

**UserActionListener.java** - interceptează doar UserActionEvent
```java
public class UserActionListener implements EventListener {
    @Override
    public boolean canHandle(Event event) {
        return "USER_ACTION".equals(event.getEventType());
    }
    
    @Override
    public void onEvent(Event event) {
        if (event instanceof UserActionEvent) {
            UserActionEvent uae = (UserActionEvent) event;
            System.out.println("User action: " + uae.getAction());
        }
    }
}
```

#### Utilizare

**FrameworkDemo.java**
```java
public class FrameworkDemo {
    public static void main(String[] args) {
        // Inițializare
        EventPublisher publisher = new EventPublisher();
        
        // Creare listeners
        LoggerListener logger = new LoggerListener();
        UserActionListener userListener = new UserActionListener();
        
        // Înregistrare listeners
        publisher.addListener(logger);
        publisher.addListener(userListener);
        
        // Creare și publicare evenimente
        Event event1 = new UserActionEvent("1", "login", userData);
        Event event2 = new StatusChangeEvent("2", statusData);
        
        publisher.publishEvent(event1);
        publisher.publishEvent(event2);
    }
}
```

### Pattern Observer

Framework-ul implementează pattern-ul de design **Observer**:
- **Subject** = EventPublisher
- **Observers** = EventListeners
- Observers se înregistrează la Subject
- Subject notifică Observers când apar evenimente

### Multi-threading (opțional)

Package `multithreading` conține exemplu cu:
- **EventGeneratorController** - controlează mai multe thread-uri
- Thread-uri care generează evenimente în paralel
- Demonstrează publicarea concurentă de evenimente

### Cerințe exercițiu

1. **Alegeți un scenariu** din lista de mai sus
2. **Identificați minimum 3 tipuri de evenimente** specifice scenariului
3. **Creați un proiect nou** (`File` → `New Project` → `Java Application`)
   - Denumire: `lab1-events` (sau similar)
4. **Copiați clasele framework-ului** în proiectul vostru:
   - Package `event.framework` (Event, EventListener, EventPublisher, EventStore)
   - Creați propriile evenimente și listeners
5. **Implementați o aplicație demo** care:
   - Creează câteva evenimente specifice scenariului
   - Le publică prin EventPublisher
   - Listeners specifici reacționează la evenimente
6. **Testați aplicația** - rulați și verificați că evenimentele sunt procesate corect

### Structura recomandată proiect

```
lab1-events/
├── src/
│   ├── event/
│   │   └── framework/       # Framework copiat
│   │       ├── Event.java
│   │       ├── EventListener.java
│   │       ├── EventPublisher.java
│   │       └── EventStore.java
│   ├── events/              # Evenimentele voastre
│   │   ├── StudentScannedQREvent.java
│   │   ├── AttendanceConfirmedEvent.java
│   │   └── QRCodeExpiredEvent.java
│   ├── listeners/           # Listeners-ii voștri
│   │   ├── AttendanceListener.java
│   │   └── NotificationListener.java
│   └── demo/
│       └── AttendanceDemo.java  # Aplicația demo
```

### Timp estimat

**1-2 ore** pentru cineva cu cunoștințe de bază Java.

## Exercițiul 2 - Spring Boot Introduction

### Obiective

- Înțelegerea structurii unei aplicații Spring Boot
- Familiarizare cu conceptele de bază Spring IoC
- Customizare aplicație existentă

### Repository

`lab1-25/examples/SpringBootSensorDemo`

### Structura proiectului

#### 1. Adnotări importante

- **`@SpringBootApplication`** - punct de intrare în aplicație
- **`@Service`** - marchează o clasă ca service (component managed de Spring)
- **`@Value`** - injectează valori din fișiere de configurare
- **`@Autowired`** - dependency injection automată

#### 2. Application.properties

Fișier de configurare în `src/main/resources/application.properties`:
```properties
sensor.temperature.min=15.0
sensor.temperature.max=30.0
sensor.humidity.min=40.0
sensor.humidity.max=70.0
```

Valorile sunt citite în cod prin `@Value("${sensor.temperature.min}")`.

#### 3. CommandLineRunner

Aplicația implementează `CommandLineRunner` pentru a fi o aplicație console (nu web):
```java
@Override
public void run(String... args) throws Exception {
    // Cod care se execută la pornirea aplicației
}
```

### Componente aplicației

1. **SensorService** - serviciu care gestionează senzorii
2. **Sensor classes** - clase pentru diferite tipuri de senzori
3. **Application** - clasa principală cu `main()`

### Cerințe exercițiu

1. **Încărcați proiectul** în IntelliJ IDEA sau NetBeans
2. **Rulați aplicația** și observați output-ul
3. **Înțelegeți arhitectura:**
   - Care sunt componentele principale?
   - Cum funcționează dependency injection?
   - Cum sunt folosite adnotările?
4. **Customizare (Task 2 & 3):**
   - Adăugați un nou tip de senzor (ex: senzor de umiditate)
   - Modificați proprietățile senzorilor existenți
   - Adăugați noi metrici sau funcționalități

### Resurse recomandate

- **Cartea:** "Spring in Action" (disponibilă pe canalul Teams, secțiunea Books)
- **Documentație:** [docs.spring.io](https://docs.spring.io)
- **Cartea:** "Thinking in Java" by Bruce Eckel (pentru principii Java)

### Timp estimat

**1-2 ore** pentru familiarizare și customizări de bază.

## Notă importantă despre proiect

Scenariile din Exercițiul 1 vor fi extinse și folosite pentru **mini-proiectul final** al materiei. La sfârșitul semestrului, veți implementa o aplicație mai complexă bazată pe unul din aceste scenarii, integrând:
- Baze de date
- Message brokers (Kafka/RabbitMQ/ActiveMQ)
- Mini interfață grafică
- Event-driven architecture

Alegeți un scenariu care vă interesează, deoarece veți lucra la el tot semestrul!

## Timp total estimat

**3 ore** pentru finalizarea completă a ambelor exerciții.

## Livrabile

### Pentru Git workflow
- Repository inițializat și configurat
- Issues create pentru fiecare exercițiu
- Branch-uri corespunzătoare
- Commit-uri cu mesaje descriptive
- Pull requests finalizate și merged

### Pentru Exercițiul 1
- Folder `lab1/exercitiul1/` în repository
- Cod sursă al aplicației event-driven
- README.md cu:
  - Scenariul ales
  - Evenimentele identificate
  - Instrucțiuni de rulare

### Pentru Exercițiul 2
- Folder `lab1/exercitiul2/` în repository
- Proiect Spring Boot modificat
- README.md cu descrierea modificărilor

## Întrebări frecvente

**Î: Trebuie să creez un branch separat pentru fiecare exercițiu?**
R: Puteți lucra cu un singur branch per laborator sau câte un branch per exercițiu, după preferință.

**Î: Pot folosi alte IDE-uri decât IntelliJ sau NetBeans?**
R: Da, dar acestea sunt recomandate pentru suport optim.

**Î: Ce fac dacă am conflicte la merge?**
R: În cazul în care lucrați singur pe propriul repository și nu modificați direct main, nu ar trebui să aveți conflicte. Dacă apar, rezolvați-le manual editând fișierele afectate.

**Î: Trebuie să folosesc Docker pentru acest laborator?**
R: Nu pentru Laboratorul 1, dar îl veți folosi în laboratoarele viitoare pentru servicii precum Kafka.

## Resurse suplimentare

- Repository laborator: [Link din Teams]
- Canalul Teams: General → secțiunea Files → Books
- Documentație Git: https://git-scm.com/doc
- Documentație GitHub: https://docs.github.com

---

**Succes la laborator!**