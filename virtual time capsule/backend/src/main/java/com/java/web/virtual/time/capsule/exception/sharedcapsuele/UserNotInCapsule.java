package com.java.web.virtual.time.capsule.exception.sharedcapsuele;

public class UserNotInCapsule extends RuntimeException {
    public UserNotInCapsule(String message) {
        super(message);
    }

    public UserNotInCapsule(String message, Throwable cause) {
        super(message, cause);
    }
}
