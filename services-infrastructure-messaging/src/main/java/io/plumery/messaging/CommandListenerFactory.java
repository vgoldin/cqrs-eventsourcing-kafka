package io.plumery.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.CommandListener;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.messaging.kafka.KafkaCommandListener;
import org.hibernate.validator.constraints.NotEmpty;

public class CommandListenerFactory {
    @NotEmpty
    @JsonProperty
    private String boostrap;

    public String getBoostrap() {
        return boostrap;
    }

    public void setBoostrap(String boostrap) {
        this.boostrap = boostrap;
    }

    public CommandListener build(Environment environment,
                                 EventPublisher applicationEventPublisher, Class aggregateRoot) {
        KafkaCommandListener listener =
                new KafkaCommandListener(boostrap,
                        environment.getName(),
                        environment.getObjectMapper(),
                        applicationEventPublisher,
                        aggregateRoot.getSimpleName());

        environment.lifecycle().manage(listener);

        return listener;
    }
}
