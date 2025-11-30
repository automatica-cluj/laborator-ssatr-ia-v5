package ro.utcluj.ssatr.lab3.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class pentru gestionarea conexiunilor la baza de date PostgreSQL.
 * Folosește HikariCP pentru connection pooling.
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    private static final String DB_URL = "jdbc:postgresql://control.aut.utcluj.ro:5432/dronedb";
    //private static final String DB_URL = "jdbc:postgresql://localhost:5432/dronedb";
    private static final String DB_USER = "drone_admin";
    private static final String DB_PASSWORD = "Dr0n3Fl33t#2024!Secure";

    private static HikariDataSource dataSource;

    static {
        try {
            initializeDataSource();
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Inițializează connection pool-ul HikariCP.
     */
    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        // Configurări HikariCP
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes

        // Pool name
        config.setPoolName("DroneDB-Pool");

        // Connection test query
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);
    }

    /**
     * Obține o conexiune din connection pool.
     *
     * @return Connection object
     * @throws SQLException dacă conexiunea nu poate fi obținută
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Închide connection pool-ul.
     * Apelați această metodă la shutdown-ul aplicației.
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    /**
     * Testează conexiunea la baza de date.
     *
     * @return true dacă conexiunea este validă, false altfel
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
}
