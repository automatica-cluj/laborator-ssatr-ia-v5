package ro.utcluj.ssatr.lab3.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.simulator.commands.CommandListener;
import ro.utcluj.ssatr.lab3.simulator.commands.DroneCommandExecutor;
import ro.utcluj.ssatr.lab3.simulator.database.DroneDatabaseRegistry;
import ro.utcluj.ssatr.lab3.simulator.physics.DronePhysicsEngine;
import ro.utcluj.ssatr.lab3.simulator.state.DroneState;
import ro.utcluj.ssatr.lab3.simulator.telemetry.TelemetryPublisher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main orchestrator for drone simulation.
 * Coordinates all components: state, physics, telemetry, and commands.
 *
 * Refactored to follow Single Responsibility Principle.
 * Each aspect (state, physics, database, Kafka) is handled by a dedicated component.
 */
public class DroneSimulator {
    private static final Logger logger = LoggerFactory.getLogger(DroneSimulator.class);

    // Core components
    private final String droneId;
    private final DroneState state;
    private final DronePhysicsEngine physics;
    private final TelemetryPublisher telemetryPublisher;
    private final CommandListener commandListener;
    private final ScheduledExecutorService telemetryScheduler;

    // Configuration
    private final int telemetryInterval;

    /**
     * Creates a new drone simulator.
     *
     * @param droneId Unique identifier for this drone
     * @param startLat Initial latitude
     * @param startLon Initial longitude
     * @param telemetryInterval How often to emit telemetry (in seconds)
     */
    public DroneSimulator(String droneId, double startLat, double startLon, int telemetryInterval) {
        this.droneId = droneId;
        this.telemetryInterval = telemetryInterval;

        // Initialize state
        this.state = new DroneState(droneId, startLat, startLon);

        // Initialize physics engine
        this.physics = new DronePhysicsEngine();

        // Initialize telemetry publisher
        this.telemetryPublisher = new TelemetryPublisher();

        // Initialize command processing
        DroneCommandExecutor commandExecutor = new DroneCommandExecutor(droneId, state);
        this.commandListener = new CommandListener(droneId, commandExecutor);

        // Initialize telemetry scheduler
        this.telemetryScheduler = Executors.newScheduledThreadPool(1,
                r -> new Thread(r, "Telemetry-" + droneId));

        // Register drone in database
        DroneDatabaseRegistry registry = new DroneDatabaseRegistry();
        registry.registerDrone(state);

        logger.info("Drone {} initialized at ({}, {})", droneId, startLat, startLon);
    }

    /**
     * Starts the drone simulation.
     * Begins emitting telemetry and listening for commands.
     */
    public void start() {
        logger.info("Starting drone {} simulation (telemetry every {} seconds)",
                droneId, telemetryInterval);

        // Start telemetry emission loop
        telemetryScheduler.scheduleAtFixedRate(() -> {
            try {
                // Update physics (movement, battery, etc.)
                physics.updateState(state);

                // Publish current state as telemetry
                telemetryPublisher.publish(state);

            } catch (Exception e) {
                logger.error("Error in telemetry loop for drone {}", droneId, e);
            }
        }, 0, telemetryInterval, TimeUnit.SECONDS);

        // Start command listener
        commandListener.start();

        logger.info("Drone {} simulation started successfully", droneId);
    }

    /**
     * Stops the drone simulation and releases all resources.
     */
    public void stop() {
        logger.info("Stopping drone {} simulation", droneId);

        // Stop telemetry emission
        telemetryScheduler.shutdown();
        try {
            if (!telemetryScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                telemetryScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            telemetryScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Stop command listener
        commandListener.stop();

        // Close telemetry publisher
        telemetryPublisher.close();

        logger.info("Drone {} simulation stopped", droneId);
    }

    /**
     * Gets the drone ID.
     */
    public String getDroneId() {
        return droneId;
    }

    /**
     * Gets current battery level.
     */
    public double getBatteryLevel() {
        return state.getBatteryLevel();
    }

    /**
     * Gets current status.
     */
    public String getStatus() {
        return state.getStatus();
    }

    /**
     * Gets current position (latitude).
     */
    public double getLatitude() {
        return state.getLatitude();
    }

    /**
     * Gets current position (longitude).
     */
    public double getLongitude() {
        return state.getLongitude();
    }

    /**
     * Gets current altitude.
     */
    public double getAltitude() {
        return state.getAltitude();
    }

    /**
     * Gets the underlying state object (for advanced use cases).
     */
    public DroneState getState() {
        return state;
    }
}
