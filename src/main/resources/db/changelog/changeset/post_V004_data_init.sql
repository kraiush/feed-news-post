INSERT INTO post(content, author_id, published, published_at)
VALUES ('The story about Buratino', 911, true, current_timestamp),
       ('The theory of relativity', 933, false, null),
       ('Caramba', 933, true, current_timestamp),
       ('BarraCuda', 911, false, null),
       ('Ku-Ka-Re-Ku', 977, true, current_timestamp),
       ('KuKaRaCha', 955, true, current_timestamp),
       ('Crowing', 933, true, current_timestamp),
       ('Cinderella and Alice',911, true, current_timestamp);

INSERT INTO comment(content, author_id, post_id)
VALUES ('Stunningly', 933, 1),
       ('Terrible', 955, 1),
       ('Wonderful', 911, 5),
       ('Amazing!', 977, 3);

INSERT INTO hashtag(name)
VALUES ('#adventures_2021'),
       ('#Altai_2023'),
       ('#League_Champions_2025');

INSERT INTO post_hashtags(post_id, hashtag_id)
VALUES (1, 1),
       (1, 3),
       (3, 3),
       (7, 1),
       (7, 2);


