CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    login TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    roles TEXT[] NOT NULL
);
