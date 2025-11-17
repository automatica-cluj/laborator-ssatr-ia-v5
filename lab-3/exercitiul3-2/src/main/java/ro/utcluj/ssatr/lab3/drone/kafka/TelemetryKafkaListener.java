package ro.utcluj.ssatr.lab3.drone.kafka;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.model.TelemetrySnapshot;
import ro.utcluj.ssatr.lab3.drone.repository.DroneRepository;
import ro.utcluj.ssatr.lab3.drone.service.TelemetryService;

/**
 * Kafka listener pentru consumarea telemetriei din topic "drone-telemetry".
 * Folosește @KafkaListener annotation pentru a asculta automat mesajele.
 */
@Component
public class TelemetryKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryKafkaListener.class);

    @Autowired
    private TelemetryService telemetryService;

    @Autowired
    private DroneRepository droneRepository;

    private final Gson gson = new Gson();

    /**
     * Metoda este apelată automat de Spring Kafka pentru fiecare mesaj din topic.
     * @param message JSON string cu datele de telemetrie
     */
    @KafkaListener(topics = "drone-telemetry", groupId = "drone-dashboard-group")
    public void listenTelemetry(String message) {
        try {
            logger.debug("Received telemetry message: {}", message);

            // Parse JSON în TelemetryDTO
            TelemetryDTO telemetryDTO = gson.fromJson(message, TelemetryDTO.class);

            // Verificare dronă există în DB
            Drone drone = droneRepository.findById(telemetryDTO.getDroneId()).orElse(null);
            if (drone == null) {
                logger.warn("Received telemetry for unknown drone: {}", telemetryDTO.getDroneId());
                return;
            }

            // Creare TelemetrySnapshot entity
            TelemetrySnapshot snapshot = new TelemetrySnapshot();
            snapshot.setDrone(drone);
            snapshot.setTimestamp(telemetryDTO.getTimestamp());
            snapshot.setLatitude(telemetryDTO.getLatitude());
            snapshot.setLongitude(telemetryDTO.getLongitude());
            snapshot.setAltitude(telemetryDTO.getAltitude());
            snapshot.setSpeed(telemetryDTO.getSpeed());
            snapshot.setHeading(telemetryDTO.getHeading());
            snapshot.setBatteryLevel(telemetryDTO.getBatteryLevel());
            snapshot.setTemperature(telemetryDTO.getTemperature());
            snapshot.setVibration(telemetryDTO.getVibration());
            snapshot.setStatus(drone.getStatus());

            // Salvare în DB
            telemetryService.saveTelemetry(snapshot);

            // Actualizare status dronă
            telemetryService.updateDroneFromTelemetry(telemetryDTO.getDroneId(), snapshot);

            logger.debug("Telemetry saved for drone: {}", telemetryDTO.getDroneId());

            // TODO: Studenții pot adăuga:
            // - Broadcast la WebSocket clients pentru update real-time în UI
            // - Detectare anomalii și trimitere alerte
            // - Publicare evenimente în topic "drone-events"

        } catch (Exception e) {
            logger.error("Error processing telemetry message: {}", message, e);
        }
    }

    /**
     * DTO pentru deserializarea telemetriei din JSON.
     * Matches formatul din exercițiul 3-1.
     */
    private static class TelemetryDTO {
        private String droneId;
        private long timestamp;
        private double latitude;
        private double longitude;
        private double altitude;
        private double speed;
        private int heading;
        private double batteryLevel;
        private String status;
        private double temperature;
        private double vibration;

        // Getters
        public String getDroneId() { return droneId; }
        public long getTimestamp() { return timestamp; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getAltitude() { return altitude; }
        public double getSpeed() { return speed; }
        public int getHeading() { return heading; }
        public double getBatteryLevel() { return batteryLevel; }
        public String getStatus() { return status; }
        public double getTemperature() { return temperature; }
        public double getVibration() { return vibration; }
    }
}
