# Sample Spring Boot REST API with RabbitMQ

A didactic Spring Boot application demonstrating REST API endpoints and RabbitMQ messaging integration for the SSATR laboratory.

## Overview

This application demonstrates RabbitMQ with **two separate queues**:
- **API Queue** (`ssatr.api.messages`) - Used by REST endpoints to publish/retrieve messages
- **Processing Queue** (`ssatr.processing.messages`) - Monitored by background listener for automatic processing

Key features:
- **REST API endpoint** to publish messages to the API queue
- **REST API endpoint** to retrieve all messages from the API queue
- **Background listener** that monitors the PROCESSING queue (separate from API queue)

## Architecture

### Components

1. **MessageController** - REST endpoints (`/api/messages`)
   - `POST /api/messages` - Publish a message to the **API queue**
   - `GET /api/messages` - Retrieve all messages from the **API queue**
   - `GET /api/messages/health` - Health check endpoint

2. **MessageService** - Business logic for publishing and consuming messages from the **API queue**

3. **MessageListener** - Background component that listens to the **PROCESSING queue** (different from API queue)

4. **RabbitMQConfig** - Configuration for both RabbitMQ queues and JSON message converter

5. **Message** - Simple POJO representing a message (id, content, timestamp)

### Queue Separation

The application uses **two independent queues**:
- **API Queue**: Controlled by REST endpoints - messages stay here until consumed via GET endpoint
- **Processing Queue**: Monitored by MessageListener - messages are automatically processed when published

This separation demonstrates how different queues can serve different purposes in a microservices architecture.

## Prerequisites

### Install and Run RabbitMQ Locally

#### Using Docker (Recommended)
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

#### Using Package Manager
- **Windows**: Download from https://www.rabbitmq.com/download.html
- **macOS**: `brew install rabbitmq`
- **Linux**: `sudo apt-get install rabbitmq-server` (Ubuntu/Debian)

RabbitMQ Management UI will be available at: http://localhost:15672
- Username: `guest`
- Password: `guest`

### Verify RabbitMQ is Running
```bash
# Check if RabbitMQ is listening on port 5672
netstat -an | grep 5672

# Or access the management UI
# http://localhost:15672
```

## Build and Run

### Compile the Project
```bash
mvn clean compile
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

### Build JAR Package
```bash
mvn package
java -jar target/sample-spring-restapi-rq-1.0-SNAPSHOT.jar
```

## Usage Examples

### 1. Check Application Health
```bash
curl http://localhost:8080/api/messages/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "RabbitMQ Message Service"
}
```

### 2. Publish a Message to the Queue
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content": "Hello, RabbitMQ!"}'
```

**Response:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "content": "Hello, RabbitMQ!",
  "timestamp": "2025-10-25T14:30:45.123"
}
```

**What happens:**
- Message is sent to the **API queue** (`ssatr.api.messages`)
- Message is stored in the API queue
- **Note:** The `MessageListener` will NOT process this message because it monitors the **processing queue**, not the API queue

### 3. Publish Multiple Messages
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content": "First message"}'

curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content": "Second message"}'

curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content": "Third message"}'
```

### 4. Retrieve All Messages from the Queue
```bash
curl http://localhost:8080/api/messages
```

**Response:**
```json
[
  {
    "id": "abc123",
    "content": "First message",
    "timestamp": "2025-10-25T14:30:45.123"
  },
  {
    "id": "def456",
    "content": "Second message",
    "timestamp": "2025-10-25T14:30:46.456"
  }
]
```

**Note:** This endpoint **consumes** messages from the API queue (removes them). After retrieval, the messages are no longer in the API queue.

### 5. Publish Messages to the Processing Queue (for listener processing)

To see the `MessageListener` in action, you need to publish messages directly to the **processing queue**:

```bash
# Using RabbitMQ Management UI
# Go to http://localhost:15672
# Navigate to Queues -> ssatr.processing.messages -> Publish message
# Payload: {"id":"test-1","content":"Process this!","timestamp":"2025-10-25T14:30:45"}
```

Or use a simple script to publish to the processing queue. You can add this helper endpoint to the controller (see "Next Steps" section).

**What happens:**
- Message is sent to the **processing queue** (`ssatr.processing.messages`)
- The `MessageListener` automatically receives and processes it
- Check the application console to see the listener logs

### 6. Using PowerShell (Windows)
```powershell
# Publish a message to API queue
Invoke-RestMethod -Uri http://localhost:8080/api/messages `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"content": "Hello from PowerShell!"}'

# Retrieve messages from API queue
Invoke-RestMethod -Uri http://localhost:8080/api/messages
```

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Server port
server.port=8080

# RabbitMQ connection
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Queue names (two separate queues)
app.rabbitmq.queue.api=ssatr.api.messages
app.rabbitmq.queue.processing=ssatr.processing.messages
```

## Understanding the Flow

### Message Publishing Flow (REST API)
1. Client sends POST request to `/api/messages`
2. `MessageController` receives the request
3. `MessageService.publishMessage()` sends message to **API queue** (`ssatr.api.messages`)
4. API returns the message to the client
5. Message remains in the API queue until consumed via GET endpoint
6. **Note:** `MessageListener` does NOT process this message (it monitors the processing queue)

### Message Retrieval Flow (REST API)
1. Client sends GET request to `/api/messages`
2. `MessageController` receives the request
3. `MessageService.readAllMessages()` retrieves all messages from the **API queue**
4. Messages are **removed from the API queue** and returned to the client

### Background Processing Flow
1. Message is published to the **processing queue** (`ssatr.processing.messages`)
   - Can be done via RabbitMQ Management UI
   - Or by adding a helper endpoint (see Next Steps)
2. `MessageListener.processMessage()` is triggered automatically
3. Message is processed in the background
4. Processing logs appear in the application console
5. Message is removed from the processing queue after processing

### Key Insight: Two Independent Queues
- **API Queue** = Controlled by REST endpoints (manual consumption via GET)
- **Processing Queue** = Monitored by listener (automatic processing)
- Messages in one queue do NOT affect the other queue

## Learning Points

### REST API Concepts
- `@RestController` - Marks class as REST endpoint handler
- `@RequestMapping` - Maps URL paths to controller methods
- `@PostMapping` / `@GetMapping` - HTTP method mappings
- `@RequestBody` - Binds HTTP request body to Java object
- `ResponseEntity` - Represents HTTP response with status codes

### RabbitMQ Concepts
- **Queue** - Message storage (FIFO buffer)
- **Multiple Queues** - Separate queues for different purposes (API vs Processing)
- **Producer** - Publishes messages (our REST endpoint)
- **Consumer** - Receives messages (our listener and GET endpoint)
- **Message Converter** - Serializes/deserializes JSON messages
- `@RabbitListener` - Automatic message consumption from specific queue
- **Queue Independence** - Each queue operates independently

### Spring Boot Features
- Dependency injection with constructor autowiring
- Configuration properties with `@Value`
- Auto-configuration for RabbitMQ
- Component scanning (`@Service`, `@Component`, `@Configuration`)

## Troubleshooting

### Connection Refused to RabbitMQ
- Ensure RabbitMQ is running: `docker ps` or check service status
- Verify port 5672 is accessible
- Check credentials in `application.properties`

### Messages Not Being Processed by Listener
- **Most common issue:** Messages published to API queue, not processing queue
- Listener only monitors `ssatr.processing.messages` queue
- Publish messages to the processing queue via RabbitMQ Management UI
- Check application logs for errors
- Verify queue names match in configuration
- Ensure `MessageListener` is being instantiated (check logs on startup)

### Port 8080 Already in Use
- Change port in `application.properties`: `server.port=8081`
- Or kill process using port 8080

## Testing

Run unit tests:
```bash
mvn test
```

## Next Steps for Learning

1. **Add endpoint to publish to processing queue** - Create `POST /api/messages/process` endpoint
   ```java
   @PostMapping("/process")
   public ResponseEntity<Message> publishToProcessingQueue(@RequestBody Map<String, String> request) {
       String content = request.get("content");
       Message message = new Message(UUID.randomUUID().toString(), content);
       rabbitTemplate.convertAndSend(processingQueueName, message);
       return ResponseEntity.status(HttpStatus.CREATED).body(message);
   }
   ```

2. **Add message routing** - Automatically route API queue messages to processing queue based on criteria

3. **Add message persistence** - Store messages in a database

4. **Add message filtering** - Retrieve messages by criteria

5. **Add dead letter queue** - Handle failed message processing

6. **Add message priorities** - Process urgent messages first

7. **Add authentication** - Secure the REST endpoints

8. **Add exchanges** - Use topic/fanout exchanges for advanced routing

## Additional Resources

- [Spring AMQP Documentation](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ Tutorial](https://www.rabbitmq.com/getstarted.html)
- [Spring Boot REST Documentation](https://spring.io/guides/gs/rest-service/)
