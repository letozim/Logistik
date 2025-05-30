package org.example.model;

import java.time.LocalDate;

public class FahrerDetails {
    private int personId;
    private String fuehrerscheinklasse;
    private String fahrzeugtyp;
    private String fuehrerscheinNummer;
    private LocalDate fuehrerscheinAusgestelltAm;
    private LocalDate fuehrerscheinAblaufAm;
    private LocalDate medizinischeUntersuchungAblauf;
    private FahrerVerfuegbarkeit verfuegbarkeit;

    // Konstruktoren
    public FahrerDetails() {
        this.verfuegbarkeit = FahrerVerfuegbarkeit.VERFUEGBAR;
    }

    public FahrerDetails(int personId, String fuehrerscheinklasse, String fahrzeugtyp) {
        this();
        this.personId = personId;
        this.fuehrerscheinklasse = fuehrerscheinklasse;
        this.fahrzeugtyp = fahrzeugtyp;
    }

    // Getter & Setter
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public String getFuehrerscheinklasse() { return fuehrerscheinklasse; }
    public void setFuehrerscheinklasse(String fuehrerscheinklasse) {
        this.fuehrerscheinklasse = fuehrerscheinklasse;
    }

    public String getFahrzeugtyp() { return fahrzeugtyp; }
    public void setFahrzeugtyp(String fahrzeugtyp) { this.fahrzeugtyp = fahrzeugtyp; }

    public String getFuehrerscheinNummer() { return fuehrerscheinNummer; }
    public void setFuehrerscheinNummer(String fuehrerscheinNummer) {
        this.fuehrerscheinNummer = fuehrerscheinNummer;
    }

    public LocalDate getFuehrerscheinAusgestelltAm() { return fuehrerscheinAusgestelltAm; }
    public void setFuehrerscheinAusgestelltAm(LocalDate fuehrerscheinAusgestelltAm) {
        this.fuehrerscheinAusgestelltAm = fuehrerscheinAusgestelltAm;
    }

    public LocalDate getFuehrerscheinAblaufAm() { return fuehrerscheinAblaufAm; }
    public void setFuehrerscheinAblaufAm(LocalDate fuehrerscheinAblaufAm) {
        this.fuehrerscheinAblaufAm = fuehrerscheinAblaufAm;
    }

    public LocalDate getMedizinischeUntersuchungAblauf() { return medizinischeUntersuchungAblauf; }
    public void setMedizinischeUntersuchungAblauf(LocalDate medizinischeUntersuchungAblauf) {
        this.medizinischeUntersuchungAblauf = medizinischeUntersuchungAblauf;
    }

    public FahrerVerfuegbarkeit getVerfuegbarkeit() { return verfuegbarkeit; }
    public void setVerfuegbarkeit(FahrerVerfuegbarkeit verfuegbarkeit) {
        this.verfuegbarkeit = verfuegbarkeit;
    }
}
