package org.example.model;

public class Person {
    private int id;
    private String name;
    private String adresse;
    private String telefon;
    private String email;

    // Konstruktoren
    public Person() {}

    public Person(int id, String name, String adresse, String telefon, String email) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.telefon = telefon;
        this.email = email;
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
}
