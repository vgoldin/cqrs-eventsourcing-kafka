package io.plumery.eventstore.jdbc.exception;

import io.plumery.core.ID;
import io.plumery.core.exception.ApplicationException;

public class ConcurrencyException extends ApplicationException {
    public ConcurrencyException(String message, ID aggregateRootId, int version) {
        super(message, aggregateRootId, version);
    }
}
