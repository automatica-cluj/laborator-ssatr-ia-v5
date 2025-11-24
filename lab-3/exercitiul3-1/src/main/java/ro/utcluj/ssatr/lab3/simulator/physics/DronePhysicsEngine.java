package ro.utcluj.ssatr.lab3.simulator.physics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.simulator.state.DroneState;

import java.util.Random;

/**
 * Simulates physical behavior: movement, battery drain, temperature changes.
 * Handles all physics-related state updates.
 */
public class DronePhysicsEngine {
    private static final Logger logger = LoggerFactory.getLogger(DronePhysicsEngine.class);

    private final Random random;

    // Physics constants
    private static final double GPS_DRIFT_RANGE = 0.001; // ~100m in degrees
    private static final double MIN_FLYING_ALTITUDE = 10.0;
    private static final double MAX_FLYING_ALTITUDE = 150.0;
    private static final double MIN_SPEED = 5.0;
    private static final double MAX_SPEED = 20.0;
    private static final double BATTERY_DRAIN_PER_CYCLE = 0.15; // % per telemetry cycle when flying
    private static final double BATTERY_CHARGE_PER_CYCLE = 0.5; // % per telemetry cycle when landed
    private static final double BASE_TEMPERATURE = 20.0;
    private static final double FLYING_TEMPERATURE_INCREASE = 5.0;
    private static final double TEMPERATURE_VARIANCE = 10.0;

    public DronePhysicsEngine() {
        this.random = new Random();
    }

    /**
     * Updates drone state based on current status and physics simulation.
     */
    public void updateState(DroneState state) {
        String status = state.getStatus();

        switch (status) {
            case "FLYING":
                simulateFlying(state);
                break;
            case "LANDED":
                simulateLanded(state);
                break;
            case "EMERGENCY":
                simulateEmergency(state);
                break;
            case "CHARGING":
                simulateCharging(state);
                break;
            case "IDLE":
                // No state changes when idle
                break;
        }

        // Always update temperature
        simulateTemperature(state);

        // Check for critical conditions
        checkCriticalConditions(state);
    }

    /**
     * Simulates flying state: movement, battery drain.
     */
    private void simulateFlying(DroneState state) {
        // Simulate GPS drift (random walk)
        double latDrift = (random.nextDouble() - 0.5) * GPS_DRIFT_RANGE;
        double lonDrift = (random.nextDouble() - 0.5) * GPS_DRIFT_RANGE;
        state.setLatitude(state.getLatitude() + latDrift);
        state.setLongitude(state.getLongitude() + lonDrift);

        // Simulate altitude variation
        if (state.getAltitude() < MIN_FLYING_ALTITUDE) {
            state.setAltitude(MIN_FLYING_ALTITUDE);
        }
        double altVariation = (random.nextDouble() - 0.5) * 20.0;
        double newAltitude = Math.max(MIN_FLYING_ALTITUDE,
                Math.min(MAX_FLYING_ALTITUDE, state.getAltitude() + altVariation));
        state.setAltitude(newAltitude);

        // Simulate speed
        double newSpeed = MIN_SPEED + random.nextDouble() * (MAX_SPEED - MIN_SPEED);
        state.setSpeed(newSpeed);

        // Simulate heading changes
        int headingChange = random.nextInt(60) - 30; // -30 to +30 degrees
        state.setHeading(state.getHeading() + headingChange);

        // Battery drain (higher speed = more drain)
        double speedFactor = state.getSpeed() / MAX_SPEED;
        double drainAmount = BATTERY_DRAIN_PER_CYCLE * (0.5 + speedFactor * 0.5);
        state.drainBattery(drainAmount);

        // Log low battery warning
        if (state.isBatteryLow() && state.getBatteryLevel() > 19.0) {
            logger.warn("Drone {} low battery: {:.1f}%", state.getDroneId(), state.getBatteryLevel());
        }
    }

    /**
     * Simulates landed state: battery charging, zero motion.
     */
    private void simulateLanded(DroneState state) {
        // On ground - no movement
        state.setAltitude(0.0);
        state.setSpeed(0.0);

        // Slow battery recharge
        state.chargeBattery(BATTERY_CHARGE_PER_CYCLE);
    }

    /**
     * Simulates emergency state: rapid descent.
     */
    private void simulateEmergency(DroneState state) {
        // Emergency descent
        state.setAltitude(0.0);
        state.setSpeed(0.0);

        // Battery continues to drain (systems still running)
        state.drainBattery(BATTERY_DRAIN_PER_CYCLE * 0.5);
    }

    /**
     * Simulates charging state: rapid battery recovery.
     */
    private void simulateCharging(DroneState state) {
        // Grounded and charging
        state.setAltitude(0.0);
        state.setSpeed(0.0);

        // Faster charging than just landed
        state.chargeBattery(BATTERY_CHARGE_PER_CYCLE * 2.0);

        // Once fully charged, switch to IDLE
        if (state.getBatteryLevel() >= 99.9) {
            state.setStatus("IDLE");
            logger.info("Drone {} fully charged, switching to IDLE", state.getDroneId());
        }
    }

    /**
     * Simulates temperature based on activity.
     */
    private void simulateTemperature(DroneState state) {
        double baseTemp = BASE_TEMPERATURE + random.nextDouble() * TEMPERATURE_VARIANCE;

        // Increase temperature when flying
        if ("FLYING".equals(state.getStatus())) {
            baseTemp += FLYING_TEMPERATURE_INCREASE;
        }

        state.setTemperature(baseTemp);
    }

    /**
     * Checks for critical conditions and auto-transitions state.
     */
    private void checkCriticalConditions(DroneState state) {
        // Critical battery - force emergency landing
        if (state.isBatteryCritical() && "FLYING".equals(state.getStatus())) {
            logger.error("Drone {} critical battery ({:.1f}%), entering EMERGENCY mode",
                    state.getDroneId(), state.getBatteryLevel());
            state.setStatus("EMERGENCY");
        }
    }
}
