package org.example.model;

public class Fahrer extends Person {
    private String fuehrerscheinklasse;
    private String fahrzeugtyp;

    public Fahrer() {}

    public Fahrer(int id, String name, String adresse, String telefon, String email,
                  String fuehrerscheinklasse, String fahrzeugtyp) {
        super(id, name, adresse, telefon, email);
        this.fuehrerscheinklasse = fuehrerscheinklasse;
        this.fahrzeugtyp = fahrzeugtyp;
    }

    public Fahrer(int id, String name, String telefon, String fahrzeugtyp) {
        super(id, name, "", telefon, "");
        this.fuehrerscheinklasse = "";
        this.fahrzeugtyp = fahrzeugtyp;
    }

    public Fahrer(int id, String name, String adresse, String telefon, String email) {
        super(id, name, adresse, telefon, email);
        this.fuehrerscheinklasse = "";
        this.fahrzeugtyp = "";
    }


    public String getFuehrerscheinklasse() {
        return fuehrerscheinklasse;
    }

    public void setFuehrerscheinklasse(String fuehrerscheinklasse) {
        this.fuehrerscheinklasse = fuehrerscheinklasse;
    }

    public String getFahrzeugtyp() {
        return fahrzeugtyp;
    }

    public void setFahrzeugtyp(String fahrzeugtyp) {
        this.fahrzeugtyp = fahrzeugtyp;
    }
}
