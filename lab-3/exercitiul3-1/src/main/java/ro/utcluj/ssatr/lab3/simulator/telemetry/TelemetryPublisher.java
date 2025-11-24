package ro.utcluj.ssatr.lab3.simulator.telemetry;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.model.TelemetryData;
import ro.utcluj.ssatr.lab3.simulator.state.DroneState;
import ro.utcluj.ssatr.lab3.utils.KafkaUtils;

import java.util.Random;

/**
 * Publishes telemetry data to Kafka.
 * Handles Kafka producer lifecycle and telemetry serialization.
 */
public class TelemetryPublisher {
    private static final Logger logger = LoggerFactory.getLogger(TelemetryPublisher.class);

    private final KafkaProducer<String, String> producer;
    private final Gson gson;
    private final Random random;

    public TelemetryPublisher() {
        this.producer = new KafkaProducer<>(KafkaUtils.createProducerProperties());
        this.gson = new Gson();
        this.random = new Random();
        logger.debug("TelemetryPublisher initialized");
    }

    /**
     * Publishes current drone state as telemetry to Kafka.
     */
    public void publish(DroneState state) {
        try {
            TelemetryData telemetry = createTelemetryFromState(state);
            String json = gson.toJson(telemetry);

            ProducerRecord<String, String> record = new ProducerRecord<>(
                    KafkaUtils.TOPIC_TELEMETRY,
                    state.getDroneId(),
                    json
            );

            producer.send(record, this::handleSendResult);

        } catch (Exception e) {
            logger.error("Failed to publish telemetry for drone {}", state.getDroneId(), e);
        }
    }

    /**
     * Creates TelemetryData object from DroneState.
     */
    private TelemetryData createTelemetryFromState(DroneState state) {
        return new TelemetryData(
                state.getDroneId(),
                System.currentTimeMillis(),
                state.getLatitude(),
                state.getLongitude(),
                state.getAltitude(),
                state.getSpeed(),
                state.getHeading(),
                state.getBatteryLevel(),
                state.getStatus(),
                state.getTemperature(),
                random.nextDouble() * 0.05 // vibration (0.0 - 0.05)
        );
    }

    /**
     * Handles Kafka send callback - logs success or failure.
     */
    private void handleSendResult(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            logger.error("Failed to send telemetry to Kafka", exception);
        } else {
            logger.debug("Telemetry sent to partition {} at offset {}",
                    metadata.partition(), metadata.offset());
        }
    }

    /**
     * Closes the Kafka producer and releases resources.
     */
    public void close() {
        if (producer != null) {
            logger.debug("Closing TelemetryPublisher");
            producer.close();
        }
    }
}
