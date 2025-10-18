package com.group4.macromanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import java.io.IOException;

public class SignupController {

    // FXML elements
    @FXML private Hyperlink loginLink;

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
}
