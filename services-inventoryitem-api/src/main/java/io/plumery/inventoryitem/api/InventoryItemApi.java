package io.plumery.inventoryitem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.CommandDispatcher;
import io.plumery.core.serializer.IDSerializer;
import io.plumery.inventoryitem.api.denormalizer.KafkaDenormalizer;
import io.plumery.inventoryitem.api.denormalizer.hazelcast.HazelcastManaged;
import io.plumery.inventoryitem.api.query.InventoryItemsQuery;
import io.plumery.inventoryitem.api.resources.InventoryItemResource;

public class InventoryItemApi extends Application<InventoryItemApiConfiguration> {
    public static void main(String[] args) throws Exception {
        new InventoryItemApi().run(args);
    }

    @Override
    public void run(InventoryItemApiConfiguration configuration, Environment environment) throws Exception {
        configureObjectMapper(environment);

        CommandDispatcher commandDispatcher = configuration.getCommandDispatcherFactory().build(environment);

        environment.jersey().register(new InventoryItemResource(new InventoryItemsQuery(), commandDispatcher));
        environment.lifecycle().manage(new KafkaDenormalizer());
        environment.lifecycle().manage(new HazelcastManaged());
    }

    private static void configureObjectMapper(Environment environment) {
        ObjectMapper mapper = environment.getObjectMapper();
        mapper.findAndRegisterModules();

        SimpleModule module = new SimpleModule();
        module.addSerializer(ID.class, new IDSerializer());
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
