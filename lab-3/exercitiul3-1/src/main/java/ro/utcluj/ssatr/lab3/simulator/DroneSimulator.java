package ro.utcluj.ssatr.lab3.simulator;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.model.TelemetryData;
import ro.utcluj.ssatr.lab3.utils.KafkaUtils;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simulează o dronă care emit telemetrie periodic.
 * TODO: Studenții vor extinde această clasă pentru a:
 * - Consuma comenzi din topic "drone-commands" și reacționa
 * - Simula misiuni complete (decolare, mers pe waypoints, aterizare)
 * - Gestiona stări mai complexe
 */
public class DroneSimulator {
    private static final Logger logger = LoggerFactory.getLogger(DroneSimulator.class);

    private final String droneId;
    private final KafkaProducer<String, String> producer;
    private final Gson gson;
    private final Random random;
    private final ScheduledExecutorService scheduler;

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
        this.gson = new Gson();
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(1);
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
     * Emit telemetrie periodic conform intervalului configurat.
     */
    public void start() {
        logger.info("Starting drone {} simulation (telemetry every {} seconds)", droneId, telemetryInterval);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateState();
                emitTelemetry();
            } catch (Exception e) {
                logger.error("Error in drone {} simulation", droneId, e);
            }
        }, 0, telemetryInterval, TimeUnit.SECONDS);
    }

    /**
     * Actualizează starea dronei (simulare mișcare, descărcare baterie, etc.)
     * TODO: Studenții pot extinde această metodă pentru simulări mai realiste.
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
        }

        // Simulare temperatură
        temperature = 20.0 + random.nextDouble() * 10.0;
        if ("FLYING".equals(status)) {
            temperature += 5.0; // Se încălzește când zboară
        }

        // TODO: Studenții pot adăuga logică pentru răspuns la comenzi
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
     * TODO: Metodă pentru simulare decolare.
     * Studenții vor implementa această metodă.
     */
    public void takeOff() {
        logger.info("Drone {} taking off", droneId);
        status = "FLYING";
        // TODO: Animație decolare, creștere altitudine
    }

    /**
     * TODO: Metodă pentru simulare aterizare.
     * Studenții vor implementa această metodă.
     */
    public void land() {
        logger.info("Drone {} landing", droneId);
        status = "LANDED";
        currentAltitude = 0.0;
        currentSpeed = 0.0;
        // TODO: Animație aterizare
    }

    /**
     * Oprește simularea dronei și închide resursele.
     */
    public void stop() {
        logger.info("Stopping drone {} simulation", droneId);
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
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
