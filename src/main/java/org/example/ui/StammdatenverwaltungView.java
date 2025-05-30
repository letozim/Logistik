package org.example.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.example.model.Kunde;
import org.example.model.Lieferant;
import org.example.model.Fahrer;

import org.example.repository.KundeRepository;
import org.example.repository.LieferantRepository;
import org.example.repository.FahrerRepository;

import org.example.model.PersonRolle;
import org.example.model.PersonTyp;
import org.example.model.FahrerDetails;
import org.example.model.FahrerVerfuegbarkeit;


import java.util.List;
import java.util.Optional;

public class StammdatenverwaltungView extends Application {

    private final KundeRepository kundeRepo = new KundeRepository();
    private final LieferantRepository lieferantRepo = new LieferantRepository();
    private final FahrerRepository fahrerRepo = new FahrerRepository();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Stammdatenverwaltung");

        TabPane tabPane = new TabPane();

        Tab tabKunden = new Tab("Kunden");
        tabKunden.setContent(createKundenPane());
        tabKunden.setClosable(false);

        Tab tabLieferanten = new Tab("Lieferanten");
        tabLieferanten.setContent(createLieferantenPane());
        tabLieferanten.setClosable(false);

        Tab tabFahrer = new Tab("Fahrer");
        tabFahrer.setContent(createFahrerPane());
        tabFahrer.setClosable(false);

        tabPane.getTabs().addAll(tabKunden, tabLieferanten, tabFahrer);

        Scene scene = new Scene(tabPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private BorderPane createKundenPane() {
        BorderPane root = new BorderPane();
        TextField suchfeld = new TextField();
        suchfeld.setPromptText("Suche Kunden...");

        TableView<Kunde> tableView = new TableView<>();

        TableColumn<Kunde, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        TableColumn<Kunde, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        TableColumn<Kunde, String> colAdresse = new TableColumn<>("Adresse");
        colAdresse.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAdresse()));
        TableColumn<Kunde, String> colTelefon = new TableColumn<>("Telefon");
        colTelefon.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefon()));
        TableColumn<Kunde, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        tableView.getColumns().addAll(colId, colName, colAdresse, colTelefon, colEmail);

        ObservableList<Kunde> kundenListe = FXCollections.observableArrayList(kundeRepo.findAll());
        FilteredList<Kunde> gefilterteKunden = new FilteredList<>(kundenListe, p -> true);
        tableView.setItems(gefilterteKunden);

        suchfeld.textProperty().addListener((obs, alt, neu) -> {
            String filter = neu.toLowerCase();
            gefilterteKunden.setPredicate(k -> {
                if (filter.isEmpty()) return true;
                return k.getName().toLowerCase().contains(filter)
                        || k.getAdresse().toLowerCase().contains(filter)
                        || k.getTelefon().toLowerCase().contains(filter)
                        || k.getEmail().toLowerCase().contains(filter);
            });
        });

        Button btnHinzufuegen = new Button("Hinzufügen");
        Button btnBearbeiten = new Button("Bearbeiten");
        Button btnLoeschen = new Button("Löschen");

        HBox buttons = new HBox(10, btnHinzufuegen, btnBearbeiten, btnLoeschen);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        root.setTop(new VBox(new Label("Kunden verwalten"), suchfeld));
        root.setCenter(tableView);
        root.setBottom(buttons);

        btnHinzufuegen.setOnAction(e -> {
            KundeDialog kundeDialog = new KundeDialog();
            Optional<Kunde> result = kundeDialog.zeigeDialog(null, null);
            result.ifPresent(k -> {
                kundeRepo.save(k);
                kundenListe.setAll(kundeRepo.findAll());
            });
        });

        btnBearbeiten.setOnAction(e -> {
            Kunde ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlt != null) {
                Optional<Kunde> result = KundeDialog.zeigeDialog(null, ausgewaehlt);
                result.ifPresent(k -> {
                    kundeRepo.update(k);
                    kundenListe.setAll(kundeRepo.findAll());
                });
            } else {
                zeigeWarnung("Bitte wählen Sie einen Kunden zum Bearbeiten aus.");
            }
        });

        btnLoeschen.setOnAction(e -> {
            Kunde ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlt != null) {
                kundeRepo.delete(ausgewaehlt.getId());
                kundenListe.setAll(kundeRepo.findAll());
            } else {
                zeigeWarnung("Bitte wählen Sie einen Kunden zum Löschen aus.");
            }
        });

        return root;
    }

    private BorderPane createLieferantenPane() {
        BorderPane root = new BorderPane();
        TextField suchfeld = new TextField();
        suchfeld.setPromptText("Suche Lieferanten...");

        TableView<Lieferant> tableView = new TableView<>();

        TableColumn<Lieferant, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        TableColumn<Lieferant, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        TableColumn<Lieferant, String> colAdresse = new TableColumn<>("Adresse");
        colAdresse.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAdresse()));
        TableColumn<Lieferant, String> colTelefon = new TableColumn<>("Telefon");
        colTelefon.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefon()));
        TableColumn<Lieferant, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        tableView.getColumns().addAll(colId, colName, colAdresse, colTelefon, colEmail);

        ObservableList<Lieferant> lieferantenListe = FXCollections.observableArrayList(lieferantRepo.findAll());
        FilteredList<Lieferant> gefilterteLieferanten = new FilteredList<>(lieferantenListe, p -> true);
        tableView.setItems(gefilterteLieferanten);

        suchfeld.textProperty().addListener((obs, alt, neu) -> {
            String filter = neu.toLowerCase();
            gefilterteLieferanten.setPredicate(l -> {
                if (filter.isEmpty()) return true;
                return l.getName().toLowerCase().contains(filter)
                        || l.getAdresse().toLowerCase().contains(filter)
                        || l.getTelefon().toLowerCase().contains(filter)
                        || l.getEmail().toLowerCase().contains(filter);
            });
        });

        Button btnHinzufuegen = new Button("Hinzufügen");
        Button btnBearbeiten = new Button("Bearbeiten");
        Button btnLoeschen = new Button("Löschen");

        HBox buttons = new HBox(10, btnHinzufuegen, btnBearbeiten, btnLoeschen);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        root.setTop(new VBox(new Label("Lieferanten verwalten"), suchfeld));
        root.setCenter(tableView);
        root.setBottom(buttons);

        btnHinzufuegen.setOnAction(e -> {
            Optional<Lieferant> result = LieferantDialog.zeigeDialog(null, null);
            result.ifPresent(l -> {
                lieferantRepo.save(l);
                lieferantenListe.setAll(lieferantRepo.findAll());
            });
        });

        btnBearbeiten.setOnAction(e -> {
            Lieferant ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlt != null) {
                Optional<Lieferant> result = LieferantDialog.zeigeDialog(null, ausgewaehlt);
                result.ifPresent(l -> {
                    lieferantRepo.update(l);
                    lieferantenListe.setAll(lieferantRepo.findAll());
                });
            } else {
                zeigeWarnung("Bitte wählen Sie einen Lieferanten zum Bearbeiten aus.");
            }
        });

        btnLoeschen.setOnAction(e -> {
            Lieferant ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlt != null) {
                lieferantRepo.delete(ausgewaehlt.getId());
                lieferantenListe.setAll(lieferantRepo.findAll());
            } else {
                zeigeWarnung("Bitte wählen Sie einen Lieferanten zum Löschen aus.");
            }
        });

        return root;
    }

    private BorderPane createFahrerPane() {
        BorderPane root = new BorderPane();

        TextField suchfeld = new TextField();
        suchfeld.setPromptText("Suche Fahrer...");

        TableView<Fahrer> tableView = new TableView<>();

        TableColumn<Fahrer, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        TableColumn<Fahrer, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        TableColumn<Fahrer, String> colTelefon = new TableColumn<>("Telefon");
        colTelefon.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefon()));
        TableColumn<Fahrer, String> colFuehrerschein = new TableColumn<>("Führerscheinklasse");
        colFuehrerschein.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFuehrerscheinklasse()));
        TableColumn<Fahrer, String> colFahrzeugtyp = new TableColumn<>("Fahrzeugtyp");
        colFahrzeugtyp.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFahrzeugtyp()));

        tableView.getColumns().addAll(colId, colName, colTelefon, colFuehrerschein, colFahrzeugtyp);

        ObservableList<Fahrer> fahrerListe = FXCollections.observableArrayList(fahrerRepo.findAll());
        FilteredList<Fahrer> gefilterteFahrer = new FilteredList<>(fahrerListe, p -> true);
        tableView.setItems(gefilterteFahrer);

        suchfeld.textProperty().addListener((obs, alt, neu) -> {
            String filter = neu.toLowerCase();
            gefilterteFahrer.setPredicate(f -> {
                if (filter.isEmpty()) return true;
                return f.getName().toLowerCase().contains(filter)
                        || f.getTelefon().toLowerCase().contains(filter)
                        || f.getFuehrerscheinklasse().toLowerCase().contains(filter)
                        || f.getFahrzeugtyp().toLowerCase().contains(filter);
            });
        });

        Button btnHinzufuegen = new Button("Hinzufügen");
        Button btnBearbeiten = new Button("Bearbeiten");
        Button btnLoeschen = new Button("Löschen");

        HBox buttons = new HBox(10, btnHinzufuegen, btnBearbeiten, btnLoeschen);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setPadding(new Insets(10));

        root.setTop(new VBox(new Label("Fahrer verwalten"), suchfeld));
        root.setCenter(tableView);
        root.setBottom(buttons);


        btnHinzufuegen.setOnAction(e -> {
            System.out.println("=== DEBUG: Fahrer-Dialog wird geöffnet ===");

            try {
                Optional<Fahrer> result = FahrerDialog.zeigeDialog(null, null);

                System.out.println("Dialog-Result vorhanden: " + result.isPresent());

                if (result.isPresent()) {
                    Fahrer f = result.get();
                    System.out.println("Fahrer empfangen:");
                    System.out.println("  - Name: " + f.getName());
                    System.out.println("  - Telefon: " + f.getTelefon());
                    System.out.println("  - Führerscheinklasse: " + f.getFuehrerscheinklasse());
                    System.out.println("  - Fahrzeugtyp: " + f.getFahrzeugtyp());
                    System.out.println("  - Verfügbarkeit: " + f.getVerfuegbarkeit());
                    System.out.println("  - Typ: " + f.getTyp());

                    System.out.println("=== Versuche Speichern ===");

                    try {
                        fahrerRepo.save(f);
                        System.out.println("✅ ERFOLGREICH gespeichert!");
                        System.out.println("Neue ID: " + f.getId());

                        // Liste neu laden
                        List<Fahrer> alleFahrer = fahrerRepo.findAll();
                        System.out.println("Anzahl Fahrer in DB: " + alleFahrer.size());
                        fahrerListe.setAll(alleFahrer);

                    } catch (Exception saveEx) {
                        System.err.println("❌ FEHLER beim Speichern:");
                        System.err.println("Typ: " + saveEx.getClass().getSimpleName());
                        System.err.println("Message: " + saveEx.getMessage());
                        saveEx.printStackTrace();

                        // User informieren
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Speicherfehler");
                        alert.setHeaderText("Fahrer konnte nicht gespeichert werden");
                        alert.setContentText("Fehler: " + saveEx.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    System.out.println("❌ Dialog wurde abgebrochen oder gab kein Result zurück");
                }

            } catch (Exception dialogEx) {
                System.err.println("❌ FEHLER beim Dialog:");
                dialogEx.printStackTrace();
            }

            System.out.println("=== DEBUG Ende ===\n");
        });

        btnBearbeiten.setOnAction(e -> {
            Fahrer ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlt != null) {
                Optional<Fahrer> result = FahrerDialog.zeigeDialog(null, ausgewaehlt);
                result.ifPresent(f -> {
                    fahrerRepo.update(f);
                    fahrerListe.setAll(fahrerRepo.findAll());
                });
            } else {
                zeigeWarnung("Bitte wählen Sie einen Fahrer zum Bearbeiten aus.");
            }
        });

        btnLoeschen.setOnAction(e -> {
            Fahrer ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlt != null) {
                fahrerRepo.delete(ausgewaehlt.getId());
                fahrerListe.setAll(fahrerRepo.findAll());
            } else {
                zeigeWarnung("Bitte wählen Sie einen Fahrer zum Löschen aus.");
            }
        });

        return root;
    }

    private void zeigeWarnung(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
