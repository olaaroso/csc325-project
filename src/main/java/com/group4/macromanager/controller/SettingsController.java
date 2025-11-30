package com.group4.macromanager.controller;

import com.group4.macromanager.Main;
import com.group4.macromanager.model.UserSettings;
import com.group4.macromanager.session.SettingsManager;
import com.group4.macromanager.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class SettingsController extends BaseController {

    // Match FXML fx:id names
    @FXML private TextField calorieField;
    @FXML private TextField proteinField;
    @FXML private TextField carbField;
    @FXML private TextField fatField;
    @FXML private ToggleButton lightToggle;
    @FXML private ToggleButton darkToggle;

    @FXML
    public void initialize() {
        initializePage("settings");
        setupToggles();
        loadCurrentSettings();
    }

    private void setupToggles() {
        // Theme toggles - only one can be selected
        lightToggle.setOnAction(e -> {
            if (lightToggle.isSelected()) {
                darkToggle.setSelected(false);
            }
        });
        darkToggle.setOnAction(e -> {
            if (darkToggle.isSelected()) {
                lightToggle.setSelected(false);
            }
        });
    }

    private void loadCurrentSettings() {
        UserSettings settings = getCurrentUserSettings();
        if (settings != null) {

            calorieField.setText(String.valueOf((int)settings.getCalorieTarget()));
            proteinField.setText(String.valueOf((int)settings.getProteinTarget()));
            carbField.setText(String.valueOf((int)settings.getCarbTarget()));
            fatField.setText(String.valueOf((int)settings.getFatTarget()));

            // Set theme toggles
            if ("dark".equals(settings.getTheme())) {
                darkToggle.setSelected(true);
                lightToggle.setSelected(false);
            } else {
                lightToggle.setSelected(true);
                darkToggle.setSelected(false);
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showAlert("Please fill in all fields with valid values.");
            return;
        }

        try {
            String theme = darkToggle.isSelected() ? "dark" : "light";

            UserSettings settings = new UserSettings(
                    getCurrentUserId(),
                    Double.parseDouble(calorieField.getText()),
                    Double.parseDouble(proteinField.getText()),
                    Double.parseDouble(carbField.getText()),
                    Double.parseDouble(fatField.getText()),
                    theme
            );

            SettingsManager.getInstance().saveSettings(settings);
            showSuccessAlert("Settings saved successfully!");

            // Navigate back to dashboard
            PageNavigationManager.switchTo("dashboardPage.fxml");

        } catch (Exception e) {
            showAlert("Failed to save settings: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Reload original settings to reset form
        loadCurrentSettings();
        showSuccessAlert("Changes cancelled");
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (!ValidationUtil.isValidDouble(calorieField.getText())) {
            ValidationUtil.markInvalid(calorieField);
            isValid = false;
        }
        if (!ValidationUtil.isValidDouble(proteinField.getText())) {
            ValidationUtil.markInvalid(proteinField);
            isValid = false;
        }
        if (!ValidationUtil.isValidDouble(carbField.getText())) {
            ValidationUtil.markInvalid(carbField);
            isValid = false;
        }
        if (!ValidationUtil.isValidDouble(fatField.getText())) {
            ValidationUtil.markInvalid(fatField);
            isValid = false;
        }

        return isValid;
    }
}