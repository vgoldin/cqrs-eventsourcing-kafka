package io.plumery;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.plumery.eventstore.EventStoreFactory;
import io.plumery.messaging.CommandListenerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InventoryItemDomainConfiguration extends Configuration {
    @Valid
    @NotNull
    private EventStoreFactory eventStoreFactory;

    @Valid
    @NotNull
    private CommandListenerFactory commandListenerFactory;

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
