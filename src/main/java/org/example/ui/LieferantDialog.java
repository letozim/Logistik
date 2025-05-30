package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Lieferant;
import org.example.model.LieferantBewertung;
import org.example.model.PersonTyp;

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

        // Felder
        TextField tfName = new TextField();
        TextField tfAdresse = new TextField();
        TextField tfTelefon = new TextField();
        TextField tfEmail = new TextField();
        ComboBox<PersonTyp> cbTyp = new ComboBox<>();
        ComboBox<String> cbUnternehmensform = new ComboBox<>();
        TextField tfUstid = new TextField();
        TextField tfLieferantennummer = new TextField();
        TextField tfHandelsregister = new TextField();
        ComboBox<LieferantBewertung> cbBewertung = new ComboBox<>();
        TextField tfZahlungskonditionen = new TextField();
        ComboBox<String> cbHauptkategorie = new ComboBox<>();

        // ComboBox-Optionen setzen
        cbTyp.getItems().addAll(PersonTyp.values());
        cbTyp.setValue(PersonTyp.UNTERNEHMEN); // Standard für Lieferanten

        cbUnternehmensform.getItems().addAll("", "GmbH", "AG", "e.K.", "OHG", "KG", "GmbH & Co. KG",
                "UG (haftungsbeschränkt)", "Einzelunternehmen", "Freiberufler");

        cbBewertung.getItems().addAll(LieferantBewertung.values());
        cbBewertung.setValue(LieferantBewertung.C);

        cbHauptkategorie.getItems().addAll("", "Transport", "Logistik", "Technik", "Büromaterial",
                "Fahrzeuge", "Kraftstoffe", "IT-Services", "Reinigung", "Sonstiges");

        // Felder vorausfüllen falls Lieferant bearbeitet wird
        if (lieferant != null) {
            tfName.setText(lieferant.getName());
            tfAdresse.setText(lieferant.getAdresse());
            tfTelefon.setText(lieferant.getTelefon());
            tfEmail.setText(lieferant.getEmail());
            cbTyp.setValue(lieferant.getTyp());
            cbUnternehmensform.setValue(lieferant.getUnternehmensform());
            tfUstid.setText(lieferant.getUstid());
            tfLieferantennummer.setText(lieferant.getLieferantennummer());
            tfHandelsregister.setText(lieferant.getHandelsregisternummer());
            cbBewertung.setValue(lieferant.getBewertung());
            tfZahlungskonditionen.setText(lieferant.getZahlungskonditionen());
            cbHauptkategorie.setValue(lieferant.getHauptkategorie());
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

        grid.add(new Label("Unternehmensform:"), 0, row);
        grid.add(cbUnternehmensform, 1, row++);

        grid.add(new Label("USt-ID:"), 0, row);
        grid.add(tfUstid, 1, row++);

        grid.add(new Label("Lieferantennummer:"), 0, row);
        grid.add(tfLieferantennummer, 1, row++);

        grid.add(new Label("Handelsregister:"), 0, row);
        grid.add(tfHandelsregister, 1, row++);

        grid.add(new Label("Bewertung:"), 0, row);
        grid.add(cbBewertung, 1, row++);

        grid.add(new Label("Zahlungskonditionen:"), 0, row);
        grid.add(tfZahlungskonditionen, 1, row++);

        grid.add(new Label("Hauptkategorie:"), 0, row);
        grid.add(cbHauptkategorie, 1, row++);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, row);
        grid.add(btnAbbrechen, 1, row);

        Scene scene = new Scene(grid, 450, 500);
        dialog.setScene(scene);

        AtomicReference<Lieferant> resultLieferant = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            String name = tfName.getText().trim();
            String adresse = tfAdresse.getText().trim();
            String telefon = tfTelefon.getText().trim();
            String email = tfEmail.getText().trim();
            PersonTyp typ = cbTyp.getValue();
            String unternehmensform = cbUnternehmensform.getValue();
            String ustid = tfUstid.getText().trim();
            String lieferantennummer = tfLieferantennummer.getText().trim();
            String handelsregister = tfHandelsregister.getText().trim();
            LieferantBewertung bewertung = cbBewertung.getValue();
            String zahlungskonditionen = tfZahlungskonditionen.getText().trim();
            String hauptkategorie = cbHauptkategorie.getValue();

            // Validierung
            if (name.isEmpty()) {
                showAlert("Name darf nicht leer sein.");
                return;
            }

            if (typ == null) {
                showAlert("Typ muss ausgewählt werden.");
                return;
            }

            if (bewertung == null) {
                showAlert("Bewertung muss ausgewählt werden.");
                return;
            }

            // Lieferant-Objekt erstellen oder aktualisieren
            Lieferant neuerLieferant = (lieferant == null) ? new Lieferant() : lieferant;
            neuerLieferant.setName(name);
            neuerLieferant.setAdresse(adresse);
            neuerLieferant.setTelefon(telefon);
            neuerLieferant.setEmail(email);
            neuerLieferant.setTyp(typ);
            neuerLieferant.setUnternehmensform(unternehmensform != null ? unternehmensform : "");
            neuerLieferant.setUstid(ustid);
            neuerLieferant.setLieferantennummer(lieferantennummer);
            neuerLieferant.setHandelsregisternummer(handelsregister);
            neuerLieferant.setBewertung(bewertung);
            neuerLieferant.setZahlungskonditionen(zahlungskonditionen);
            neuerLieferant.setHauptkategorie(hauptkategorie != null ? hauptkategorie : "");

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