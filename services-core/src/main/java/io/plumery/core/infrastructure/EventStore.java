package io.plumery.core.infrastructure;

import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;

public interface EventStore {
    void saveEvents(String aggregateId, Iterable<? extends Event> events, int expectedVersion);
    void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion);
    Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId);
}
