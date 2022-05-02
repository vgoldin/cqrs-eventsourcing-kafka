package io.plumery.inventoryitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.core.infrastructure.Repository;
import io.plumery.core.serializer.IDSerializer;
import io.plumery.inventoryitem.core.command.handler.*;
import io.plumery.inventoryitem.core.domain.InventoryItem;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import io.plumery.inventoryitem.core.infrastructure.InventoryItemRepository;
import io.plumery.messaging.ActionHandlerResolver;

public class InventoryItemDomain extends Application<InventoryItemDomainConfiguration> {
    public static void main(String[] args) throws Exception {
        new InventoryItemDomain().run(args);
    }

    @Override
    public void run(InventoryItemDomainConfiguration configuration, Environment environment) throws Exception {
        configureObjectMapper(environment);

        ActionHandlerResolver resolver = ActionHandlerResolver.newInstance();

        EventPublisher publisher = configuration.getEventPublisherFactory().build(environment);
        EventStore eventStore = configuration.getEventStoreFactory().build(environment, publisher, getDefaultEventsPackage());
        Repository<InventoryItem> repository = new InventoryItemRepository(eventStore);

        registerCommandHandlers(resolver, repository);

        configuration.getCommandListenerFactory().build(environment, publisher, InventoryItem.class);
    }

    private static void configureObjectMapper(Environment environment) {
        ObjectMapper mapper = environment.getObjectMapper();
        mapper.findAndRegisterModules();

        SimpleModule module = new SimpleModule();
        module.addSerializer(ID.class, new IDSerializer());
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private static void registerCommandHandlers(ActionHandlerResolver resolver, Repository<InventoryItem> repository) {
        resolver.registerActionHandler(new CheckInItemsToInventoryHandler(repository));
        resolver.registerActionHandler(new CreateInventoryItemHandler(repository));
        resolver.registerActionHandler(new DeactivateInventoryItemHandler(repository));
        resolver.registerActionHandler(new RemoveItemsFromInventoryHandler(repository));
        resolver.registerActionHandler(new RenameInventoryCommandHandler(repository));
    }

    private static String getDefaultEventsPackage() {
        return InventoryItemCreated.class.getPackage().getName();
    }
}
