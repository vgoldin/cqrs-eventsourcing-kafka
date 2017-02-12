package io.plumery.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaEventPublisher implements EventPublisher {
    private final KafkaProducer producer;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisher(String zookeeper, ObjectMapper objectMapper) {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        this.producer = new KafkaProducer<>(props);
        this.objectMapper = objectMapper;
    }

    @Override
    public <T extends Event> void publish(T event) {
        String topic = event.getClass().getSimpleName();
        String key = event.id.toString();
        String value = serializeEvent(event);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
    }

    private <T extends Event> String serializeEvent(T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
