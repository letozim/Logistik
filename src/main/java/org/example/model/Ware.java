package org.example.model;

public class Ware {
    private String artikelnummer;
    private String name;
    private String beschreibung;
    private int menge;
    private String einheit;
 private  Double preis; // Optional, falls Preis benötigt wird

    public Ware(String artikelnummer, String name, String beschreibung, int menge, String einheit, Double preis) {
        this.artikelnummer = artikelnummer;
        this.name = name;
        this.beschreibung = beschreibung;
        this.menge = menge;
        this.einheit = einheit;
        this.preis = preis; // Standardwert, falls Preis nicht angegeben wird
    }

    public String getArtikelnummer() {
        return artikelnummer;
    }

    public void setArtikelnummer(String artikelnummer) {
        this.artikelnummer = artikelnummer;
    }
    public Double getPreis() {
        return preis;
    }
    public void setPreis(Double preis) {
        this.preis = preis;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public String getEinheit() {
        return einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }
}

