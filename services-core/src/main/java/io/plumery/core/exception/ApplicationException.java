package io.plumery.core.exception;

import io.plumery.core.AggregateRoot;
import io.plumery.core.ID;

public class ApplicationException extends RuntimeException {
    private final int version;
    private ID aggregateRootId;
    private final Class<? extends AggregateRoot> aggregateRoot;

    public ApplicationException(String message,
                                ID aggregateRootId,
                                Class<? extends AggregateRoot> aggregateRoot,
                                int version) {
        super(message);
        this.aggregateRootId = aggregateRootId;
        this.aggregateRoot = aggregateRoot;
        this.version = version;
    }

    public Class<? extends AggregateRoot> getAggregateRoot() {
        return aggregateRoot;
    }

    public ID getAggregateRootId() {
        return aggregateRootId;
    }

    public int getVersion() {
        return version;
    }
}
