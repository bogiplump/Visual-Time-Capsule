package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleException extends RuntimeException {
    public CapsuleException(String message) {
        super(message);
    }

    public CapsuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
