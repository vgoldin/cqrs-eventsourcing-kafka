package io.plumery.eventstore;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.eventstore.kafka.KafkaEventStore;
import io.plumery.eventstore.local.LocalEventStore;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by veniamin on 10/02/2017.
 */
public class EventStoreFactory {
    private static Logger LOG = LoggerFactory.getLogger(EventStoreFactory.class);
    private static final String KAFKA = "kafka";
    private static final String LOCAL = "local";

    private String bootstrap;

    @JsonProperty
    private String type = LOCAL;

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }

    public String getBootstrap() {
        return bootstrap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EventStore build(Environment enviroment, EventPublisher publisher, String eventsPackage) {
        EventStore eventStore;

        if (type.equals(KAFKA)) {
            eventStore = new KafkaEventStore.Builder()
                    .withZookeeper(bootstrap)
                    .withGroupId(enviroment.getName())
                    .withObjectMapper(enviroment.getObjectMapper())
                    .withEventsPackage(eventsPackage)
                    .build();
        } else {
            eventStore = new LocalEventStore(publisher);
        }

        LOG.info("Configured EventStore ["+ eventStore.getClass().getSimpleName() +
                "] with Events Package [" + eventsPackage + "] and Publisher ["
                + publisher.getClass().getSimpleName() + "]");

        return eventStore;
    }
}
