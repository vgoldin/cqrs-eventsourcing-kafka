package io.plumery.eventstore.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.eventstore.serializer.IDSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.stream.StreamSupport;

/**
 * Created by veniamin on 30/01/2017.
 */
public class KafkaEventStore implements EventStore {
    private KafkaProducer<String, String> producer;
    private ObjectMapper objectMapper;
    private EventPublisher eventPublisher;

    public KafkaEventStore(String zookeeper, EventPublisher eventPublisher, ObjectMapper objectMapper) {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        this.producer = new KafkaProducer<>(props);
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        events.forEach(event -> {
            try {
                String json = objectMapper.writeValueAsString(event);
                ProducerRecord<String, String> record =
                        new ProducerRecord<>(streamName, aggregateId, json);
                producer.send(record);
                eventPublisher.publish(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId) {
        //TODO
        return null;
    }
}
