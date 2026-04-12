package com.yemenptc.bss.sdk.event;

/**
 * Exception thrown when CloudEvent serialization fails.
 */
public class TmfEventException extends RuntimeException {
    public TmfEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
