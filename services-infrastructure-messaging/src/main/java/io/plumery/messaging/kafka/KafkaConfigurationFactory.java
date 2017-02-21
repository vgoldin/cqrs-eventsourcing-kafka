package io.plumery.messaging.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by veniamin on 21/02/2017.
 */
public class KafkaConfigurationFactory {
    @NotEmpty
    @JsonProperty
    private String bootstrap;

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getBootstrap() {
        return bootstrap;
    }
}
