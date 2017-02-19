package io.plumery.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Event;

import java.io.Serializable;

public class EventDescriptor implements Serializable{
    public final String aggregateId;
    public final Event event;
    public final int version;

    @JsonCreator
    public EventDescriptor(
            @JsonProperty("aggregateId")  String aggregateId,
            @JsonProperty("event") Event event,
            @JsonProperty("version") int version) {
        this.aggregateId = aggregateId;
        this.event = event;
        this.version = version;
    }
}
