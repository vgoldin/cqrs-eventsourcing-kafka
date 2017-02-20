package io.plumery.inventoryitem.api.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class EventEnvelope {
    public final String eventType;
    public final Map eventData;
    public final LocalDateTime timestamp;

    @JsonIgnore
    public Optional<String> eventId;

    @JsonCreator
    public EventEnvelope(
            @JsonProperty("eventType") String eventType,
            @JsonProperty("eventData") Map eventData,
            @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }
}
