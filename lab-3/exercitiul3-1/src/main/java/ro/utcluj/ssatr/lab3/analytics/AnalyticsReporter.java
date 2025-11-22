package ro.utcluj.ssatr.lab3.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Generează rapoarte SQL din baza de date PostgreSQL.
 * TODO: Studenții vor extinde cu:
 * - Mai multe tipuri de rapoarte
 * - Export CSV
 * - Vizualizări grafice text (ASCII charts)
 */
public class AnalyticsReporter {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsReporter.class);

    /**
     * Raport: Statistici generale per dronă.
     */
    public void generateDroneStatistics() {
        String sql = "SELECT " +
                "drone_id, " +
                "COUNT(*) as total_records, " +
                "AVG(battery_level) as avg_battery, " +
                "AVG(altitude) as avg_altitude, " +
                "MAX(speed) as max_speed, " +
                "MIN(timestamp) as first_seen, " +
                "MAX(timestamp) as last_seen " +
                "FROM telemetry_logs " +
                "GROUP BY drone_id " +
                "ORDER BY drone_id";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n=== Drone Statistics Report ===");
            System.out.println("─".repeat(120));
            System.out.printf("%-15s | %-10s | %-12s | %-12s | %-10s | %-20s | %-20s%n",
                    "Drone ID", "Records", "Avg Battery", "Avg Altitude", "Max Speed", "First Seen", "Last Seen");
            System.out.println("─".repeat(120));

            while (rs.next()) {
                System.out.printf("%-15s | %-10d | %11.2f%% | %11.2fm | %9.2fm/s | %-20s | %-20s%n",
                        rs.getString("drone_id"),
                        rs.getInt("total_records"),
                        rs.getDouble("avg_battery"),
                        rs.getDouble("avg_altitude"),
                        rs.getDouble("max_speed"),
                        formatTimestamp(rs.getLong("first_seen")),
                        formatTimestamp(rs.getLong("last_seen"))
                );
            }
            System.out.println("─".repeat(120));

        } catch (SQLException e) {
            logger.error("Failed to generate drone statistics", e);
        }
    }

    /**
     * Raport: Evenimente critice (low battery, high temperature).
     */
    public void generateCriticalEvents() {
        String sql = "SELECT " +
                "drone_id, " +
                "timestamp, " +
                "battery_level, " +
                "temperature, " +
                "status " +
                "FROM telemetry_logs " +
                "WHERE battery_level < 20 OR temperature > 45 " +
                "ORDER BY timestamp DESC " +
                "LIMIT 50";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n=== Critical Events Report (Last 50) ===");
            System.out.println("─".repeat(100));
            System.out.printf("%-15s | %-20s | %-12s | %-12s | %-10s%n",
                    "Drone ID", "Timestamp", "Battery", "Temperature", "Status");
            System.out.println("─".repeat(100));

            int count = 0;
            while (rs.next()) {
                System.out.printf("%-15s | %-20s | %11.2f%% | %11.2f°C | %-10s%n",
                        rs.getString("drone_id"),
                        formatTimestamp(rs.getLong("timestamp")),
                        rs.getDouble("battery_level"),
                        rs.getDouble("temperature"),
                        rs.getString("status")
                );
                count++;
            }
            System.out.println("─".repeat(100));
            System.out.println("Total critical events: " + count);

        } catch (SQLException e) {
            logger.error("Failed to generate critical events report", e);
        }
    }

    /**
     * Raport: Traiectorie dronă (ultimele N poziții GPS).
     */
    public void generateDroneTrajectory(String droneId, int limit) {
        String sql = "SELECT " +
                "timestamp, " +
                "latitude, " +
                "longitude, " +
                "altitude, " +
                "speed, " +
                "heading " +
                "FROM telemetry_logs " +
                "WHERE drone_id = ? " +
                "ORDER BY timestamp DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, droneId);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n=== Drone Trajectory: " + droneId + " (Last " + limit + " points) ===");
                System.out.println("─".repeat(110));
                System.out.printf("%-20s | %-12s | %-12s | %-10s | %-10s | %-8s%n",
                        "Timestamp", "Latitude", "Longitude", "Altitude", "Speed", "Heading");
                System.out.println("─".repeat(110));

                while (rs.next()) {
                    System.out.printf("%-20s | %11.7f | %11.7f | %9.2fm | %9.2fm/s | %7d°%n",
                            formatTimestamp(rs.getLong("timestamp")),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getDouble("altitude"),
                            rs.getDouble("speed"),
                            rs.getInt("heading")
                    );
                }
                System.out.println("─".repeat(110));
            }

        } catch (SQLException e) {
            logger.error("Failed to generate trajectory for drone {}", droneId, e);
        }
    }

    /**
     * Raport: Statistici pe intervale de timp (ultimele 24h, pe ore).
     * TODO: Studenții pot extinde cu query-uri mai complexe.
     */
    public void generateHourlyStatistics() {
        String sql = "SELECT " +
                "date_trunc('hour', to_timestamp(timestamp/1000)) as hour, " +
                "drone_id, " +
                "COUNT(*) as records_count, " +
                "AVG(battery_level) as avg_battery, " +
                "AVG(speed) as avg_speed " +
                "FROM telemetry_logs " +
                "WHERE timestamp > (extract(epoch from NOW() - interval '24 hours') * 1000) " +
                "GROUP BY hour, drone_id " +
                "ORDER BY hour DESC, drone_id " +
                "LIMIT 50";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n=== Hourly Statistics (Last 24h) ===");
            System.out.println("─".repeat(100));
            System.out.printf("%-20s | %-15s | %-10s | %-12s | %-12s%n",
                    "Hour", "Drone ID", "Records", "Avg Battery", "Avg Speed");
            System.out.println("─".repeat(100));

            while (rs.next()) {
                System.out.printf("%-20s | %-15s | %-10d | %11.2f%% | %11.2fm/s%n",
                        rs.getString("hour"),
                        rs.getString("drone_id"),
                        rs.getInt("records_count"),
                        rs.getDouble("avg_battery"),
                        rs.getDouble("avg_speed")
                );
            }
            System.out.println("─".repeat(100));

        } catch (SQLException e) {
            logger.error("Failed to generate hourly statistics", e);
        }
    }

    /**
     * Formatează timestamp (milliseconds) în format citibil.
     */
    private String formatTimestamp(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
    }

    /**
     * Interfață consolă pentru selectarea rapoartelor.
     */
    public void startInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        logger.info("Starting analytics reporter interactive mode");
        printMenu();

        while (running) {
            try {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                switch (input.toUpperCase()) {
                    case "1":
                        generateDroneStatistics();
                        break;

                    case "2":
                        generateCriticalEvents();
                        break;

                    case "3":
                        System.out.print("Enter drone ID: ");
                        String droneId = scanner.nextLine().trim();
                        System.out.print("Enter number of points (default 50): ");
                        String limitStr = scanner.nextLine().trim();
                        int limit = limitStr.isEmpty() ? 50 : Integer.parseInt(limitStr);
                        generateDroneTrajectory(droneId, limit);
                        break;

                    case "4":
                        generateHourlyStatistics();
                        break;

                    case "HELP":
                    case "H":
                        printMenu();
                        break;

                    case "EXIT":
                    case "QUIT":
                    case "Q":
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option. Type HELP for menu.");
                }

            } catch (Exception e) {
                logger.error("Error in interactive mode", e);
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        logger.info("Analytics reporter interactive mode stopped");
    }

    private void printMenu() {
        System.out.println("\n=== Analytics Reporter ===");
        System.out.println("1 - Drone Statistics");
        System.out.println("2 - Critical Events");
        System.out.println("3 - Drone Trajectory");
        System.out.println("4 - Hourly Statistics");
        System.out.println("H - Help (show menu)");
        System.out.println("Q - Quit");
        System.out.println("==========================\n");
    }

    public static void main(String[] args) {
        logger.info("Starting AnalyticsReporter Application");

        // Test database connection
        if (!DatabaseUtils.testConnection()) {
            logger.error("Failed to connect to database. Exiting.");
            System.exit(1);
        }

        AnalyticsReporter reporter = new AnalyticsReporter();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseUtils::closeDataSource));

        // Start interactive mode
        reporter.startInteractiveMode();
    }
}
