package io.plumery.eventstore.exception;

import io.plumery.core.ID;

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException(String name, ID id) {
        super(name + "; Id=" + id.toString());
    }
}
