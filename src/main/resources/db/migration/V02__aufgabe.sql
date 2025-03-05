CREATE TABLE ubung.ZEITEN (
    benutzer BIGINT REFERENCES ubung.BENUTZER,
    projekt BIGINT REFERENCES ubung.PROJEKTE,
    stunde INT,
    erstelltAm TIMESTAMP,
    aktualisiertAm TIMESTAMP,
    datum DATE
);