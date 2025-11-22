package ro.utcluj.ssatr.lab3.simulator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.model.TelemetryData;
import ro.utcluj.ssatr.lab3.utils.KafkaUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulează o dronă care emit telemetrie periodic și consumă comenzi din Kafka.
 * Ascultă comenzi din topic "drone-commands" și reacționează.
 */
public class DroneSimulator {
    private static final Logger logger = LoggerFactory.getLogger(DroneSimulator.class);

    private final String droneId;
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;
    private final Gson gson;
    private final Random random;
    private final ScheduledExecutorService telemetryScheduler;
    private final ExecutorService commandExecutor;
    private final AtomicBoolean running;

    // Starea curentă a dronei
    private double currentLatitude;
    private double currentLongitude;
    private double currentAltitude;
    private double currentSpeed;
    private int currentHeading;
    private double batteryLevel;
    private String status;
    private double temperature;

    // Frecvența de emitere telemetrie (în secunde)
    private final int telemetryInterval;

    public DroneSimulator(String droneId, double startLat, double startLon, int telemetryInterval) {
        this.droneId = droneId;
        this.producer = new KafkaProducer<>(KafkaUtils.createProducerProperties());
        this.consumer = new KafkaConsumer<>(KafkaUtils.createConsumerProperties("drone-simulator-" + droneId));
        this.gson = new Gson();
        this.random = new Random();
        this.telemetryScheduler = Executors.newScheduledThreadPool(1);
        this.commandExecutor = Executors.newSingleThreadExecutor();
        this.running = new AtomicBoolean(false);
        this.telemetryInterval = telemetryInterval;

        // Inițializare stare
        this.currentLatitude = startLat;
        this.currentLongitude = startLon;
        this.currentAltitude = 0.0;
        this.currentSpeed = 0.0;
        this.currentHeading = 0;
        this.batteryLevel = 100.0;
        this.status = "IDLE";
        this.temperature = 20.0 + random.nextDouble() * 5.0;

        logger.info("Drone {} initialized at ({}, {})", droneId, startLat, startLon);
    }

    /**
     * Pornește simularea dronei.
     * Emit telemetrie periodic și ascultă comenzi din Kafka.
     */
    public void start() {
        logger.info("Starting drone {} simulation (telemetry every {} seconds)", droneId, telemetryInterval);
        running.set(true);

        // Pornește emiterea telemetriei
        telemetryScheduler.scheduleAtFixedRate(() -> {
            try {
                updateState();
                emitTelemetry();
            } catch (Exception e) {
                logger.error("Error in drone {} simulation", droneId, e);
            }
        }, 0, telemetryInterval, TimeUnit.SECONDS);

        // Pornește consumatorul de comenzi
        consumer.subscribe(Collections.singletonList(KafkaUtils.TOPIC_COMMANDS));
        commandExecutor.submit(this::consumeCommands);
        logger.info("Drone {} started listening for commands on topic {}", droneId, KafkaUtils.TOPIC_COMMANDS);
    }

    /**
     * Actualizează starea dronei (simulare mișcare, descărcare baterie, etc.)
     */
    private void updateState() {
        // Simulare mișcare GPS (doar dacă drona zboară)
        if ("FLYING".equals(status)) {
            currentLatitude += (random.nextDouble() - 0.5) * 0.001; // ~100m
            currentLongitude += (random.nextDouble() - 0.5) * 0.001;
            currentAltitude = 50.0 + random.nextDouble() * 100.0;
            currentSpeed = 5.0 + random.nextDouble() * 15.0;
            currentHeading = random.nextInt(360);

            // Descărcare baterie
            batteryLevel = Math.max(0, batteryLevel - (0.1 + random.nextDouble() * 0.2));

            // Simulare warning low battery
            if (batteryLevel < 20 && batteryLevel > 19) {
                logger.warn("Drone {} low battery: {}%", droneId, batteryLevel);
            }

            // Trecere automată la EMERGENCY dacă bateria este critică
            if (batteryLevel < 10) {
                status = "EMERGENCY";
                logger.error("Drone {} entering EMERGENCY mode (battery: {}%)", droneId, batteryLevel);
            }
        } else if ("LANDED".equals(status)) {
            // Când este pe sol, bateria se reîncarcă încet
            batteryLevel = Math.min(100.0, batteryLevel + 0.5);
            currentAltitude = 0.0;
            currentSpeed = 0.0;
        }

        // Simulare temperatură
        temperature = 20.0 + random.nextDouble() * 10.0;
        if ("FLYING".equals(status)) {
            temperature += 5.0; // Se încălzește când zboară
        }
    }

    /**
     * Emit telemetrie în Kafka topic.
     */
    private void emitTelemetry() {
        TelemetryData telemetry = new TelemetryData(
                droneId,
                System.currentTimeMillis(),
                currentLatitude,
                currentLongitude,
                currentAltitude,
                currentSpeed,
                currentHeading,
                batteryLevel,
                status,
                temperature,
                random.nextDouble() * 0.05 // vibration
        );

        String json = gson.toJson(telemetry);
        ProducerRecord<String, String> record = new ProducerRecord<>(
                KafkaUtils.TOPIC_TELEMETRY,
                droneId,
                json
        );

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send telemetry for drone {}", droneId, exception);
            } else {
                logger.debug("Telemetry sent for drone {} to partition {} at offset {}",
                        droneId, metadata.partition(), metadata.offset());
            }
        });
    }

    /**
     * Consumă comenzi din Kafka și reacționează.
     */
    private void consumeCommands() {
        logger.info("Command consumer started for drone {}", droneId);

        try {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        processCommand(record.value());
                    } catch (Exception e) {
                        logger.error("Error processing command for drone {}: {}", droneId, record.value(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in command consumer for drone {}", droneId, e);
        } finally {
            logger.info("Command consumer stopped for drone {}", droneId);
        }
    }

    /**
     * Procesează o comandă primită din Kafka.
     */
    private void processCommand(String commandJson) {
        try {
            JsonObject commandObj = gson.fromJson(commandJson, JsonObject.class);
            String targetDroneId = commandObj.get("droneId").getAsString();

            // Verifică dacă comanda este pentru această dronă
            if (!droneId.equals(targetDroneId)) {
                return; // Comanda este pentru altă dronă
            }

            String commandType = commandObj.get("commandType").getAsString();
            logger.info("Drone {} received command: {}", droneId, commandType);

            switch (commandType) {
                case "TAKE_OFF":
                    takeOff();
                    break;
                case "LAND":
                    land();
                    break;
                case "RETURN_HOME":
                    returnHome();
                    break;
                case "GOTO_WAYPOINT":
                    if (commandObj.has("parameters")) {
                        JsonObject params = commandObj.getAsJsonObject("parameters");
                        double lat = params.get("latitude").getAsDouble();
                        double lon = params.get("longitude").getAsDouble();
                        double alt = params.get("altitude").getAsDouble();
                        gotoWaypoint(lat, lon, alt);
                    }
                    break;
                case "EMERGENCY_LAND":
                    emergencyLand();
                    break;
                default:
                    logger.warn("Drone {} received unknown command: {}", droneId, commandType);
            }
        } catch (Exception e) {
            logger.error("Error parsing command JSON: {}", commandJson, e);
        }
    }

    /**
     * Simulare decolare.
     */
    public void takeOff() {
        if ("IDLE".equals(status) || "LANDED".equals(status)) {
            logger.info("Drone {} taking off", droneId);
            status = "FLYING";
            currentAltitude = 10.0; // Începe la 10m
            currentSpeed = 2.0;
        } else {
            logger.warn("Drone {} cannot take off - current status: {}", droneId, status);
        }
    }

    /**
     * Simulare aterizare.
     */
    public void land() {
        if ("FLYING".equals(status) || "EMERGENCY".equals(status)) {
            logger.info("Drone {} landing", droneId);
            status = "LANDED";
            currentAltitude = 0.0;
            currentSpeed = 0.0;
        } else {
            logger.warn("Drone {} cannot land - current status: {}", droneId, status);
        }
    }

    /**
     * Simulare întoarcere acasă.
     */
    public void returnHome() {
        logger.info("Drone {} returning home", droneId);
        status = "FLYING";
        // Simulare întoarcere la poziția inițială (în realitate ar fi o traiectorie)
        // Pentru simplitate, doar setăm statusul și vom ateriza automat
    }

    /**
     * Simulare mers la waypoint.
     */
    public void gotoWaypoint(double latitude, double longitude, double altitude) {
        logger.info("Drone {} going to waypoint ({}, {}, {})", droneId, latitude, longitude, altitude);
        status = "FLYING";
        // Simulare mișcare către waypoint
        currentLatitude = latitude;
        currentLongitude = longitude;
        currentAltitude = altitude;
    }

    /**
     * Simulare aterizare de urgență.
     */
    public void emergencyLand() {
        logger.warn("Drone {} emergency landing!", droneId);
        status = "EMERGENCY";
        currentAltitude = 0.0;
        currentSpeed = 0.0;
    }

    /**
     * Oprește simularea dronei și închide resursele.
     */
    public void stop() {
        logger.info("Stopping drone {} simulation", droneId);
        running.set(false);

        // Oprește telemetry scheduler
        telemetryScheduler.shutdown();
        try {
            if (!telemetryScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                telemetryScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            telemetryScheduler.shutdownNow();
        }

        // Oprește command executor
        commandExecutor.shutdown();
        try {
            if (!commandExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                commandExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            commandExecutor.shutdownNow();
        }

        // Închide Kafka resources
        consumer.close();
        producer.close();
        logger.info("Drone {} simulation stopped", droneId);
    }

    // Getters pentru test și debugging
    public String getDroneId() {
        return droneId;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public String getStatus() {
        return status;
    }
}
