package io.plumery.inventoryitem.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.plumery.messaging.CommandDispatcherFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InventoryItemApiConfiguration extends Configuration {
    @Valid
    @NotNull
    private CommandDispatcherFactory commandDispatcherFactory;

    @JsonProperty("commandDispatcher")
    public CommandDispatcherFactory getCommandDispatcherFactory() {
        return commandDispatcherFactory;
    }

    @JsonProperty("commandDispatcher")
    public void setCommandDispatcherFactory(CommandDispatcherFactory commandDispatcherFactory) {
        this.commandDispatcherFactory = commandDispatcherFactory;
    }
}
