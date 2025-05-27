package org.example.ui;

import org.example.model.Ware;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class WareDialog {

    public Optional<Ware> zeigeDialog(Stage owner, Ware ware) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(ware == null ? "Neue Ware anlegen" : "Ware bearbeiten");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Felder
        TextField tfArtikelnummer = new TextField();
        TextField tfName = new TextField();
        TextField tfBeschreibung = new TextField();
        TextField tfMenge = new TextField();
        TextField tfEinheit = new TextField();
        TextField tfPreis = new TextField();

        if (ware != null) {
            tfArtikelnummer.setText(ware.getArtikelnummer());
            tfName.setText(ware.getName());
            tfBeschreibung.setText(ware.getBeschreibung());
            tfMenge.setText(String.valueOf(ware.getMenge()));
            tfEinheit.setText(ware.getEinheit());
            tfPreis.setText(ware.getPreis() != null ? ware.getPreis().toString() : "");

            // Artikelnummer darf beim Bearbeiten NICHT geändert werden
            tfArtikelnummer.setDisable(true);
        }

        // Grid befüllen
        grid.add(new Label("Artikelnummer:"), 0, 0);
        grid.add(tfArtikelnummer, 1, 0);

        grid.add(new Label("Name:"), 0, 1);
        grid.add(tfName, 1, 1);

        grid.add(new Label("Beschreibung:"), 0, 2);
        grid.add(tfBeschreibung, 1, 2);

        grid.add(new Label("Menge:"), 0, 3);
        grid.add(tfMenge, 1, 3);

        grid.add(new Label("Einheit:"), 0, 4);
        grid.add(tfEinheit, 1, 4);

        grid.add(new Label("Preis:"), 0, 5);
        grid.add(tfPreis, 1, 5);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, 6);
        grid.add(btnAbbrechen, 1, 6);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);

        final Ware[] ergebnisWare = new Ware[1];

        btnOk.setOnAction(e -> {
            try {
                String artikelnummer = tfArtikelnummer.getText().trim();
                String name = tfName.getText().trim();
                String beschreibung = tfBeschreibung.getText().trim();
                int menge = Integer.parseInt(tfMenge.getText().trim());
                String einheit = tfEinheit.getText().trim();
                Double preis = tfPreis.getText().isEmpty() ? null : Double.parseDouble(tfPreis.getText().trim());

                if (artikelnummer.isEmpty() || name.isEmpty()) {
                    showAlert("Artikelnummer und Name dürfen nicht leer sein.");
                    return;
                }

                ergebnisWare[0] = new Ware(artikelnummer, name, beschreibung, menge, einheit, preis);
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert("Bitte gültige Zahl für Menge und Preis eingeben.");
            }
        });

        btnAbbrechen.setOnAction(e -> {
            ergebnisWare[0] = null;
            dialog.close();
        });

        dialog.showAndWait();

        return Optional.ofNullable(ergebnisWare[0]);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehlerhafte Eingabe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
