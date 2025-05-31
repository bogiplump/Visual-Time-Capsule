package com.java.web.virtual.time.capsule.exception.memory;

public class MemoryNotOwnedByYou extends RuntimeException {
    public MemoryNotOwnedByYou(String message) {
        super(message);
    }

    public MemoryNotOwnedByYou(String message, Throwable cause) {
        super(message, cause);
    }
}
