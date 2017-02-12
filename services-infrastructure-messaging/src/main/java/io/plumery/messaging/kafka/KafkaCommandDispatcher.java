package io.plumery.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.plumery.core.Command;
import io.plumery.core.infrastructure.CommandDispatcher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaCommandDispatcher implements CommandDispatcher {
    private final KafkaProducer producer;
    private final ObjectMapper objectMapper;

    public KafkaCommandDispatcher(String zookeeper, ObjectMapper objectMapper) {
        Properties props = new Properties();
        props.put("bootstrap.servers", zookeeper);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        this.producer = new KafkaProducer<>(props);
        this.objectMapper = objectMapper;
    }

    private <T extends Command> String serializeCommand(T command) {
        String json;
        try {
            json = objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        String topic = command.getClass().getSimpleName();
        String key = command.id.toString();
        String value = serializeCommand(command);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
    }
}
