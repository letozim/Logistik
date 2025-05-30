package org.example.model;

public class Lieferant {
    private Person person;
    private LieferantDetails lieferantDetails;

    // Konstruktoren
    public Lieferant() {
        this.person = new Person();
        this.lieferantDetails = new LieferantDetails();
        this.person.addRolle(PersonRolle.LIEFERANT);
        this.person.setTyp(PersonTyp.UNTERNEHMEN); // Standard für Lieferanten
    }

    public Lieferant(Person person, LieferantDetails lieferantDetails) {
        this.person = person;
        this.lieferantDetails = lieferantDetails;
        if (!person.hasRolle(PersonRolle.LIEFERANT)) {
            this.person.addRolle(PersonRolle.LIEFERANT);
        }
    }

    // Delegation zu Person
    public int getId() { return person.getId(); }
    public void setId(int id) {
        person.setId(id);
        lieferantDetails.setPersonId(id);
    }

    public String getName() { return person.getName(); }
    public void setName(String name) { person.setName(name); }

    public String getAdresse() { return person.getAdresse(); }
    public void setAdresse(String adresse) { person.setAdresse(adresse); }

    public String getTelefon() { return person.getTelefon(); }
    public void setTelefon(String telefon) { person.setTelefon(telefon); }

    public String getEmail() { return person.getEmail(); }
    public void setEmail(String email) { person.setEmail(email); }

    public PersonTyp getTyp() { return person.getTyp(); }
    public void setTyp(PersonTyp typ) { person.setTyp(typ); }

    // Delegation zu LieferantDetails
    public String getUnternehmensform() { return lieferantDetails.getUnternehmensform(); }
    public void setUnternehmensform(String unternehmensform) {
        lieferantDetails.setUnternehmensform(unternehmensform);
    }

    public String getUstid() { return lieferantDetails.getUstid(); }
    public void setUstid(String ustid) { lieferantDetails.setUstid(ustid); }

    public String getLieferantennummer() { return lieferantDetails.getLieferantennummer(); }
    public void setLieferantennummer(String lieferantennummer) {
        lieferantDetails.setLieferantennummer(lieferantennummer);
    }

    public String getHandelsregisternummer() { return lieferantDetails.getHandelsregisternummer(); }
    public void setHandelsregisternummer(String handelsregisternummer) {
        lieferantDetails.setHandelsregisternummer(handelsregisternummer);
    }

    public LieferantBewertung getBewertung() { return lieferantDetails.getBewertung(); }
    public void setBewertung(LieferantBewertung bewertung) { lieferantDetails.setBewertung(bewertung); }

    public String getZahlungskonditionen() { return lieferantDetails.getZahlungskonditionen(); }
    public void setZahlungskonditionen(String zahlungskonditionen) {
        lieferantDetails.setZahlungskonditionen(zahlungskonditionen);
    }

    public String getHauptkategorie() { return lieferantDetails.getHauptkategorie(); }
    public void setHauptkategorie(String hauptkategorie) {
        lieferantDetails.setHauptkategorie(hauptkategorie);
    }

    // Getter für die zusammengesetzten Objekte
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public LieferantDetails getLieferantDetails() { return lieferantDetails; }
    public void setLieferantDetails(LieferantDetails lieferantDetails) {
        this.lieferantDetails = lieferantDetails;
    }
}