package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ui.WareVerwaltungView;
import org.example.ui.StammdatenverwaltungView;

public class MainApp extends Application {
    private Stage wareVerwaltungStage;
    private Stage stammdatenVerwaltungStage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Logistik Verwaltung - Hauptfenster");

        Button btnWarenVerwaltung = new Button("Warenverwaltung öffnen");
        Button btnStammdatenVerwaltung = new Button("Stammdatenverwaltung öffnen");

        btnWarenVerwaltung.setOnAction(e -> zeigeWarenVerwaltung());
        btnStammdatenVerwaltung.setOnAction(e -> zeigeStammdatenVerwaltung());

        VBox root = new VBox(15, btnWarenVerwaltung, btnStammdatenVerwaltung);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 350, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void zeigeWarenVerwaltung() {
        if (wareVerwaltungStage == null || !wareVerwaltungStage.isShowing()) {
            wareVerwaltungStage = new Stage();
            try {
                new WareVerwaltungView().start(wareVerwaltungStage);
            } catch (Exception e) {
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
                e.printStackTrace();
            }
        } else {
            stammdatenVerwaltungStage.toFront();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
