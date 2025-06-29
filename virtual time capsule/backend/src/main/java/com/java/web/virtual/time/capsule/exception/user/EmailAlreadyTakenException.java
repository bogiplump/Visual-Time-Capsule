package com.java.web.virtual.time.capsule.exception.user;

public class EmailAlreadyTakenException extends RuntimeException{
    public EmailAlreadyTakenException() {
        super("Email already taken");
    }
    public EmailAlreadyTakenException(final String message) {
        super(message);
    }
    public EmailAlreadyTakenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
