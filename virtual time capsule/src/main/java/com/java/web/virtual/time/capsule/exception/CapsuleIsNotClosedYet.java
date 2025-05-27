package com.java.web.virtual.time.capsule.exception;

public class CapsuleIsNotClosedYet extends RuntimeException {
    public CapsuleIsNotClosedYet(String message) {
        super(message);
    }

    public CapsuleIsNotClosedYet(String message, Throwable cause) {
        super(message, cause);
    }
}
