CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE regions(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE qualifications(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE levels(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE assessments(
    id SERIAL PRIMARY KEY,
    region_id INTEGER NOT NULL REFERENCES regions (id),
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    capacity INTEGER NOT NULL
);

CREATE TABLE exams(
    id SERIAL PRIMARY KEY,
    qualification_id INTEGER NOT NULL REFERENCES qualifications(id),
    level_id INTEGER NOT NULL REFERENCES levels(id),
    name TEXT
);

CREATE TABLE schedule(
    id BIGSERIAL PRIMARY KEY,
    assessment_id INTEGER NOT NULL REFERENCES assessments (id),
    exam_id INTEGER NOT NULL REFERENCES exams(id),
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
