package com.java.web.virtual.time.capsule.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }
    public UserNotFoundException(final String message) {
        super(message);
    }
    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
