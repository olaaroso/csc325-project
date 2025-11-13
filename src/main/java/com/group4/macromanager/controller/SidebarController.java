package com.group4.macromanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SidebarController {

    // FXML elements
    @FXML private VBox navLinks;
    @FXML private Button dashboardButton;
    @FXML private Button foodLibraryButton;
    @FXML private Button customFoodsButton;
    @FXML private Button mealBuilderButton;
    @FXML private Button historyButton;
    @FXML private Button settingsButton;

    private final Map<String, Button> buttonMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Map for easier lookup
        buttonMap.put("dashboard", dashboardButton);
        buttonMap.put("foodLibrary", foodLibraryButton);
        buttonMap.put("customFoods", customFoodsButton);
        buttonMap.put("mealBuilder", mealBuilderButton);
        buttonMap.put("history", historyButton);
        buttonMap.put("settings", settingsButton);
    }

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

    @FXML private void handleLogout() throws IOException {
        // This one is temporary, logout logic will be implemented later*
        PageNavigationManager.switchTo("loginPage.fxml");
    }

    // Helper method to save page state before navigation
    private void saveCurrentPageState() {
        // Implement state saving logic if needed
    }
}