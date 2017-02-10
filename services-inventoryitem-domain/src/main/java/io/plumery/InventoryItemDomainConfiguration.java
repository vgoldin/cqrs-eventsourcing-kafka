package io.plumery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.MultimapBuilder;
import io.dropwizard.Configuration;
import io.plumery.eventstore.EventStoreFactory;
import io.plumery.messaging.CommandDispatcherFactory;
import io.plumery.messaging.CommandListenerFactory;
import io.plumery.messaging.EventPublisherFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InventoryItemDomainConfiguration extends Configuration {
    @Valid
    @NotNull
    private EventPublisherFactory eventPublisherFactory;

    @Valid
    @NotNull
    private EventStoreFactory eventStoreFactory;

    @Valid
    @NotNull
    private CommandListenerFactory commandListenerFactory;

    @JsonProperty("eventPublisher")
    public EventPublisherFactory getEventPublisherFactory() {
        return eventPublisherFactory;
    }

    @JsonProperty("eventPublisher")
    public void setEventPublisherFactory(EventPublisherFactory eventPublisherFactory) {
        this.eventPublisherFactory = eventPublisherFactory;
    }

    @JsonProperty("eventStore")
    public EventStoreFactory getEventStoreFactory() {
        return eventStoreFactory;
    }

    @JsonProperty("eventStore")
    public void setEventStoreFactory(EventStoreFactory eventStoreFactory) {
        this.eventStoreFactory = eventStoreFactory;
    }

    @JsonProperty("commandListener")
    public CommandListenerFactory getCommandListenerFactory() {
        return commandListenerFactory;
    }

    @JsonProperty("commandListener")
    public void setCommandListenerFactory(CommandListenerFactory commandListenerFactory) {
        this.commandListenerFactory = commandListenerFactory;
    }
}
