package com.group4.macromanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController extends BaseController {

    // FXML elements
    @FXML private Hyperlink signupLink;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    // Event handlers
    @FXML
    private void initialize() {
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

    @FXML
    public void handleLoginButtonClick(ActionEvent event) throws IOException {
        // This handler will process the login

        // TODO: add validation logic here once Auth branch is merged

        // FOR NOW: this button navigates directly to the dashboard page
        PageNavigationManager.switchTo("dashboardPage.fxml");
    }
}
