package com.java.web.virtual.time.capsule.exception.capsule;

public class CapsuleIsOpened extends RuntimeException {
    public CapsuleIsOpened(String message) {
        super(message);
    }

    public CapsuleIsOpened(String message, Throwable cause) {
        super(message, cause);
    }
}