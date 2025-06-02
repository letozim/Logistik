package org.example.model;

public enum AuftragPrioritaet {
    NIEDRIG("Niedrig", "#95a5a6"),
    NORMAL("Normal", "#3498db"),
    HOCH("Hoch", "#f39c12"),
    DRINGEND("Dringend", "#e74c3c");

    private final String bezeichnung;
    private final String farbe;

    AuftragPrioritaet(String bezeichnung, String farbe) {
        this.bezeichnung = bezeichnung;
        this.farbe = farbe;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getFarbe() {
        return farbe;
    }

    public String getDbWert() {
        return bezeichnung;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

    public static AuftragPrioritaet fromDbWert(String dbWert) {
        if (dbWert == null) return NORMAL;
        for (AuftragPrioritaet p : values()) {
            if (p.bezeichnung.equals(dbWert)) {
                return p;
            }
        }
        return NORMAL;
    }
}