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
        FXMLLoader loader = new FXMLLoader(PageNavigationManager.class.getResource("/fxml/" + fxml));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }
}
