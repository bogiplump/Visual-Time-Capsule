package com.java.web.virtual.time.capsule.exception.sharedcapsuele;

public class UserAlreadyInCapsule extends RuntimeException {
    public UserAlreadyInCapsule(String message) {
        super(message);
    }

    public UserAlreadyInCapsule(String message, Throwable cause) {
        super(message, cause);
    }
}
