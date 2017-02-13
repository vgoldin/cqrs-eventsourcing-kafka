package io.plumery.messaging.kafka;

import io.plumery.core.Event;

import java.time.LocalDateTime;

/**
 * Created by veniamin on 13/02/2017.
 */
public class EventEnvelope {
    public final String eventType;
    public final Event eventData;
    public final LocalDateTime timestamp;

    public EventEnvelope(String eventType, Event eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = LocalDateTime.now();
    }
}
