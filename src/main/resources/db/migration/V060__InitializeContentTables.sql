CREATE TABLE post
(
    id                    BIGSERIAL PRIMARY KEY,
    title                 VARCHAR(144) NOT NULL,
    date                  TIMESTAMP NOT NULL,
    content               TEXT NOT NULL,
    pin_position          INT NOT NULL,
    hidden                BOOLEAN NOT NULL
);
