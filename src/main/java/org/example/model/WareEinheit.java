package org.example.model;

public enum WareEinheit {
    STUECK("Stueck", "Stück"),
    KG("kg", "Kilogramm"),
    LITER("Liter", "Liter"),
    METER("Meter", "Meter"),
    QUADRATMETER("Quadratmeter", "Quadratmeter"),
    KUBIKMETER("Kubikmeter", "Kubikmeter"),
    TONNE("Tonne", "Tonne"),
    GRAMM("Gramm", "Gramm"),
    PAKET("Paket", "Paket");

    private final String dbWert;
    private final String anzeigeName;

    WareEinheit(String dbWert, String anzeigeName) {
        this.dbWert = dbWert;
        this.anzeigeName = anzeigeName;
    }

    public String getDbWert() {
        return dbWert;
    }

    public String getAnzeigeName() {
        return anzeigeName;
    }

    @Override
    public String toString() {
        return anzeigeName;
    }

    public static WareEinheit fromDbWert(String dbWert) {
        if (dbWert == null) return STUECK;
        for (WareEinheit e : values()) {
            if (e.dbWert.equals(dbWert)) {
                return e;
            }
        }
        return STUECK;
    }
}