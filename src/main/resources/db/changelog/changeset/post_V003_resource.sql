CREATE TABLE IF NOT EXISTS resource
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    key        VARCHAR(50) NOT NULL,
    name       VARCHAR(100),
    type       VARCHAR(50),
    size       BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    post_id    BIGINT      NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX resource_key_idx ON resource (key);
