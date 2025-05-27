package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.ui.WareVerwaltungView;

public class MainApp extends Application {
    private Stage wareVerwaltungStage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Logistik Verwaltung - Hauptfenster");

        Button btnWarenVerwaltung = new Button("Warenverwaltung öffnen");
        btnWarenVerwaltung.setOnAction(e -> zeigeWarenVerwaltung()); // ✅ Verwende die Methode hier

        StackPane root = new StackPane(btnWarenVerwaltung);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void zeigeWarenVerwaltung() {
        if (wareVerwaltungStage == null || !wareVerwaltungStage.isShowing()) {
            wareVerwaltungStage = new Stage();
            try {
                new WareVerwaltungView().start(wareVerwaltungStage); //  Fenster starten
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            wareVerwaltungStage.toFront();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
