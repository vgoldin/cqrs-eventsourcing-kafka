package io.plumery.inventoryitem.api.denormalizer;

import io.dropwizard.lifecycle.Managed;
import io.plumery.inventoryitem.api.InventoryItemApiConfiguration;
import io.plumery.inventoryitem.api.denormalizer.handler.InventoryItemCreatedHandler;
import io.plumery.inventoryitem.api.denormalizer.handler.InventoryItemDeactivatedHandler;
import io.plumery.inventoryitem.api.denormalizer.handler.InventoryItemRenamedHandler;
import io.plumery.inventoryitem.api.core.EventEnvelope;
import io.plumery.inventoryitem.api.denormalizer.serialize.JsonDeserializer;
import io.plumery.inventoryitem.api.denormalizer.serialize.JsonSerializer;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import io.plumery.inventoryitem.core.events.InventoryItemDeactivated;
import io.plumery.inventoryitem.core.events.InventoryItemRenamed;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;

import java.util.Properties;

/**
 * Created by ben.goldin on 13/02/2017.
 */
public class KafkaDenormalizer implements Managed {
    private static final String INVENTORY_ITEM_TOPIC = "InventoryItem";
    private final String bootstrap;
    private KafkaStreams kafkaStreams;

    public KafkaDenormalizer(InventoryItemApiConfiguration configuration) {
        bootstrap = configuration.getCommandDispatcherFactory().getBoostrap();
    }

    @Override
    public void start() throws Exception {
        Predicate<String, EventEnvelope> inventoryItemCreated = (k, v) -> k.equals(InventoryItemCreated.class.getSimpleName());
        Predicate<String, EventEnvelope> inventoryItemRenamed =  (k, v) -> k.equals(InventoryItemRenamed.class.getSimpleName());
        Predicate<String, EventEnvelope> inventoryItemDeactivated = (k, v) -> k.equals(InventoryItemDeactivated.class.getSimpleName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, EventEnvelope>[] filteredStreams = builder
                .stream(INVENTORY_ITEM_TOPIC, Consumed.with(Serdes.String(), initializeEnvelopeSerde()))
                .selectKey((k, v) -> v.eventType)
                .branch(inventoryItemCreated, inventoryItemRenamed, inventoryItemDeactivated);

        filteredStreams[0].process(InventoryItemCreatedHandler::new);
        filteredStreams[1].process(InventoryItemRenamedHandler::new);
        filteredStreams[2].process(InventoryItemDeactivatedHandler::new);

        kafkaStreams = new KafkaStreams(builder.build(), getProperties());
        kafkaStreams.cleanUp(); // -- only because we are using in-memory
        kafkaStreams.start();
    }

    @Override
    public void stop() throws Exception {
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "InventoryItemsDenormalizationApplication");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    private Serde<EventEnvelope> initializeEnvelopeSerde() {
        JsonSerializer<EventEnvelope> envelopeJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<EventEnvelope> envelopeJsonDeserializer = new JsonDeserializer<>();
        return Serdes.serdeFrom(envelopeJsonSerializer, envelopeJsonDeserializer);
    }
}
