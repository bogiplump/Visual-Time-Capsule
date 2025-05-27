package com.java.web.virtual.time.capsule.exception;

public class CapsuleIsOpened extends RuntimeException {
    public CapsuleIsOpened(String message) {
        super(message);
    }

    public CapsuleIsOpened(String message, Throwable cause) {
        super(message, cause);
    }
}