package com.group4.macromanager.controller;

// This class handles switching between FXML pages

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageNavigationManager {
    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    // Method for switching fxml pages
    public static void switchTo(String fxml) throws IOException {
        // Load fxml file that is passed as the parameter, and load it into a new scene
        FXMLLoader loader = new FXMLLoader(PageNavigationManager.class.getResource("/fxml/" + fxml));
        Scene scene = new Scene(loader.load());

        // Add main.css to the new scene (root css file)
        scene.getStylesheets().add(PageNavigationManager.class.getResource("/css/main.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}
