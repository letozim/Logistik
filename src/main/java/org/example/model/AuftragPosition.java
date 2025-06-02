package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuftragPosition {
    private int positionId;
    private int auftragId;
    private int wareId;
    private String wareName; // Für Anzeige
    private String wareArtikelnummer; // Für Anzeige
    private int menge;
    private BigDecimal einzelpreis;
    private BigDecimal gesamtpreis;
    private String bemerkung;
    private LocalDateTime erstelltAm;

    // Konstruktoren
    public AuftragPosition() {
        this.menge = 1;
        this.einzelpreis = BigDecimal.ZERO;
        this.gesamtpreis = BigDecimal.ZERO;
        this.erstelltAm = LocalDateTime.now();
    }

    public AuftragPosition(int wareId, String wareName, int menge, BigDecimal einzelpreis) {
        this();
        this.wareId = wareId;
        this.wareName = wareName;
        this.menge = menge;
        this.einzelpreis = einzelpreis;
        this.gesamtpreis = einzelpreis.multiply(new BigDecimal(menge));
    }

    // Business Logic
    public void berechneGesamtpreis() {
        this.gesamtpreis = einzelpreis.multiply(new BigDecimal(menge));
    }

    // Getter & Setter
    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }

    public int getAuftragId() { return auftragId; }
    public void setAuftragId(int auftragId) { this.auftragId = auftragId; }

    public int getWareId() { return wareId; }
    public void setWareId(int wareId) { this.wareId = wareId; }

    public String getWareName() { return wareName; }
    public void setWareName(String wareName) { this.wareName = wareName; }

    public String getWareArtikelnummer() { return wareArtikelnummer; }
    public void setWareArtikelnummer(String wareArtikelnummer) {
        this.wareArtikelnummer = wareArtikelnummer;
    }

    public int getMenge() { return menge; }
    public void setMenge(int menge) {
        this.menge = menge;
        berechneGesamtpreis();
    }

    public BigDecimal getEinzelpreis() { return einzelpreis; }
    public void setEinzelpreis(BigDecimal einzelpreis) {
        this.einzelpreis = einzelpreis;
        berechneGesamtpreis();
    }

    public BigDecimal getGesamtpreis() { return gesamtpreis; }
    public void setGesamtpreis(BigDecimal gesamtpreis) { this.gesamtpreis = gesamtpreis; }

    public String getBemerkung() { return bemerkung; }
    public void setBemerkung(String bemerkung) { this.bemerkung = bemerkung; }

    public LocalDateTime getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(LocalDateTime erstelltAm) { this.erstelltAm = erstelltAm; }
}
