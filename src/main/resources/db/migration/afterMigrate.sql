CREATE OR REPLACE VIEW ubung.anzeige_view AS(
    SELECT p.benutzer,
            p.plan_von,
            p.plan_bis,
            z.stunde,
            z.datum
    FROM ubung.PLAN p
    JOIN ubung.ZEITEN z ON p.plan_von < z.datum AND p.plan_bis >= z.datum AND z.benutzer = p.benutzer

);

CREATE OR REPLACE VIEW ubung.plan_view AS (
    SELECT b.benutzername,
            x.projektename,
            p.*
    FROM ubung.PLAN p
    JOIN ubung.BENUTZER b ON p.benutzer = b.id
    JOIN ubung.PROJEKTE x ON p.projekt = x.id
);

CREATE OR REPLACE VIEW ubung.planzeit_view AS (
    SELECT p.projekt,
            p.plan_von,
            SUM(z.stunde) AS totalstunde
    FROM ubung.PLAN p
    JOIN ubung.ZEITEN z ON p.projekt = z.projekt AND p.plan_von < z.datum AND p.plan_bis >= z.datum
    GROUP BY p.projekt,
             p.plan_von
);


