package org.example.model;

public enum PersonTyp {
    UNTERNEHMEN("Unternehmen"),
    PRIVATPERSON("Privatperson");

    private final String dbWert;

    PersonTyp(String dbWert) {
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
    public static PersonTyp fromDbWert(String dbWert) {
        if (dbWert == null) return PRIVATPERSON;
        for (PersonTyp t : values()) {
            if (t.dbWert.equals(dbWert)) {
                return t;
            }
        }
        return PRIVATPERSON;
    }
}