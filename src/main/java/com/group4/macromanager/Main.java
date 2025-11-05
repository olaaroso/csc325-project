package com.group4.macromanager;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.group4.macromanager.controller.PageNavigationManager;
import com.group4.macromanager.model.FirestoreContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Firestore firestore;
    public static FirebaseAuth firebaseAuth;
    private final FirestoreContext firestoreContext = new FirestoreContext();

    @Override
    public void start(Stage stage) throws IOException {

        // Initialize firestore and firebaseAuth
        firestore = firestoreContext.firebase();
        firebaseAuth = FirebaseAuth.getInstance();

        // Init fxml loader
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/customFoodFormPage.fxml"));

        // Init the scene
        Scene scene = new Scene(fxmlLoader.load());

        // Add main.css and sidebar.css to the scene (root css files)
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        stage.setTitle("MacroManager");
        stage.setScene(scene);
        stage.show();

        // Make stage available to PageNavigationManager
        PageNavigationManager.setStage(stage);
    }

    public static void main(String[] args) {
        // Launch the app
        launch(args);
    }
}
