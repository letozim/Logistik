package org.example.model;

public class Lieferant extends Person {
    private String unternehmensform;

    public Lieferant() {}

    public Lieferant(int id, String name, String adresse, String telefon, String email, String unternehmensform) {
        super(id, name, adresse, telefon, email);
        this.unternehmensform = unternehmensform;
    }

    public Lieferant(int id, String name, String adresse, String telefon, String email) {
        super(id, name, adresse, telefon, email);
        this.unternehmensform = "";
    }

    public String getUnternehmensform() {
        return unternehmensform;
    }

    public void setUnternehmensform(String unternehmensform) {
        this.unternehmensform = unternehmensform;
    }
}
