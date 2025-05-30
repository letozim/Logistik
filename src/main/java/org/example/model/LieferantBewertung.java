package org.example.model;
public enum LieferantBewertung {
    A("A", "Sehr gut"),
    B("B", "Gut"),
    C("C", "Befriedigend"),
    D("D", "Ausreichend");

    private final String dbWert;
    private final String beschreibung;

    LieferantBewertung(String dbWert, String beschreibung) {
        this.dbWert = dbWert;
        this.beschreibung = beschreibung;
    }

    public String getDbWert() {
        return dbWert;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    @Override
    public String toString() {
        return dbWert + " - " + beschreibung;
    }

    // Hilfsmethode für DB-Konvertierung
    public static LieferantBewertung fromDbWert(String dbWert) {
        if (dbWert == null) return C;
        for (LieferantBewertung b : values()) {
            if (b.dbWert.equals(dbWert)) {
                return b;
            }
        }
        return C;
    }
}