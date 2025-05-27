package com.java.web.virtual.time.capsule.exception;

public class MemoryNotInBank extends RuntimeException {
    public MemoryNotInBank(String message) {
        super(message);
    }

    public MemoryNotInBank(String message, Throwable cause) {
        super(message, cause);
    }
}
