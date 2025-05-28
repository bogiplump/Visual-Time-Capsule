package com.java.web.virtual.time.capsule.exception.memory;

public class MemoryNotFound extends RuntimeException {
    public MemoryNotFound(String message) {
        super(message);
    }

    public MemoryNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
