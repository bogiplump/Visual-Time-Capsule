package com.java.web.virtual.time.capsule.exception;

public class InvitationAlreadySent extends RuntimeException {
    public InvitationAlreadySent() {

    }

    public InvitationAlreadySent(final String message) {
        super(message);
    }
    public InvitationAlreadySent(final String message, final Throwable cause) {
        super(message, cause);
    }
}
