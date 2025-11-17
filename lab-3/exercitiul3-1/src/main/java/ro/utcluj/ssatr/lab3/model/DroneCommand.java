package ro.utcluj.ssatr.lab3.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model pentru comenzi trimise către drone.
 */
public class DroneCommand {
    private String commandId;
    private String droneId;
    private String commandType; // TAKE_OFF, LAND, GOTO_WAYPOINT, RETURN_HOME, EMERGENCY_LAND
    private long timestamp;
    private Map<String, Object> parameters;

    public DroneCommand() {
        this.parameters = new HashMap<>();
    }

    public DroneCommand(String commandId, String droneId, String commandType, long timestamp) {
        this.commandId = commandId;
        this.droneId = droneId;
        this.commandType = commandType;
        this.timestamp = timestamp;
        this.parameters = new HashMap<>();
    }

    // Getters and Setters
    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getDroneId() {
        return droneId;
    }

    public void setDroneId(String droneId) {
        this.droneId = droneId;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @Override
    public String toString() {
        return "DroneCommand{" +
                "commandId='" + commandId + '\'' +
                ", droneId='" + droneId + '\'' +
                ", commandType='" + commandType + '\'' +
                ", timestamp=" + timestamp +
                ", parameters=" + parameters +
                '}';
    }
}
