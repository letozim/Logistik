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
import javafx.stage.Modality;
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
    private final Button btnUebersicht = new Button("Übersicht");

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
                new Separator(), btnUebersicht, btnMindestbestand, btnImport);
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

        btnUebersicht.setOnAction(e -> zeigeWarenUebersicht());
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

    private void zeigeWarenUebersicht() {
        try {
            List<Ware> alleWaren = wareRepository.findAll();
            List<Ware> unterMindestbestand = wareRepository.findUnterMindestbestand();

            // Statistiken berechnen
            WareStatistik stats = berechneStatistiken(alleWaren);

            // Neues Fenster für Übersicht
            Stage uebersichtStage = new Stage();
            uebersichtStage.setTitle("Waren-Übersicht - Dashboard");
            uebersichtStage.initModality(Modality.NONE);

            ScrollPane scrollPane = new ScrollPane();
            VBox mainContent = new VBox(15);
            mainContent.setPadding(new Insets(20));
            mainContent.setStyle("-fx-background-color: #f8f9fa;");

            // Titel
            Label titel = new Label("📊 Warenverwaltung - Dashboard");
            titel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Zusammenfassung-Karten
            HBox zusammenfassungBox = new HBox(15);
            zusammenfassungBox.getChildren().addAll(
                    createInfoCard("📦 Gesamt Waren", String.valueOf(stats.gesamtAnzahl), "#3498db"),
                    createInfoCard("⚠️ Unter Mindestbestand", String.valueOf(stats.unterMindestbestand), "#e74c3c"),
                    createInfoCard("💰 Lagerwert", String.format("%.2f €", stats.gesamtLagerwert), "#27ae60"),
                    createInfoCard("🏢 Kategorien", String.valueOf(stats.anzahlKategorien), "#9b59b6")
            );

            // Detailstatistiken
            VBox detailsBox = new VBox(10);

            // Kategorien-Übersicht
            VBox kategorienBox = createSectionBox("📋 Kategorien-Übersicht", stats.kategorieDetails);

            // Lagerorte-Übersicht
            VBox lagerorteBox = createSectionBox("📍 Lagerorte-Übersicht", stats.lagerortDetails);

            // Top 10 wertvollste Waren
            VBox topWarenBox = createTopWarenBox("💎 Top 10 wertvollste Waren", stats.topWertvolleWaren);

            // Kritische Bestände (falls vorhanden)
            if (!unterMindestbestand.isEmpty()) {
                VBox kritischeBestaendeBox = createKritischeBestaendeBox("🚨 Kritische Bestände", unterMindestbestand);
                detailsBox.getChildren().add(kritischeBestaendeBox);
            }

            // Lieferanten-Übersicht
            VBox lieferantenBox = createSectionBox("🚚 Lieferanten-Übersicht", stats.lieferantDetails);

            detailsBox.getChildren().addAll(kategorienBox, lagerorteBox, topWarenBox, lieferantenBox);

            // Alles zusammenfügen
            mainContent.getChildren().addAll(titel, zusammenfassungBox, detailsBox);

            scrollPane.setContent(mainContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            Scene scene = new Scene(scrollPane, 900, 700);
            uebersichtStage.setScene(scene);
            uebersichtStage.show();

        } catch (Exception e) {
            zeigeWarnung("Fehler beim Erstellen der Übersicht: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createInfoCard(String titel, String wert, String farbe) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);", farbe));
        card.setPrefWidth(200);

        Label titelLabel = new Label(titel);
        titelLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label wertLabel = new Label(wert);
        wertLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        card.getChildren().addAll(titelLabel, wertLabel);
        return card;
    }

    private VBox createSectionBox(String titel, java.util.Map<String, Integer> daten) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        box.setPadding(new Insets(15));

        Label titelLabel = new Label(titel);
        titelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox inhalt = new VBox(5);

        for (java.util.Map.Entry<String, Integer> entry : daten.entrySet()) {
            HBox zeile = new HBox();
            zeile.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(entry.getKey());
            name.setStyle("-fx-font-size: 12px;");
            name.setPrefWidth(150);

            Label anzahl = new Label(entry.getValue() + " Artikel");
            anzahl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

            // Progress Bar für visuelle Darstellung
            ProgressBar progress = new ProgressBar();
            progress.setPrefWidth(100);
            progress.setProgress((double) entry.getValue() / daten.values().stream().mapToInt(Integer::intValue).max().orElse(1));
            progress.setStyle("-fx-accent: #3498db;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            zeile.getChildren().addAll(name, spacer, anzahl, progress);
            inhalt.getChildren().add(zeile);
        }

        box.getChildren().addAll(titelLabel, inhalt);
        return box;
    }

    private VBox createTopWarenBox(String titel, List<Ware> topWaren) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        box.setPadding(new Insets(15));

        Label titelLabel = new Label(titel);
        titelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox inhalt = new VBox(5);

        for (int i = 0; i < Math.min(10, topWaren.size()); i++) {
            Ware ware = topWaren.get(i);
            HBox zeile = new HBox(10);
            zeile.setAlignment(Pos.CENTER_LEFT);

            Label rang = new Label("#" + (i + 1));
            rang.setStyle("-fx-font-weight: bold; -fx-text-fill: #f39c12;");
            rang.setPrefWidth(30);

            Label name = new Label(ware.getBezeichnung());
            name.setStyle("-fx-font-size: 12px;");
            name.setPrefWidth(200);

            Label artikelnr = new Label("(" + ware.getArtikelnummer() + ")");
            artikelnr.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
            artikelnr.setPrefWidth(80);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label wert = new Label(String.format("%.2f €", ware.lagerwert()));
            wert.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

            zeile.getChildren().addAll(rang, name, artikelnr, spacer, wert);
            inhalt.getChildren().add(zeile);
        }

        box.getChildren().addAll(titelLabel, inhalt);
        return box;
    }

    private VBox createKritischeBestaendeBox(String titel, List<Ware> kritischeWaren) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: #fff5f5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8;");
        box.setPadding(new Insets(15));

        Label titelLabel = new Label(titel);
        titelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        VBox inhalt = new VBox(5);

        for (Ware ware : kritischeWaren) {
            HBox zeile = new HBox(10);
            zeile.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(ware.getBezeichnung());
            name.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            name.setPrefWidth(180);

            Label bestand = new Label(ware.getAktuellerBestand() + " / " + ware.getMindestbestand());
            bestand.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");
            bestand.setPrefWidth(60);

            int fehlt = ware.getMindestbestand() - ware.getAktuellerBestand();
            Label fehltLabel = new Label("Fehlen: " + fehlt);
            fehltLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            zeile.getChildren().addAll(name, bestand, spacer, fehltLabel);
            inhalt.getChildren().add(zeile);
        }

        box.getChildren().addAll(titelLabel, inhalt);
        return box;
    }

    private WareStatistik berechneStatistiken(List<Ware> alleWaren) {
        WareStatistik stats = new WareStatistik();

        stats.gesamtAnzahl = alleWaren.size();
        stats.unterMindestbestand = (int) alleWaren.stream().filter(Ware::istUnterMindestbestand).count();
        stats.gesamtLagerwert = alleWaren.stream()
                .filter(w -> w.getVerkaufspreis() != null)
                .mapToDouble(w -> w.lagerwert().doubleValue())
                .sum();

        // Kategorien-Statistik
        stats.kategorieDetails = new java.util.LinkedHashMap<>();
        alleWaren.stream()
                .filter(w -> w.getKategorie() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        w -> w.getKategorie().getKategorieName(),
                        java.util.stream.Collectors.counting()
                ))
                .forEach((k, v) -> stats.kategorieDetails.put(k, v.intValue()));

        stats.anzahlKategorien = stats.kategorieDetails.size();

        // Lagerorte-Statistik
        stats.lagerortDetails = new java.util.LinkedHashMap<>();
        alleWaren.stream()
                .filter(w -> w.getLagerort() != null && !w.getLagerort().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(
                        Ware::getLagerort,
                        java.util.stream.Collectors.counting()
                ))
                .forEach((k, v) -> stats.lagerortDetails.put(k, v.intValue()));

        // Lieferanten-Statistik
        stats.lieferantDetails = new java.util.LinkedHashMap<>();
        alleWaren.stream()
                .filter(w -> w.getLieferantName() != null && !w.getLieferantName().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(
                        Ware::getLieferantName,
                        java.util.stream.Collectors.counting()
                ))
                .forEach((k, v) -> stats.lieferantDetails.put(k, v.intValue()));

        // Top wertvolle Waren
        stats.topWertvolleWaren = alleWaren.stream()
                .filter(w -> w.getVerkaufspreis() != null && w.getAktuellerBestand() > 0)
                .sorted((w1, w2) -> w2.lagerwert().compareTo(w1.lagerwert()))
                .collect(java.util.stream.Collectors.toList());

        return stats;
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

    // Hilfsklasse für Statistiken
    private static class WareStatistik {
        int gesamtAnzahl;
        int unterMindestbestand;
        double gesamtLagerwert;
        int anzahlKategorien;
        java.util.Map<String, Integer> kategorieDetails;
        java.util.Map<String, Integer> lagerortDetails;
        java.util.Map<String, Integer> lieferantDetails;
        List<Ware> topWertvolleWaren;
    }

    public static void main(String[] args) {
        launch(args);
    }
}