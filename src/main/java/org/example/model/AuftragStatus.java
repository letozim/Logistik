package org.example.model;

public enum AuftragStatus {
    NEU("Neu", "#3498db"),
    BESTAETIGT("Bestätigt", "#f39c12"),
    IN_BEARBEITUNG("In Bearbeitung", "#e67e22"),
    UNTERWEGS("Unterwegs", "#9b59b6"),
    GELIEFERT("Geliefert", "#27ae60"),
    ABGESCHLOSSEN("Abgeschlossen", "#2c3e50"),
    STORNIERT("Storniert", "#e74c3c");

    private final String bezeichnung;
    private final String farbe;

    AuftragStatus(String bezeichnung, String farbe) {
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
        return name().replace("_", "_");
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

    public static AuftragStatus fromDbWert(String dbWert) {
        if (dbWert == null) return NEU;
        try {
            return valueOf(dbWert.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return NEU;
        }
    }
}