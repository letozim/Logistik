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
import org.example.service.WarenService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WareVerwaltungView extends Application {

    private final WarenService warenService = new WarenService();
    private final TableView<Ware> tableView = new TableView<>();

    private final ObservableList<Ware> masterDaten = FXCollections.observableArrayList();
    private final FilteredList<Ware> gefilterteWaren = new FilteredList<>(masterDaten, p -> true);

    private final TextField suchfeld = new TextField();
    private final Button btnHinzufuegen = new Button("Hinzufügen");
    private final Button btnBearbeiten = new Button("Bearbeiten");
    private final Button btnLoeschen = new Button("Löschen");
    private final Button btnImport = new Button("Importieren");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Warenverwaltung");

        BorderPane root = new BorderPane();

        VBox topBox = new VBox();
        topBox.setPadding(new Insets(10));
        topBox.setSpacing(10);

        Label suchLabel = new Label("Suchen:");
        suchfeld.setPromptText("Suche nach Name, Artikelnummer, Beschreibung...");
        suchfeld.textProperty().addListener((obs, alt, neu) -> filtereTabelle(neu));
        topBox.getChildren().addAll(suchLabel, suchfeld);

        root.setTop(topBox);

        konfiguriereTabelle();
        tableView.setItems(gefilterteWaren);
        ladeAlleWaren();

        HBox buttonLeiste = new HBox(10, btnHinzufuegen, btnBearbeiten, btnLoeschen, btnImport);
        buttonLeiste.setPadding(new Insets(10));
        buttonLeiste.setAlignment(Pos.CENTER);
        root.setBottom(buttonLeiste);
        root.setCenter(tableView);

        btnHinzufuegen.setOnAction(e -> {
            WareDialog dialog = new WareDialog();
            dialog.zeigeDialog(primaryStage, null).ifPresent(wareNeu -> {
                warenService.wareHinzufuegen(wareNeu);
                ladeAlleWaren();
            });
        });

        btnBearbeiten.setOnAction(e -> {
            Ware ausgewaehlteWare = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlteWare != null) {
                WareDialog dialog = new WareDialog();
                dialog.zeigeDialog(primaryStage, ausgewaehlteWare).ifPresent(aktualisierteWare -> {
                    warenService.wareAktualisieren(aktualisierteWare);
                    ladeAlleWaren();
                });
            } else {
                zeigeWarnung("Bitte wählen Sie eine Ware zum Bearbeiten aus.");
            }
        });

        btnLoeschen.setOnAction(e -> {
            Ware ausgewaehlteWare = tableView.getSelectionModel().getSelectedItem();
            if (ausgewaehlteWare != null) {
                warenService.wareLoeschen(ausgewaehlteWare.getArtikelnummer());
                ladeAlleWaren();
            } else {
                zeigeWarnung("Bitte wählen Sie eine Ware zum Löschen aus.");
            }
        });

        btnImport.setOnAction(e -> csvImportieren(primaryStage));

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void ladeAlleWaren() {
        List<Ware> alleWaren = warenService.getAlleWaren();
        masterDaten.setAll(alleWaren);
    }

    private void filtereTabelle(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            gefilterteWaren.setPredicate(w -> true);
        } else {
            String lower = filterText.toLowerCase();
            gefilterteWaren.setPredicate(w ->
                    w.getArtikelnummer().toLowerCase().contains(lower) ||
                            w.getName().toLowerCase().contains(lower) ||
                            w.getBeschreibung().toLowerCase().contains(lower)
            );
        }
    }

    private void konfiguriereTabelle() {
        TableColumn<Ware, String> colArtikelnummer = new TableColumn<>("Artikelnummer");
        colArtikelnummer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArtikelnummer()));

        TableColumn<Ware, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Ware, String> colBeschreibung = new TableColumn<>("Beschreibung");
        colBeschreibung.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBeschreibung()));

        TableColumn<Ware, Integer> colMenge = new TableColumn<>("Menge");
        colMenge.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMenge()).asObject());

        TableColumn<Ware, String> colEinheit = new TableColumn<>("Einheit");
        colEinheit.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEinheit()));

        TableColumn<Ware, Double> colPreis = new TableColumn<>("Preis");
        colPreis.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPreis()).asObject());

        tableView.getColumns().addAll(colArtikelnummer, colName, colBeschreibung, colMenge, colEinheit, colPreis);
    }

    private void csvImportieren(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV-Datei auswählen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String zeile;
                List<Ware> importierteWaren = new ArrayList<>();
                boolean ersteZeile = true;

                while ((zeile = reader.readLine()) != null) {
                    if (ersteZeile) {
                        ersteZeile = false; // Header überspringen
                        continue;
                    }

                    String[] teile = zeile.split(",");
                    if (teile.length < 6) continue;

                    Ware ware = new Ware(
                            teile[0].trim(),
                            teile[1].trim(),
                            teile[2].trim(),
                            Integer.parseInt(teile[3].trim()),
                            teile[4].trim(),
                            Double.parseDouble(teile[5].trim())
                    );
                    importierteWaren.add(ware);
                }

                for (Ware w : importierteWaren) {
                    warenService.wareHinzufuegen(w);
                }

                ladeAlleWaren();

            } catch (Exception ex) {
                zeigeWarnung("Fehler beim Import: " + ex.getMessage());
            }
        }
    }

    private void zeigeWarnung(String text) {
        new Alert(Alert.AlertType.WARNING, text).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
