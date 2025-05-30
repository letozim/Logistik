package org.example.model;

public class LieferantDetails {
    private int personId;
    private String unternehmensform;
    private String ustid;
    private String lieferantennummer;
    private String handelsregisternummer;
    private LieferantBewertung bewertung;
    private String zahlungskonditionen;
    private String hauptkategorie;

    // Konstruktoren
    public LieferantDetails() {
        this.bewertung = LieferantBewertung.C;
    }

    public LieferantDetails(int personId, String unternehmensform) {
        this();
        this.personId = personId;
        this.unternehmensform = unternehmensform;
    }

    // Getter & Setter
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public String getUnternehmensform() { return unternehmensform; }
    public void setUnternehmensform(String unternehmensform) {
        this.unternehmensform = unternehmensform;
    }

    public String getUstid() { return ustid; }
    public void setUstid(String ustid) { this.ustid = ustid; }

    public String getLieferantennummer() { return lieferantennummer; }
    public void setLieferantennummer(String lieferantennummer) {
        this.lieferantennummer = lieferantennummer;
    }

    public String getHandelsregisternummer() { return handelsregisternummer; }
    public void setHandelsregisternummer(String handelsregisternummer) {
        this.handelsregisternummer = handelsregisternummer;
    }

    public LieferantBewertung getBewertung() { return bewertung; }
    public void setBewertung(LieferantBewertung bewertung) { this.bewertung = bewertung; }

    public String getZahlungskonditionen() { return zahlungskonditionen; }
    public void setZahlungskonditionen(String zahlungskonditionen) {
        this.zahlungskonditionen = zahlungskonditionen;
    }

    public String getHauptkategorie() { return hauptkategorie; }
    public void setHauptkategorie(String hauptkategorie) { this.hauptkategorie = hauptkategorie; }
}