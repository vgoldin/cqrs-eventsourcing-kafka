package io.plumery.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaEventPublisher implements EventPublisher {
    private static Logger LOG = LoggerFactory.getLogger(KafkaEventPublisher.class);
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
    public <T extends Event> void publish(String streamName, T event) {
        LOG.debug("Publishing event ["+event.getClass().getSimpleName()+"] for [" + streamName + "]");

        String topic = streamName;
        String eventType = event.getClass().getSimpleName();

        EventEnvelope envelope = new EventEnvelope(eventType, event);
        String value = serializeEnvelope(envelope);

        // -- the key of the record is an aggregate Id to ensure the order of the events for the same aggregate
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.id.toString(), value);
        producer.send(record);
    }

    private String serializeEnvelope(EventEnvelope envelope) {
        String json;
        try {
            json = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
