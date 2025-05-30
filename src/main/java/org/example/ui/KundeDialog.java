package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Kunde;
import org.example.model.PersonTyp;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class KundeDialog {

    public static Optional<Kunde> zeigeDialog(Stage owner, Kunde kunde) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(kunde == null ? "Neuen Kunden anlegen" : "Kundendaten bearbeiten");

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
        TextField tfBetreuenderMitarbeiter = new TextField();
        TextField tfKundennummer = new TextField();
        Spinner<Integer> spZahlungsziel = new Spinner<>(0, 365, 30);

        // ComboBox-Optionen setzen
        cbTyp.getItems().addAll(PersonTyp.values());
        cbTyp.setValue(PersonTyp.PRIVATPERSON);

        // Spinner konfigurieren
        spZahlungsziel.setEditable(true);

        // Felder vorausfüllen falls Kunde bearbeitet wird
        if (kunde != null) {
            tfName.setText(kunde.getName());
            tfAdresse.setText(kunde.getAdresse());
            tfTelefon.setText(kunde.getTelefon());
            tfEmail.setText(kunde.getEmail());
            cbTyp.setValue(kunde.getTyp());
            tfBetreuenderMitarbeiter.setText(kunde.getBetreuenderMitarbeiter());
            tfKundennummer.setText(kunde.getKundennummer());
            spZahlungsziel.getValueFactory().setValue(kunde.getZahlungsziel());
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

        grid.add(new Label("Kundennummer:"), 0, row);
        grid.add(tfKundennummer, 1, row++);

        grid.add(new Label("Betreuender Mitarbeiter:"), 0, row);
        grid.add(tfBetreuenderMitarbeiter, 1, row++);

        grid.add(new Label("Zahlungsziel (Tage):"), 0, row);
        grid.add(spZahlungsziel, 1, row++);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, row);
        grid.add(btnAbbrechen, 1, row);

        Scene scene = new Scene(grid, 400, 350);
        dialog.setScene(scene);

        AtomicReference<Kunde> resultKunde = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            String name = tfName.getText().trim();
            String adresse = tfAdresse.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String email = tfEmail.getText().trim();
            PersonTyp typ = cbTyp.getValue();
            String betreuenderMitarbeiter = tfBetreuenderMitarbeiter.getText().trim();
            String kundennummer = tfKundennummer.getText().trim();
            int zahlungsziel = spZahlungsziel.getValue();

            // Validierung
            if (name.isEmpty()) {
                showAlert("Name darf nicht leer sein.");
                return;
            }

            if (typ == null) {
                showAlert("Typ muss ausgewählt werden.");
                return;
            }

            // Kunde-Objekt erstellen oder aktualisieren
            Kunde neuerKunde = (kunde == null) ? new Kunde() : kunde;
            neuerKunde.setName(name);
            neuerKunde.setAdresse(adresse);
            neuerKunde.setTelefon(telefon);
            neuerKunde.setEmail(email);
            neuerKunde.setTyp(typ);
            neuerKunde.setBetreuenderMitarbeiter(betreuenderMitarbeiter);
            neuerKunde.setKundennummer(kundennummer);
            neuerKunde.setZahlungsziel(zahlungsziel);

            resultKunde.set(neuerKunde);
            dialog.close();
        });

        btnAbbrechen.setOnAction(e -> {
            dialog.close();
        });

        dialog.showAndWait();

        return Optional.ofNullable(resultKunde.get());
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehlerhafte Eingabe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

