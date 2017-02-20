package io.plumery.inventoryitem.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.plumery.inventoryitem.api.stream.StreamBroadcasterFactory;
import io.plumery.messaging.CommandDispatcherFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InventoryItemApiConfiguration extends Configuration {
    @Valid
    @NotNull
    private CommandDispatcherFactory commandDispatcherFactory;

    @Valid
    @NotNull
    private StreamBroadcasterFactory streamBroadcasterFactory;

    @JsonProperty("commandDispatcher")
    public CommandDispatcherFactory getCommandDispatcherFactory() {
        return commandDispatcherFactory;
    }

    @JsonProperty("commandDispatcher")
    public void setCommandDispatcherFactory(CommandDispatcherFactory commandDispatcherFactory) {
        this.commandDispatcherFactory = commandDispatcherFactory;
    }

    @JsonProperty("streamBroadcaster")
    public StreamBroadcasterFactory getStreamBroadcasterFactory() {
        return streamBroadcasterFactory;
    }

    @JsonProperty("streamBroadcaster")
    public void setStreamBroadcasterFactory(StreamBroadcasterFactory streamBroadcasterFactory) {
        this.streamBroadcasterFactory = streamBroadcasterFactory;
    }
}
