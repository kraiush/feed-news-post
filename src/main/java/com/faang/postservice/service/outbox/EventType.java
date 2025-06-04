package com.faang.postservice.service.outbox;

public enum EventType {
    CREATED,
    PUBLISHED,
    UPDATED,
    COMMENTED,
    COMMENT_UPDATED,
    LIKED_POST,
    LIKED_COMMENT,
    VIEWED,
    CANCELLED
}
