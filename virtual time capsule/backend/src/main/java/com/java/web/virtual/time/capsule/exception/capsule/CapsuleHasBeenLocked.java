package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleHasBeenLocked extends RuntimeException {
    public CapsuleHasBeenLocked(String message) {
        super(message);
    }

    public CapsuleHasBeenLocked(String message, Throwable cause) {
        super(message, cause);
    }
}