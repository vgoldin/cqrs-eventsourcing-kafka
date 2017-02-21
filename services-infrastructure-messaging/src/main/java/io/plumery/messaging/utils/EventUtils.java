package io.plumery.messaging.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Event;
import io.plumery.core.ID;
import io.plumery.core.exception.ApplicationException;

public class EventUtils {
    public static Event exceptionToEvent(String aggregateId, Exception ex) {
        String message = ex.getMessage();

        if (ex instanceof ApplicationException) {
            ApplicationException e = (ApplicationException) ex;

            return new ApplicationErrorEvent(
                    message, aggregateId,
                    e.getAction().getSimpleName());
        } else {
            return new SystemErrorEvent(message, aggregateId,
                    ex.getClass().getName());
        }

    }

    public static class ApplicationErrorEvent extends Event {
        public final String message;
        public final String action;

        @JsonCreator
        public ApplicationErrorEvent(
                @JsonProperty("message") String message,
                @JsonProperty("aggregateId") String aggregateId,
                @JsonProperty("action") String action) {
            this.message = message;
            this.id = ID.fromObject(aggregateId);
            this.action = action;
        }
    }

    public static class SystemErrorEvent extends Event {
        public final String message;
        public final String exceptionType;

        @JsonCreator
        public SystemErrorEvent(
                @JsonProperty("message") String message,
                @JsonProperty("errorEventId") String errorEventId,
                @JsonProperty("exceptionType") String exceptionType) {
            this.message = message;
            this.id = ID.fromObject(errorEventId);
            this.exceptionType = exceptionType;
        }
    }
}
