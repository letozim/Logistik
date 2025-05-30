package org.example.model;

import java.time.LocalDateTime;

public class WareKategorie {
    private int kategorieId;
    private String kategorieName;
    private String beschreibung;
    private boolean aktiv;
    private LocalDateTime erstelltAm;

    // Konstruktoren
    public WareKategorie() {
        this.aktiv = true;
        this.erstelltAm = LocalDateTime.now();
    }

    public WareKategorie(int kategorieId, String kategorieName) {
        this();
        this.kategorieId = kategorieId;
        this.kategorieName = kategorieName;
    }

    public WareKategorie(int kategorieId, String kategorieName, String beschreibung) {
        this(kategorieId, kategorieName);
        this.beschreibung = beschreibung;
    }

    // Getter & Setter
    public int getKategorieId() { return kategorieId; }
    public void setKategorieId(int kategorieId) { this.kategorieId = kategorieId; }

    public String getKategorieName() { return kategorieName; }
    public void setKategorieName(String kategorieName) { this.kategorieName = kategorieName; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public boolean isAktiv() { return aktiv; }
    public void setAktiv(boolean aktiv) { this.aktiv = aktiv; }

    public LocalDateTime getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(LocalDateTime erstelltAm) { this.erstelltAm = erstelltAm; }

    @Override
    public String toString() {
        return kategorieName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WareKategorie that = (WareKategorie) obj;
        return kategorieId == that.kategorieId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(kategorieId);
    }
}