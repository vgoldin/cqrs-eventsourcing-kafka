package io.plumery.core.exception;

import io.plumery.core.Action;
import io.plumery.core.AggregateRoot;
import io.plumery.core.ID;

public class ApplicationException extends RuntimeException {
    private final Class<? extends Action> action;
    private ID aggregateRootId;
    private final Class<? extends AggregateRoot> aggregateRoot;

    public ApplicationException(String message,
                                ID aggregateRootId,
                                Class<? extends AggregateRoot> aggregateRoot,
                                Class<? extends Action> action) {
        super(message);
        this.aggregateRootId = aggregateRootId;
        this.aggregateRoot = aggregateRoot;
        this.action = action;
    }

    public Class<? extends AggregateRoot> getAggregateRoot() {
        return aggregateRoot;
    }

    public Class<? extends Action> getAction() {
        return action;
    }

    public ID getAggregateRootId() {
        return aggregateRootId;
    }
}
