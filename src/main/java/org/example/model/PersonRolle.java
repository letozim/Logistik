package org.example.model;

public enum PersonRolle {
    KUNDE("Kunde"),
    LIEFERANT("Lieferant"),
    FAHRER("Fahrer"),
    MITARBEITER("Mitarbeiter");

    private final String dbWert;

    PersonRolle(String dbWert) {
        this.dbWert = dbWert;
    }

    public String getDbWert() {
        return dbWert;
    }

    public String getBezeichnung() {
        return dbWert;
    }

    @Override
    public String toString() {
        return dbWert;
    }

    // Hilfsmethode für DB-Konvertierung
    public static PersonRolle fromDbWert(String dbWert) {
        if (dbWert == null) return null;
        for (PersonRolle r : values()) {
            if (r.dbWert.equals(dbWert)) {
                return r;
            }
        }
        return null;
    }
}