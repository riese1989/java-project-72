DROP TABLE IF EXISTS urls CASCADE;

CREATE TABLE urls (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS url_checks CASCADE;

CREATE TABLE url_checks (
    id SERIAL PRIMARY KEY,
    url_id INTEGER NOT NULL REFERENCES urls(id),
    status_code INTEGER NOT NULL,
    h1 TEXT,
    title TEXT,
    description TEXT,
    created_at TIMESTAMP NOT NULL
)
