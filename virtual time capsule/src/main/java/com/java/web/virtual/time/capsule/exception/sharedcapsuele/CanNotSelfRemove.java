package com.java.web.virtual.time.capsule.exception.sharedcapsuele;

public class CanNotSelfRemove extends RuntimeException {
    public CanNotSelfRemove(String message) {
        super(message);
    }

    public CanNotSelfRemove(String message, Throwable cause) {
        super(message, cause);
    }
}
