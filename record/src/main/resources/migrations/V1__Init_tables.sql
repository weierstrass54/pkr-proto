CREATE TABLE users(
    id BIGINT PRIMARY KEY,
    login TEXT NOT NULL,
    first_name TEXT NOT NULL,
    middle_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL,
    employer TEXT,
    appointment TEXT
);

CREATE TABLE passport(
    user_id BIGINT REFERENCES users(id),
    series INTEGER NOT NULL,
    number INTEGER NOT NULL,
    issued_by TEXT NOT NULL,
    issued_at DATE NOT NULL
);
