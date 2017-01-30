package io.plumery.core.api.infrastructure;

import io.plumery.core.api.Event;

public interface EventPublisher {
    <T extends Event> void publish(T event);
    <T extends Event> void publish(String streamName, T event);
}