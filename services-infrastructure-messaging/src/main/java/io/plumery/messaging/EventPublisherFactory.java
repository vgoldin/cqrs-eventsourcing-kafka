package io.plumery.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.messaging.kafka.KafkaEventPublisher;
import io.plumery.messaging.local.LocalEventPublisher;
import org.hibernate.validator.constraints.NotEmpty;

public class EventPublisherFactory {
    @NotEmpty
    @JsonProperty
    private String zookeeper;

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public EventPublisher build(Environment environment) {
        return new KafkaEventPublisher(zookeeper, environment.getObjectMapper());
    }
}
