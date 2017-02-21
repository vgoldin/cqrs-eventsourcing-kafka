package io.plumery.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.CommandDispatcher;
import io.plumery.messaging.kafka.KafkaCommandDispatcher;
import org.hibernate.validator.constraints.NotEmpty;

public class CommandDispatcherFactory {
    @NotEmpty
    @JsonProperty
    private String boostrap;

    public void setBoostrap(String boostrap) {
        this.boostrap = boostrap;
    }

    public String getBoostrap() {
        return boostrap;
    }

    public CommandDispatcher build(Environment environment) {
        return new KafkaCommandDispatcher(boostrap, environment.getObjectMapper());
    }
}
