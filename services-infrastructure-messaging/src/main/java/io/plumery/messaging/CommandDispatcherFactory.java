package io.plumery.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.CommandDispatcher;
import org.hibernate.validator.constraints.NotEmpty;

public class CommandDispatcherFactory {
    @NotEmpty
    @JsonProperty
    private String zookeeper;

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public CommandDispatcher build(Environment environment) {
        //FIXME
        return null;
    }
}
