CREATE TABLE ubung.PLAN (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    benutzer BIGINT REFERENCES ubung.BENUTZER,
    projekt BIGINT REFERENCES ubung.PROJEKTE,
    stunde INT,
    plan_von DATE,
    plan_bis DATE,
    erstelltAm TIMESTAMP,
    erstelltVon BIGINT REFERENCES ubung.BENUTZER,
    aktualisiertAm TIMESTAMP,
    aktualisiertVon BIGINT REFERENCES ubung.BENUTZER
);