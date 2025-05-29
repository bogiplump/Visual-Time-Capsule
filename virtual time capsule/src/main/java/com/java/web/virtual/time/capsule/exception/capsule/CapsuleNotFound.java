package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleNotFound extends RuntimeException {
    public CapsuleNotFound() {
        super("Capsule with this id was not found");
    }

    public CapsuleNotFound(String message) {
        super(message);
    }

    public CapsuleNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
