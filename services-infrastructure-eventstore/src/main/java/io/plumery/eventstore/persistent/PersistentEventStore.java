package io.plumery.eventstore.persistent;

import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PersistentEventStore implements EventStore {
    private static Logger LOG = LoggerFactory.getLogger(PersistentEventStore.class);
    private final PersistentStorage persistentStorage;
    private final EventPublisher eventPublisher;

    public PersistentEventStore(PersistentStorage persistentStorage, EventPublisher eventPublisher) {
        this.persistentStorage = persistentStorage;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        //TODO
    }
}
