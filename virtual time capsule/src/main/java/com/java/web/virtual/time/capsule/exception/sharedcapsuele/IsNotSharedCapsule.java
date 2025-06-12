package com.java.web.virtual.time.capsule.exception.sharedcapsuele;

public class IsNotSharedCapsule extends RuntimeException {
    public IsNotSharedCapsule(String message) {
        super(message);
    }

    public IsNotSharedCapsule(String message, Throwable cause) {
        super(message, cause);
    }
}
