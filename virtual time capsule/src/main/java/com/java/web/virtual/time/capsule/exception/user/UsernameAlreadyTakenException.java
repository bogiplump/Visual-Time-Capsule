package com.java.web.virtual.time.capsule.exception.user;

public class UsernameAlreadyTakenException extends RuntimeException{
    public UsernameAlreadyTakenException() {
        super("Username is already taken");
    }
    public UsernameAlreadyTakenException(final String message) {
        super(message);
    }
    public UsernameAlreadyTakenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
