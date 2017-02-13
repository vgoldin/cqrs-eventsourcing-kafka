package io.plumery;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Sets;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.plumery.core.ActionHandler;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.core.infrastructure.Repository;
import io.plumery.eventstore.serializer.IDSerializer;
import io.plumery.inventoryitem.core.command.handler.*;
import io.plumery.inventoryitem.core.domain.InventoryItem;
import io.plumery.inventoryitem.core.domain.event.InventoryItemCreated;
import io.plumery.inventoryitem.core.infrastructure.InventoryItemRepository;
import io.plumery.messaging.ActionHandlerResolver;

import java.util.Set;

public class InventoryItemDomain extends Application<InventoryItemDomainConfiguration> {
    public static void main(String[] args) throws Exception {
        new InventoryItemDomain().run(args);
    }

    @Override
    public void run(InventoryItemDomainConfiguration configuration, Environment environment) throws Exception {
        configureObjectMapper(environment);

        ActionHandlerResolver resolver = ActionHandlerResolver.newInstance();

        EventStore eventStore = configuration.getEventStoreFactory().build(environment, getDefaultEventsPackage());
        Repository<InventoryItem> repository = new InventoryItemRepository(eventStore);

        registerCommandHandlers(resolver, repository);

        configuration.getCommandListenerFactory().build(environment);
    }

    private static void configureObjectMapper(Environment environment) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ID.class, new IDSerializer());
        environment.getObjectMapper().registerModule(module);
    }

    private static void registerCommandHandlers(ActionHandlerResolver resolver, Repository<InventoryItem> repository) {
        Set<ActionHandler> commandHandlers = Sets.newHashSet(
                new CheckInItemsToIventoryHandler(repository),
                new CreateInventoryItemHandler(repository),
                new DeactivateInventoryItemHandler(repository),
                new RemoveItemsFromInventoryHandler(repository),
                new RenameInventoryCommandHandler(repository));

        resolver.setActionHandlers(commandHandlers);
    }

    private static String getDefaultEventsPackage() {
        return InventoryItemCreated.class.getPackage().getName();
    }
}
