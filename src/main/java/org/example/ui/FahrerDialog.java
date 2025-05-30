package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Fahrer;
import org.example.model.FahrerVerfuegbarkeit;
import org.example.model.PersonTyp;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class FahrerDialog {

    public static Optional<Fahrer> zeigeDialog(Stage owner, Fahrer fahrer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(fahrer == null ? "Neuen Fahrer anlegen" : "Fahrerdaten bearbeiten");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Felder
        TextField tfName = new TextField();
        TextField tfAdresse = new TextField();
        TextField tfTelefon = new TextField();
        TextField tfEmail = new TextField();
        ComboBox<PersonTyp> cbTyp = new ComboBox<>();
        ComboBox<String> cbFuehrerscheinklasse = new ComboBox<>();
        ComboBox<String> cbFahrzeugtyp = new ComboBox<>();
        TextField tfFuehrerscheinNummer = new TextField();
        DatePicker dpAusgestellt = new DatePicker();
        DatePicker dpAblauf = new DatePicker();
        DatePicker dpMedUntersuchung = new DatePicker();
        ComboBox<FahrerVerfuegbarkeit> cbVerfuegbarkeit = new ComboBox<>();

        // ComboBox-Optionen setzen
        cbTyp.getItems().addAll(PersonTyp.values());
        cbTyp.setValue(PersonTyp.PRIVATPERSON);

        cbFuehrerscheinklasse.getItems().addAll("", "B", "C", "C1", "CE", "C1E", "D", "D1", "DE", "D1E");
        cbFahrzeugtyp.getItems().addAll("", "PKW", "Transporter", "LKW", "Sattelzug", "Bus");
        cbVerfuegbarkeit.getItems().addAll(FahrerVerfuegbarkeit.values());
        cbVerfuegbarkeit.setValue(FahrerVerfuegbarkeit.VERFUEGBAR);

        // Felder vorausfüllen falls Fahrer bearbeitet wird
        if (fahrer != null) {
            tfName.setText(fahrer.getName());
            tfAdresse.setText(fahrer.getAdresse());
            tfTelefon.setText(fahrer.getTelefon());
            tfEmail.setText(fahrer.getEmail());
            cbTyp.setValue(fahrer.getTyp());
            cbFuehrerscheinklasse.setValue(fahrer.getFuehrerscheinklasse());
            cbFahrzeugtyp.setValue(fahrer.getFahrzeugtyp());
            tfFuehrerscheinNummer.setText(fahrer.getFuehrerscheinNummer());
            dpAusgestellt.setValue(fahrer.getFuehrerscheinAusgestelltAm());
            dpAblauf.setValue(fahrer.getFuehrerscheinAblaufAm());
            dpMedUntersuchung.setValue(fahrer.getMedizinischeUntersuchungAblauf());
            cbVerfuegbarkeit.setValue(fahrer.getVerfuegbarkeit());
        }

        // Layout
        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(tfName, 1, row++);

        grid.add(new Label("Typ:"), 0, row);
        grid.add(cbTyp, 1, row++);

        grid.add(new Label("Adresse:"), 0, row);
        grid.add(tfAdresse, 1, row++);

        grid.add(new Label("Telefon:"), 0, row);
        grid.add(tfTelefon, 1, row++);

        grid.add(new Label("Email:"), 0, row);
        grid.add(tfEmail, 1, row++);

        grid.add(new Label("Führerscheinklasse:"), 0, row);
        grid.add(cbFuehrerscheinklasse, 1, row++);

        grid.add(new Label("Fahrzeugtyp:"), 0, row);
        grid.add(cbFahrzeugtyp, 1, row++);

        grid.add(new Label("Führerscheinnummer:"), 0, row);
        grid.add(tfFuehrerscheinNummer, 1, row++);

        grid.add(new Label("Ausgestellt am:"), 0, row);
        grid.add(dpAusgestellt, 1, row++);

        grid.add(new Label("Ablauf am:"), 0, row);
        grid.add(dpAblauf, 1, row++);

        grid.add(new Label("Med. Untersuchung bis:"), 0, row);
        grid.add(dpMedUntersuchung, 1, row++);

        grid.add(new Label("Verfügbarkeit:"), 0, row);
        grid.add(cbVerfuegbarkeit, 1, row++);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, row);
        grid.add(btnAbbrechen, 1, row);

        Scene scene = new Scene(grid, 450, 500);
        dialog.setScene(scene);

        AtomicReference<Fahrer> resultFahrer = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            String name = tfName.getText().trim();
            String adresse = tfAdresse.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String email = tfEmail.getText().trim();
            PersonTyp typ = cbTyp.getValue();
            String fuehrerscheinklasse = cbFuehrerscheinklasse.getValue();
            String fahrzeugtyp = cbFahrzeugtyp.getValue();
            String fuehrerscheinNummer = tfFuehrerscheinNummer.getText().trim();
            LocalDate ausgestellt = dpAusgestellt.getValue();
            LocalDate ablauf = dpAblauf.getValue();
            LocalDate medUntersuchung = dpMedUntersuchung.getValue();
            FahrerVerfuegbarkeit verfuegbarkeit = cbVerfuegbarkeit.getValue();

            // Validierung
            if (name.isEmpty()) {
                showAlert("Name darf nicht leer sein.");
                return;
            }

            if (typ == null) {
                showAlert("Typ muss ausgewählt werden.");
                return;
            }

            if (verfuegbarkeit == null) {
                showAlert("Verfügbarkeit muss ausgewählt werden.");
                return;
            }

            // Datum-Validierung
            if (ausgestellt != null && ablauf != null && ausgestellt.isAfter(ablauf)) {
                showAlert("Ausstellungsdatum kann nicht nach dem Ablaufdatum liegen.");
                return;
            }

            // Fahrer-Objekt erstellen oder aktualisieren
            Fahrer neuerFahrer = (fahrer == null) ? new Fahrer() : fahrer;
            neuerFahrer.setName(name);
            neuerFahrer.setAdresse(adresse);
            neuerFahrer.setTelefon(telefon);
            neuerFahrer.setEmail(email);
            neuerFahrer.setTyp(typ);
            neuerFahrer.setFuehrerscheinklasse(fuehrerscheinklasse != null ? fuehrerscheinklasse : "");
            neuerFahrer.setFahrzeugtyp(fahrzeugtyp != null ? fahrzeugtyp : "");
            neuerFahrer.setFuehrerscheinNummer(fuehrerscheinNummer);
            neuerFahrer.setFuehrerscheinAusgestelltAm(ausgestellt);
            neuerFahrer.setFuehrerscheinAblaufAm(ablauf);
            neuerFahrer.setMedizinischeUntersuchungAblauf(medUntersuchung);
            neuerFahrer.setVerfuegbarkeit(verfuegbarkeit);

            resultFahrer.set(neuerFahrer);
            dialog.close();
        });

        btnAbbrechen.setOnAction(e -> {
            dialog.close();
        });

        dialog.showAndWait();

        return Optional.ofNullable(resultFahrer.get());
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehlerhafte Eingabe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}