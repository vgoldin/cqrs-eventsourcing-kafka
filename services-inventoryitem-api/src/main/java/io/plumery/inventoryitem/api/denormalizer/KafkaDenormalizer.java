package io.plumery.inventoryitem.api.denormalizer;

import io.dropwizard.lifecycle.Managed;
import io.plumery.inventoryitem.api.denormalizer.handler.InventoryItemCreatedHandler;
import io.plumery.inventoryitem.api.denormalizer.serialize.JsonDeserializer;
import io.plumery.inventoryitem.api.denormalizer.serialize.JsonSerializer;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.Predicate;

import java.util.Properties;

/**
 * Created by veniamin on 13/02/2017.
 */
public class KafkaDenormalizer implements Managed {
    private KafkaStreams kafkaStreams;

    @Override
    public void start() throws Exception {
        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        Serde<EventEnvelope> envelopeSerde = initializeEnvelopeSerde();

        Predicate<String, EventEnvelope> inventoryItemCreated = (k, v) -> k.equals(InventoryItemCreated.class.getSimpleName());
        Predicate<String, EventEnvelope> inventoryItemRenamed =  (k, v) -> k.equals("InventoryItemRenamed");
        Predicate<String, EventEnvelope> inventoryItemDeactivated = (k, v) -> k.equals("InventoryItemDeactivated");

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, EventEnvelope>[] filteredStreams = builder
                .stream(Serdes.String(), envelopeSerde, "InventoryItem")
                .selectKey((k, v) -> v.eventType)
                .branch(inventoryItemCreated, inventoryItemRenamed, inventoryItemDeactivated);

        filteredStreams[0].process(InventoryItemCreatedHandler::new);

        kafkaStreams = new KafkaStreams(builder, streamsConfig);
        kafkaStreams.cleanUp(); // -- only because we are using in-memory
        kafkaStreams.start();
    }

    @Override
    public void stop() throws Exception {
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "InventoryItemsDenormalizationApplication");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    private Serde<EventEnvelope> initializeEnvelopeSerde() {
        JsonSerializer<EventEnvelope> envelopeJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<EventEnvelope> envelopeJsonDeserializer = new JsonDeserializer<>(EventEnvelope.class);
        return Serdes.serdeFrom(envelopeJsonSerializer, envelopeJsonDeserializer);
    }
}
