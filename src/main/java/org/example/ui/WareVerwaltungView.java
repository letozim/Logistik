package org.example.ui;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.example.model.Ware;
import org.example.model.WareKategorie;
import org.example.model.WareEinheit;
import org.example.repository.WareRepository;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WareVerwaltungView extends Application {

    private final WareRepository wareRepository = new WareRepository();
    private final TableView<Ware> tableView = new TableView<>();

    private final ObservableList<Ware> masterDaten = FXCollections.observableArrayList();
    private final FilteredList<Ware> gefilterteWaren = new FilteredList<>(masterDaten, p -> true);

    private final TextField suchfeld = new TextField();
    private final Button btnHinzufuegen = new Button("Hinzufügen");
    private final Button btnBearbeiten = new Button("Bearbeiten");
    private final Button btnLoeschen = new Button("Löschen");
    private final Button btnImport = new Button("Importieren");
    private final Button btnMindestbestand = new Button("Mindestbestand-Warnung");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Warenverwaltung - CRM");

        BorderPane root = new BorderPane();

        // Top-Bereich mit Suche und Info
        VBox topBox = new VBox();
        topBox.setPadding(new Insets(10));
        topBox.setSpacing(10);

        Label titel = new Label("Warenverwaltung");
        titel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label suchLabel = new Label("Suchen:");
        suchfeld.setPromptText("Suche nach Artikelnummer, Bezeichnung, Beschreibung, Lieferant...");
        suchfeld.textProperty().addListener((obs, alt, neu) -> filtereTabelle(neu));

        topBox.getChildren().addAll(titel, suchLabel, suchfeld);
        root.setTop(topBox);

        // Tabelle konfigurieren
        konfiguriereTabelle();
        tableView.setItems(gefilterteWaren);
        ladeAlleWaren();

        // Button-Leiste
        HBox buttonLeiste = new HBox(10, btnHinzufuegen, btnBearbeiten, btnLoeschen,
                new Separator(), btnMindestbestand, btnImport);
        buttonLeiste.setPadding(new Insets(10));
        buttonLeiste.setAlignment(Pos.CENTER);
        root.setBottom(buttonLeiste);
        root.setCenter(tableView);

        // Event-Handler
        setupEventHandlers(primaryStage);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupEventHandlers(Stage primaryStage) {
        btnHinzufuegen.setOnAction(e -> {
            try {
                Optional<Ware> result = WareDialog.zeigeDialog(primaryStage, null);
                result.ifPresent(wareNeu -> {
                    wareRepository.save(wareNeu);
                    ladeAlleWaren();
                    zeigeInfo("Ware '" + wareNeu.getBezeichnung() + "' wurde erfolgreich hinzugefügt.");
                });
            } catch (Exception ex) {
                zeigeWarnung("Fehler beim Hinzufügen: " + ex.getMessage());
            }
        });

        btnBearbeiten.setOnAction(e -> {
            Ware ausgewaehlteWare = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlteWare != null) {
                try {
                    Optional<Ware> result = WareDialog.zeigeDialog(primaryStage, ausgewaehlteWare);
                    result.ifPresent(aktualisierteWare -> {
                        wareRepository.update(aktualisierteWare);
                        ladeAlleWaren();
                        zeigeInfo("Ware '" + aktualisierteWare.getBezeichnung() + "' wurde erfolgreich aktualisiert.");
                    });
                } catch (Exception ex) {
                    zeigeWarnung("Fehler beim Bearbeiten: " + ex.getMessage());
                }
            } else {
                zeigeWarnung("Bitte wählen Sie eine Ware zum Bearbeiten aus.");
            }
        });

        btnLoeschen.setOnAction(e -> {
            Ware ausgewaehlteWare = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlteWare != null) {
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Ware löschen");
                confirmDialog.setHeaderText("Ware wirklich löschen?");
                confirmDialog.setContentText("Möchten Sie die Ware '" + ausgewaehlteWare.getBezeichnung() + "' wirklich löschen?");

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        wareRepository.delete(ausgewaehlteWare.getWareId());
                        ladeAlleWaren();
                        zeigeInfo("Ware wurde erfolgreich gelöscht.");
                    } catch (Exception ex) {
                        zeigeWarnung("Fehler beim Löschen: " + ex.getMessage());
                    }
                }
            } else {
                zeigeWarnung("Bitte wählen Sie eine Ware zum Löschen aus.");
            }
        });

        btnMindestbestand.setOnAction(e -> zeigeMindestbestandWarnung());
        btnImport.setOnAction(e -> csvImportieren(primaryStage));
    }

    private void ladeAlleWaren() {
        try {
            List<Ware> alleWaren = wareRepository.findAll();
            masterDaten.setAll(alleWaren);
            System.out.println("Loaded " + alleWaren.size() + " Waren from database");
        } catch (Exception e) {
            zeigeWarnung("Fehler beim Laden der Waren: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filtereTabelle(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            gefilterteWaren.setPredicate(w -> true);
        } else {
            String lower = filterText.toLowerCase();
            gefilterteWaren.setPredicate(w -> {
                // Suche in verschiedenen Feldern
                boolean matches = false;

                if (w.getArtikelnummer() != null && w.getArtikelnummer().toLowerCase().contains(lower)) matches = true;
                if (w.getBezeichnung() != null && w.getBezeichnung().toLowerCase().contains(lower)) matches = true;
                if (w.getBeschreibung() != null && w.getBeschreibung().toLowerCase().contains(lower)) matches = true;
                if (w.getLieferantName() != null && w.getLieferantName().toLowerCase().contains(lower)) matches = true;
                if (w.getLagerort() != null && w.getLagerort().toLowerCase().contains(lower)) matches = true;
                if (w.getKategorie() != null && w.getKategorie().getKategorieName().toLowerCase().contains(lower)) matches = true;

                return matches;
            });
        }
    }

    private void konfiguriereTabelle() {
        // Artikelnummer
        TableColumn<Ware, String> colArtikelnummer = new TableColumn<>("Artikelnummer");
        colArtikelnummer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArtikelnummer()));
        colArtikelnummer.setPrefWidth(120);

        // Bezeichnung
        TableColumn<Ware, String> colBezeichnung = new TableColumn<>("Bezeichnung");
        colBezeichnung.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBezeichnung()));
        colBezeichnung.setPrefWidth(200);

        // Kategorie
        TableColumn<Ware, String> colKategorie = new TableColumn<>("Kategorie");
        colKategorie.setCellValueFactory(data -> {
            WareKategorie kat = data.getValue().getKategorie();
            return new SimpleStringProperty(kat != null ? kat.getKategorieName() : "");
        });
        colKategorie.setPrefWidth(100);

        // Aktueller Bestand
        TableColumn<Ware, Integer> colBestand = new TableColumn<>("Bestand");
        colBestand.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAktuellerBestand()).asObject());
        colBestand.setPrefWidth(80);

        // Bestand mit Warnung färben wenn unter Mindestbestand
        colBestand.setCellFactory(column -> new TableCell<Ware, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Ware ware = getTableView().getItems().get(getIndex());
                    if (ware.istUnterMindestbestand()) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Mindestbestand
        TableColumn<Ware, Integer> colMindestbestand = new TableColumn<>("Mindest.");
        colMindestbestand.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMindestbestand()).asObject());
        colMindestbestand.setPrefWidth(70);

        // Einheit
        TableColumn<Ware, String> colEinheit = new TableColumn<>("Einheit");
        colEinheit.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEinheit().getAnzeigeName()));
        colEinheit.setPrefWidth(80);

        // Verkaufspreis
        TableColumn<Ware, String> colVerkaufspreis = new TableColumn<>("VK-Preis");
        colVerkaufspreis.setCellValueFactory(data -> {
            BigDecimal preis = data.getValue().getVerkaufspreis();
            return new SimpleStringProperty(preis != null ? String.format("%.2f €", preis) : "");
        });
        colVerkaufspreis.setPrefWidth(80);

        // Lieferant
        TableColumn<Ware, String> colLieferant = new TableColumn<>("Lieferant");
        colLieferant.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLieferantName()));
        colLieferant.setPrefWidth(150);

        // Lagerort
        TableColumn<Ware, String> colLagerort = new TableColumn<>("Lagerort");
        colLagerort.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLagerort()));
        colLagerort.setPrefWidth(100);

        tableView.getColumns().addAll(colArtikelnummer, colBezeichnung, colKategorie,
                colBestand, colMindestbestand, colEinheit,
                colVerkaufspreis, colLieferant, colLagerort);
    }

    private void zeigeMindestbestandWarnung() {
        try {
            List<Ware> unterMindestbestand = wareRepository.findUnterMindestbestand();

            if (unterMindestbestand.isEmpty()) {
                zeigeInfo("Alle Waren sind ausreichend auf Lager!");
                return;
            }

            StringBuilder nachricht = new StringBuilder();
            nachricht.append("Folgende Waren sind unter dem Mindestbestand:\n\n");

            for (Ware ware : unterMindestbestand) {
                int fehlt = ware.getMindestbestand() - ware.getAktuellerBestand();
                nachricht.append(String.format("• %s (%s): %d von %d %s (fehlen: %d)\n",
                        ware.getBezeichnung(),
                        ware.getArtikelnummer(),
                        ware.getAktuellerBestand(),
                        ware.getMindestbestand(),
                        ware.getEinheit().getAnzeigeName(),
                        fehlt));
            }

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mindestbestand-Warnung");
            alert.setHeaderText(unterMindestbestand.size() + " Waren unter Mindestbestand");
            alert.setContentText(nachricht.toString());

            // Scroll-fähigen Text für lange Listen
            TextArea textArea = new TextArea(nachricht.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(300);
            alert.getDialogPane().setExpandableContent(textArea);

            alert.showAndWait();

        } catch (Exception e) {
            zeigeWarnung("Fehler beim Prüfen des Mindestbestands: " + e.getMessage());
        }
    }

    private void csvImportieren(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV-Datei für Waren-Import auswählen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String zeile;
                List<Ware> importierteWaren = new ArrayList<>();
                int zeilenNummer = 0;
                int erfolgreich = 0;
                int fehler = 0;

                // Header-Info für User
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("CSV-Import");
                infoAlert.setHeaderText("CSV-Format");
                infoAlert.setContentText("Erwartetes Format: Artikelnummer, Bezeichnung, Beschreibung, Bestand, Mindestbestand, Verkaufspreis\nErste Zeile wird als Header übersprungen.");
                infoAlert.showAndWait();

                while ((zeile = reader.readLine()) != null) {
                    zeilenNummer++;

                    if (zeilenNummer == 1) {
                        continue; // Header überspringen
                    }

                    try {
                        String[] teile = zeile.split(",");
                        if (teile.length < 6) {
                            System.err.println("Zeile " + zeilenNummer + ": Zu wenige Spalten");
                            fehler++;
                            continue;
                        }

                        Ware ware = new Ware();
                        ware.setArtikelnummer(teile[0].trim());
                        ware.setBezeichnung(teile[1].trim());
                        ware.setBeschreibung(teile[2].trim());
                        ware.setAktuellerBestand(Integer.parseInt(teile[3].trim()));
                        ware.setMindestbestand(Integer.parseInt(teile[4].trim()));
                        ware.setVerkaufspreis(new BigDecimal(teile[5].trim()));
                        ware.setEinheit(WareEinheit.STUECK); // Standard

                        // Optional: weitere Felder wenn vorhanden
                        if (teile.length > 6 && !teile[6].trim().isEmpty()) {
                            ware.setLagerort(teile[6].trim());
                        }

                        importierteWaren.add(ware);
                        erfolgreich++;

                    } catch (Exception e) {
                        System.err.println("Fehler in Zeile " + zeilenNummer + ": " + e.getMessage());
                        fehler++;
                    }
                }

                // Waren in Datenbank speichern
                for (Ware ware : importierteWaren) {
                    try {
                        wareRepository.save(ware);
                    } catch (Exception e) {
                        System.err.println("Fehler beim Speichern von " + ware.getArtikelnummer() + ": " + e.getMessage());
                        fehler++;
                        erfolgreich--;
                    }
                }

                ladeAlleWaren();

                // Ergebnis anzeigen
                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                resultAlert.setTitle("Import abgeschlossen");
                resultAlert.setHeaderText("CSV-Import Ergebnis");
                resultAlert.setContentText(String.format(
                        "Verarbeitete Zeilen: %d\nErfolgreich importiert: %d\nFehler: %d",
                        zeilenNummer - 1, erfolgreich, fehler));
                resultAlert.showAndWait();

            } catch (Exception ex) {
                zeigeWarnung("Fehler beim Import: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void zeigeWarnung(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void zeigeInfo(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}