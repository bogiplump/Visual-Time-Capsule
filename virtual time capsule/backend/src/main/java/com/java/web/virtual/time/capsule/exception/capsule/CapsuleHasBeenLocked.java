package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleHasBeenLocked extends CapsuleException {
    public CapsuleHasBeenLocked(String message) {
        super(message);
    }

    public CapsuleHasBeenLocked(String message, Throwable cause) {
        super(message, cause);
    }
}