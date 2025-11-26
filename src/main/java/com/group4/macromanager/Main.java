package com.group4.macromanager;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.group4.macromanager.controller.PageNavigationManager;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.service.FirestoreFoodService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Firestore firestore;
    public static FirebaseAuth firebaseAuth;
    private static Scene primaryScene;

    @Override
    public void start(Stage stage) throws IOException {

        // Initialize firestore and firebaseAuth
        FirestoreContext.initialize();
        firestore = FirestoreContext.getDb();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize food recommendations
        FirestoreFoodService foodService = new FirestoreFoodService();
        foodService.initializeRecommendations();

        // Init fxml loader
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/loginPage.fxml"));
        primaryScene = new Scene(fxmlLoader.load());

        // Apply light theme by default
        applyLightTheme();

        stage.setTitle("MacroManager");
        stage.setScene(primaryScene);
        stage.show();

        // Make stage available to PageNavigationManager
        PageNavigationManager.setStage(stage);
    }

    private void applyLightTheme() {
        primaryScene.getStylesheets().clear();
        primaryScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        primaryScene.getStylesheets().add(getClass().getResource("/css/light-theme.css").toExternalForm());
    }

    // Static method to get the primary scene for theme switching
    public static Scene getPrimaryScene() {
        return primaryScene;
    }

    // Static method to apply theme changes globally
    public static void applyTheme(String theme) {
        if (primaryScene != null) {
            primaryScene.getStylesheets().clear();
            primaryScene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            if ("dark".equals(theme)) {
                primaryScene.getStylesheets().add(Main.class.getResource("/css/dark-theme.css").toExternalForm());
            } else {
                primaryScene.getStylesheets().add(Main.class.getResource("/css/light-theme.css").toExternalForm());
            }
        }
    }

    public static void main(String[] args) {
        // Launch the app
        launch(args);
    }
}
