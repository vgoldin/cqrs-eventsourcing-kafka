package io.plumery.messaging.kafka;

import io.plumery.core.Event;
import io.plumery.core.infrastructure.EventPublisher;

public class KafkaEventPublisher implements EventPublisher {
    @Override
    public <T extends Event> void publish(String streamName, T event) {

    }
}
