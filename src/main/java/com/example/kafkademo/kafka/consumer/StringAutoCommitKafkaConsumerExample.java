package com.example.kafkademo.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class StringAutoCommitKafkaConsumerExample {

    private final static String TOPIC = "com-egen-bootcamp-multiple-partitions-string";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String CLIENT_ID_CONFIG = "string-consumer-gagan";
    private final static String GROUP_ID_CONFIG = "string-cg-gagan-autocommit";


    public static Consumer<String, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Auto-Commits configurations
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        // Create the consumer using props.
        final Consumer<String, String> consumer = new KafkaConsumer<>(props);

        return consumer;
    }

    static void runConsumer() {
        final Consumer<String, String> consumer = createConsumer();
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));

        try {

            while (true) {
                final ConsumerRecords<String, String> consumerRecords =
                        consumer.poll(Duration.ofSeconds(1000));

                if (consumerRecords.isEmpty()) {
                    System.out.println("No Records found for ...." + CLIENT_ID_CONFIG);
                    continue;
                }

                consumerRecords.forEach(record -> {
                            System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                                    record.key(), record.value(), record.partition(), record.offset());
                            process(record.value());
                        }
                );

            }
        } catch (Exception exception) {
            System.err.println("Error in Consumer..." + exception);
        } finally {
            consumer.close();
            System.out.println("DONE");
        }
    }

    private static void process(String message) {
        if (message.equals("Hello! this is sample multi-partition producer message:5"))
            throw new RuntimeException();
    }

    public static void main(String[] args) {
        runConsumer();
    }
}
