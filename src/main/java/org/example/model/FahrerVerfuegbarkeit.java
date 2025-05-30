package org.example.model;

public enum FahrerVerfuegbarkeit {
    VERFUEGBAR("Verfuegbar", "Verfügbar"),
    NICHT_VERFUEGBAR("Nicht_verfuegbar", "Nicht verfügbar"),
    URLAUB("Urlaub", "Urlaub"),
    KRANK("Krank", "Krank");

    private final String dbWert;
    private final String anzeigeName;

    FahrerVerfuegbarkeit(String dbWert, String anzeigeName) {
        this.dbWert = dbWert;
        this.anzeigeName = anzeigeName;
    }

    public String getDbWert() {
        return dbWert;
    }

    public String getAnzeigeName() {
        return anzeigeName;
    }

    public String getBezeichnung() {
        return anzeigeName;
    }

    @Override
    public String toString() {
        return anzeigeName;
    }

    // Hilfsmethode für DB-Konvertierung
    public static FahrerVerfuegbarkeit fromDbWert(String dbWert) {
        if (dbWert == null) return VERFUEGBAR;
        for (FahrerVerfuegbarkeit v : values()) {
            if (v.dbWert.equals(dbWert)) {
                return v;
            }
        }
        return VERFUEGBAR;
    }
}