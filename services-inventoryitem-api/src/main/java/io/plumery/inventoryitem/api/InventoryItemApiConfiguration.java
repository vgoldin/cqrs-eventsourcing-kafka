package io.plumery.inventoryitem.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.plumery.inventoryitem.api.stream.StreamBroadcasterFactory;
import io.plumery.messaging.CommandDispatcherFactory;
import io.plumery.messaging.kafka.KafkaConfigurationFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InventoryItemApiConfiguration extends Configuration {
    @Valid
    @NotNull
    private KafkaConfigurationFactory kafkaConfigurationFactory;
    private CommandDispatcherFactory commandDispatcherFactory;
    private StreamBroadcasterFactory streamBroadcasterFactory;

    @JsonProperty("kafka")
    public void setKafkaConfigurationFactory(KafkaConfigurationFactory kafkaConfigurationFactory) {
        this.kafkaConfigurationFactory = kafkaConfigurationFactory;

        this.commandDispatcherFactory = new CommandDispatcherFactory();
        this.commandDispatcherFactory.setBoostrap(kafkaConfigurationFactory.getBootstrap());

        this.streamBroadcasterFactory = new StreamBroadcasterFactory();
        this.streamBroadcasterFactory.setBootstrap(kafkaConfigurationFactory.getBootstrap());
    }

    public CommandDispatcherFactory getCommandDispatcherFactory() {
        return commandDispatcherFactory;
    }

    public StreamBroadcasterFactory getStreamBroadcasterFactory() {
        return streamBroadcasterFactory;
    }
}
