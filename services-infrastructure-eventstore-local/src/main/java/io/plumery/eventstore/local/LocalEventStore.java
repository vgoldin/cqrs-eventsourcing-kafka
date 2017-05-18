package io.plumery.eventstore.local;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.plumery.core.AggregateRoot;
import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;
import io.plumery.core.infrastructure.EventStore;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

public class LocalEventStore implements EventStore {
    private static Logger LOG = LoggerFactory.getLogger(LocalEventStore.class);
    private static final String EVENTSTORE_MAP = "eventStore";
    private final EventPublisher eventPublisher;
    private final Map<String, List<EventDescriptor>> storage;
    private final RecordManager recMan;

    private final Function<EventDescriptor, Event> descriptorToEvent = new Function<EventDescriptor, Event>() {
        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public Event apply(@Nullable EventDescriptor input) {
            return input.event;
        }
    };

    public LocalEventStore(EventPublisher eventPublisher) {
        try {
            recMan = RecordManagerFactory.createRecordManager(LocalEventStore.class.getSimpleName());
            storage = recMan.treeMap(EVENTSTORE_MAP);

            addShutdownHook();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveEvents(String streamName, String aggregateId, Iterable<? extends Event> events, int expectedVersion) {
        LOG.debug("Saving events for [" + streamName + "] with Id [" + aggregateId + "]");
        List<EventDescriptor> eventDescriptors;
        if (!storage.containsKey(aggregateId)) {
            eventDescriptors = Lists.newArrayList();
        } else {
            eventDescriptors = storage.get(aggregateId);

            int actualVersion = eventDescriptors.get(eventDescriptors.size() - 1).version;
            if (actualVersion != expectedVersion && expectedVersion != -1) {
                throw new ConcurrentModificationException("The actual version is ["+actualVersion+"] and the expected version is ["+expectedVersion+"]");
            }
        }

        int version = expectedVersion;

        for (Event event : events) {
            version++;
            event.version = version;

            eventDescriptors.add(new EventDescriptor(aggregateId, event, version));
            eventPublisher.publish(streamName, event);
        }

        storage.put(aggregateId, eventDescriptors);
        try {
            recMan.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<? extends Event> getEventsForAggregate(Class<? extends AggregateRoot> aggregate, String aggregateId){
        if (storage.containsKey(aggregateId)) {
            return Iterables.transform(storage.get(aggregateId), descriptorToEvent);
        }

        return Lists.newArrayList();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                recMan.close();
            } catch (IOException e) {
            }
        }));
    }
}