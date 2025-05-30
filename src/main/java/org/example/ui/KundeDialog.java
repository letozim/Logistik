package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Kunde;

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

        TextField tfName = new TextField();
        TextField tfAdresse = new TextField();
        TextField tfTelefon = new TextField();
        TextField tfEmail = new TextField();

        if (kunde != null) {
            tfName.setText(kunde.getName());
            tfAdresse.setText(kunde.getAdresse());
            tfTelefon.setText(kunde.getTelefon());
            tfEmail.setText(kunde.getEmail());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(tfAdresse, 1, 1);
        grid.add(new Label("Telefon:"), 0, 2);
        grid.add(tfTelefon, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(tfEmail, 1, 3);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, 4);
        grid.add(btnAbbrechen, 1, 4);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);

        // 💡 Funktionierender Rückgabewert
        AtomicReference<Kunde> resultKunde = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            String name = tfName.getText().trim();
            String adresse = tfAdresse.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String email = tfEmail.getText().trim();

            if (name.isEmpty()) {
                showAlert("Name darf nicht leer sein.");
                return;
            }

            // Kunde-Objekt erstellen oder aktualisieren
            Kunde neuerKunde = (kunde == null) ? new Kunde() : kunde;
            neuerKunde.setName(name);
            neuerKunde.setAdresse(adresse);
            neuerKunde.setTelefon(telefon);
            neuerKunde.setEmail(email);

            // Das Ergebnis speichern
            resultKunde.set(neuerKunde);

            // Dialog schließen
            dialog.close();
        });

        btnAbbrechen.setOnAction(e -> {
            dialog.close(); // Kein Setzen nötig – bleibt null
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
