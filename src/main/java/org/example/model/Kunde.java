package org.example.model;

public class Kunde {
    private Person person;
    private KundeDetails kundeDetails;

    // Konstruktoren
    public Kunde() {
        this.person = new Person();
        this.kundeDetails = new KundeDetails();
        this.person.addRolle(PersonRolle.KUNDE);
    }

    public Kunde(Person person, KundeDetails kundeDetails) {
        this.person = person;
        this.kundeDetails = kundeDetails;
        if (!person.hasRolle(PersonRolle.KUNDE)) {
            this.person.addRolle(PersonRolle.KUNDE);
        }
    }

    // Delegation zu Person
    public int getId() { return person.getId(); }
    public void setId(int id) {
        person.setId(id);
        kundeDetails.setPersonId(id);
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

    // Delegation zu KundeDetails
    public String getBetreuenderMitarbeiter() { return kundeDetails.getBetreuenderMitarbeiter(); }
    public void setBetreuenderMitarbeiter(String betreuenderMitarbeiter) {
        kundeDetails.setBetreuenderMitarbeiter(betreuenderMitarbeiter);
    }

    public String getKundennummer() { return kundeDetails.getKundennummer(); }
    public void setKundennummer(String kundennummer) { kundeDetails.setKundennummer(kundennummer); }

    public int getZahlungsziel() { return kundeDetails.getZahlungsziel(); }
    public void setZahlungsziel(int zahlungsziel) { kundeDetails.setZahlungsziel(zahlungsziel); }

    // Getter für die zusammengesetzten Objekte
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public KundeDetails getKundeDetails() { return kundeDetails; }
    public void setKundeDetails(KundeDetails kundeDetails) { this.kundeDetails = kundeDetails; }
}
