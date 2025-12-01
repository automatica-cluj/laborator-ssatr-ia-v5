package ro.utcluj.ssatr.lab3.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class pentru configurații Kafka.
 */
public class KafkaUtils {

    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
    private static final Properties config = new Properties();

    public static final String BOOTSTRAP_SERVERS;
    public static final String TOPIC_TELEMETRY;
    public static final String TOPIC_COMMANDS;
    public static final String TOPIC_EVENTS;

    static {
        try {
            loadConfiguration();
            BOOTSTRAP_SERVERS = config.getProperty("kafka.bootstrap.servers");
            TOPIC_TELEMETRY = config.getProperty("kafka.topic.telemetry");
            TOPIC_COMMANDS = config.getProperty("kafka.topic.commands");
            TOPIC_EVENTS = config.getProperty("kafka.topic.events");
            logger.info("Kafka configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load Kafka configuration", e);
            throw new RuntimeException("Kafka configuration initialization failed", e);
        }
    }

    /**
     * Încarcă configurația din fișierul application.properties.
     */
    private static void loadConfiguration() throws IOException {
        try (InputStream input = KafkaUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            config.load(input);
        }
    }

    /**
     * Creează Properties pentru Kafka Producer.
     *
     * @return Properties object cu configurații producer
     */
    public static Properties createProducerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Configurații opționale pentru performanță și reliabilitate din properties
        props.put(ProducerConfig.ACKS_CONFIG, config.getProperty("kafka.producer.acks", "1"));
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(config.getProperty("kafka.producer.retries", "3")));
        props.put(ProducerConfig.LINGER_MS_CONFIG, Integer.parseInt(config.getProperty("kafka.producer.linger.ms", "10")));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.parseInt(config.getProperty("kafka.producer.batch.size", "16384")));

        return props;
    }

    /**
     * Creează Properties pentru Kafka Consumer.
     *
     * @param groupId ID-ul grupului de consumatori
     * @return Properties object cu configurații consumer
     */
    public static Properties createConsumerProperties(String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Configurații offset management din properties
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getProperty("kafka.consumer.auto.offset.reset", "latest"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, config.getProperty("kafka.consumer.enable.auto.commit", "false"));

        // Configurații polling din properties
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.parseInt(config.getProperty("kafka.consumer.max.poll.records", "100")));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.parseInt(config.getProperty("kafka.consumer.session.timeout.ms", "30000")));

        return props;
    }

    /**
     * Returnează toate topic-urile folosite în aplicație.
     *
     * @return Array cu numele topic-urilor
     */
    public static String[] getAllTopics() {
        return new String[]{TOPIC_TELEMETRY, TOPIC_COMMANDS, TOPIC_EVENTS};
    }
}
