package io.plumery.eventstore.persistent;

import io.plumery.eventstore.EventDescriptor;

import java.util.List;
import java.util.UUID;

/**
 * Created by veniamin on 17/05/2017.
 */
public interface PersistentStorage {
    public Iterable<PersistedEvent> readEventsFor(UUID aggregateId);
    public void appendEventTo(UUID aggregateId, int expectedVersion, Iterable<PersistableEvent> events);

    boolean containsKey(String aggregateId);
}
