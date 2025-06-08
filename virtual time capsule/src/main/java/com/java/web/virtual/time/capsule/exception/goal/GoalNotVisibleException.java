package com.java.web.virtual.time.capsule.exception.goal;

public class GoalNotVisibleException extends RuntimeException {
    public GoalNotVisibleException() {
        super("Goal not visible to user.");
    }
    public GoalNotVisibleException(final String message) {
        super(message);
    }
    public GoalNotVisibleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
