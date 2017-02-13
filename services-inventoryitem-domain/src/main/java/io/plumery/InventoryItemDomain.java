package io.plumery;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.core.infrastructure.Repository;
import io.plumery.eventstore.serializer.IDSerializer;
import io.plumery.inventoryitem.core.command.handler.*;
import io.plumery.inventoryitem.core.domain.InventoryItem;
import io.plumery.inventoryitem.core.domain.event.InventoryItemCreated;
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

        configuration.getCommandListenerFactory().build(environment);
    }

    private static void configureObjectMapper(Environment environment) {
        environment.getObjectMapper().findAndRegisterModules();

        SimpleModule module = new SimpleModule();
        module.addSerializer(ID.class, new IDSerializer());
        environment.getObjectMapper().registerModule(module);
    }

    private static void registerCommandHandlers(ActionHandlerResolver resolver, Repository<InventoryItem> repository) {
        resolver.registerActionHandler(new CheckInItemsToIventoryHandler(repository));
        resolver.registerActionHandler(new CreateInventoryItemHandler(repository));
        resolver.registerActionHandler(new DeactivateInventoryItemHandler(repository));
        resolver.registerActionHandler(new RemoveItemsFromInventoryHandler(repository));
        resolver.registerActionHandler(new RenameInventoryCommandHandler(repository));
    }

    private static String getDefaultEventsPackage() {
        return InventoryItemCreated.class.getPackage().getName();
    }
}
