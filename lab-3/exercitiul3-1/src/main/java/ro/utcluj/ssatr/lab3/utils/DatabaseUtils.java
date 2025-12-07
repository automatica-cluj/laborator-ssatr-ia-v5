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
 * Uses ConfigurationManager for centralized property access.
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

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
        HikariConfig hikariConfig = new HikariConfig();

        // Configurări conexiune
        hikariConfig.setJdbcUrl(ConfigurationManager.getProperty("db.url"));
        hikariConfig.setUsername(ConfigurationManager.getProperty("db.username"));
        hikariConfig.setPassword(ConfigurationManager.getProperty("db.password"));

        // Configurări HikariCP din properties
        hikariConfig.setMaximumPoolSize(ConfigurationManager.getIntProperty("db.pool.maximumPoolSize", 10));
        hikariConfig.setMinimumIdle(ConfigurationManager.getIntProperty("db.pool.minimumIdle", 2));
        hikariConfig.setConnectionTimeout(ConfigurationManager.getLongProperty("db.pool.connectionTimeout", 30000));
        hikariConfig.setIdleTimeout(ConfigurationManager.getLongProperty("db.pool.idleTimeout", 600000));
        hikariConfig.setMaxLifetime(ConfigurationManager.getLongProperty("db.pool.maxLifetime", 1800000));

        // Pool name
        hikariConfig.setPoolName(ConfigurationManager.getProperty("db.pool.poolName", "DroneDB-Pool"));

        // Connection test query
        hikariConfig.setConnectionTestQuery(ConfigurationManager.getProperty("db.pool.connectionTestQuery", "SELECT 1"));

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
