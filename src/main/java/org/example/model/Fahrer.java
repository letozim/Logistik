package org.example.model;

import java.time.LocalDate;

public class Fahrer {
    private Person person;
    private FahrerDetails fahrerDetails;

    // Konstruktoren
    public Fahrer() {
        this.person = new Person();
        this.fahrerDetails = new FahrerDetails();
        this.person.addRolle(PersonRolle.FAHRER);
    }

    public Fahrer(Person person, FahrerDetails fahrerDetails) {
        this.person = person;
        this.fahrerDetails = fahrerDetails;
        if (!person.hasRolle(PersonRolle.FAHRER)) {
            this.person.addRolle(PersonRolle.FAHRER);
        }
    }

    // Delegation zu Person
    public int getId() { return person.getId(); }
    public void setId(int id) {
        person.setId(id);
        fahrerDetails.setPersonId(id);
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

    // Delegation zu FahrerDetails
    public String getFuehrerscheinklasse() { return fahrerDetails.getFuehrerscheinklasse(); }
    public void setFuehrerscheinklasse(String fuehrerscheinklasse) {
        fahrerDetails.setFuehrerscheinklasse(fuehrerscheinklasse);
    }

    public String getFahrzeugtyp() { return fahrerDetails.getFahrzeugtyp(); }
    public void setFahrzeugtyp(String fahrzeugtyp) { fahrerDetails.setFahrzeugtyp(fahrzeugtyp); }

    public String getFuehrerscheinNummer() { return fahrerDetails.getFuehrerscheinNummer(); }
    public void setFuehrerscheinNummer(String fuehrerscheinNummer) {
        fahrerDetails.setFuehrerscheinNummer(fuehrerscheinNummer);
    }

    public LocalDate getFuehrerscheinAusgestelltAm() { return fahrerDetails.getFuehrerscheinAusgestelltAm(); }
    public void setFuehrerscheinAusgestelltAm(LocalDate fuehrerscheinAusgestelltAm) {
        fahrerDetails.setFuehrerscheinAusgestelltAm(fuehrerscheinAusgestelltAm);
    }

    public LocalDate getFuehrerscheinAblaufAm() { return fahrerDetails.getFuehrerscheinAblaufAm(); }
    public void setFuehrerscheinAblaufAm(LocalDate fuehrerscheinAblaufAm) {
        fahrerDetails.setFuehrerscheinAblaufAm(fuehrerscheinAblaufAm);
    }

    public LocalDate getMedizinischeUntersuchungAblauf() { return fahrerDetails.getMedizinischeUntersuchungAblauf(); }
    public void setMedizinischeUntersuchungAblauf(LocalDate medizinischeUntersuchungAblauf) {
        fahrerDetails.setMedizinischeUntersuchungAblauf(medizinischeUntersuchungAblauf);
    }

    public FahrerVerfuegbarkeit getVerfuegbarkeit() { return fahrerDetails.getVerfuegbarkeit(); }
    public void setVerfuegbarkeit(FahrerVerfuegbarkeit verfuegbarkeit) {
        fahrerDetails.setVerfuegbarkeit(verfuegbarkeit);
    }

    // Getter für die zusammengesetzten Objekte
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public FahrerDetails getFahrerDetails() { return fahrerDetails; }
    public void setFahrerDetails(FahrerDetails fahrerDetails) { this.fahrerDetails = fahrerDetails; }

    // Hilfsmethoden
    public boolean istVerfuegbar() {
        return fahrerDetails.getVerfuegbarkeit() == FahrerVerfuegbarkeit.VERFUEGBAR;
    }

    public boolean fuehrerscheinBaldAbgelaufen() {
        if (fahrerDetails.getFuehrerscheinAblaufAm() == null) return false;
        return fahrerDetails.getFuehrerscheinAblaufAm().isBefore(LocalDate.now().plusMonths(1));
    }
}