package com.java.web.virtual.time.capsule.exception;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException() {
        super("Could not find capsule group.");
    }
    public GroupNotFoundException(final String message) {
        super(message);
    }
    public GroupNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
