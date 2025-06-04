package com.faang.postservice.exception;

public class NotAccessException extends RuntimeException {

    public NotAccessException(String message) {
        super(message);
    }
}
