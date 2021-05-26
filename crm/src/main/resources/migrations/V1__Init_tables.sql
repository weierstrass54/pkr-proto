CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE regions(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE qualifications(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE levels(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE assessments(
    id BIGSERIAL PRIMARY KEY,
    region_id INTEGER NOT NULL REFERENCES regions (id),
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    capacity INTEGER NOT NULL
);

CREATE TABLE schedule(
    id BIGSERIAL PRIMARY KEY,
    assessment_id BIGINT NOT NULL REFERENCES assessments (id),
    period TSTZRANGE NOT NULL,
    EXCLUDE USING GIST (assessment_id WITH =, period WITH &&)
);

CREATE TABLE users(
    id BIGINT PRIMARY KEY,
    login TEXT NOT NULL,
    first_name TEXT NOT NULL,
    middle_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL
);
