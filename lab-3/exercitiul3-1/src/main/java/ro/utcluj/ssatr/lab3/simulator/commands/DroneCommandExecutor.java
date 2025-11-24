package ro.utcluj.ssatr.lab3.simulator.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.simulator.state.DroneState;

/**
 * Executes commands on drone state.
 * Parses command JSON and performs corresponding state mutations.
 */
public class DroneCommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(DroneCommandExecutor.class);

    private final String droneId;
    private final DroneState state;
    private final Gson gson;

    public DroneCommandExecutor(String droneId, DroneState state) {
        this.droneId = droneId;
        this.state = state;
        this.gson = new Gson();
    }

    /**
     * Parses and executes a command from JSON string.
     */
    public void execute(String commandJson) {
        try {
            JsonObject commandObj = gson.fromJson(commandJson, JsonObject.class);
            String targetDroneId = commandObj.get("droneId").getAsString();

            // Filter - only execute if command is for this drone
            if (!droneId.equals(targetDroneId)) {
                return;
            }

            String commandType = commandObj.get("commandType").getAsString();
            logger.info("Drone {} received command: {}", droneId, commandType);

            executeCommand(commandType, commandObj);

        } catch (Exception e) {
            logger.error("Failed to execute command for drone {}: {}", droneId, commandJson, e);
        }
    }

    /**
     * Executes specific command based on type.
     */
    private void executeCommand(String commandType, JsonObject commandObj) {
        switch (commandType) {
            case "TAKE_OFF":
                executeTakeOff();
                break;
            case "LAND":
                executeLand();
                break;
            case "RETURN_HOME":
                executeReturnHome();
                break;
            case "GOTO_WAYPOINT":
                executeGotoWaypoint(commandObj);
                break;
            case "EMERGENCY_LAND":
                executeEmergencyLand();
                break;
            default:
                logger.warn("Drone {} received unknown command type: {}", droneId, commandType);
        }
    }

    /**
     * Executes TAKE_OFF command.
     */
    private void executeTakeOff() {
        String currentStatus = state.getStatus();

        if ("IDLE".equals(currentStatus) || "LANDED".equals(currentStatus)) {
            logger.info("Drone {} taking off", droneId);
            state.setStatus("FLYING");
            state.setAltitude(10.0); // Initial altitude
            state.setSpeed(2.0);     // Initial speed
        } else {
            logger.warn("Drone {} cannot take off - current status: {}", droneId, currentStatus);
        }
    }

    /**
     * Executes LAND command.
     */
    private void executeLand() {
        String currentStatus = state.getStatus();

        if ("FLYING".equals(currentStatus) || "EMERGENCY".equals(currentStatus)) {
            logger.info("Drone {} landing", droneId);
            state.setStatus("LANDED");
            state.setAltitude(0.0);
            state.setSpeed(0.0);
        } else {
            logger.warn("Drone {} cannot land - current status: {}", droneId, currentStatus);
        }
    }

    /**
     * Executes RETURN_HOME command.
     */
    private void executeReturnHome() {
        logger.info("Drone {} returning home to ({}, {})",
                droneId, state.getHomeLat(), state.getHomeLon());

        state.setStatus("FLYING");
        state.returnToHome();

        // Simulate return flight
        state.setAltitude(50.0);
        state.setSpeed(10.0);
    }

    /**
     * Executes GOTO_WAYPOINT command.
     */
    private void executeGotoWaypoint(JsonObject commandObj) {
        if (!commandObj.has("parameters")) {
            logger.warn("Drone {} received GOTO_WAYPOINT without parameters", droneId);
            return;
        }

        try {
            JsonObject params = commandObj.getAsJsonObject("parameters");
            double targetLat = params.get("target_latitude").getAsDouble();
            double targetLon = params.get("target_longitude").getAsDouble();
            double targetAlt = params.get("target_altitude").getAsDouble();

            logger.info("Drone {} going to waypoint ({}, {}, {}m)",
                    droneId, targetLat, targetLon, targetAlt);

            state.setStatus("FLYING");

            // Instant teleport for simplicity (in reality, this would be gradual movement)
            // For Exercise 3-3, students can implement gradual navigation
            state.updatePosition(targetLat, targetLon, targetAlt);
            state.setSpeed(12.0);

        } catch (Exception e) {
            logger.error("Drone {} failed to parse GOTO_WAYPOINT parameters", droneId, e);
        }
    }

    /**
     * Executes EMERGENCY_LAND command.
     */
    private void executeEmergencyLand() {
        logger.warn("Drone {} executing EMERGENCY LAND!", droneId);
        state.setStatus("EMERGENCY");
        state.setAltitude(0.0);
        state.setSpeed(0.0);
    }

    /**
     * Gets the drone ID this executor handles.
     */
    public String getDroneId() {
        return droneId;
    }
}
