package ro.utcluj.ssatr.lab3.simulator.commands;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcluj.ssatr.lab3.utils.KafkaUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listens for commands from Kafka and dispatches them to the executor.
 * Runs in a separate thread to continuously poll for commands.
 */
public class CommandListener implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);

    private final String droneId;
    private final KafkaConsumer<String, String> consumer;
    private final DroneCommandExecutor executor;
    private final AtomicBoolean running;

    public CommandListener(String droneId, DroneCommandExecutor executor) {
        this.droneId = droneId;
        this.executor = executor;
        this.consumer = new KafkaConsumer<>(
                KafkaUtils.createConsumerProperties("drone-simulator-" + droneId)
        );
        this.running = new AtomicBoolean(false);
    }

    /**
     * Starts the command listener in a new thread.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            consumer.subscribe(Collections.singletonList(KafkaUtils.TOPIC_COMMANDS));
            Thread listenerThread = new Thread(this, "CommandListener-" + droneId);
            listenerThread.setDaemon(false); // Keep running until explicitly stopped
            listenerThread.start();
            logger.info("CommandListener started for drone {} on topic {}",
                    droneId, KafkaUtils.TOPIC_COMMANDS);
        } else {
            logger.warn("CommandListener for drone {} is already running", droneId);
        }
    }

    /**
     * Main run loop - polls Kafka for commands and dispatches to executor.
     */
    @Override
    public void run() {
        logger.info("Command consumer thread started for drone {}", droneId);

        try {
            while (running.get()) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            logger.debug("Drone {} received message from Kafka: {}",
                                    droneId, record.value());
                            executor.execute(record.value());
                        } catch (Exception e) {
                            logger.error("Error executing command for drone {}: {}",
                                    droneId, record.value(), e);
                        }
                    }
                } catch (Exception e) {
                    if (running.get()) { // Only log if we're still supposed to be running
                        logger.error("Error polling commands for drone {}", droneId, e);
                    }
                }
            }
        } finally {
            logger.info("Command consumer thread stopped for drone {}", droneId);
        }
    }

    /**
     * Stops the command listener and closes the Kafka consumer.
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping CommandListener for drone {}", droneId);

            try {
                consumer.wakeup(); // Interrupt the poll() call
            } catch (Exception e) {
                logger.warn("Error waking up consumer for drone {}", droneId, e);
            }

            try {
                consumer.close();
                logger.debug("Kafka consumer closed for drone {}", droneId);
            } catch (Exception e) {
                logger.error("Error closing Kafka consumer for drone {}", droneId, e);
            }
        }
    }

    /**
     * Checks if the listener is currently running.
     */
    public boolean isRunning() {
        return running.get();
    }
}
