package io.plumery.inventoryitem.api.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaTopicBroadcaster extends StreamBroadcaster {
    private final static Logger LOG = LoggerFactory.getLogger(KafkaTopicBroadcaster.class);
    private final String APPLICATION_EVENT_STREAM = "InventoryItem.ApplicationEvents";
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer consumer;
    private final ObjectMapper objectMapper;

    public KafkaTopicBroadcaster(String name, ObjectMapper objectMapper, String zookeeper) {
        super();
        Properties props = new Properties();
        try {
            props.put("client.id", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException();
        }
        props.put("bootstrap.servers", zookeeper);
        props.put("group.id", name);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");

        this.consumer = new KafkaConsumer(props);
        this.objectMapper = objectMapper;
    }

    @Override
    public void start() throws Exception {
        new Thread(() -> {
            try {
                consumer.subscribe(Arrays.asList(APPLICATION_EVENT_STREAM));

                while (!closed.get()) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            EventEnvelope envelope =
                                    objectMapper.readValue(record.value(),
                                    EventEnvelope.class);
                            envelope.eventId = Optional.of(record.key());

                            super.setChanged();
                            super.notifyObservers(envelope);

                            consumer.commitSync();
                        } catch (Exception ex) {
                            LOG.error("Can not process record", ex);
                        }
                    }
                }
            } catch (WakeupException e) {
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
            }
        }).start();
    }

    @Override
    public void stop() throws Exception {
        closed.set(true);
        consumer.wakeup();
    }
}
