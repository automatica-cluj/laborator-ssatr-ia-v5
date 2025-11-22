package ro.utcluj.ssatr.lab3.processor;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.model.TelemetryData;
import ro.utcluj.ssatr.lab3.utils.DatabaseUtils;
import ro.utcluj.ssatr.lab3.utils.KafkaUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;

/**
 * Procesează telemetria primită din Kafka și o persistă în PostgreSQL.
 * TODO: Studenții vor extinde pentru:
 * - Detectare anomalii (low battery, high temperature)
 * - Publicare evenimente în topic "drone-events"
 * - Batch processing pentru performanță
 * - Gestionare avansată erori și retry logic
 */
public class TelemetryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TelemetryProcessor.class);

    private final KafkaConsumer<String, String> consumer;
    private final Gson gson;
    private volatile boolean running = true;

    public TelemetryProcessor() {
        this.consumer = new KafkaConsumer<>(
                KafkaUtils.createConsumerProperties("telemetry-processor-group")
        );
        this.gson = new Gson();

        // Subscribe to telemetry topic
        consumer.subscribe(Collections.singletonList(KafkaUtils.TOPIC_TELEMETRY));

        logger.info("TelemetryProcessor initialized and subscribed to topic: {}",
                KafkaUtils.TOPIC_TELEMETRY);
    }

    /**
     * Pornește procesarea telemetriei.
     */
    public void start() {
        logger.info("Starting telemetry processing loop");

        while (running) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                if (!records.isEmpty()) {
                    logger.debug("Received {} telemetry records", records.count());

                    for (ConsumerRecord<String, String> record : records) {
                        processTelemetry(record.value());
                    }

                    // Manual commit după procesare
                    consumer.commitSync();
                    logger.debug("Committed offsets for {} records", records.count());
                }
            } catch (Exception e) {
                logger.error("Error processing telemetry", e);
                // TODO: Implementați retry logic sau dead letter queue
            }
        }

        logger.info("Telemetry processing loop stopped");
    }

    /**
     * Procesează un mesaj de telemetrie individual.
     *
     * @param jsonData JSON string cu telemetria
     */
    private void processTelemetry(String jsonData) {
        try {
            // Parse JSON
            TelemetryData telemetry = gson.fromJson(jsonData, TelemetryData.class);

            // Validare (TODO: Studenții pot extinde validările)
            if (telemetry.getDroneId() == null || telemetry.getDroneId().isEmpty()) {
                logger.warn("Received telemetry with null or empty droneId, skipping");
                return;
            }

            logger.debug("Processing telemetry for drone: {}", telemetry.getDroneId());

            // Detectare anomalii (TODO: Extindere)
            checkForAnomalies(telemetry);

            // Persistare în database
            persistToDatabase(telemetry);

            // TODO: Actualizare status dronă în tabela drones
            updateDroneStatus(telemetry);

        } catch (Exception e) {
            logger.error("Failed to process telemetry: {}", jsonData, e);
        }
    }

    /**
     * Verifică anomalii în telemetrie.
     * TODO: Studenții vor extinde pentru detecție mai complexă și publicare evenimente.
     */
    private void checkForAnomalies(TelemetryData telemetry) {
        // Low battery warning
        if (telemetry.getBatteryLevel() < 20.0) {
            logger.warn("Low battery detected for drone {}: {}%",
                    telemetry.getDroneId(), telemetry.getBatteryLevel());
            // TODO: Publicați eveniment în topic "drone-events"
        }

        // High temperature warning
        if (telemetry.getTemperature() > 45.0) {
            logger.warn("High temperature detected for drone {}: {}°C",
                    telemetry.getDroneId(), telemetry.getTemperature());
            // TODO: Publicați eveniment în topic "drone-events"
        }

        // TODO: Adăugați alte verificări (altitude limits, speed limits, etc.)
    }

    /**
     * Persistă telemetria în baza de date PostgreSQL.
     */
    private void persistToDatabase(TelemetryData telemetry) {
        String sql = "INSERT INTO telemetry_logs " +
                "(drone_id, timestamp, latitude, longitude, altitude, speed, heading, " +
                "battery_level, temperature, vibration, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, telemetry.getDroneId());
            pstmt.setLong(2, telemetry.getTimestamp());
            pstmt.setDouble(3, telemetry.getLatitude());
            pstmt.setDouble(4, telemetry.getLongitude());
            pstmt.setDouble(5, telemetry.getAltitude());
            pstmt.setDouble(6, telemetry.getSpeed());
            pstmt.setInt(7, telemetry.getHeading());
            pstmt.setDouble(8, telemetry.getBatteryLevel());
            pstmt.setDouble(9, telemetry.getTemperature());
            pstmt.setDouble(10, telemetry.getVibration());
            pstmt.setString(11, telemetry.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            logger.debug("Telemetry persisted for drone {}, rows affected: {}",
                    telemetry.getDroneId(), rowsAffected);

        } catch (SQLException e) {
            logger.error("Failed to persist telemetry for drone {}", telemetry.getDroneId(), e);
            // TODO: Implementați retry logic sau salvare într-o coadă pentru reprocessare
        }
    }

    /**
     * Actualizează status-ul dronei în tabela drones.
     * TODO: Studenții vor implementa această metodă.
     */
    private void updateDroneStatus(TelemetryData telemetry) {
        String sql = "UPDATE drones SET " +
                "battery_level = ?, " +
                "status = ?, " +
                "last_seen = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, telemetry.getBatteryLevel());
            pstmt.setString(2, telemetry.getStatus());
            pstmt.setLong(3, telemetry.getTimestamp());
            pstmt.setString(4, telemetry.getDroneId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to update drone status for {}", telemetry.getDroneId(), e);
        }
    }

    /**
     * Oprește procesarea telemetriei.
     */
    public void stop() {
        logger.info("Stopping TelemetryProcessor");
        running = false;
        consumer.wakeup();
    }

    /**
     * Închide consumer-ul Kafka.
     */
    public void close() {
        consumer.close();
        logger.info("TelemetryProcessor closed");
    }

    public static void main(String[] args) {
        logger.info("Starting TelemetryProcessor Application");

        TelemetryProcessor processor = new TelemetryProcessor();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processor.stop();
            processor.close();
            DatabaseUtils.closeDataSource();
        }));

        // Start processing
        processor.start();
    }
}
