package com.java.web.virtual.time.capsule.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException() {
        super("Goal not found.");
    }
    public GoalNotFoundException(final String message) {
        super(message);
    }
    public GoalNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
