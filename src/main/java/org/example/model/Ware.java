package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ware {
    private int wareId;
    private String artikelnummer;
    private String bezeichnung;
    private String beschreibung;
    private WareKategorie kategorie;
    private WareEinheit einheit;
    private BigDecimal einkaufspreis;
    private BigDecimal verkaufspreis;
    private int mindestbestand;
    private int aktuellerBestand;
    private int lieferantId;
    private String lieferantName; // Für Anzeige
    private String lagerort;
    private int lieferzeitTage;
    private boolean aktiv;
    private LocalDateTime erstelltAm;
    private LocalDateTime geaendertAm;

    // Konstruktoren
    public Ware() {
        this.einheit = WareEinheit.STUECK;
        this.einkaufspreis = BigDecimal.ZERO;
        this.verkaufspreis = BigDecimal.ZERO;
        this.mindestbestand = 0;
        this.aktuellerBestand = 0;
        this.lieferzeitTage = 7;
        this.aktiv = true;
        this.erstelltAm = LocalDateTime.now();
        this.geaendertAm = LocalDateTime.now();
    }

    public Ware(String artikelnummer, String bezeichnung) {
        this();
        this.artikelnummer = artikelnummer;
        this.bezeichnung = bezeichnung;
    }

    // Geschäftslogik-Methoden
    public boolean istUnterMindestbestand() {
        return aktuellerBestand < mindestbestand;
    }

    public BigDecimal gewinnMarge() {
        if (einkaufspreis.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return verkaufspreis.subtract(einkaufspreis);
    }

    public BigDecimal gewinnMargeInProzent() {
        if (einkaufspreis.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return gewinnMarge().divide(einkaufspreis, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public BigDecimal lagerwert() {
        return verkaufspreis.multiply(new BigDecimal(aktuellerBestand));
    }

    // Getter & Setter
    public int getWareId() { return wareId; }
    public void setWareId(int wareId) { this.wareId = wareId; }

    public String getArtikelnummer() { return artikelnummer; }
    public void setArtikelnummer(String artikelnummer) { this.artikelnummer = artikelnummer; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public WareKategorie getKategorie() { return kategorie; }
    public void setKategorie(WareKategorie kategorie) { this.kategorie = kategorie; }

    public WareEinheit getEinheit() { return einheit; }
    public void setEinheit(WareEinheit einheit) { this.einheit = einheit; }

    public BigDecimal getEinkaufspreis() { return einkaufspreis; }
    public void setEinkaufspreis(BigDecimal einkaufspreis) { this.einkaufspreis = einkaufspreis; }

    public BigDecimal getVerkaufspreis() { return verkaufspreis; }
    public void setVerkaufspreis(BigDecimal verkaufspreis) { this.verkaufspreis = verkaufspreis; }

    public int getMindestbestand() { return mindestbestand; }
    public void setMindestbestand(int mindestbestand) { this.mindestbestand = mindestbestand; }

    public int getAktuellerBestand() { return aktuellerBestand; }
    public void setAktuellerBestand(int aktuellerBestand) { this.aktuellerBestand = aktuellerBestand; }

    public int getLieferantId() { return lieferantId; }
    public void setLieferantId(int lieferantId) { this.lieferantId = lieferantId; }

    public String getLieferantName() { return lieferantName; }
    public void setLieferantName(String lieferantName) { this.lieferantName = lieferantName; }

    public String getLagerort() { return lagerort; }
    public void setLagerort(String lagerort) { this.lagerort = lagerort; }

    public int getLieferzeitTage() { return lieferzeitTage; }
    public void setLieferzeitTage(int lieferzeitTage) { this.lieferzeitTage = lieferzeitTage; }

    public boolean isAktiv() { return aktiv; }
    public void setAktiv(boolean aktiv) { this.aktiv = aktiv; }

    public LocalDateTime getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(LocalDateTime erstelltAm) { this.erstelltAm = erstelltAm; }

    public LocalDateTime getGeaendertAm() { return geaendertAm; }
    public void setGeaendertAm(LocalDateTime geaendertAm) { this.geaendertAm = geaendertAm; }
}