-- Create Movies Table
CREATE TABLE movies (
    id              UUID          PRIMARY KEY,
    title           VARCHAR(50)     NOT NULL,
    year            INTEGER         NOT NULL,
    director        Varchar(30)     NOT NULL,
    rating          DECIMAL
);

CREATE TABLE users (
    id              UUID       NOT NULL PRIMARY KEY,
    first_name      VARCHAR(40)     NOT NULL,
    last_name       VARCHAR(40)     NOT NULL,
    email           VARCHAR(40)     NOT NULL
);

CREATE TABLE moviecomments (
    id              UUID       NOT NULL PRIMARY KEY,
    movie_id        UUID       NOT NULL references movies(id),
    user_id         UUID       NOT NULL references users(id),
    comment         TEXT
);