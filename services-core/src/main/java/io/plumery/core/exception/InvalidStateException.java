package io.plumery.core.exception;

import io.plumery.core.ID;

public class InvalidStateException extends ApplicationException {
    public InvalidStateException(String message, ID aggregateRootId, int version) {
        super(message, aggregateRootId, version);
    }
}
