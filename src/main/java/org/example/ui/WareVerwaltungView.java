package org.example.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Ware;
import org.example.service.WarenService;

import java.util.List;

public class WareVerwaltungView extends Application {

    private final WarenService warenService = new WarenService();
    private final ObservableList<Ware> warenDaten = FXCollections.observableArrayList();
    private FilteredList<Ware> gefilterteDaten;
    private final TableView<Ware> tableView = new TableView<>();

    private final Button btnHinzufuegen = new Button("Hinzufügen");
    private final Button btnBearbeiten = new Button("Bearbeiten");
    private final Button btnLoeschen = new Button("Löschen");

    private final TextField suchFeld = new TextField();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Warenverwaltung");

        BorderPane root = new BorderPane();

        // Suchfeld
        suchFeld.setPromptText("Suche nach Name oder Artikelnummer...");
        suchFeld.textProperty().addListener((obs, alt, neu) -> {
            gefilterteDaten.setPredicate(ware -> {
                if (neu == null || neu.isEmpty()) return true;
                String suchtext = neu.toLowerCase();
                return ware.getArtikelnummer().toLowerCase().contains(suchtext) ||
                        ware.getName().toLowerCase().contains(suchtext);
            });
        });

        // Tabelle konfigurieren
        konfiguriereTabelle();

        // Daten initial laden
        gefilterteDaten = new FilteredList<>(warenDaten, p -> true);
        tableView.setItems(gefilterteDaten);
        ladeAlleWaren();

        // Buttons konfigurieren
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

        HBox buttonLeiste = new HBox(10, btnHinzufuegen, btnBearbeiten, btnLoeschen);
        buttonLeiste.setAlignment(Pos.CENTER);
        buttonLeiste.setPadding(new Insets(10));

        VBox topBox = new VBox(10, suchFeld);
        topBox.setPadding(new Insets(10));

        root.setTop(topBox);
        root.setCenter(tableView);
        root.setBottom(buttonLeiste);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void ladeAlleWaren() {
        List<Ware> alleWaren = warenService.getAlleWaren();
        warenDaten.setAll(alleWaren);
    }

    private void konfiguriereTabelle() {
        TableColumn<Ware, String> colArtikelnummer = new TableColumn<>("Artikelnummer");
        colArtikelnummer.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getArtikelnummer()));

        TableColumn<Ware, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Ware, String> colBeschreibung = new TableColumn<>("Beschreibung");
        colBeschreibung.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBeschreibung()));

        TableColumn<Ware, Integer> colMenge = new TableColumn<>("Menge");
        colMenge.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getMenge()).asObject());

        TableColumn<Ware, String> colEinheit = new TableColumn<>("Einheit");
        colEinheit.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEinheit()));

        TableColumn<Ware, Double> colPreis = new TableColumn<>("Preis");
        colPreis.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPreis()).asObject());

        tableView.getColumns().addAll(colArtikelnummer, colName, colBeschreibung, colMenge, colEinheit, colPreis);
    }

    private void zeigeWarnung(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
