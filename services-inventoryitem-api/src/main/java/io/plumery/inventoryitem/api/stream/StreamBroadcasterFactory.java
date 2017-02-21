package io.plumery.inventoryitem.api.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

public class StreamBroadcasterFactory {
    @NotEmpty
    @JsonProperty
    private String bootstrap;

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getBootstrap() {
        return bootstrap;
    }

    public StreamBroadcaster build(Environment environment) {
        StreamBroadcaster broadcaster =
                new KafkaTopicBroadcaster(environment.getName(),
                        environment.getObjectMapper(),
                        bootstrap);

        environment.lifecycle().manage(broadcaster);

        return broadcaster;
    }
}
