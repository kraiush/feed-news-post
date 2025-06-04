package com.faang.postservice.exception;

public class NotAvailableOperationException extends RuntimeException {

    public NotAvailableOperationException(String message) {
        super(message);
    }
}
