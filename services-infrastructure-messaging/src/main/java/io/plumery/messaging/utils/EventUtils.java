package io.plumery.messaging.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.plumery.core.Event;
import io.plumery.core.ID;
import io.plumery.core.exception.ApplicationException;
import io.plumery.core.exception.SystemException;

public class EventUtils {
    public static Event exceptionToEvent(Exception ex) {
        String message = ex.getMessage();

        if (ex instanceof ApplicationException) {
            ApplicationException e = (ApplicationException) ex;
            String id = e.getAggregateRootId().toString();

            return new ApplicationErrorEvent(message, id, e.getVersion());
        } else if (ex instanceof SystemException) {
            SystemException e = (SystemException) ex;

            return new SystemErrorEvent(message, e.getErrorEventId(), ex.getClass().getName());
        }

        throw new IllegalArgumentException(ex.getClass().getSimpleName() + " is not supported.");
    }

    public static class ApplicationErrorEvent extends Event {
        public final String message;

        @JsonCreator
        public ApplicationErrorEvent(
                @JsonProperty("message") String message,
                @JsonProperty("aggregateId") String aggregateId,
                @JsonProperty("version") Integer version) {
            this.message = message;
            this.id = ID.fromObject(aggregateId);
            this.version = version;
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
