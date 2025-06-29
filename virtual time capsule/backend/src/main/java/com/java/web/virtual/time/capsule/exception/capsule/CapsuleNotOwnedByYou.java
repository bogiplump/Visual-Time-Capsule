package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleNotOwnedByYou extends RuntimeException {
    public CapsuleNotOwnedByYou(String message) {
        super(message);
    }

    public CapsuleNotOwnedByYou(String message, Throwable cause) {
        super(message, cause);
    }
}
