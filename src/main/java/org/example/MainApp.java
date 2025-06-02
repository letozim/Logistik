package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.stage.Stage;
import org.example.ui.*;

public class MainApp extends Application {
    private Stage wareVerwaltungStage;
    private Stage stammdatenVerwaltungStage;
    private Stage auftragVerwaltungStage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🚛 Logistik CRM - Hauptmenü");

        // Modernes Design erstellen
        VBox root = createMainLayout();

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(createCSS());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createMainLayout() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setId("main-container");

        // Header Bereich
        VBox headerBox = createHeader();

        // Menü-Buttons Bereich
        GridPane menuGrid = createMenuGrid();

        // Footer Bereich
        HBox footerBox = createFooter();

        root.getChildren().addAll(headerBox, menuGrid, footerBox);
        return root;
    }

    private VBox createHeader() {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🚛 Logistik CRM");
        titleLabel.setId("title-label");

        Label subtitleLabel = new Label("Professionelle Verwaltung für Ihr Logistikunternehmen");
        subtitleLabel.setId("subtitle-label");

        Separator separator = new Separator();
        separator.setMaxWidth(400);
        separator.getStyleClass().add("title-separator");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel, separator);
        return headerBox;
    }

    private GridPane createMenuGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        // Stammdaten Button
        Button btnStammdaten = createMenuButton(
                "👥 Stammdaten",
                "Kunden, Fahrer & Lieferanten verwalten",
                "#3498db"
        );
        btnStammdaten.setOnAction(e -> zeigeStammdatenVerwaltung());

        // Warenverwaltung Button
        Button btnWaren = createMenuButton(
                "📦 Warenverwaltung",
                "Artikel, Lager & Bestände verwalten",
                "#27ae60"
        );
        btnWaren.setOnAction(e -> zeigeWarenVerwaltung());

        // Auftragsverwaltung Button (NEU!)
        Button btnAuftraege = createMenuButton(
                "📋 Auftragsverwaltung",
                "Aufträge erstellen & verwalten",
                "#e74c3c"
        );
        btnAuftraege.setOnAction(e -> zeigeAuftragVerwaltung());

        // Layout (2x2 Grid)
        grid.add(btnStammdaten, 0, 0);
        grid.add(btnWaren, 1, 0);
        grid.add(btnAuftraege, 0, 1);

        // Platzhalter für zukünftige Module
        Button btnPlatzhalter = createMenuButton(
                "🚚 Fahrzeuge",
                "Fahrzeugverwaltung (Coming Soon)",
                "#95a5a6"
        );
        btnPlatzhalter.setDisable(true);
        grid.add(btnPlatzhalter, 1, 1);

        return grid;
    }

    private Button createMenuButton(String title, String description, String color) {
        VBox buttonContent = new VBox(8);
        buttonContent.setAlignment(Pos.CENTER);
        buttonContent.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.setStyle(String.format(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: %s;",
                color
        ));

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(180);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        buttonContent.getChildren().addAll(titleLabel, descLabel);

        Button button = new Button();
        button.setGraphic(buttonContent);
        button.setPrefSize(200, 120);
        button.getStyleClass().add("menu-button");

        // Hover-Effekt
        button.setOnMouseEntered(e -> {
            button.setStyle(String.format(
                    "-fx-background-color: %s; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 4);",
                    color
            ));
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
            descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #ecf0f1;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("");
            titleLabel.setStyle(String.format(
                    "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: %s;",
                    color
            ));
            descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        });

        return button;
    }

    private HBox createFooter() {
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER);

        Label footerLabel = new Label("© 2024 Logistik CRM - Version 1.0");
        footerLabel.setId("footer-label");

        footerBox.getChildren().add(footerLabel);
        return footerBox;
    }

    private void zeigeWarenVerwaltung() {
        if (wareVerwaltungStage == null || !wareVerwaltungStage.isShowing()) {
            wareVerwaltungStage = new Stage();
            try {
                new WareVerwaltungView().start(wareVerwaltungStage);
            } catch (Exception e) {
                showError("Fehler beim Öffnen der Warenverwaltung", e.getMessage());
                e.printStackTrace();
            }
        } else {
            wareVerwaltungStage.toFront();
        }
    }

    private void zeigeStammdatenVerwaltung() {
        if (stammdatenVerwaltungStage == null || !stammdatenVerwaltungStage.isShowing()) {
            stammdatenVerwaltungStage = new Stage();
            try {
                new StammdatenverwaltungView().start(stammdatenVerwaltungStage);
            } catch (Exception e) {
                showError("Fehler beim Öffnen der Stammdatenverwaltung", e.getMessage());
                e.printStackTrace();
            }
        } else {
            stammdatenVerwaltungStage.toFront();
        }
    }

    private void zeigeAuftragVerwaltung() {
        if (auftragVerwaltungStage == null || !auftragVerwaltungStage.isShowing()) {
            auftragVerwaltungStage = new Stage();
            try {
                new AuftragVerwaltungView().start(auftragVerwaltungStage);
            } catch (Exception e) {
                showError("Fehler beim Öffnen der Auftragsverwaltung", e.getMessage());
                e.printStackTrace();
            }
        } else {
            auftragVerwaltungStage.toFront();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String createCSS() {
        return "data:text/css," +
                "#main-container {" +
                "  -fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);" +
                "}" +
                "#title-label {" +
                "  -fx-font-size: 32px;" +
                "  -fx-font-weight: bold;" +
                "  -fx-text-fill: #2c3e50;" +
                "  -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);" +
                "}" +
                "#subtitle-label {" +
                "  -fx-font-size: 14px;" +
                "  -fx-text-fill: #34495e;" +
                "  -fx-font-style: italic;" +
                "}" +
                ".title-separator {" +
                "  -fx-background-color: #bdc3c7;" +
                "}" +
                ".menu-button {" +
                "  -fx-background-color: white;" +
                "  -fx-background-radius: 12;" +
                "  -fx-border-color: #bdc3c7;" +
                "  -fx-border-width: 1;" +
                "  -fx-border-radius: 12;" +
                "  -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
                "  -fx-cursor: hand;" +
                "}" +
                ".menu-button:hover {" +
                "  -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 4);" +
                "}" +
                ".menu-button:pressed {" +
                "  -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);" +
                "}" +
                ".menu-button:disabled {" +
                "  -fx-opacity: 0.6;" +
                "  -fx-cursor: default;" +
                "}" +
                "#footer-label {" +
                "  -fx-font-size: 10px;" +
                "  -fx-text-fill: #7f8c8d;" +
                "}";
    }

    public static void main(String[] args) {
        launch(args);
    }
}