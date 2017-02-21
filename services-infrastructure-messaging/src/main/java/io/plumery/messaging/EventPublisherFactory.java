package io.plumery.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.messaging.kafka.KafkaEventPublisher;
import org.hibernate.validator.constraints.NotEmpty;

public class EventPublisherFactory {
    @NotEmpty
    @JsonProperty
    private String bootstrap;

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public EventPublisher build(Environment environment) {
        return new KafkaEventPublisher(bootstrap, environment.getObjectMapper());
    }
}
