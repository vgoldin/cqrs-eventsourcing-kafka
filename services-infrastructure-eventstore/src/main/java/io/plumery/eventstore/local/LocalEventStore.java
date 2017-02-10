package io.plumery.eventstore.local;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalEventStore implements EventStore {
    private final EventPublisher eventPublisher;
    private final Map<String, List<EventDescriptor>> storage = new ConcurrentHashMap<>();

    private final Function<EventDescriptor, Event> descriptorToEvent = new Function<EventDescriptor, Event>() {
        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public Event apply(@Nullable EventDescriptor input) {
            return input.event;
        }
    };

    public LocalEventStore(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        List<EventDescriptor> eventDescriptors;
        if (!storage.containsKey(aggregateId)) {
            eventDescriptors = Lists.newArrayList();

            storage.put(aggregateId, eventDescriptors);
        } else {
            eventDescriptors = storage.get(aggregateId);
            // TODO: check version
        }

        int version = expectedVersion;

        for (Event event : events) {
            version++;
            eventDescriptors.add(new EventDescriptor(aggregateId, event, version));
            eventPublisher.publish(streamName, event);
        }
    }

    @Override
    public Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId){
        if (storage.containsKey(aggregateId)) {
            return Iterables.transform(storage.get(aggregateId), descriptorToEvent);
        }

        return Lists.newArrayList();
    }

    private class EventDescriptor {
        private final String aggregateId;
        private final Event event;
        private final int version;

        private EventDescriptor(String aggregateId, Event event, int version) {
            this.aggregateId = aggregateId;
            this.event = event;
            this.version = version;
        }
    }
}