# Real-Time IoT System Architecture

## Project Scope
The purpose of this project is to put into practice the UML Capsule concept for defining and implementing an IoT solution with real-time characteristics. You will work with a RabbitMQ message broker configured for managing priorities and temporal constraints.

## Requirements

### 1. Theoretical Study
- Study the IoT concept and its applications in domains with real-time requirements
- Study the UML Capsules concept
- Study RabbitMQ

### 2. Solution Design
Design an IoT application/solution with a layered architecture that contains:

#### Mandatory Components:

**A. Two sensors with different characteristics:**

- **Critical Sensor** - publishes data with high priority and strict deadline
  - Processing deadline: ≤ 200ms
  - Message priority: 10 (maximum)
  - Publishing frequency: configurable (e.g., 100ms - 1s)
  - Includes timestamp
  
- **Normal Sensor** - publishes data with normal priority
  - Processing deadline: ≤ 2000ms
  - Message priority: 5 (medium)
  - Publishing frequency: configurable (e.g., 1s - 5s)
  - Includes timestamp

**B. Two-level layered controller:**

- **Reactive Controller (Fast Path)**
  - Processes ONLY data from the critical sensor
  - Implements simple and deterministic decision logic
  - Generates urgent commands with high priority
  - Processing latency: < 100ms (simulated through random sleep)
  - Example logic: if value > critical_threshold → immediate STOP command

- **Analytical Controller (Slow Path)**
  - Processes data from the normal sensor
  - Can implement complex logic (statistical calculations, trends)
  - Generates normal commands with medium priority
  - Relaxed latency: 1-5 seconds (simulated through random sleep)
  - Example logic: parameter optimization, fine adjustments

**C. One actuator with priority queue:**
- Receives commands from both controllers
- Executes commands in priority order (not FIFO)
- Confirms execution with timestamp (ACK)
- Simulates differentiated actions (urgent vs normal)

**D. Performance monitor:**
- Detects missed deadlines
- Publishes alarms when performance drops below thresholds
- Displays real-time metrics (console or file)

**E. RabbitMQ Broker**

### 3. Message Structure
Each message from a sensor must contain temporal information:

```json
{
    "sensor_id": "string",
    "value": float,
    "timestamp": float,      // Unix timestamp with milliseconds
    "deadline": int,         // in milliseconds (relative)
    "priority": int,         // 1-10
    "sequence": int,         // for loss detection
    "type": "string"         // "critical" or "normal"
}
```

### 4. RabbitMQ Configuration
A mechanism shall be implemented to facilitate communication between components according to specifications.

## Deliverables

1. **UML Capsules Diagram** (.pdf)
2. **Source code**
3. **README.md file** containing:
   - Description of the chosen IoT scenario (industrial/medical/smart home, etc.)
   - Installation and execution instructions

## Appendix: Real-World Scenario Examples

### Scenario A: Industrial Monitoring
- Critical sensor: Hydraulic pressure (50ms deadline)
- Normal sensor: Vibrations (500ms deadline)
- Reactive controller: Emergency shutdown if pressure > limit
- Analytical controller: Wear detection through vibration analysis

### Scenario B: Medical IoT System
- Critical sensor: Heart rate (100ms deadline)
- Normal sensor: Body temperature (1000ms deadline)
- Reactive controller: Immediate alarm for arrhythmias
- Analytical controller: Long-term trends