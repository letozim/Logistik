package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Lieferant;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class LieferantDialog {

    public static Optional<Lieferant> zeigeDialog(Stage owner, Lieferant lieferant) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(lieferant == null ? "Neuen Lieferanten anlegen" : "Lieferantendaten bearbeiten");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        TextField tfName = new TextField();
        TextField tfAdresse = new TextField();
        TextField tfTelefon = new TextField();
        TextField tfEmail = new TextField();
        ComboBox<String> cbUnternehmensform = new ComboBox<>();

        // ComboBox-Optionen setzen
        cbUnternehmensform.getItems().addAll("", "GmbH", "AG", "e.K.", "OHG", "KG", "GmbH & Co. KG",
                "UG (haftungsbeschränkt)", "Einzelunternehmen", "Freiberufler");

        if (lieferant != null) {
            tfName.setText(lieferant.getName());
            tfAdresse.setText(lieferant.getAdresse());
            tfTelefon.setText(lieferant.getTelefon());
            tfEmail.setText(lieferant.getEmail());
            cbUnternehmensform.setValue(lieferant.getUnternehmensform());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(tfAdresse, 1, 1);
        grid.add(new Label("Telefon:"), 0, 2);
        grid.add(tfTelefon, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(tfEmail, 1, 3);
        grid.add(new Label("Unternehmensform:"), 0, 4);
        grid.add(cbUnternehmensform, 1, 4);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, 5);
        grid.add(btnAbbrechen, 1, 5);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);

        AtomicReference<Lieferant> resultLieferant = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            String name = tfName.getText().trim();
            String adresse = tfAdresse.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String email = tfEmail.getText().trim();
            String unternehmensform = cbUnternehmensform.getValue();

            if (name.isEmpty()) {
                showAlert("Name darf nicht leer sein.");
                return;
            }

            // Lieferant-Objekt erstellen oder aktualisieren
            Lieferant neuerLieferant = (lieferant == null) ? new Lieferant() : lieferant;
            neuerLieferant.setName(name);
            neuerLieferant.setAdresse(adresse);
            neuerLieferant.setTelefon(telefon);
            neuerLieferant.setEmail(email);
            neuerLieferant.setUnternehmensform(unternehmensform != null ? unternehmensform : "");

            resultLieferant.set(neuerLieferant);
            dialog.close();
        });

        btnAbbrechen.setOnAction(e -> {
            dialog.close();
        });

        dialog.showAndWait();

        return Optional.ofNullable(resultLieferant.get());
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehlerhafte Eingabe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}