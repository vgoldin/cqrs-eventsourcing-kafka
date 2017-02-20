package io.plumery.inventoryitem.api.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

public class StreamBroadcasterFactory {
    @NotEmpty
    @JsonProperty
    private String zookeeper;

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public StreamBroadcaster build(Environment environment) {
        StreamBroadcaster broadcaster =
                new KafkaTopicBroadcaster(environment.getName(),
                        environment.getObjectMapper(),
                        zookeeper);

        environment.lifecycle().manage(broadcaster);

        return broadcaster;
    }
}
