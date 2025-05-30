package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.*;
import org.example.repository.LieferantRepository;
import org.example.repository.WareRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class WareDialog {

    public static Optional<Ware> zeigeDialog(Stage owner, Ware ware) {
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
        TextField tfBezeichnung = new TextField();
        TextArea taBeschreibung = new TextArea();
        ComboBox<WareKategorie> cbKategorie = new ComboBox<>();
        ComboBox<WareEinheit> cbEinheit = new ComboBox<>();
        TextField tfEinkaufspreis = new TextField();
        TextField tfVerkaufspreis = new TextField();
        Spinner<Integer> spMindestbestand = new Spinner<>(0, 99999, 0);
        Spinner<Integer> spAktuellerBestand = new Spinner<>(0, 99999, 0);
        ComboBox<Lieferant> cbLieferant = new ComboBox<>();
        TextField tfLagerort = new TextField();
        Spinner<Integer> spLieferzeit = new Spinner<>(1, 365, 7);
        CheckBox chbAktiv = new CheckBox();

        // TextArea konfigurieren
        taBeschreibung.setPrefRowCount(3);
        taBeschreibung.setWrapText(true);

        // Spinner konfigurieren
        spMindestbestand.setEditable(true);
        spAktuellerBestand.setEditable(true);
        spLieferzeit.setEditable(true);

        // CheckBox Standard
        chbAktiv.setSelected(true);

        // ComboBox-Optionen laden
        try {
            WareRepository wareRepo = new WareRepository();
            List<WareKategorie> kategorien = wareRepo.findAllKategorien();
            cbKategorie.getItems().addAll(kategorien);

            LieferantRepository lieferantRepo = new LieferantRepository();
            List<Lieferant> lieferanten = lieferantRepo.findAll();
            cbLieferant.getItems().addAll(lieferanten);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der ComboBox-Daten: " + e.getMessage());
        }

        cbEinheit.getItems().addAll(WareEinheit.values());
        cbEinheit.setValue(WareEinheit.STUECK);

        // Custom CellFactory für Lieferanten-ComboBox
        cbLieferant.setCellFactory(param -> new ListCell<Lieferant>() {
            @Override
            protected void updateItem(Lieferant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getLieferantennummer() + ")");
                }
            }
        });

        cbLieferant.setButtonCell(new ListCell<Lieferant>() {
            @Override
            protected void updateItem(Lieferant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Felder vorausfüllen falls Ware bearbeitet wird
        if (ware != null) {
            tfArtikelnummer.setText(ware.getArtikelnummer());
            tfBezeichnung.setText(ware.getBezeichnung());
            taBeschreibung.setText(ware.getBeschreibung());
            cbKategorie.setValue(ware.getKategorie());
            cbEinheit.setValue(ware.getEinheit());

            if (ware.getEinkaufspreis() != null) {
                tfEinkaufspreis.setText(ware.getEinkaufspreis().toString());
            }
            if (ware.getVerkaufspreis() != null) {
                tfVerkaufspreis.setText(ware.getVerkaufspreis().toString());
            }

            spMindestbestand.getValueFactory().setValue(ware.getMindestbestand());
            spAktuellerBestand.getValueFactory().setValue(ware.getAktuellerBestand());

            // Lieferant auswählen
            if (ware.getLieferantId() > 0) {
                for (Lieferant l : cbLieferant.getItems()) {
                    if (l.getId() == ware.getLieferantId()) {
                        cbLieferant.setValue(l);
                        break;
                    }
                }
            }

            tfLagerort.setText(ware.getLagerort());
            spLieferzeit.getValueFactory().setValue(ware.getLieferzeitTage());
            chbAktiv.setSelected(ware.isAktiv());
        }

        // Layout
        int row = 0;
        grid.add(new Label("Artikelnummer:"), 0, row);
        grid.add(tfArtikelnummer, 1, row++);

        grid.add(new Label("Bezeichnung:"), 0, row);
        grid.add(tfBezeichnung, 1, row++);

        grid.add(new Label("Beschreibung:"), 0, row);
        grid.add(taBeschreibung, 1, row++);

        grid.add(new Label("Kategorie:"), 0, row);
        grid.add(cbKategorie, 1, row++);

        grid.add(new Label("Einheit:"), 0, row);
        grid.add(cbEinheit, 1, row++);

        grid.add(new Label("Einkaufspreis:"), 0, row);
        grid.add(tfEinkaufspreis, 1, row++);

        grid.add(new Label("Verkaufspreis:"), 0, row);
        grid.add(tfVerkaufspreis, 1, row++);

        grid.add(new Label("Mindestbestand:"), 0, row);
        grid.add(spMindestbestand, 1, row++);

        grid.add(new Label("Aktueller Bestand:"), 0, row);
        grid.add(spAktuellerBestand, 1, row++);

        grid.add(new Label("Lieferant:"), 0, row);
        grid.add(cbLieferant, 1, row++);

        grid.add(new Label("Lagerort:"), 0, row);
        grid.add(tfLagerort, 1, row++);

        grid.add(new Label("Lieferzeit (Tage):"), 0, row);
        grid.add(spLieferzeit, 1, row++);

        grid.add(new Label("Aktiv:"), 0, row);
        grid.add(chbAktiv, 1, row++);

        Button btnOk = new Button("OK");
        Button btnAbbrechen = new Button("Abbrechen");
        grid.add(btnOk, 0, row);
        grid.add(btnAbbrechen, 1, row);

        Scene scene = new Scene(grid, 500, 600);
        dialog.setScene(scene);

        AtomicReference<Ware> resultWare = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            // Validierung
            String artikelnummer = tfArtikelnummer.getText().trim();
            String bezeichnung = tfBezeichnung.getText().trim();
            String beschreibung = taBeschreibung.getText().trim();
            WareKategorie kategorie = cbKategorie.getValue();
            WareEinheit einheit = cbEinheit.getValue();
            String einkaufspreisStr = tfEinkaufspreis.getText().trim();
            String verkaufspreisStr = tfVerkaufspreis.getText().trim();
            int mindestbestand = spMindestbestand.getValue();
            int aktuellerBestand = spAktuellerBestand.getValue();
            Lieferant lieferant = cbLieferant.getValue();
            String lagerort = tfLagerort.getText().trim();
            int lieferzeit = spLieferzeit.getValue();
            boolean aktiv = chbAktiv.isSelected();

            if (artikelnummer.isEmpty()) {
                showAlert("Artikelnummer darf nicht leer sein.");
                return;
            }

            if (bezeichnung.isEmpty()) {
                showAlert("Bezeichnung darf nicht leer sein.");
                return;
            }

            if (einheit == null) {
                showAlert("Einheit muss ausgewählt werden.");
                return;
            }

            // Preise validieren
            BigDecimal einkaufspreis = BigDecimal.ZERO;
            BigDecimal verkaufspreis = BigDecimal.ZERO;

            try {
                if (!einkaufspreisStr.isEmpty()) {
                    einkaufspreis = new BigDecimal(einkaufspreisStr);
                    if (einkaufspreis.compareTo(BigDecimal.ZERO) < 0) {
                        showAlert("Einkaufspreis darf nicht negativ sein.");
                        return;
                    }
                }
            } catch (NumberFormatException ex) {
                showAlert("Einkaufspreis ist kein gültiger Betrag.");
                return;
            }

            try {
                if (!verkaufspreisStr.isEmpty()) {
                    verkaufspreis = new BigDecimal(verkaufspreisStr);
                    if (verkaufspreis.compareTo(BigDecimal.ZERO) < 0) {
                        showAlert("Verkaufspreis darf nicht negativ sein.");
                        return;
                    }
                }
            } catch (NumberFormatException ex) {
                showAlert("Verkaufspreis ist kein gültiger Betrag.");
                return;
            }

            // Ware-Objekt erstellen oder aktualisieren
            Ware neueWare = (ware == null) ? new Ware() : ware;
            neueWare.setArtikelnummer(artikelnummer);
            neueWare.setBezeichnung(bezeichnung);
            neueWare.setBeschreibung(beschreibung);
            neueWare.setKategorie(kategorie);
            neueWare.setEinheit(einheit);
            neueWare.setEinkaufspreis(einkaufspreis);
            neueWare.setVerkaufspreis(verkaufspreis);
            neueWare.setMindestbestand(mindestbestand);
            neueWare.setAktuellerBestand(aktuellerBestand);

            if (lieferant != null) {
                neueWare.setLieferantId(lieferant.getId());
                neueWare.setLieferantName(lieferant.getName());
            } else {
                neueWare.setLieferantId(0);
                neueWare.setLieferantName(null);
            }

            neueWare.setLagerort(lagerort);
            neueWare.setLieferzeitTage(lieferzeit);
            neueWare.setAktiv(aktiv);

            resultWare.set(neueWare);
            dialog.close();
        });

        btnAbbrechen.setOnAction(e -> {
            dialog.close();
        });

        dialog.showAndWait();

        return Optional.ofNullable(resultWare.get());
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehlerhafte Eingabe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}