package ro.utcluj.ssatr.lab3.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration manager for the application.
 * Single point of access for all configuration properties.
 *
 * This class loads the application.properties file once and provides
 * thread-safe access to all configuration values.
 */
public class ConfigurationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

    private static final Properties properties = new Properties();
    private static volatile boolean initialized = false;

    // Configuration file name (can be overridden via system property)
    private static final String DEFAULT_CONFIG_FILE = "application-custom.properties";
    private static final String CONFIG_FILE_PROPERTY = "config.file";

    static {
        initialize();
    }

    /**
     * Initializes the configuration manager by loading the properties file.
     */
    private static synchronized void initialize() {
        if (initialized) {
            return;
        }

        String configFileName = System.getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);

        try (InputStream input = ConfigurationManager.class.getClassLoader()
                .getResourceAsStream(configFileName)) {

            if (input == null) {
                throw new IOException("Unable to find " + configFileName);
            }

            properties.load(input);
            initialized = true;

            logger.info("Configuration loaded successfully from: {}", configFileName);
            logger.info("Kafka Bootstrap Servers: {}", properties.getProperty("kafka.bootstrap.servers"));
            logger.info("Database URL: {}", properties.getProperty("db.url"));

        } catch (IOException e) {
            logger.error("Failed to load configuration from {}", configFileName, e);
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }

    /**
     * Gets a property value as a String.
     *
     * @param key The property key
     * @return The property value, or null if not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets a property value as a String with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value if property is not found
     * @return The property value, or defaultValue if not found
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets a property value as an Integer.
     *
     * @param key The property key
     * @param defaultValue The default value if property is not found or invalid
     * @return The property value as Integer
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}: {}, using default: {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets a property value as a Long.
     *
     * @param key The property key
     * @param defaultValue The default value if property is not found or invalid
     * @return The property value as Long
     */
    public static long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid long value for property {}: {}, using default: {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets a property value as a Boolean.
     *
     * @param key The property key
     * @param defaultValue The default value if property is not found
     * @return The property value as Boolean
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Gets a property value as a Double.
     *
     * @param key The property key
     * @param defaultValue The default value if property is not found or invalid
     * @return The property value as Double
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid double value for property {}: {}, using default: {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Checks if a property key exists.
     *
     * @param key The property key
     * @return true if the property exists, false otherwise
     */
    public static boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Gets all properties (read-only).
     *
     * @return Properties object (should not be modified)
     */
    public static Properties getAllProperties() {
        return (Properties) properties.clone();
    }

    /**
     * Reloads the configuration from the properties file.
     * Use with caution - this may cause inconsistent state in running components.
     */
    public static synchronized void reload() {
        initialized = false;
        properties.clear();
        initialize();
        logger.info("Configuration reloaded");
    }
}
