package com.group4.macromanager.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class SettingsController {

    // Daily macro fields
    @FXML private TextField calorieField, proteinField, carbField, fatField;

    // Toggles (buttons are injected; groups we’ll create in code)
    @FXML private ToggleButton metricToggle, imperialToggle, lightToggle, darkToggle;
  
    // Sidebar flag
    @FXML
    private SidebarController sidebarIncludeController;

    @FXML
    private void initialize() {
        // Highlight current page in the sidebar
        sidebarIncludeController.setActivePage("settings");
      
        // --- Create ToggleGroups in code (fixes SceneBuilder coercion errors) ---
        if (metricToggle != null && imperialToggle != null) {
            ToggleGroup units = new ToggleGroup();
            metricToggle.setToggleGroup(units);
            imperialToggle.setToggleGroup(units);
            metricToggle.setSelected(true);
        }
        if (lightToggle != null && darkToggle != null) {
            ToggleGroup theme = new ToggleGroup();
            lightToggle.setToggleGroup(theme);
            darkToggle.setToggleGroup(theme);
            lightToggle.setSelected(true);
        }
    }

    // Handlers referenced from FXML
    @FXML private void handleUnitsChanged() {}
    @FXML private void handleThemeChanged() {}
    @FXML private void handleSaveSettings() {}
    @FXML private void handleCancel() {}

    @FXML private void handleToggleDarkMode() {
        if (darkModeCheck != null && darkModeCheck.isSelected()) {
            if (!s.getRoot().getStyleClass().contains("dark")) s.getRoot().getStyleClass().add("dark");
        } else {
            s.getRoot().getStyleClass().remove("dark");
        }
    }
}
