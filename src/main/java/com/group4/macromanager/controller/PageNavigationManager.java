package com.group4.macromanager.controller;

// This class handles switching between FXML pages

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageNavigationManager {
    private static Stage stage;
    private static String currentTheme = "light"; // Track current theme

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void setCurrentTheme(String theme) {
        currentTheme = theme;
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    // Method for switching fxml pages
    public static void switchTo(String fxml) throws IOException {
        // Load fxml file that is passed as the parameter, and load it into a new scene
        FXMLLoader loader = new FXMLLoader(PageNavigationManager.class.getResource("/fxml/" + fxml));
        Scene scene = new Scene(loader.load());

        // Apply stylesheets using the same logic as Main.applyTheme()
        scene.getStylesheets().add(PageNavigationManager.class.getResource("/css/main.css").toExternalForm());

        if ("dark".equals(currentTheme)) {
            scene.getStylesheets().add(PageNavigationManager.class.getResource("/css/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(PageNavigationManager.class.getResource("/css/light-theme.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }
}
