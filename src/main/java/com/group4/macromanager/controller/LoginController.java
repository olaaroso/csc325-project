package com.group4.macromanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import java.io.IOException;

public class LoginController {

    // FXML elements
    @FXML private Hyperlink signupLink;

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

        // FOR NOW: this button navigates directly to the dashboard page
        PageNavigationManager.switchTo("dashboardPage.fxml");
    }
}
