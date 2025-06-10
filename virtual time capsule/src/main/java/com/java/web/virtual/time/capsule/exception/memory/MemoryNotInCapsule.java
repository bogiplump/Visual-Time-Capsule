package com.java.web.virtual.time.capsule.exception.memory;

public class MemoryNotInCapsule extends RuntimeException {
    public MemoryNotInCapsule() {
        super("Memory was not in this capsule");
    }

    public MemoryNotInCapsule(String message) {
        super(message);
    }

    public MemoryNotInCapsule(String message, Throwable cause) {
        super(message, cause);
    }
}
