package io.plumery.core.exception;

import io.plumery.core.ID;

public class InvalidArgumentException extends ApplicationException {
    public InvalidArgumentException(String message, ID aggregateRootId, int version) {
        super(message, aggregateRootId, version);
    }
}
