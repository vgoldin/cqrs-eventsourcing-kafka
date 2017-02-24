package io.plumery.inventoryitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.plumery.eventstore.EventStoreFactory;
import io.plumery.messaging.CommandListenerFactory;
import io.plumery.messaging.EventPublisherFactory;
import io.plumery.messaging.kafka.KafkaConfigurationFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InventoryItemDomainConfiguration extends Configuration {
    @Valid
    @NotNull
    private EventStoreFactory eventStoreFactory;
    private CommandListenerFactory commandListenerFactory;
    private EventPublisherFactory eventPublisher;

    @Valid
    @NotNull
    private KafkaConfigurationFactory kafkaConfigurationFactory;

    @JsonProperty("eventStore")
    public EventStoreFactory getEventStoreFactory() {
        return eventStoreFactory;
    }

    @JsonProperty("eventStore")
    public void setEventStoreFactory(EventStoreFactory eventStoreFactory) {
        this.eventStoreFactory = eventStoreFactory;
    }

    public CommandListenerFactory getCommandListenerFactory() {
        return commandListenerFactory;
    }
    public EventPublisherFactory getEventPublisherFactory() {
        return eventPublisher;
    }

    @JsonProperty("kafka")
    public void setKafkaConfigurationFactory(KafkaConfigurationFactory kafkaConfigurationFactory) {
        this.kafkaConfigurationFactory = kafkaConfigurationFactory;

        this.commandListenerFactory = new CommandListenerFactory();
        this.commandListenerFactory.setBoostrap(kafkaConfigurationFactory.getBootstrap());

        this.eventPublisher = new EventPublisherFactory();
        this.eventPublisher.setBootstrap(kafkaConfigurationFactory.getBootstrap());
    }
}
