# Sistem IoT cu Arhitectură de Timp Real

## Scop proiect

Scopul acestui proiect este de a pune în practică conceptul UML Capsule pentru definirea și implementarea unei soluții IoT cu caracteristici de timp real. Veți lucra cu un broker de mesaje de tip RabbitMQ configurat pentru gestionarea priorităților și constrângerilor temporale.

## Cerințe

### 1. Studiu teoretic

- Să se studieze conceptul IoT și aplicațiile sale în domenii cu cerințe de timp real
- Să se studieze conceptul UML Capsules
- Să se studieze  RabbitMQ

### 2. Proiectarea soluției

Să se conceapă o aplicație/soluție IoT cu arhitectură stratificată care să conțină:

#### Componente obligatorii:

**A. Doi senzori cu caracteristici diferite:**

- **Senzor Critic** - publică date cu prioritate înaltă și deadline strict
  - Deadline de procesare: ≤ 200ms
  - Prioritate mesaje: 10 (maximă)
  - Frecvență publicare: configurabilă (ex: 100ms - 1s)
  - Include timestamp
- **Senzor Normal** - publică date cu prioritate normală
  - Deadline de procesare: ≤ 2000ms
  - Prioritate mesaje: 5 (medie)
  - Frecvență publicare: configurabilă (ex: 1s - 5s)
  - Include timestamp 

**B. Controler stratificat pe două niveluri:**

- **Controler Reactiv (Fast Path)**
  - Procesează DOAR datele de la senzorul critic
  - Implementează logică de decizie simplă și deterministă
  - Generează comenzi urgente cu prioritate înaltă
  - Latență de procesare: < 100ms (simulata printr-un random sleep)
  - Exemplu logică: dacă valoare > prag_critic → comandă STOP imediată
- **Controler Analitic (Slow Path)**
  - Procesează datele de la senzorul normal
  - Poate implementa logică complexă (calcule statistice, trend-uri)
  - Generează comenzi normale cu prioritate medie
  - Latență relaxată: 1-5 secunde (simulata printr-un random sleep)
  - Exemplu logică: optimizare parametri, ajustări fine

**C. Un actuator cu coadă de prioritate:**

- Primește comenzi de la ambii controleri
- Execută comenzile în ordinea priorității (nu FIFO)
- Confirmă execuția cu timestamp (ACK)
- Simulează acțiuni diferențiate (urgente vs normale)

**D. Monitor de performanță:**

- Detectează deadline-uri ratate (missed deadlines)
- Publică alarme când performanțele scad sub praguri
- Afișează metrici în timp real (consolă sau fișier)

**F. Broker RabbitMQ**

### 3. Structura mesajelor

Fiecare mesaj de la senzor trebuie să conțină informații temporale:

```json
{
    "sensor_id": "string",
    "value": float,
    "timestamp": float,      // Unix timestamp cu milisecunde
    "deadline": int,         // în milisecunde (realtiv)
    "priority": int,         // 1-10
    "sequence": int,        // pentru detectare pierderi
    "type": "string"        // "critical" sau "normal"
}
```

### 4. Configurația RabbitMQ

Se va implementa mecanism care sa faciliteze comunicarea intre componente conform specificatiilor. 

## Livrabile

1. **Diagrama UML Capsules** (.pdf) 
2. **Codul sursă** 
3. **Fișier README.md** care să conțină:
   - Descrierea scenariului IoT ales (industrial/medical/smart home etc.)
   - Instrucțiuni de instalare și rulare


## Anexa Exemple Scenarii Reale

### Scenariul A: Monitorizare industrială

- Senzor critic: Presiune hidraulică (deadline 50ms)

- Senzor normal: Vibrații (deadline 500ms)
- Controler reactiv: Oprire urgentă dacă presiune > limită
- Controler analitic: Detectare uzură prin analiza vibrațiilor

### Scenariul B: Sistem medical IoT

- Senzor critic: Ritm cardiac (deadline 100ms)
- Senzor normal: Temperatură corp (deadline 1000ms)
- Controler reactiv: Alarmă imediată la aritmii
- Controler analitic: Trend-uri pe termen lung
