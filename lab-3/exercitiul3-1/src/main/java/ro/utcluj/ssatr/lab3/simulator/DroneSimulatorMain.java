package ro.utcluj.ssatr.lab3.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class pentru pornirea mai multor drone simulators.
 */
public class DroneSimulatorMain {
    private static final Logger logger = LoggerFactory.getLogger(DroneSimulatorMain.class);

    public static void main(String[] args) {
        logger.info("Starting Drone Simulator Application");

        // Lista de drone
        List<DroneSimulator> drones = new ArrayList<>();

        // Creăm 5 drone cu poziții inițiale diferite (Cluj-Napoca area)
//        drones.add(new DroneSimulator("DRONE-001", 46.7712, 23.6236, 2));
        drones.add(new DroneSimulator("DRONE-002", 46.7750, 23.6250, 3));
//        drones.add(new DroneSimulator("DRONE-003", 46.7680, 23.6200, 2));
//        drones.add(new DroneSimulator("DRONE-004", 46.7800, 23.6300, 4));
//        drones.add(new DroneSimulator("DRONE-005", 46.7650, 23.6180, 3));
        drones.add(new DroneSimulator("DRONE-006", 46.7650, 23.6180, 3));
        // Pornim toate drone-urile
        for (DroneSimulator drone : drones) {
            drone.start();
        }

        // TODO: Studenții pot adăuga:
        // - Simulare automată pentru decolare după X secunde
        // - Interacțiune prin consolă pentru control manual
        // - Consumer pentru comenzi din Kafka

        // Exemplu: După 10 secunde, prima dronă decolează
//        new Thread(() -> {
//            try {
//                Thread.sleep(10000);
//                logger.info("Commanding DRONE-001 to take off");
//                drones.get(0).takeOff();
//            } catch (InterruptedException e) {
//                logger.error("Interrupted", e);
//            }
//        }).start();

        // Shutdown hook pentru oprire curată
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down all drones...");
            for (DroneSimulator drone : drones) {
                drone.stop();
            }
        }));

        logger.info("Drone Simulator Application started. Press Ctrl+C to stop.");
    }
}
