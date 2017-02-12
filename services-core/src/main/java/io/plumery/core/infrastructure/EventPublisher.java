package io.plumery.core.infrastructure;

import io.plumery.core.Event;

public interface EventPublisher {
    <T extends Event> void publish(T event);
}