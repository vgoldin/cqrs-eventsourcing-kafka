package io.plumery.eventstore.kafka;

import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventStore;

/**
 * Created by veniamin on 30/01/2017.
 */
public class KafkaEventStore implements EventStore {
    @Override
    public void saveEvents(String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        //TODO
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        //TODO
    }

    @Override
    public Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId) {
        //TODO

        return null;
    }
}
