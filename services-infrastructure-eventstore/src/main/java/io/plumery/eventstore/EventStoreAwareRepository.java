package io.plumery.eventstore;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;
import io.plumery.core.ID;
import io.plumery.core.infrastructure.EventStore;
import io.plumery.core.infrastructure.Repository;
import io.plumery.eventstore.exception.AggregateNotFoundException;

import static io.plumery.eventstore.utils.ReflectionHelper.getParameterizedClass;
import static io.plumery.eventstore.utils.ReflectionHelper.instantiate;

/**
 * Generic Event Store aware Repository implementation
 */
public abstract class EventStoreAwareRepository<T extends AggregateRoot> implements Repository<T> {
    protected EventStore store;

    @Override
    public void save(T aggregate, int version) {
        Preconditions.checkState(aggregate.id != null, "There is no aggregate id. " +
                "Possible bug in events flow or subsequent aggregate operation without actual aggregate initiation was called.");
        store.saveEvents(aggregate.getClass().getSimpleName(), aggregate.id.toString(),
                aggregate.getUncommittedChanges(), version);

        aggregate.markChangesAsCommitted();
    }

    @Override
    public T getById(ID id) {
        T aggregate =  (T) instantiate(getParameterizedClass(getClass()));
        Iterable<? extends Event> events  = store.getEventsForAggregate(aggregate.getClass(), id.toString());
        if (events == null || Iterables.isEmpty(events)) {
            throw new AggregateNotFoundException(getParameterizedClass(getClass()).getName(), id);
        } else {
            aggregate.loadFromHistory(events);
            return aggregate;
        }
    }
}