package com.group4.macromanager.controller;

import com.group4.macromanager.session.AuthSessionManager;
import com.group4.macromanager.session.CustomFoodSession;
import com.group4.macromanager.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SidebarController extends BaseController {

    // FXML elements
    @FXML private VBox navLinks;
    @FXML private Button dashboardButton;
    @FXML private Button foodLibraryButton;
    @FXML private Button customFoodsButton;
    @FXML private Button mealBuilderButton;
    @FXML private Button historyButton;
    @FXML private Button settingsButton;
    @FXML private Button profileButton;

    private final Map<String, Button> buttonMap = new HashMap<>();

    // -------------------- Initialization --------------------

    @FXML
    public void initialize() {
        // Map for easier lookup
        buttonMap.put("dashboard", dashboardButton);
        buttonMap.put("foodLibrary", foodLibraryButton);
        buttonMap.put("customFoods", customFoodsButton);
        buttonMap.put("mealBuilder", mealBuilderButton);
        buttonMap.put("history", historyButton);
        buttonMap.put("settings", settingsButton);

        // Update profile button text
        updateProfileButton();
    }

    // -------------------- Profile Button --------------------

    // Update profile button text with username
    private void updateProfileButton() {
        String username = AuthSessionManager.getInstance().getCurrentUsername();
        profileButton.setText(username);
    }

    @FXML
    private void handleProfileClick() {
        // Create logout confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Profile");
        alert.setHeaderText("Hello, " + AuthSessionManager.getInstance().getCurrentUsername() + "!");
        alert.setContentText("Would you like to logout?");

        // Customize buttons
        ButtonType logoutButton = new ButtonType("Logout");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        // Set buttons in alert
        alert.getButtonTypes().setAll(logoutButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == logoutButton) {
                handleLogout();
            }
        });
    }

    // Handle user logout
    private void handleLogout() {
        try {
            // Clear session
            AuthSessionManager.getInstance().clearSession();

            // Navigate to login page
            PageNavigationManager.switchTo("loginPage.fxml");
        }
        catch (Exception e) {
            AlertUtil.showError("Failed to logout: " + e.getMessage());
        }
    }

    // -------------------- Navigation & Highlighting --------------------

    // Call this from parent controllers to highlight the active page
    public void setActivePage(String pageKey) {
        buttonMap.values().forEach(btn -> btn.getStyleClass().remove("nav-button-active"));

        Button activeButton = buttonMap.get(pageKey);
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    // Navigation methods
    @FXML private void goToDashboard() throws IOException {
        PageNavigationManager.switchTo("dashboardPage.fxml");
    }

    @FXML private void goToFoodLibrary() throws IOException {
        PageNavigationManager.switchTo("foodLibraryPage.fxml");
    }

    @FXML private void goToCustomFoods() throws IOException {
        foodSession.clearSession(); // Clear any existing session data
        PageNavigationManager.switchTo("customFoodFormPage.fxml");
    }

    @FXML private void goToMealBuilder() throws IOException {
        PageNavigationManager.switchTo("mealBuilderPage.fxml");
    }

    @FXML private void goToHistory() throws IOException {
        PageNavigationManager.switchTo("historyReportsPage.fxml");
    }

    @FXML private void goToSettings() throws IOException {
        PageNavigationManager.switchTo("settingsPage.fxml");
    }

    // Helper method to save page state before navigation
    private void saveCurrentPageState() {
        // Implement state saving logic if needed
    }
}