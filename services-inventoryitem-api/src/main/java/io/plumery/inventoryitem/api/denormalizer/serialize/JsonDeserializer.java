package io.plumery.inventoryitem.api.denormalizer.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by veniamin on 18/02/2017.
 */
public class JsonDeserializer<T> implements Deserializer<T> {
    private final ObjectMapper mapper;
    private final Class<T> clazz;

    public JsonDeserializer(Class<T> eventEnvelopeClass) {
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules();
        this.clazz = eventEnvelopeClass;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }
}
