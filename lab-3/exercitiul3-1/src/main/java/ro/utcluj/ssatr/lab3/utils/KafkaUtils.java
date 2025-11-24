package ro.utcluj.ssatr.lab3.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Utility class pentru configurații Kafka.
 */
public class KafkaUtils {

    public static final String BOOTSTRAP_SERVERS = "control.aut.utcluj.ro:9092";
    //public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String TOPIC_TELEMETRY = "drone-telemetry";
    public static final String TOPIC_COMMANDS = "drone-commands";
    public static final String TOPIC_EVENTS = "drone-events";

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

        // Configurații opționale pentru performanță și reliabilitate
        props.put(ProducerConfig.ACKS_CONFIG, "1"); // Așteaptă confirmarea de la leader
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Reîncearcă 3 ori în caz de eroare
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10); // Așteaptă 10ms pentru batching
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Batch size în bytes

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

        // Configurații offset management
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Citește doar mesajele noi
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Dezactivăm auto-commit pentru control manual

        // Configurații polling
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // Maxim 100 mesaje per poll
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 30 secunde

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
