package com.group4.macromanager.controller;

import com.group4.macromanager.model.AuthManager;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.model.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class SignupController {

    // FXML elements
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signupButton;
    @FXML private Hyperlink loginLink;

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
            showAlert("Initialization Error", "Failed to initialize Firestore Context: " + e.getMessage());
        }

        // Initialize the loginLink's setOnAction handler
        // - Navigates the user to the login page
        loginLink.setOnAction(event -> {
            try {
                PageNavigationManager.switchTo("loginPage.fxml");
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    // onAction handler methods
    @FXML
    public void handleSignupButtonClick(ActionEvent event) throws IOException {
        // This handler will process the signup and registration logic

        // Obtain email, password, and confirm password from the fields and store the values in respective variables
        // - Uses trim() to strip any entered whitespace
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Simple validation
        // Guard clause to prevent submission without filling all fields
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Missing fields", "Please fill out all fields.");
            return;
        }
        // Guard clause to check if password doesn't equal confirmPassword
        if (!password.equals(confirmPassword)) {
            showAlert("Password mismatch", "Passwords don't match.");
            return;
        }

        // Disable button while registering to prevent any extra clicks
        signupButton.setDisable(true);
        signupButton.setText("Signing up...");

        // Execute signup process off the JavaFX thread
        // NOTE: a new thread is used for long-running operations to run in the background so the UI doesn't freeze
        new Thread(() -> {
            try {
                var session = authManager.registerUser(email, password);

                // Use runLater() method because only the JavaFX thread can modify the UI
                // This means: once the background thread is done, schedule this UI update to run on JavaFX thread
                Platform.runLater(() -> {
                    showAlert("Success", "Redirecting to login...");

                    // DO NOT Set session, the user will be redirected to log in first
                    // SessionManager.setCurrentSession(session);

                    // Navigate to dashboard
                    try {
                        PageNavigationManager.switchTo("loginPage.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Re-enable signup button and set text back to 'Create Account'
                    signupButton.setDisable(false);
                    signupButton.setText("Create Account");
                });
            }
            catch (Exception ex) {
               Platform.runLater(() -> {
                   // Alert the user that there was an error
                   showAlert("Signup Error", ex.getMessage());
                   System.out.println("Signup Error: " + ex.getMessage());
                   // Re-enable signup button and set text back to 'Create Account'
                   signupButton.setDisable(false);
                   signupButton.setText("Create Account");
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
