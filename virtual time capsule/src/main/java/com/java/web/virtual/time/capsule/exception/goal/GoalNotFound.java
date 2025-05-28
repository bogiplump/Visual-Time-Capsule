package com.java.web.virtual.time.capsule.exception.goal;

public class GoalNotFound extends RuntimeException {
    public GoalNotFound(String message) {
        super(message);
    }

    public GoalNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
