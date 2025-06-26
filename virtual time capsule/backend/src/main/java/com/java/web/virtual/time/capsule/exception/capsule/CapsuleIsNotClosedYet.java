package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleIsNotClosedYet extends CapsuleException {
    public CapsuleIsNotClosedYet(String message) {
        super(message);
    }

    public CapsuleIsNotClosedYet(String message, Throwable cause) {
        super(message, cause);
    }
}
