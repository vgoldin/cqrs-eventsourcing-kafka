package io.plumery.core.exception;

public class SystemException extends RuntimeException {
    private final String errorEventId;

    public SystemException(String errorEventId, Exception ex) {
        super(ex);
        this.errorEventId = errorEventId;
    }

    public String getErrorEventId() {
        return errorEventId;
    }
}
