package ro.utcluj.ssatr.lab3.simulator.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates drone state (position, battery, status).
 * Single source of truth for drone's current state.
 * Thread-safe for concurrent access from telemetry and command threads.
 */
public class DroneState {
    private static final Logger logger = LoggerFactory.getLogger(DroneState.class);

    private final String droneId;

    // Position state
    private volatile double latitude;
    private volatile double longitude;
    private volatile double altitude;

    // Motion state
    private volatile double speed;
    private volatile int heading;

    // System state
    private volatile double batteryLevel;
    private volatile String status;
    private volatile double temperature;

    // Initial home position
    private final double homeLat;
    private final double homeLon;

    public DroneState(String droneId, double initialLat, double initialLon) {
        this.droneId = droneId;
        this.latitude = initialLat;
        this.longitude = initialLon;
        this.altitude = 0.0;
        this.speed = 0.0;
        this.heading = 0;
        this.batteryLevel = 100.0;
        this.status = "IDLE";
        this.temperature = 25.0;
        this.homeLat = initialLat;
        this.homeLon = initialLon;
    }

    /**
     * Validates if transition from current status to new status is allowed.
     */
    public synchronized boolean canTransitionTo(String newStatus) {
        switch (status) {
            case "IDLE":
            case "LANDED":
                return "FLYING".equals(newStatus) || "CHARGING".equals(newStatus);
            case "FLYING":
                return "LANDED".equals(newStatus) || "EMERGENCY".equals(newStatus) || "IDLE".equals(newStatus);
            case "CHARGING":
                return "IDLE".equals(newStatus);
            case "EMERGENCY":
                return "LANDED".equals(newStatus);
            default:
                return false;
        }
    }

    /**
     * Sets status with validation.
     */
    public synchronized void setStatus(String newStatus) {
        if (canTransitionTo(newStatus)) {
            logger.debug("Drone {} status transition: {} -> {}", droneId, status, newStatus);
            this.status = newStatus;
        } else {
            logger.warn("Drone {} invalid status transition attempted: {} -> {}", droneId, status, newStatus);
        }
    }

    /**
     * Updates position (latitude, longitude, altitude).
     */
    public synchronized void updatePosition(double lat, double lon, double alt) {
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
    }

    /**
     * Updates motion parameters (speed, heading).
     */
    public synchronized void updateMotion(double speed, int heading) {
        this.speed = speed;
        this.heading = ((heading % 360) + 360) % 360; // Normalize to 0-359, handling negatives
    }

    /**
     * Sets battery level with bounds checking.
     */
    public synchronized void setBatteryLevel(double level) {
        this.batteryLevel = Math.max(0.0, Math.min(100.0, level));
    }

    /**
     * Drains battery by specified amount.
     */
    public synchronized void drainBattery(double amount) {
        setBatteryLevel(batteryLevel - amount);
    }

    /**
     * Charges battery by specified amount.
     */
    public synchronized void chargeBattery(double amount) {
        setBatteryLevel(batteryLevel + amount);
    }

    /**
     * Checks if battery is critical (< 10%).
     */
    public boolean isBatteryCritical() {
        return batteryLevel < 10.0;
    }

    /**
     * Checks if battery is low (< 20%).
     */
    public boolean isBatteryLow() {
        return batteryLevel < 20.0;
    }

    /**
     * Resets position to home coordinates.
     */
    public synchronized void returnToHome() {
        this.latitude = homeLat;
        this.longitude = homeLon;
    }

    // Getters (thread-safe via volatile)
    public String getDroneId() {
        return droneId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public int getHeading() {
        return heading;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public String getStatus() {
        return status;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setHeading(int heading) {
        // Normalize heading to 0-359 range, handling negative values
        this.heading = ((heading % 360) + 360) % 360;
    }

    public double getHomeLat() {
        return homeLat;
    }

    public double getHomeLon() {
        return homeLon;
    }

    @Override
    public String toString() {
        return String.format("DroneState{id='%s', status='%s', pos=(%.4f,%.4f,%.1fm), battery=%.1f%%}",
                droneId, status, latitude, longitude, altitude, batteryLevel);
    }
}
