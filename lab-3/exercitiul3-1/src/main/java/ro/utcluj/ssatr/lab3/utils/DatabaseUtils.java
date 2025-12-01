package ro.utcluj.ssatr.lab3.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class pentru gestionarea conexiunilor la baza de date PostgreSQL.
 * Folosește HikariCP pentru connection pooling.
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    private static final Properties config = new Properties();
    private static HikariDataSource dataSource;

    static {
        try {
            loadConfiguration();
            initializeDataSource();
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Încarcă configurația din fișierul application.properties.
     */
    private static void loadConfiguration() throws IOException {
        try (InputStream input = DatabaseUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            config.load(input);
            logger.info("Configuration loaded successfully from application.properties");
        }
    }

    /**
     * Inițializează connection pool-ul HikariCP.
     */
    private static void initializeDataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        // Configurări conexiune
        hikariConfig.setJdbcUrl(config.getProperty("db.url"));
        hikariConfig.setUsername(config.getProperty("db.username"));
        hikariConfig.setPassword(config.getProperty("db.password"));

        // Configurări HikariCP din properties
        hikariConfig.setMaximumPoolSize(Integer.parseInt(config.getProperty("db.pool.maximumPoolSize", "10")));
        hikariConfig.setMinimumIdle(Integer.parseInt(config.getProperty("db.pool.minimumIdle", "2")));
        hikariConfig.setConnectionTimeout(Long.parseLong(config.getProperty("db.pool.connectionTimeout", "30000")));
        hikariConfig.setIdleTimeout(Long.parseLong(config.getProperty("db.pool.idleTimeout", "600000")));
        hikariConfig.setMaxLifetime(Long.parseLong(config.getProperty("db.pool.maxLifetime", "1800000")));

        // Pool name
        hikariConfig.setPoolName(config.getProperty("db.pool.poolName", "DroneDB-Pool"));

        // Connection test query
        hikariConfig.setConnectionTestQuery(config.getProperty("db.pool.connectionTestQuery", "SELECT 1"));

        dataSource = new HikariDataSource(hikariConfig);
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
