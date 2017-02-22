package io.plumery.core.exception;

import io.plumery.core.ID;

public abstract class ApplicationException extends RuntimeException {
    private final int version;
    private ID aggregateRootId;
    private String action;

    public ApplicationException(String message,
                                ID aggregateRootId,
                                int version) {
        super(message);
        this.aggregateRootId = aggregateRootId;
        this.version = version;
    }

    public ID getAggregateRootId() {
        return aggregateRootId;
    }

    public int getVersion() {
        return version;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
