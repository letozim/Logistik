package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Fahrer;

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

        TextField tfName = new TextField();
        TextField tfAdresse = new TextField();
        TextField tfTelefon = new TextField();
        TextField tfEmail = new TextField();
        ComboBox<String> cbFuehrerscheinklasse = new ComboBox<>();
        ComboBox<String> cbFahrzeugtyp = new ComboBox<>();

        // ComboBox-Optionen setzen
        cbFuehrerscheinklasse.getItems().addAll("", "B", "C", "C1", "CE", "C1E", "D", "D1", "DE", "D1E");
        cbFahrzeugtyp.getItems().addAll("", "PKW", "Transporter", "LKW", "Sattelzug", "Bus");

        if (fahrer != null) {
            tfName.setText(fahrer.getName());
            tfAdresse.setText(fahrer.getAdresse());
            tfTelefon.setText(fahrer.getTelefon());
            tfEmail.setText(fahrer.getEmail());
            cbFuehrerscheinklasse.setValue(fahrer.getFuehrerscheinklasse());
            cbFahrzeugtyp.setValue(fahrer.getFahrzeugtyp());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(tfAdresse, 1, 1);
        grid.add(new Label("Telefon:"), 0, 2);
        grid.add(tfTelefon, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(tfEmail, 1, 3);
        grid.add(new Label("Führerscheinklasse:"), 0, 4);
        grid.add(cbFuehrerscheinklasse, 1, 4);
        grid.add(new Label("Fahrzeugtyp:"), 0, 5);
        grid.add(cbFahrzeugtyp, 1, 5);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, 6);
        grid.add(btnAbbrechen, 1, 6);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);

        AtomicReference<Fahrer> resultFahrer = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            String name = tfName.getText().trim();
            String adresse = tfAdresse.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String email = tfEmail.getText().trim();
            String fuehrerscheinklasse = cbFuehrerscheinklasse.getValue();
            String fahrzeugtyp = cbFahrzeugtyp.getValue();

            if (name.isEmpty()) {
                showAlert("Name darf nicht leer sein.");
                return;
            }

            // Fahrer-Objekt erstellen oder aktualisieren
            Fahrer neuerFahrer = (fahrer == null) ? new Fahrer() : fahrer;
            neuerFahrer.setName(name);
            neuerFahrer.setAdresse(adresse);
            neuerFahrer.setTelefon(telefon);
            neuerFahrer.setEmail(email);
            neuerFahrer.setFuehrerscheinklasse(fuehrerscheinklasse != null ? fuehrerscheinklasse : "");
            neuerFahrer.setFahrzeugtyp(fahrzeugtyp != null ? fahrzeugtyp : "");

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