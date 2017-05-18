package io.plumery.eventstore.persistent.jdbc;

import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcEventStore implements EventStore {
    private static Logger LOG = LoggerFactory.getLogger(JdbcEventStore.class);
    private EventPublisher eventPublisher;

    public JdbcEventStore(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {

    }

    @Override
    public Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId) {
        return null;
    }
}
