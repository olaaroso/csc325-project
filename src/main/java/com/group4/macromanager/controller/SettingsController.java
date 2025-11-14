package com.group4.macromanager.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class SettingsController {

    @FXML private TextField displayNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> languageCombo;

    // Daily macro fields
    @FXML private TextField calorieField, proteinField, carbField, fatField;

    // Toggles (buttons are injected; groups we’ll create in code)
    @FXML private ToggleButton metricToggle, imperialToggle, lightToggle, darkToggle;

    // Security (might not exist on this page)
    @FXML private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;

    // Appearance & Notifications (might not exist on this page)
    @FXML private CheckBox darkModeCheck, pushCheck, emailNotifCheck;
    @FXML private ComboBox<String> fontSizeCombo;

    @FXML
    private void initialize() {
        // --- Null-safe setup for fields that may or may not be on this FXML ---
        if (emailField != null)       emailField.setText("user@example.com");
        if (displayNameField != null) displayNameField.setText("Your Name");
        if (languageCombo != null) {
            languageCombo.getItems().setAll("English", "Español", "Français", "ਪੰਜਾਬੀ");
            languageCombo.getSelectionModel().selectFirst();
        }
        if (fontSizeCombo != null) {
            fontSizeCombo.getItems().setAll("Small", "Medium", "Large");
            fontSizeCombo.getSelectionModel().select("Medium");
        }
        if (pushCheck != null)        pushCheck.setSelected(true);
        if (emailNotifCheck != null)  emailNotifCheck.setSelected(true);

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

    // Optional handlers you already had:
    @FXML private void handleSaveAccount() {}
    @FXML private void handleResetAccount() {}
    @FXML private void handleChangePassword() {}
    @FXML private void handleToggleDarkMode() {
        if (displayNameField == null) return;
        Scene s = displayNameField.getScene();
        if (s == null) return;
        if (darkModeCheck != null && darkModeCheck.isSelected()) {
            if (!s.getRoot().getStyleClass().contains("dark")) s.getRoot().getStyleClass().add("dark");
        } else {
            s.getRoot().getStyleClass().remove("dark");
        }
    }
    @FXML private void handleTogglePush() {}
    @FXML private void handleToggleEmail() {}
    @FXML private void handleLogout() {}
    @FXML private void handleDeleteAccount() {}
}
