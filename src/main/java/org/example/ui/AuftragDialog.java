package org.example.ui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.BigDecimalStringConverter;

import org.example.model.*;
import org.example.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AuftragDialog {

    private static KundeRepository kundeRepository = new KundeRepository();
    private static FahrerRepository fahrerRepository = new FahrerRepository();
    private static WareRepository wareRepository = new WareRepository();

    public static Optional<Auftrag> zeigeDialog(Stage owner, Auftrag auftrag) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(auftrag == null ? "Neuen Auftrag erstellen" : "Auftrag bearbeiten");

        TabPane tabPane = new TabPane();

        // Tab 1: Grunddaten
        Tab tabGrunddaten = new Tab("Grunddaten");
        tabGrunddaten.setClosable(false);
        VBox grunddatenBox = createGrunddatenTab(auftrag);
        tabGrunddaten.setContent(grunddatenBox);

        // Tab 2: Positionen
        Tab tabPositionen = new Tab("Positionen");
        tabPositionen.setClosable(false);
        VBox positionenBox = createPositionenTab(auftrag);
        tabPositionen.setContent(positionenBox);

        tabPane.getTabs().addAll(tabGrunddaten, tabPositionen);

        // Button-Leiste
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        Button btnOk = new Button("Speichern");
        Button btnAbbrechen = new Button("Abbrechen");
        buttonBox.getChildren().addAll(btnAbbrechen, btnOk);

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(tabPane, buttonBox);

        Scene scene = new Scene(mainBox, 800, 600);
        dialog.setScene(scene);

        AtomicReference<Auftrag> resultAuftrag = new AtomicReference<>(null);

        btnOk.setOnAction(e -> {
            try {
                Auftrag neuerAuftrag = collectAuftragData(grunddatenBox, positionenBox, auftrag);
                if (validateAuftrag(neuerAuftrag)) {
                    resultAuftrag.set(neuerAuftrag);
                    dialog.close();
                }
            } catch (Exception ex) {
                showAlert("Fehler beim Speichern: " + ex.getMessage());
            }
        });

        btnAbbrechen.setOnAction(e -> dialog.close());

        dialog.showAndWait();
        return Optional.ofNullable(resultAuftrag.get());
    }

    private static VBox createGrunddatenTab(Auftrag auftrag) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        // Überschrift
        Label titel = new Label("Auftragsdaten");
        titel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Kunde (Pflicht)
        Label lblKunde = new Label("Kunde:*");
        ComboBox<Kunde> cbKunde = new ComboBox<>();
        cbKunde.setId("cbKunde");
        cbKunde.setPrefWidth(300);

        try {
            List<Kunde> kunden = kundeRepository.findAll();
            cbKunde.getItems().addAll(kunden);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Kunden: " + e.getMessage());
        }

        cbKunde.setCellFactory(param -> new ListCell<Kunde>() {
            @Override
            protected void updateItem(Kunde item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getEmail() + ")");
                }
            }
        });

        cbKunde.setButtonCell(new ListCell<Kunde>() {
            @Override
            protected void updateItem(Kunde item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Fahrer (Optional)
        Label lblFahrer = new Label("Fahrer:");
        ComboBox<Fahrer> cbFahrer = new ComboBox<>();
        cbFahrer.setId("cbFahrer");
        cbFahrer.setPrefWidth(300);

        try {
            List<Fahrer> fahrer = fahrerRepository.findVerfuegbareFahrer();
            cbFahrer.getItems().addAll(fahrer);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Fahrer: " + e.getMessage());
        }

        cbFahrer.setCellFactory(param -> new ListCell<Fahrer>() {
            @Override
            protected void updateItem(Fahrer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getFuehrerscheinklasse() + ")");
                }
            }
        });

        cbFahrer.setButtonCell(new ListCell<Fahrer>() {
            @Override
            protected void updateItem(Fahrer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("(Kein Fahrer ausgewählt)");
                } else {
                    setText(item.getName());
                }
            }
        });

        // Lieferadresse
        Label lblLieferadresse = new Label("Lieferadresse:");
        TextArea taLieferadresse = new TextArea();
        taLieferadresse.setId("taLieferadresse");
        taLieferadresse.setPrefRowCount(3);
        taLieferadresse.setWrapText(true);

        // Lieferdatum
        Label lblLieferdatum = new Label("Lieferdatum:");
        DatePicker dpLieferdatum = new DatePicker();
        dpLieferdatum.setId("dpLieferdatum");
        dpLieferdatum.setValue(LocalDate.now().plusDays(1)); // Standard: morgen

        // Status
        Label lblStatus = new Label("Status:");
        ComboBox<AuftragStatus> cbStatus = new ComboBox<>();
        cbStatus.setId("cbStatus");
        cbStatus.getItems().addAll(AuftragStatus.values());
        cbStatus.setValue(AuftragStatus.NEU);

        // Priorität
        Label lblPrioritaet = new Label("Priorität:");
        ComboBox<AuftragPrioritaet> cbPrioritaet = new ComboBox<>();
        cbPrioritaet.setId("cbPrioritaet");
        cbPrioritaet.getItems().addAll(AuftragPrioritaet.values());
        cbPrioritaet.setValue(AuftragPrioritaet.NORMAL);

        // Bemerkungen
        Label lblBemerkungen = new Label("Bemerkungen:");
        TextArea taBemerkungen = new TextArea();
        taBemerkungen.setId("taBemerkungen");
        taBemerkungen.setPrefRowCount(2);
        taBemerkungen.setWrapText(true);

        // Interne Notizen
        Label lblInterneNotizen = new Label("Interne Notizen:");
        TextArea taInterneNotizen = new TextArea();
        taInterneNotizen.setId("taInterneNotizen");
        taInterneNotizen.setPrefRowCount(2);
        taInterneNotizen.setWrapText(true);

        // Layout
        int row = 0;
        grid.add(lblKunde, 0, row);
        grid.add(cbKunde, 1, row++);

        grid.add(lblFahrer, 0, row);
        grid.add(cbFahrer, 1, row++);

        grid.add(lblLieferadresse, 0, row);
        grid.add(taLieferadresse, 1, row++);

        grid.add(lblLieferdatum, 0, row);
        grid.add(dpLieferdatum, 1, row++);

        grid.add(lblStatus, 0, row);
        grid.add(cbStatus, 1, row++);

        grid.add(lblPrioritaet, 0, row);
        grid.add(cbPrioritaet, 1, row++);

        grid.add(lblBemerkungen, 0, row);
        grid.add(taBemerkungen, 1, row++);

        grid.add(lblInterneNotizen, 0, row);
        grid.add(taInterneNotizen, 1, row++);

        // Automatische Lieferadresse-Übernahme
        cbKunde.setOnAction(e -> {
            Kunde selectedKunde = cbKunde.getValue();
            if (selectedKunde != null && (taLieferadresse.getText() == null || taLieferadresse.getText().trim().isEmpty())) {
                taLieferadresse.setText(selectedKunde.getAdresse());
            }
        });

        // Vorhandene Daten laden
        if (auftrag != null) {
            // Kunde setzen
            for (Kunde k : cbKunde.getItems()) {
                if (k.getId() == auftrag.getKundeId()) {
                    cbKunde.setValue(k);
                    break;
                }
            }

            // Fahrer setzen
            for (Fahrer f : cbFahrer.getItems()) {
                if (f.getId() == auftrag.getFahrerId()) {
                    cbFahrer.setValue(f);
                    break;
                }
            }

            taLieferadresse.setText(auftrag.getLieferadresse());
            dpLieferdatum.setValue(auftrag.getLieferdatum());
            cbStatus.setValue(auftrag.getStatus());
            cbPrioritaet.setValue(auftrag.getPrioritaet());
            taBemerkungen.setText(auftrag.getBemerkungen());
            taInterneNotizen.setText(auftrag.getInterneNotizen());
        }

        box.getChildren().addAll(titel, grid);
        return box;
    }

    private static VBox createPositionenTab(Auftrag auftrag) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label titel = new Label("Auftragspositionen");
        titel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Ware hinzufügen Bereich
        HBox addBox = new HBox(10);
        addBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<Ware> cbWare = new ComboBox<>();
        cbWare.setPrefWidth(200);

        try {
            List<Ware> waren = wareRepository.findAll();
            cbWare.getItems().addAll(waren);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Waren: " + e.getMessage());
        }

        cbWare.setCellFactory(param -> new ListCell<Ware>() {
            @Override
            protected void updateItem(Ware item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getBezeichnung() + " (" + item.getArtikelnummer() + ")");
                }
            }
        });

        cbWare.setButtonCell(new ListCell<Ware>() {
            @Override
            protected void updateItem(Ware item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Ware auswählen...");
                } else {
                    setText(item.getBezeichnung());
                }
            }
        });

        Spinner<Integer> spMenge = new Spinner<>(1, 9999, 1);
        spMenge.setEditable(true);
        spMenge.setPrefWidth(80);

        Button btnHinzufuegen = new Button("Hinzufügen");

        addBox.getChildren().addAll(new Label("Ware:"), cbWare, new Label("Menge:"), spMenge, btnHinzufuegen);

        // Tabelle für Positionen
        TableView<AuftragPosition> tablePositionen = new TableView<>();
        tablePositionen.setId("tablePositionen");
        tablePositionen.setEditable(true);

        // Spalten
        TableColumn<AuftragPosition, String> colArtikelnummer = new TableColumn<>("Artikel-Nr.");
        colArtikelnummer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWareArtikelnummer()));
        colArtikelnummer.setPrefWidth(100);

        TableColumn<AuftragPosition, String> colWareName = new TableColumn<>("Bezeichnung");
        colWareName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWareName()));
        colWareName.setPrefWidth(200);

        TableColumn<AuftragPosition, Integer> colMenge = new TableColumn<>("Menge");
        colMenge.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMenge()).asObject());
        colMenge.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colMenge.setOnEditCommit(event -> {
            AuftragPosition position = event.getRowValue();
            position.setMenge(event.getNewValue());
            tablePositionen.refresh();
        });
        colMenge.setPrefWidth(80);

        TableColumn<AuftragPosition, BigDecimal> colEinzelpreis = new TableColumn<>("Einzelpreis");
        colEinzelpreis.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEinzelpreis()));
        colEinzelpreis.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        colEinzelpreis.setOnEditCommit(event -> {
            AuftragPosition position = event.getRowValue();
            position.setEinzelpreis(event.getNewValue());
            tablePositionen.refresh();
        });
        colEinzelpreis.setPrefWidth(100);

        TableColumn<AuftragPosition, String> colGesamtpreis = new TableColumn<>("Gesamtpreis");
        colGesamtpreis.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f €", data.getValue().getGesamtpreis())));
        colGesamtpreis.setPrefWidth(100);

        // Löschen-Button Spalte
        TableColumn<AuftragPosition, Void> colLoeschen = new TableColumn<>("Aktion");
        colLoeschen.setCellFactory(param -> new TableCell<AuftragPosition, Void>() {
            private final Button btnLoeschen = new Button("Löschen");

            {
                btnLoeschen.setOnAction(event -> {
                    AuftragPosition position = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(position);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnLoeschen);
                }
            }
        });
        colLoeschen.setPrefWidth(80);

        tablePositionen.getColumns().addAll(colArtikelnummer, colWareName, colMenge,
                colEinzelpreis, colGesamtpreis, colLoeschen);

        // Hinzufügen-Logik
        btnHinzufuegen.setOnAction(e -> {
            Ware selectedWare = cbWare.getValue();
            if (selectedWare != null) {
                int menge = spMenge.getValue();
                BigDecimal einzelpreis = selectedWare.getVerkaufspreis() != null ?
                        selectedWare.getVerkaufspreis() : BigDecimal.ZERO;

                AuftragPosition position = new AuftragPosition(
                        selectedWare.getWareId(),
                        selectedWare.getBezeichnung(),
                        menge,
                        einzelpreis
                );
                position.setWareArtikelnummer(selectedWare.getArtikelnummer());

                tablePositionen.getItems().add(position);
                cbWare.setValue(null);
                spMenge.getValueFactory().setValue(1);
            } else {
                showAlert("Bitte wählen Sie eine Ware aus.");
            }
        });

        // Gesamtsumme anzeigen
        Label lblGesamtsumme = new Label("Gesamtsumme: 0,00 €");
        lblGesamtsumme.setId("lblGesamtsumme");
        lblGesamtsumme.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Gesamtsumme automatisch aktualisieren
        tablePositionen.getItems().addListener((javafx.collections.ListChangeListener<AuftragPosition>) change -> {
            BigDecimal gesamtsumme = tablePositionen.getItems().stream()
                    .map(AuftragPosition::getGesamtpreis)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lblGesamtsumme.setText(String.format("Gesamtsumme: %.2f €", gesamtsumme));
        });

        // Vorhandene Positionen laden
        if (auftrag != null && auftrag.getPositionen() != null) {
            tablePositionen.getItems().addAll(auftrag.getPositionen());
        }

        box.getChildren().addAll(titel, addBox, tablePositionen, lblGesamtsumme);
        return box;
    }

    private static Auftrag collectAuftragData(VBox grunddatenBox, VBox positionenBox, Auftrag existingAuftrag) {
        Auftrag auftrag = existingAuftrag != null ? existingAuftrag : new Auftrag();

        // Grunddaten sammeln
        GridPane grid = (GridPane) grunddatenBox.getChildren().get(1);

        @SuppressWarnings("unchecked")
        ComboBox<Kunde> cbKunde = (ComboBox<Kunde>) grid.lookup("#cbKunde");
        @SuppressWarnings("unchecked")
        ComboBox<Fahrer> cbFahrer = (ComboBox<Fahrer>) grid.lookup("#cbFahrer");
        TextArea taLieferadresse = (TextArea) grid.lookup("#taLieferadresse");
        DatePicker dpLieferdatum = (DatePicker) grid.lookup("#dpLieferdatum");
        @SuppressWarnings("unchecked")
        ComboBox<AuftragStatus> cbStatus = (ComboBox<AuftragStatus>) grid.lookup("#cbStatus");
        @SuppressWarnings("unchecked")
        ComboBox<AuftragPrioritaet> cbPrioritaet = (ComboBox<AuftragPrioritaet>) grid.lookup("#cbPrioritaet");
        TextArea taBemerkungen = (TextArea) grid.lookup("#taBemerkungen");
        TextArea taInterneNotizen = (TextArea) grid.lookup("#taInterneNotizen");

        // Daten setzen
        if (cbKunde.getValue() != null) {
            auftrag.setKundeId(cbKunde.getValue().getId());
            auftrag.setKundeName(cbKunde.getValue().getName());
        }

        if (cbFahrer.getValue() != null) {
            auftrag.setFahrerId(cbFahrer.getValue().getId());
            auftrag.setFahrerName(cbFahrer.getValue().getName());
        }

        auftrag.setLieferadresse(taLieferadresse.getText());
        auftrag.setLieferdatum(dpLieferdatum.getValue());
        auftrag.setStatus(cbStatus.getValue());
        auftrag.setPrioritaet(cbPrioritaet.getValue());
        auftrag.setBemerkungen(taBemerkungen.getText());
        auftrag.setInterneNotizen(taInterneNotizen.getText());

        // Positionen sammeln
        @SuppressWarnings("unchecked")
        TableView<AuftragPosition> tablePositionen = (TableView<AuftragPosition>) positionenBox.lookup("#tablePositionen");
        auftrag.setPositionen(tablePositionen.getItems());

        return auftrag;
    }

    private static boolean validateAuftrag(Auftrag auftrag) {
        if (auftrag.getKundeId() == 0) {
            showAlert("Bitte wählen Sie einen Kunden aus.");
            return false;
        }

        if (auftrag.getPositionen().isEmpty()) {
            showAlert("Bitte fügen Sie mindestens eine Position hinzu.");
            return false;
        }

        return true;
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validierungsfehler");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
