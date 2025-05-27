package com.java.web.virtual.time.capsule.exception;

public class MemoryNotInCapsule extends RuntimeException {
    public MemoryNotInCapsule(String message) {
        super(message);
    }

    public MemoryNotInCapsule(String message, Throwable cause) {
        super(message, cause);
    }
}
