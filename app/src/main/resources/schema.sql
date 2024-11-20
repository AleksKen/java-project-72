DROP TABLE IF EXISTS Urls;

CREATE TABLE Urls (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS Url_checks;

CREATE TABLE Url_checks (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    url_id BIGINT REFERENCES Urls(id) NOT NULL,
    status_code INT NOT NULL,
    h1 VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);
