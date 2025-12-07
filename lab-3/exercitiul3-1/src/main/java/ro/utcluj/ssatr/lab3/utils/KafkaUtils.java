package ro.utcluj.ssatr.lab3.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Utility class pentru configurații Kafka.
 * Uses ConfigurationManager for centralized property access.
 */
public class KafkaUtils {

    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);

    public static final String BOOTSTRAP_SERVERS;
    public static final String TOPIC_TELEMETRY;
    public static final String TOPIC_COMMANDS;
    public static final String TOPIC_EVENTS;

    static {
        BOOTSTRAP_SERVERS = ConfigurationManager.getProperty("kafka.bootstrap.servers");
        TOPIC_TELEMETRY = ConfigurationManager.getProperty("kafka.topic.telemetry");
        TOPIC_COMMANDS = ConfigurationManager.getProperty("kafka.topic.commands");
        TOPIC_EVENTS = ConfigurationManager.getProperty("kafka.topic.events");
        logger.info("Kafka configuration initialized successfully");
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
        props.put(ProducerConfig.ACKS_CONFIG, ConfigurationManager.getProperty("kafka.producer.acks", "1"));
        props.put(ProducerConfig.RETRIES_CONFIG, ConfigurationManager.getIntProperty("kafka.producer.retries", 3));
        props.put(ProducerConfig.LINGER_MS_CONFIG, ConfigurationManager.getIntProperty("kafka.producer.linger.ms", 10));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, ConfigurationManager.getIntProperty("kafka.producer.batch.size", 16384));

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
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, ConfigurationManager.getProperty("kafka.consumer.auto.offset.reset", "latest"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, ConfigurationManager.getProperty("kafka.consumer.enable.auto.commit", "false"));

        // Configurații polling din properties
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, ConfigurationManager.getIntProperty("kafka.consumer.max.poll.records", 100));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, ConfigurationManager.getIntProperty("kafka.consumer.session.timeout.ms", 30000));

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
