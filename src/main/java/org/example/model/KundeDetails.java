package org.example.model;

import java.math.BigDecimal;

public class KundeDetails {
    private int personId;
    private String betreuenderMitarbeiter;
    private String kundennummer;
    private int zahlungsziel;
    private BigDecimal kreditlimit;
    private BigDecimal rabattProzent;

    // Konstruktoren
    public KundeDetails() {
        this.zahlungsziel = 30;
        this.kreditlimit = BigDecimal.ZERO;
        this.rabattProzent = BigDecimal.ZERO;
    }

    public KundeDetails(int personId, String betreuenderMitarbeiter, String kundennummer) {
        this();
        this.personId = personId;
        this.betreuenderMitarbeiter = betreuenderMitarbeiter;
        this.kundennummer = kundennummer;
    }

    // Getter & Setter
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public String getBetreuenderMitarbeiter() { return betreuenderMitarbeiter; }
    public void setBetreuenderMitarbeiter(String betreuenderMitarbeiter) {
        this.betreuenderMitarbeiter = betreuenderMitarbeiter;
    }

    public String getKundennummer() { return kundennummer; }
    public void setKundennummer(String kundennummer) { this.kundennummer = kundennummer; }

    public int getZahlungsziel() { return zahlungsziel; }
    public void setZahlungsziel(int zahlungsziel) { this.zahlungsziel = zahlungsziel; }

    public BigDecimal getKreditlimit() { return kreditlimit; }
    public void setKreditlimit(BigDecimal kreditlimit) { this.kreditlimit = kreditlimit; }

    public BigDecimal getRabattProzent() { return rabattProzent; }
    public void setRabattProzent(BigDecimal rabattProzent) { this.rabattProzent = rabattProzent; }
}