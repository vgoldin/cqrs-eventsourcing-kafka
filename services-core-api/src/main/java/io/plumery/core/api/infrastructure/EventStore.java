package io.plumery.core.api.infrastructure;

import io.plumery.core.api.AggregateRoot;
import io.plumery.core.api.Event;

public interface EventStore {
    void saveEvents(String aggregateId, Iterable<? extends Event> events, int expectedVersion);
    void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion);
    Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId);
}
