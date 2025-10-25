# Sistem de Livrare cu Distribuție și Confirmare Comenzi

## Introducere

Scopul acestui exercitiu este de a implementa un sistem de livrare (delivery) în care comenzile primite de la clienți sunt distribuite automat către toți curierii disponibili folosind mecanismul **fanout** din RabbitMQ. Primul curier care acceptă comanda o preia, iar sistemul marchează comanda ca fiind procesată și notifică ceilalți curieri să ignore acea comandă.

## Descrierea Sistemului

Sistemul este format din următoarele componente:

- **Aplicația Client** - trimite comenzi noi
- **Manager Comenzi** (Order Manager) - primește comenzi de la clienți și le distribuie curierilor
- **Aplicații Curier** (multiple instanțe) - primesc comenzi și pot accepta sau refuza comenzi
- **Broker RabbitMQ** - facilitează comunicarea bidirecțională folosind mecanismul fanout

## Cerințe

### 1. Studiu Teoretic

- Să se studieze conceptul de **fanout exchange** în RabbitMQ
- Să se studieze mecanismele de **acknowledge** și **competing consumers**
- Să se înțeleagă problema **race condition** în contextul acceptării comenzilor
- Să se studieze conceptul de **exclusive queues** și **temporary queues**

### 2. Arhitectura Soluției

#### Fluxul de Date

**A. Flux comenzi noi (Client → Manager → Curieri):**

```
Client → Manager Comenzi → Fanout Exchange "new_orders"
                               ↓
                    ┌──────────┼──────────┐
                    ↓          ↓          ↓
              Queue Curier1  Queue Curier2  Queue Curier3
                    ↓          ↓          ↓
              Aplicație      Aplicație    Aplicație
              Curier 1       Curier 2     Curier 3
```

**Flux confirmări (Curieri → Manager):**

```
Aplicație Curier 1 → Queue "order_confirmations" → Manager Comenzi
Aplicație Curier 2 → Queue "order_confirmations" → Manager Comenzi
Aplicație Curier 3 → Queue "order_confirmations" → Manager Comenzi
```

**Flux notificări anulare (Manager → Curieri):**

```
Manager Comenzi → Fanout Exchange "order_cancellations"
                        ↓
             ┌──────────┼──────────┐
             ↓          ↓          ↓
       Queue Curier1  Queue Curier2  Queue Curier3
```

#### Componente Obligatorii

**A. Aplicația Client (Order Sender)**

- Simulează clienți care plasează comenzi
- Trimite comenzi către Manager Comenzi prin RabbitMQ
- Include detalii: adresă livrare, produse, client_id, timestamp

**B. Manager Comenzi (Order Manager)**

- Primește comenzi noi de la clienți
- Distribuie comenzile către TOȚI curierii folosind **fanout exchange**
- Primește confirmări (ACK) de la curieri
- Marchează comanda ca PROCESATĂ când primul curier o acceptă
- Notifică ceilalți curieri să anuleze/ignore comanda acceptată
- Ține evidența comenzilor: în așteptare, procesate, refuzate

**C. Aplicația Curier (Courier App)** - multiple instanțe (minim 3)

- Fiecare curier are o **coadă proprie unică** pentru comenzi noi
- Primește comenzi noi prin fanout
- Afișează comenzile disponibile
- Poate **ACCEPTA** sau **REFUZA** o comandă (simulat sau input utilizator)
- Trimite confirmări (ACK/NACK) către Manager
- Primește notificări de anulare pentru comenzi deja acceptate de alții
- Ignoră comenzile care au fost deja preluate

**D. Broker RabbitMQ**

- Gestionează exchange-uri și cozi
- Asigură distribuția fanout
- Persistența mesajelor (opțional)

### 3. Structura Mesajelor

#### A. Mesaj Comandă Nouă (Client → Manager → Curieri)

```json
{
    "order_id": "string (UUID)",
    "client_id": "string",
    "timestamp": "float (Unix timestamp)",
    "delivery_address": {
        "street": "string",
        "city": "string",
        "postal_code": "string"
    },
    "items": [
        {
            "product": "string",
            "quantity": "int",
            "price": "float"
        }
    ],
    "total_amount": "float",
    "status": "PENDING"
}
```

#### B. Mesaj Confirmare (Curier → Manager)

```json
{
    "order_id": "string (UUID)",
    "courier_id": "string",
    "action": "ACCEPT | REJECT",
    "timestamp": "float",
    "estimated_delivery_time": "int (minutes, doar pentru ACCEPT)"
}
```

#### C. Mesaj Anulare/Notificare (Manager → Curieri)

```json
{
    "order_id": "string (UUID)",
    "action": "CANCEL",
    "accepted_by_courier_id": "string",
    "timestamp": "float",
    "reason": "Already accepted by another courier"
}
```

### 4. Configurația RabbitMQ

În continuare este prezentată o propunere pentru configurația RabbitMQ.

#### A. Exchange-uri Necesare:

**1. Exchange pentru comenzi noi:**

```
Name: "new_orders"
Type: fanout
Durable: true
```

**2. Exchange pentru anulări:**

```
Name: "order_cancellations"
Type: fanout
Durable: true
```

#### B. Queue-uri Necesare:

**1. Queue-uri pentru fiecare curier (comenzi noi):**

```
Name: "courier_<courier_id>_orders"
Type: standard queue
Binding: la exchange "new_orders"
Durable: false (pot fi temporary queues)
Auto-delete: true (când curierul se deconectează)
```

**2. Queue pentru confirmări (de la curieri la manager):**

```
Name: "order_confirmations"
Type: standard queue
Durable: true
Consumers: doar Order Manager
```

**3. Queue-uri pentru anulări (pentru fiecare curier):**

```
Name: "courier_<courier_id>_cancellations"
Type: standard queue
Binding: la exchange "order_cancellations"
Auto-delete: true
```

## Livrabile

1. **Diagrama UML Capsules** (.pdf)
2. **Codul sursă**
3. **Fișier README.md** care să conțină:
   - Descrierea scenariului ales (delivery, food delivery, package delivery etc.)
   - Instrucțiuni de instalare și rulare
   - Explicarea mecanismului de fanout și gestionarea race conditions

## Referințe

- **RabbitMQ Fanout Exchange**: https://www.rabbitmq.com/tutorials/tutorial-three-python.html
- **RabbitMQ CloudAMQP Fanout**: https://www.cloudamqp.com/blog/rabbitmq-fanout-exchange-explained.html
- **Competing Consumers Pattern**: https://www.enterpriseintegrationpatterns.com/patterns/messaging/CompetingConsumers.html

## Anexe - Notă

Proiectul simulează un sistem real de delivery (Uber Eats, Glovo, etc.) și vă va ajuta să înțelegeți:

- Provocările distribuției de mesaje în sisteme concurente
- Gestionarea race conditions
- Comunicarea bidirecțională prin message brokers
- Patterns de messaging (fanout, competing consumers)

