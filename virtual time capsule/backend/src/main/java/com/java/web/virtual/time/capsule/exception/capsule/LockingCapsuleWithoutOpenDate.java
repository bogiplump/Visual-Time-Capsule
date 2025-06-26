package com.java.web.virtual.time.capsule.exception.capsule;

public class LockingCapsuleWithoutOpenDate extends RuntimeException {
    public LockingCapsuleWithoutOpenDate(String message) {
        super(message);
    }

    public LockingCapsuleWithoutOpenDate(String message, Throwable cause) {
        super(message, cause);
    }
}
