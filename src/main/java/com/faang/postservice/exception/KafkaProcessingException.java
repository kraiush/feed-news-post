package com.faang.postservice.exception;

public class KafkaProcessingException extends RuntimeException {

    public KafkaProcessingException(String message) {
        super(message);
    }
}
