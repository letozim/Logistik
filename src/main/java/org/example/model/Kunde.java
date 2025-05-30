package org.example.model;

public class Kunde extends Person {
    private String betreuenderMitarbeiter;

    public Kunde() {
        super();
        this.betreuenderMitarbeiter = "";
    }

    public Kunde(int id, String name, String adresse, String telefon, String email) {
        super(id, name, adresse, telefon, email);
    }

    public Kunde(int id, String name, String adresse, String telefon, String email, String betreuenderMitarbeiter) {
        super(id, name, adresse, telefon, email);
        this.betreuenderMitarbeiter = betreuenderMitarbeiter;
    }

    public String getBetreuenderMitarbeiter() {
        return betreuenderMitarbeiter;
    }

    public void setBetreuenderMitarbeiter(String betreuenderMitarbeiter) {
        this.betreuenderMitarbeiter = betreuenderMitarbeiter;
    }
}
