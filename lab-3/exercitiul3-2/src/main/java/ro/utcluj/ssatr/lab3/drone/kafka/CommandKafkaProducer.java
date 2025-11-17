package ro.utcluj.ssatr.lab3.drone.kafka;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka producer pentru trimiterea comenzilor către drone prin topic "drone-commands".
 * Folosește KafkaTemplate din Spring Kafka.
 */
@Component
public class CommandKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(CommandKafkaProducer.class);

    private static final String COMMAND_TOPIC = "drone-commands";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Gson gson = new Gson();

    /**
     * Trimite o comandă către o dronă.
     * @param droneId ID-ul dronei
     * @param commandType Tipul comenzii (TAKE_OFF, LAND, GOTO_WAYPOINT, etc.)
     * @param parameters Parametri opționali pentru comandă
     */
    public void sendCommand(String droneId, String commandType, Map<String, Object> parameters) {
        try {
            DroneCommandDTO command = new DroneCommandDTO();
            command.setCommandId(UUID.randomUUID().toString());
            command.setDroneId(droneId);
            command.setCommandType(commandType);
            command.setTimestamp(System.currentTimeMillis());
            command.setParameters(parameters != null ? parameters : new HashMap<>());

            String json = gson.toJson(command);

            kafkaTemplate.send(COMMAND_TOPIC, droneId, json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("Command {} sent to drone {} successfully", commandType, droneId);
                        } else {
                            logger.error("Failed to send command {} to drone {}", commandType, droneId, ex);
                        }
                    });

        } catch (Exception e) {
            logger.error("Error sending command to drone {}", droneId, e);
        }
    }

    /**
     * Trimite comandă TAKE_OFF.
     */
    public void sendTakeOffCommand(String droneId) {
        Map<String, Object> params = new HashMap<>();
        params.put("target_altitude", 100.0);
        sendCommand(droneId, "TAKE_OFF", params);
    }

    /**
     * Trimite comandă LAND.
     */
    public void sendLandCommand(String droneId) {
        sendCommand(droneId, "LAND", null);
    }

    /**
     * Trimite comandă GOTO_WAYPOINT.
     */
    public void sendGotoWaypointCommand(String droneId, double lat, double lon, double alt) {
        Map<String, Object> params = new HashMap<>();
        params.put("latitude", lat);
        params.put("longitude", lon);
        params.put("altitude", alt);
        sendCommand(droneId, "GOTO_WAYPOINT", params);
    }

    /**
     * Trimite comandă RETURN_HOME.
     */
    public void sendReturnHomeCommand(String droneId) {
        sendCommand(droneId, "RETURN_HOME", null);
    }

    /**
     * Trimite comandă EMERGENCY_LAND.
     */
    public void sendEmergencyLandCommand(String droneId) {
        sendCommand(droneId, "EMERGENCY_LAND", null);
    }

    /**
     * TODO: Studenții pot adăuga comenzi custom și logică de validare.
     */

    /**
     * DTO pentru serializarea comenzilor în JSON.
     * Matches formatul din exercițiul 3-1.
     */
    private static class DroneCommandDTO {
        private String commandId;
        private String droneId;
        private String commandType;
        private long timestamp;
        private Map<String, Object> parameters;

        // Getters and Setters
        public String getCommandId() { return commandId; }
        public void setCommandId(String commandId) { this.commandId = commandId; }

        public String getDroneId() { return droneId; }
        public void setDroneId(String droneId) { this.droneId = droneId; }

        public String getCommandType() { return commandType; }
        public void setCommandType(String commandType) { this.commandType = commandType; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
}
