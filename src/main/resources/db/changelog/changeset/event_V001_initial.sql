CREATE TYPE event_type AS ENUM  (
    'CREATED',
    'PUBLISHED',
    'UPDATED',
    'COMMENTED',
    'COMMENT_UPDATED',
    'LIKED_POST',
    'LIKED_COMMENT',
    'VIEWED',
    'CANCELLED'
);

CREATE TABLE event (
    id            uuid    NOT NULL,
    event_type    varchar NOT NULL,
    event_payload varchar NOT NULL
);