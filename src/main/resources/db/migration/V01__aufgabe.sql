CREATE TABLE ubung.BENUTZER(
    id BIGSERIAL PRIMARY KEY,
    benutzername TEXT,
    erstelltAm TIMESTAMP,
    aktualisiertAm TIMESTAMP
);

CREATE TABLE ubung.PROJEKTE (
    id BIGSERIAL PRIMARY KEY,
    projektename TEXT,
    erstelltAm TIMESTAMP,
    aktualisiertAm TIMESTAMP
);