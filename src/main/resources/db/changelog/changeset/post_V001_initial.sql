CREATE TABLE post
(
    id            bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content       varchar(4096)             NOT NULL,
    author_id     bigint,
    project_id    bigint,
    published     boolean     DEFAULT false NOT NULL,
    published_at  timestamptz,
    scheduled_at  timestamptz,
    deleted       boolean     DEFAULT false NOT NULL,
    created_at    timestamptz DEFAULT current_timestamp,
    updated_at    timestamptz DEFAULT current_timestamp,
    corrected     boolean     DEFAULT false NOT NULL,
    verified_date timestamptz,
    verified      boolean     DEFAULT false NOT NULL
);

CREATE TABLE comment
(
    id            bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content       varchar(4096)             NOT NULL,
    author_id     bigint                    NOT NULL,
    post_id       bigint                    NOT NULL,
    created_at    timestamptz DEFAULT current_timestamp,
    updated_at    timestamptz DEFAULT current_timestamp,
    verified_date timestamptz,
    verified      boolean     DEFAULT false NOT NULL,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE likes
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id    bigint,
    comment_id bigint,
    user_id    bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES comment (id) ON DELETE CASCADE
);

CREATE TABLE album
(
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title       varchar(64)                     NOT NULL,
    description varchar(4096),
    author_id   bigint                          NOT NULL,
    visibility  varchar(50) DEFAULT 'ALL_USERS' NOT NULL,
    created_at  timestamptz DEFAULT current_timestamp,
    updated_at  timestamptz DEFAULT current_timestamp
);

CREATE TABLE post_album
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id    bigint NOT NULL,
    album_id   bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

CREATE TABLE favorite_albums
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    album_id   bigint NOT NULL,
    user_id    bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX album_author_title_idx ON album (author_id, title);

CREATE TABLE album_selected_users
(
    album_id         BIGINT NOT NULL,
    selected_user_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, selected_user_id),
    CONSTRAINT fk_album_selected_users_album FOREIGN KEY (album_id) REFERENCES album (id)
);

CREATE TABLE hashtag
(
    id   bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE post_hashtags
(
    post_id    bigint,
    hashtag_id bigint,
    PRIMARY KEY (post_id, hashtag_id),
    FOREIGN KEY (post_id) REFERENCES post (id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id),
    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);