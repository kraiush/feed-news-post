package com.faang.postservice.exception;

public class RedisPublishingException extends RuntimeException {

    public RedisPublishingException(String message) {
        super(message);
    }
}
