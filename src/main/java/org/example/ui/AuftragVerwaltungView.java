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
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.example.model.*;
import org.example.repository.AuftragRepository;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AuftragVerwaltungView extends Application {

    private final AuftragRepository auftragRepository = new AuftragRepository();
    private final TableView<Auftrag> tableView = new TableView<>();

    private final ObservableList<Auftrag> masterDaten = FXCollections.observableArrayList();
    private final FilteredList<Auftrag> gefilterteAuftraege = new FilteredList<>(masterDaten, p -> true);

    private final TextField suchfeld = new TextField();
    private final ComboBox<AuftragStatus> statusFilter = new ComboBox<>();
    private final Button btnErstellen = new Button("Neuer Auftrag");
    private final Button btnBearbeiten = new Button("Bearbeiten");
    private final Button btnAnzeigen = new Button("Details");
    private final Button btnLoeschen = new Button("Stornieren");
    private final Button btnUebersicht = new Button("Übersicht");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Auftragsverwaltung - CRM");

        BorderPane root = new BorderPane();

        // Top-Bereich mit Suche und Filter
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));

        Label titel = new Label("Auftragsverwaltung");
        titel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label suchLabel = new Label("Suchen:");
        suchfeld.setPromptText("Suche nach Auftragsnummer, Kunde, Fahrer...");
        suchfeld.setPrefWidth(300);
        suchfeld.textProperty().addListener((obs, alt, neu) -> filtereTabelle(neu, statusFilter.getValue()));

        Label statusLabel = new Label("Status:");
        statusFilter.getItems().add(null); // "Alle" Option
        statusFilter.getItems().addAll(AuftragStatus.values());
        statusFilter.setPromptText("Alle Status");
        statusFilter.setOnAction(e -> filtereTabelle(suchfeld.getText(), statusFilter.getValue()));

        filterBox.getChildren().addAll(suchLabel, suchfeld, statusLabel, statusFilter);
        topBox.getChildren().addAll(titel, filterBox);
        root.setTop(topBox);

        // Tabelle konfigurieren
        konfiguriereTabelle();
        tableView.setItems(gefilterteAuftraege);
        ladeAlleAuftraege();

        // Button-Leiste
        HBox buttonLeiste = new HBox(10, btnErstellen, btnBearbeiten, btnAnzeigen, btnLoeschen,
                new Separator(), btnUebersicht);
        buttonLeiste.setPadding(new Insets(10));
        buttonLeiste.setAlignment(Pos.CENTER);
        root.setBottom(buttonLeiste);
        root.setCenter(tableView);

        // Event-Handler
        setupEventHandlers(primaryStage);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupEventHandlers(Stage primaryStage) {
        btnErstellen.setOnAction(e -> {
            try {
                Optional<Auftrag> result = AuftragDialog.zeigeDialog(primaryStage, null);
                result.ifPresent(auftragNeu -> {
                    auftragRepository.save(auftragNeu);
                    ladeAlleAuftraege();
                    zeigeInfo("Auftrag '" + auftragNeu.getAuftragsnummer() + "' wurde erfolgreich erstellt.");
                });
            } catch (Exception ex) {
                zeigeWarnung("Fehler beim Erstellen: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnBearbeiten.setOnAction(e -> {
            Auftrag ausgewaehlterAuftrag = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlterAuftrag != null) {
                if (!ausgewaehlterAuftrag.istBearbeitbar()) {
                    zeigeWarnung("Dieser Auftrag kann nicht mehr bearbeitet werden (Status: " +
                            ausgewaehlterAuftrag.getStatus().getBezeichnung() + ")");
                    return;
                }

                try {
                    // Vollständigen Auftrag mit Positionen laden
                    Auftrag vollstaendigerAuftrag = auftragRepository.findById(ausgewaehlterAuftrag.getAuftragId());
                    Optional<Auftrag> result = AuftragDialog.zeigeDialog(primaryStage, vollstaendigerAuftrag);
                    result.ifPresent(aktualisierterAuftrag -> {
                        auftragRepository.update(aktualisierterAuftrag);
                        ladeAlleAuftraege();
                        zeigeInfo("Auftrag '" + aktualisierterAuftrag.getAuftragsnummer() + "' wurde erfolgreich aktualisiert.");
                    });
                } catch (Exception ex) {
                    zeigeWarnung("Fehler beim Bearbeiten: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                zeigeWarnung("Bitte wählen Sie einen Auftrag zum Bearbeiten aus.");
            }
        });

        btnAnzeigen.setOnAction(e -> {
            Auftrag ausgewaehlterAuftrag = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlterAuftrag != null) {
                zeigeAuftragDetails(ausgewaehlterAuftrag);
            } else {
                zeigeWarnung("Bitte wählen Sie einen Auftrag aus.");
            }
        });

        btnLoeschen.setOnAction(e -> {
            Auftrag ausgewaehlterAuftrag = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlterAuftrag != null) {
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Auftrag stornieren");
                confirmDialog.setHeaderText("Auftrag wirklich stornieren?");
                confirmDialog.setContentText("Möchten Sie den Auftrag '" +
                        ausgewaehlterAuftrag.getAuftragsnummer() + "' wirklich stornieren?");

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        auftragRepository.delete(ausgewaehlterAuftrag.getAuftragId());
                        ladeAlleAuftraege();
                        zeigeInfo("Auftrag wurde storniert.");
                    } catch (Exception ex) {
                        zeigeWarnung("Fehler beim Stornieren: " + ex.getMessage());
                    }
                }
            } else {
                zeigeWarnung("Bitte wählen Sie einen Auftrag aus.");
            }
        });

        btnUebersicht.setOnAction(e -> zeigeAuftragsUebersicht());
    }

    private void ladeAlleAuftraege() {
        try {
            List<Auftrag> alleAuftraege = auftragRepository.findAll();
            masterDaten.setAll(alleAuftraege);
            System.out.println("Loaded " + alleAuftraege.size() + " Aufträge from database");
        } catch (Exception e) {
            zeigeWarnung("Fehler beim Laden der Aufträge: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filtereTabelle(String filterText, AuftragStatus statusFilter) {
        gefilterteAuftraege.setPredicate(auftrag -> {
            // Status-Filter
            if (statusFilter != null && auftrag.getStatus() != statusFilter) {
                return false;
            }

            // Text-Filter
            if (filterText == null || filterText.isEmpty()) {
                return true;
            }

            String lower = filterText.toLowerCase();
            return (auftrag.getAuftragsnummer() != null && auftrag.getAuftragsnummer().toLowerCase().contains(lower)) ||
                    (auftrag.getKundeName() != null && auftrag.getKundeName().toLowerCase().contains(lower)) ||
                    (auftrag.getFahrerName() != null && auftrag.getFahrerName().toLowerCase().contains(lower)) ||
                    (auftrag.getLieferadresse() != null && auftrag.getLieferadresse().toLowerCase().contains(lower));
        });
    }

    private void konfiguriereTabelle() {
        // Auftragsnummer
        TableColumn<Auftrag, String> colAuftragsnummer = new TableColumn<>("Auftragsnr.");
        colAuftragsnummer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuftragsnummer()));
        colAuftragsnummer.setPrefWidth(120);

        // Status (mit Farbe)
        TableColumn<Auftrag, AuftragStatus> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStatus()));
        colStatus.setCellFactory(column -> new TableCell<Auftrag, AuftragStatus>() {
            @Override
            protected void updateItem(AuftragStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.getBezeichnung());
                    setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold;",
                            status.getFarbe()));
                }
            }
        });
        colStatus.setPrefWidth(120);

        // Priorität
        TableColumn<Auftrag, AuftragPrioritaet> colPrioritaet = new TableColumn<>("Priorität");
        colPrioritaet.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrioritaet()));
        colPrioritaet.setCellFactory(column -> new TableCell<Auftrag, AuftragPrioritaet>() {
            @Override
            protected void updateItem(AuftragPrioritaet prioritaet, boolean empty) {
                super.updateItem(prioritaet, empty);
                if (empty || prioritaet == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(prioritaet.getBezeichnung());
                    if (prioritaet == AuftragPrioritaet.DRINGEND || prioritaet == AuftragPrioritaet.HOCH) {
                        setStyle("-fx-text-fill: " + prioritaet.getFarbe() + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        colPrioritaet.setPrefWidth(100);

        // Kunde
        TableColumn<Auftrag, String> colKunde = new TableColumn<>("Kunde");
        colKunde.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKundeName()));
        colKunde.setPrefWidth(200);

        // Fahrer
        TableColumn<Auftrag, String> colFahrer = new TableColumn<>("Fahrer");
        colFahrer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFahrerName()));
        colFahrer.setPrefWidth(150);

        // Lieferdatum
        TableColumn<Auftrag, String> colLieferdatum = new TableColumn<>("Lieferdatum");
        colLieferdatum.setCellValueFactory(data -> {
            if (data.getValue().getLieferdatum() != null) {
                return new SimpleStringProperty(data.getValue().getLieferdatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
            return new SimpleStringProperty("");
        });
        colLieferdatum.setPrefWidth(100);

        // Gesamtsumme
        TableColumn<Auftrag, String> colGesamtsumme = new TableColumn<>("Gesamtsumme");
        colGesamtsumme.setCellValueFactory(data -> {
            BigDecimal summe = data.getValue().getGesamtsummeBrutto();
            return new SimpleStringProperty(String.format("%.2f €", summe));
        });
        colGesamtsumme.setPrefWidth(120);

        // Erstellt am
        TableColumn<Auftrag, String> colErstellt = new TableColumn<>("Erstellt");
        colErstellt.setCellValueFactory(data -> {
            if (data.getValue().getErstelltAm() != null) {
                return new SimpleStringProperty(data.getValue().getErstelltAm().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        colErstellt.setPrefWidth(120);

        tableView.getColumns().addAll(colAuftragsnummer, colStatus, colPrioritaet, colKunde,
                colFahrer, colLieferdatum, colGesamtsumme, colErstellt);
    }

    private void zeigeAuftragDetails(Auftrag auftrag) {
        try {
            // Vollständigen Auftrag mit Positionen laden
            Auftrag vollstaendigerAuftrag = auftragRepository.findById(auftrag.getAuftragId());

            Stage detailStage = new Stage();
            detailStage.setTitle("Auftragsdetails - " + auftrag.getAuftragsnummer());
            detailStage.initModality(Modality.NONE);

            VBox mainBox = new VBox(15);
            mainBox.setPadding(new Insets(20));
            mainBox.setStyle("-fx-background-color: #f8f9fa;");

            // Titel
            Label titel = new Label("📋 Auftragsdetails");
            titel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            // Auftragsinformationen
            GridPane infoGrid = new GridPane();
            infoGrid.setHgap(10);
            infoGrid.setVgap(5);
            infoGrid.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8;");

            int row = 0;
            infoGrid.add(new Label("Auftragsnummer:"), 0, row);
            infoGrid.add(new Label(vollstaendigerAuftrag.getAuftragsnummer()), 1, row++);

            infoGrid.add(new Label("Status:"), 0, row);
            Label statusLabel = new Label(vollstaendigerAuftrag.getStatus().getBezeichnung());
            statusLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold;",
                    vollstaendigerAuftrag.getStatus().getFarbe()));
            infoGrid.add(statusLabel, 1, row++);

            infoGrid.add(new Label("Kunde:"), 0, row);
            infoGrid.add(new Label(vollstaendigerAuftrag.getKundeName()), 1, row++);

            if (vollstaendigerAuftrag.getFahrerName() != null) {
                infoGrid.add(new Label("Fahrer:"), 0, row);
                infoGrid.add(new Label(vollstaendigerAuftrag.getFahrerName()), 1, row++);
            }

            if (vollstaendigerAuftrag.getLieferdatum() != null) {
                infoGrid.add(new Label("Lieferdatum:"), 0, row);
                infoGrid.add(new Label(vollstaendigerAuftrag.getLieferdatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))), 1, row++);
            }

            // Positionen-Tabelle
            Label positionenTitel = new Label("📦 Auftragspositionen");
            positionenTitel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            TableView<AuftragPosition> positionenTable = new TableView<>();
            positionenTable.getItems().addAll(vollstaendigerAuftrag.getPositionen());

            TableColumn<AuftragPosition, String> colArt = new TableColumn<>("Artikel-Nr.");
            colArt.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWareArtikelnummer()));

            TableColumn<AuftragPosition, String> colName = new TableColumn<>("Bezeichnung");
            colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWareName()));

            TableColumn<AuftragPosition, Integer> colMenge = new TableColumn<>("Menge");
            colMenge.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMenge()).asObject());

            TableColumn<AuftragPosition, String> colEinzel = new TableColumn<>("Einzelpreis");
            colEinzel.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f €", data.getValue().getEinzelpreis())));

            TableColumn<AuftragPosition, String> colGesamt = new TableColumn<>("Gesamtpreis");
            colGesamt.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f €", data.getValue().getGesamtpreis())));

            positionenTable.getColumns().addAll(colArt, colName, colMenge, colEinzel, colGesamt);

            // Gesamtsumme
            HBox summenBox = new HBox(10);
            summenBox.setAlignment(Pos.CENTER_RIGHT);
            summenBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8;");

            VBox summenLabels = new VBox(5);
            summenLabels.setAlignment(Pos.CENTER_RIGHT);
            summenLabels.getChildren().addAll(
                    new Label("Nettosumme:"),
                    new Label("MwSt (" + vollstaendigerAuftrag.getMwstSatz() + "%):"),
                    new Label("Gesamtsumme:")
            );

            VBox summenWerte = new VBox(5);
            summenWerte.setAlignment(Pos.CENTER_RIGHT);
            Label bruttoLabel = new Label(String.format("%.2f €", vollstaendigerAuftrag.getGesamtsummeBrutto()));
            bruttoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            summenWerte.getChildren().addAll(
                    new Label(String.format("%.2f €", vollstaendigerAuftrag.getGesamtsumme())),
                    new Label(String.format("%.2f €", vollstaendigerAuftrag.getMwstBetrag())),
                    bruttoLabel
            );

            summenBox.getChildren().addAll(summenLabels, summenWerte);

            mainBox.getChildren().addAll(titel, infoGrid, positionenTitel, positionenTable, summenBox);

            ScrollPane scrollPane = new ScrollPane(mainBox);
            scrollPane.setFitToWidth(true);

            Scene scene = new Scene(scrollPane, 700, 600);
            detailStage.setScene(scene);
            detailStage.show();

        } catch (Exception e) {
            zeigeWarnung("Fehler beim Laden der Auftragsdetails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void zeigeAuftragsUebersicht() {
        try {
            List<Auftrag> alleAuftraege = auftragRepository.findAll();

            Stage uebersichtStage = new Stage();
            uebersichtStage.setTitle("Auftrags-Übersicht - Dashboard");
            uebersichtStage.initModality(Modality.NONE);

            VBox mainContent = new VBox(15);
            mainContent.setPadding(new Insets(20));
            mainContent.setStyle("-fx-background-color: #f8f9fa;");

            Label titel = new Label("📊 Auftrags-Dashboard");
            titel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Status-Statistiken
            HBox statusBox = new HBox(15);
            for (AuftragStatus status : AuftragStatus.values()) {
                long anzahl = alleAuftraege.stream().filter(a -> a.getStatus() == status).count();
                VBox card = createStatusCard(status.getBezeichnung(), String.valueOf(anzahl), status.getFarbe());
                statusBox.getChildren().add(card);
            }

            // Gesamtumsatz
            BigDecimal gesamtumsatz = alleAuftraege.stream()
                    .map(Auftrag::getGesamtsummeBrutto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Label umsatzLabel = new Label(String.format("💰 Gesamtumsatz: %.2f €", gesamtumsatz));
            umsatzLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

            mainContent.getChildren().addAll(titel, statusBox, umsatzLabel);

            ScrollPane scrollPane = new ScrollPane(mainContent);
            scrollPane.setFitToWidth(true);

            Scene scene = new Scene(scrollPane, 800, 500);
            uebersichtStage.setScene(scene);
            uebersichtStage.show();

        } catch (Exception e) {
            zeigeWarnung("Fehler beim Erstellen der Übersicht: " + e.getMessage());
        }
    }

    private VBox createStatusCard(String titel, String wert, String farbe) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);", farbe));
        card.setPrefWidth(150);

        Label titelLabel = new Label(titel);
        titelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label wertLabel = new Label(wert);
        wertLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        card.getChildren().addAll(titelLabel, wertLabel);
        return card;
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