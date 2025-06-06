package com.java.web.virtual.time.capsule.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(){}

    public BadRequestException(final String message){
        super(message);
    }

    public BadRequestException(final String message, final Throwable cause){
        super(message, cause);
    }
}
