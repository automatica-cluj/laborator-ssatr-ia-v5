# Laborator 2 - Ghid de lucru

## Notă importantă despre abordare

La fel ca și în laboratorul precedent, deși scenariile descrise în exerciții pot părea complexe, **nu se dorește implementarea unor soluții "production ready"**. Focusul acestui laborator este pe **înțelegerea mecanismelor de implementare a aplicațiilor distribuite** folosind în acest caz **RabbitMQ** ca broker de mesaje.

Aspecte pe care să vă concentrați:
- Înțelegerea conceptelor de **exchange**, **queue** și **routing** în RabbitMQ
- Comunicarea **asincronă** între componente distribuite
- Gestionarea **priorităților** și **pattern-urilor de messaging** (fanout, direct, topic)
- Simularea scenariilor și validarea fluxurilor de mesaje

Nu este necesar să implementați:
- Validări exhaustive sau gestionare complexă a erorilor
- Interfețe grafice elaborate
- Mecanisme de securitate sau autentificare
- Persistență completă a datelor
- Optimizări de performanță

Scopul este să experimentați și să înțelegeți cum funcționează messaging-ul distribuit în practică.

## Resurse și Pregătire

### Instalare RabbitMQ

Pentru acest laborator este necesară instalarea brokerului de mesaje **RabbitMQ**, fie nativ, fie ca și container Docker.

#### Opțiunea 1: Instalare nativă

Consultați documentația oficială pentru instalarea RabbitMQ pe sistemul de operare folosit:
- https://www.rabbitmq.com/docs/download

#### Opțiunea 2: Instalare Docker (recomandat)

```bash
# Pornire RabbitMQ cu management plugin
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Verificare status
docker ps

# Acces interfață web: http://localhost:15672
# User: guest
# Password: guest
```

### Exemple de referință

Pentru a vă ajuta în rezolvarea exercițiilor, sunt disponibile două exemple care pot fi folosite ca ghid:

1. **Exemplu local** - în repository-ul curent:
   - `examples/sample-spring-restapi-rq` - exemplu Spring Boot cu RabbitMQ

2. **Exemplu din repository-ul cursului**:
   - https://github.com/automatica-cluj/curs-ssatr-ia-v5/tree/main/exemple_curs/demo-rabbitmq

Aceste exemple demonstrează:
- Configurarea conexiunii la RabbitMQ
- Publicarea și consumarea mesajelor
- Utilizarea exchange-uri și queue-uri
- Integrarea cu Spring Boot

## Workflow Git - Pași generali

Acest workflow este similar cu cel din Laboratorul 1. Pentru detalii complete despre lucrul cu Git, consultați [Ghidul Laborator 1](../lab-1/readme.md).

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
   - **Title:** De exemplu: "Laborator 2 - Exercițiul 1"
   - **Description:** Descriere scurtă a cerințelor
   - **Labels:** (opțional) "enhancement"
4. Click pe `Create issue`

### 3. Creare Branch din interfața web

1. Din Issue, click pe `Create a branch`
2. Denumire sugerată: `lab2-<descriere-scurta>` (ex: `lab2-iot-real-time`)
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

Lucrați la rezolvarea exercițiului în folderul `lab-2/`.

### 6. Commit și Push

```bash
# Adăugați toate fișierele modificate
git add .

# Creați un commit cu un mesaj descriptiv
git commit -m "Rezolvare parțială/completă laborator 2 - exercițiul X"

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

## Exercițiile Laboratorului 2

### Exercițiul 1 - Sistem IoT cu Arhitectură de Timp Real

Implementare sistem IoT cu caracteristici de timp real folosind RabbitMQ pentru gestionarea priorităților și constrângerilor temporale.

**Detalii complete:** [exercitiul2-1.md](exercitiul2-1.md)

**Componente principale:**
- Senzori critici și normali cu priorități diferite
- Controleri stratificați (reactiv și analitic)
- Actuator cu coadă de prioritate
- Monitor de performanță
- Broker RabbitMQ

### Exercițiul 2 - Sistem de Livrare cu Distribuție și Confirmare Comenzi

Implementare sistem de livrare în care comenzile sunt distribuite automat către toți curierii disponibili folosind mecanismul fanout din RabbitMQ.

**Detalii complete:** [exercitiul2-2.md](exercitiul2-2.md)

**Componente principale:**
- Aplicație client pentru trimitere comenzi
- Manager comenzi cu distribuție fanout
- Multiple aplicații curier
- Mecanisme de confirmare și anulare
- Broker RabbitMQ

## Timp total estimat

**4-6 ore** pentru finalizarea completă a ambelor exerciții.

## Întrebări frecvente

**Î: Trebuie să creez un branch separat pentru fiecare exercițiu?**
R: Puteți lucra cu un singur branch per laborator sau câte un branch per exercițiu, după preferință.

**Î: Este obligatoriu să folosesc Docker pentru RabbitMQ?**
R: Nu este obligatoriu, dar este recomandat pentru o instalare și configurare mai simplă.

**Î: Pot folosi alte limbaje de programare decât Java?**
R: Da, RabbitMQ suportă multiple limbaje (Python, JavaScript, etc.), dar exemplele și suportul cursului sunt optimizate pentru Java/Spring Boot.

**Î: Cum verific dacă RabbitMQ funcționează corect?**
R: Accesați interfața de management la http://localhost:15672 (user: guest, password: guest).

## Resurse suplimentare

- **RabbitMQ Documentation:** https://www.rabbitmq.com/docs
- **RabbitMQ Tutorials:** https://www.rabbitmq.com/tutorials
- **Spring AMQP Documentation:** https://docs.spring.io/spring-amqp/reference/
- **Exemple curs:** https://github.com/automatica-cluj/curs-ssatr-ia-v5
- **Cartea:** "Spring in Action" (disponibilă pe canalul Teams, secțiunea Books)

