package com.group4.macromanager.controller;

import com.group4.macromanager.model.AuthManager;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.model.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {

    // FXML elements
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink signupLink;

    // AuthManager placeholder
    private AuthManager authManager;

    // Initialize method
    @FXML
    private void initialize() {
        // Initialize Firestore and authManager
        try {
            FirestoreContext.initialize();
            authManager = new AuthManager();
        }
        catch (Exception e) {
            showAlert("Initialization Error", "Failed to Initialize Firestore Context.\n" + e.getMessage());
        }

        // Initialize the signupLink's setOnAction handler
        // - Navigates the user to the signup page
        signupLink.setOnAction(event -> {
            try {
                PageNavigationManager.switchTo("signupPage.fxml");
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    // onAction handler methods
    @FXML
    public void handleLoginButtonClick(ActionEvent event) throws IOException {
        // This handler will process the login

        // Obtain email and password from the fields and store the values in respective variables
        // - Uses trim() to strip any entered whitespace
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Guard clause to prevent submission without filling out both fields
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Missing fields", "Please enter your email and password.");
            return;
        }

        // Disable button while logging in to prevent any extra clicks
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        // Execute login process off the JavaFX thread
        // NOTE: a new thread is used for long-running operations to run in the background so the UI doesn't freeze
        new Thread(() -> {
            try {
                var session = authManager.login(email, password);

                // Use runLater() method because only the JavaFX thread can modify the UI
                // This means: once the background thread is done, schedule this UI update to run on JavaFX thread
                Platform.runLater(() -> {
                    showAlert("Success", "Logged in as " + session.email);

                    // Set session
                    SessionManager.setCurrentSession(session);

                    // Navigate to dashboard
                    try {
                        PageNavigationManager.switchTo("dashboardPage.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Re-enable login button and set text back to 'Login'
                    loginButton.setDisable(false);
                    loginButton.setText("Login");
                });
            }
            catch (Exception ex) {
                Platform.runLater(() -> {
                    // Alert the user that there was an error
                    showAlert("Login failed", ex.getMessage());
                    // Re-enable login button and set text back to 'Login'
                    loginButton.setDisable(false);
                    loginButton.setText("Login");
                });
            }
        }).start();
    }

    // Private helper methods
    private void showAlert(String title, String message) {
        // This method is used to show pop up alerts after an event occurs
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
