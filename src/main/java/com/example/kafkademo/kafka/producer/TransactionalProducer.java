package com.example.kafkademo.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class TransactionalProducer {

    private final static String TOPIC = "com-egen-bootcamp-string-transaction";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String CLIENT_ID_CONFIG = "string-trans-producer-gagan";

    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Transactional
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactional-gaganQWERTY");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        return new KafkaProducer<>(props);
    }

    static void runProducer(final int sendMessageCount) throws Exception {
        final Producer<String, String> producer = createProducer();
        try {
            producer.initTransactions();
            producer.beginTransaction();
            for (int index = 0; index < sendMessageCount; index++) {
                final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, Integer.toString(index), "Hello! this is sample producer message:" + index);

                producer.send(record);
                System.out.printf("sent record(key=%s value=%s) \n",
                        record.key(), record.value());
//                if(index == 5)
//                    throw new KafkaException(); //To Demo Uncommitted messages
            }
            System.out.println("Debugger Point");
            producer.commitTransaction();

        } catch (KafkaException exception) {
            System.out.println("Transaction aborted");
            producer.abortTransaction();
        } finally {
            producer.flush();
            producer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        runProducer(10);
    }
}
