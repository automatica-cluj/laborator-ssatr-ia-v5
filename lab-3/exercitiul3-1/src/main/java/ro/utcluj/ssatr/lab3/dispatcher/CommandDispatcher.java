package ro.utcluj.ssatr.lab3.dispatcher;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.model.DroneCommand;
import ro.utcluj.ssatr.lab3.utils.KafkaUtils;

import java.util.Scanner;
import java.util.UUID;

/**
 * Trimite comenzi către drone prin Kafka.
 * TODO: Studenții pot extinde pentru:
 * - Validare comenzi
 * - Verificare stare dronă înainte de trimitere comandă
 * - Istoric comenzi
 */
public class CommandDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(CommandDispatcher.class);

    private final KafkaProducer<String, String> producer;
    private final Gson gson;

    public CommandDispatcher() {
        this.producer = new KafkaProducer<>(KafkaUtils.createProducerProperties());
        this.gson = new Gson();
        logger.info("CommandDispatcher initialized");
    }

    /**
     * Trimite o comandă către o dronă.
     *
     * @param command Comanda de trimis
     */
    public void sendCommand(DroneCommand command) {
        try {
            String json = gson.toJson(command);
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    KafkaUtils.TOPIC_COMMANDS,
                    command.getDroneId(),
                    json
            );

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("Failed to send command {} for drone {}",
                            command.getCommandType(), command.getDroneId(), exception);
                } else {
                    logger.info("Command {} sent to drone {} (partition: {}, offset: {})",
                            command.getCommandType(), command.getDroneId(),
                            metadata.partition(), metadata.offset());
                }
            });

        } catch (Exception e) {
            logger.error("Error sending command", e);
        }
    }

    /**
     * Crează și trimite comandă TAKE_OFF.
     */
    public void sendTakeOffCommand(String droneId) {
        DroneCommand command = new DroneCommand(
                UUID.randomUUID().toString(),
                droneId,
                "TAKE_OFF",
                System.currentTimeMillis()
        );
        command.addParameter("target_altitude", 100.0);
        sendCommand(command);
    }

    /**
     * Crează și trimite comandă LAND.
     */
    public void sendLandCommand(String droneId) {
        DroneCommand command = new DroneCommand(
                UUID.randomUUID().toString(),
                droneId,
                "LAND",
                System.currentTimeMillis()
        );
        sendCommand(command);
    }

    /**
     * Crează și trimite comandă GOTO_WAYPOINT.
     */
    public void sendGotoWaypointCommand(String droneId, double lat, double lon, double alt) {
        DroneCommand command = new DroneCommand(
                UUID.randomUUID().toString(),
                droneId,
                "GOTO_WAYPOINT",
                System.currentTimeMillis()
        );
        command.addParameter("latitude", lat);
        command.addParameter("longitude", lon);
        command.addParameter("altitude", alt);
        sendCommand(command);
    }

    /**
     * Crează și trimite comandă RETURN_HOME.
     */
    public void sendReturnHomeCommand(String droneId) {
        DroneCommand command = new DroneCommand(
                UUID.randomUUID().toString(),
                droneId,
                "RETURN_HOME",
                System.currentTimeMillis()
        );
        sendCommand(command);
    }

    /**
     * Crează și trimite comandă EMERGENCY_LAND.
     */
    public void sendEmergencyLandCommand(String droneId) {
        DroneCommand command = new DroneCommand(
                UUID.randomUUID().toString(),
                droneId,
                "EMERGENCY_LAND",
                System.currentTimeMillis()
        );
        sendCommand(command);
    }

    /**
     * Pornește interfața consolă pentru trimitere comenzi interactive.
     */
    public void startConsoleInterface() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        logger.info("Starting command console interface");
        printMenu();

        while (running) {
            try {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                String[] parts = input.split("\\s+");
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "TAKEOFF":
                        if (parts.length < 2) {
                            System.out.println("Usage: TAKEOFF <drone_id>");
                        } else {
                            sendTakeOffCommand(parts[1]);
                        }
                        break;

                    case "LAND":
                        if (parts.length < 2) {
                            System.out.println("Usage: LAND <drone_id>");
                        } else {
                            sendLandCommand(parts[1]);
                        }
                        break;

                    case "GOTO":
                        if (parts.length < 5) {
                            System.out.println("Usage: GOTO <drone_id> <lat> <lon> <alt>");
                        } else {
                            String droneId = parts[1];
                            double lat = Double.parseDouble(parts[2]);
                            double lon = Double.parseDouble(parts[3]);
                            double alt = Double.parseDouble(parts[4]);
                            sendGotoWaypointCommand(droneId, lat, lon, alt);
                        }
                        break;

                    case "RTH":
                        if (parts.length < 2) {
                            System.out.println("Usage: RTH <drone_id>");
                        } else {
                            sendReturnHomeCommand(parts[1]);
                        }
                        break;

                    case "EMERGENCY":
                        if (parts.length < 2) {
                            System.out.println("Usage: EMERGENCY <drone_id>");
                        } else {
                            sendEmergencyLandCommand(parts[1]);
                        }
                        break;

                    case "HELP":
                        printMenu();
                        break;

                    case "EXIT":
                    case "QUIT":
                        running = false;
                        break;

                    default:
                        System.out.println("Unknown command. Type HELP for available commands.");
                }

            } catch (Exception e) {
                logger.error("Error processing command", e);
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        logger.info("Command console interface stopped");
    }

    private void printMenu() {
        System.out.println("\n=== Drone Command Dispatcher ===");
        System.out.println("Available commands:");
        System.out.println("  TAKEOFF <drone_id>                    - Take off drone");
        System.out.println("  LAND <drone_id>                       - Land drone");
        System.out.println("  GOTO <drone_id> <lat> <lon> <alt>     - Go to waypoint");
        System.out.println("  RTH <drone_id>                        - Return to home");
        System.out.println("  EMERGENCY <drone_id>                  - Emergency land");
        System.out.println("  HELP                                  - Show this menu");
        System.out.println("  EXIT                                  - Exit");
        System.out.println("================================\n");
    }

    /**
     * Închide producer-ul Kafka.
     */
    public void close() {
        producer.close();
        logger.info("CommandDispatcher closed");
    }

    public static void main(String[] args) {
        logger.info("Starting CommandDispatcher Application");

        CommandDispatcher dispatcher = new CommandDispatcher();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(dispatcher::close));

        // Start console interface
        dispatcher.startConsoleInterface();
    }
}
