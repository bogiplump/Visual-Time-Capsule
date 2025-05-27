package com.java.web.virtual.time.capsule.exception;

public class CapsuleNotFound extends RuntimeException {
    public CapsuleNotFound(String message) {
        super(message);
    }

    public CapsuleNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
