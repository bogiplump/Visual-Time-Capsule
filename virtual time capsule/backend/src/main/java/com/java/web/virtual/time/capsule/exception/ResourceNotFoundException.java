package com.java.web.virtual.time.capsule.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }
    public ResourceNotFoundException(final String message) {
        super(message);
    }
    public ResourceNotFoundException(final String message, final Throwable cause) {}
}
