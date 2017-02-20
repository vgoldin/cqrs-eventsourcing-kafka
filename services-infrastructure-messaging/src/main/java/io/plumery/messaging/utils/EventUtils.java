package io.plumery.messaging.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Event;
import io.plumery.core.ID;

public class EventUtils {
    public static Event exceptionToEvent(String aggregateId, Exception ex) {
        String message = ex.getMessage();

        return new ApplicationErrorEvent(message, aggregateId,
                ex.getClass().getName());
    }

    public static class ApplicationErrorEvent extends Event {
        public final String message;
        public final String exceptionType;

        @JsonCreator
        public ApplicationErrorEvent(
                @JsonProperty("message") String message,
                @JsonProperty("aggregateId") String aggregateId,
                @JsonProperty("exceptionType") String exceptionType) {
            this.message = message;
            this.id = ID.fromObject(aggregateId);
            this.exceptionType = exceptionType;
        }
    }
}
