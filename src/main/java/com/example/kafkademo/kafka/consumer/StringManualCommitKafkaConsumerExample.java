package com.example.kafkademo.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class StringManualCommitKafkaConsumerExample {

    private final static String TOPIC = "com-egen-bootcamp-multiple-partitions-string";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String CLIENT_ID_CONFIG = "string-consumer-gagan";
    private final static String GROUP_ID_CONFIG = "string-cg-gagan-manual12";

    private static int counter = 0;


    public static Consumer<String, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        //Only for understanding
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);

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
                    }
                );
                counter++;
//                process();
                consumer.commitSync();
            }
        } catch (Exception exception) {
            System.err.println("Error in Consumer..." + exception);

        } finally {
            consumer.close();
            System.out.println("DONE");
        }
    }

    private static void process() {
        try {
            if(counter == 2) {
                throw new RuntimeException(); //ToDO: Error Handling
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Processing.....");
    }

    public static void main(String[] args) {
        runConsumer();
    }
}
