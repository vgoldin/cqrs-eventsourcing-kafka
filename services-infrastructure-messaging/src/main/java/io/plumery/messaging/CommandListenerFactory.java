package io.plumery.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.CommandListener;
import io.plumery.messaging.kafka.KafkaCommandListener;
import org.hibernate.validator.constraints.NotEmpty;

public class CommandListenerFactory {
    @NotEmpty
    @JsonProperty
    private String zookeeper;

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public CommandListener build(Environment environment) {
        KafkaCommandListener listener =
                new KafkaCommandListener(zookeeper,
                        environment.getName(),
                        environment.getObjectMapper());

        environment.lifecycle().manage(listener);

        return listener;
    }
}
