package org.example.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Person {
    private int id;
    private String name;
    private String adresse;
    private String telefon;
    private String email;
    private PersonTyp typ;
    private LocalDateTime erstelltAm;
    private boolean aktiv;
    private Set<PersonRolle> rollen;

    // Konstruktoren
    public Person() {
        this.typ = PersonTyp.PRIVATPERSON;
        this.aktiv = true;
        this.rollen = new HashSet<>();
        this.erstelltAm = LocalDateTime.now();
    }

    public Person(int id, String name, String adresse, String telefon, String email) {
        this();
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.telefon = telefon;
        this.email = email;
    }

    public Person(int id, String name, String adresse, String telefon, String email,
                  PersonTyp typ, LocalDateTime erstelltAm, boolean aktiv) {
        this(id, name, adresse, telefon, email);
        this.typ = typ;
        this.erstelltAm = erstelltAm;
        this.aktiv = aktiv;
    }

    // Rollen-Methoden
    public void addRolle(PersonRolle rolle) {
        this.rollen.add(rolle);
    }

    public void removeRolle(PersonRolle rolle) {
        this.rollen.remove(rolle);
    }

    public boolean hasRolle(PersonRolle rolle) {
        return this.rollen.contains(rolle);
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public PersonTyp getTyp() { return typ; }
    public void setTyp(PersonTyp typ) { this.typ = typ; }

    public LocalDateTime getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(LocalDateTime erstelltAm) { this.erstelltAm = erstelltAm; }

    public boolean isAktiv() { return aktiv; }
    public void setAktiv(boolean aktiv) { this.aktiv = aktiv; }

    public Set<PersonRolle> getRollen() { return new HashSet<>(rollen); }
    public void setRollen(Set<PersonRolle> rollen) { this.rollen = new HashSet<>(rollen); }
}