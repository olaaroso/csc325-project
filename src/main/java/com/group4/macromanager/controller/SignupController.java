package com.group4.macromanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignupController {

    // FXML elements
    @FXML private Hyperlink loginLink;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // Event handlers
    @FXML
    private void initialize() {
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

    @FXML
    public void handleSignupButtonClick(ActionEvent event) throws IOException {
        // This handler will process the signup and registration logic

        // TODO: add validation logic here once Auth branch is merged

        // FOR NOW: this button navigates directly to the login page
        PageNavigationManager.switchTo("loginPage.fxml");
    }
}
