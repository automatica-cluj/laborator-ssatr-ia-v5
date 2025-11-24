package ro.utcluj.ssatr.lab3.simulator.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.simulator.state.DroneState;
import ro.utcluj.ssatr.lab3.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * Handles database operations for drone registration.
 * Separates database concerns from simulation logic.
 */
public class DroneDatabaseRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DroneDatabaseRegistry.class);

    private final Random random = new Random();

    // Drone name pool
    private static final String[] DRONE_NAMES = {
            "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot",
            "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima"
    };

    // Drone model pool
    private static final String[] DRONE_MODELS = {
            "DJI Mavic 3 Pro",
            "DJI Phantom 4 Pro",
            "Autel EVO II",
            "Parrot Anafi",
            "Skydio 2+",
            "Yuneec Typhoon H3",
            "DJI Mini 3 Pro",
            "Autel Robotics EVO Lite+"
    };

    /**
     * Registers or updates drone in the database.
     * If drone exists, updates status and battery.
     * If drone doesn't exist, creates new record.
     */
    public void registerDrone(DroneState state) {
        try (Connection conn = DatabaseUtils.getConnection()) {
            if (droneExists(conn, state.getDroneId())) {
                updateDrone(conn, state);
            } else {
                insertDrone(conn, state);
            }
        } catch (SQLException e) {
            logger.error("Failed to register drone {} in database", state.getDroneId(), e);
        }
    }

    /**
     * Checks if drone already exists in database.
     */
    private boolean droneExists(Connection conn, String droneId) throws SQLException {
        String sql = "SELECT id FROM drones WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, droneId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    /**
     * Updates existing drone record.
     */
    private void updateDrone(Connection conn, DroneState state) throws SQLException {
        String sql = "UPDATE drones SET status = ?, battery_level = ?, last_seen = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, state.getStatus());
            stmt.setDouble(2, state.getBatteryLevel());
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setString(4, state.getDroneId());
            stmt.executeUpdate();
            logger.info("Drone {} updated in database (status: {}, battery: {:.1f}%)",
                    state.getDroneId(), state.getStatus(), state.getBatteryLevel());
        }
    }

    /**
     * Inserts new drone record.
     */
    private void insertDrone(Connection conn, DroneState state) throws SQLException {
        String sql = "INSERT INTO drones (id, name, model, status, battery_level, last_seen) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String name = generateDroneName(state.getDroneId());
            String model = generateDroneModel();

            stmt.setString(1, state.getDroneId());
            stmt.setString(2, name);
            stmt.setString(3, model);
            stmt.setString(4, state.getStatus());
            stmt.setDouble(5, state.getBatteryLevel());
            stmt.setLong(6, System.currentTimeMillis());
            stmt.executeUpdate();

            logger.info("Drone {} registered in database (name: {}, model: {})",
                    state.getDroneId(), name, model);
        }
    }

    /**
     * Generates a consistent name for a drone based on its ID.
     * Same ID will always get the same name.
     */
    private String generateDroneName(String droneId) {
        int index = Math.abs(droneId.hashCode()) % DRONE_NAMES.length;
        return DRONE_NAMES[index];
    }

    /**
     * Generates a random drone model.
     */
    private String generateDroneModel() {
        return DRONE_MODELS[random.nextInt(DRONE_MODELS.length)];
    }
}
