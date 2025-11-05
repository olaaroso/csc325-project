package com.group4.macromanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class CustomFoodFormController {

    // FXML elements
    @FXML private SidebarController sidebarIncludeController;
    @FXML private TextField nameField;
    @FXML private TextField servingField;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private ImageView foodImage;
    @FXML private CheckBox favoriteCheckBox;

    private File selectedImageFile;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        sidebarIncludeController.setActivePage("customFoods");

        // Default placeholder image
        foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));

        // Pre-fill unit combo box so validation doesn't initially fail
        unitComboBox.setValue("grams");
    }

    // Handler functions

    // HandleUpload - handles uploaded pictures
    @FXML
    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            // foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));
            foodImage.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    // HandleSave - handler for when the user saves the entered form data
    @FXML
    private void handleSave() {
        // Trimmed field values
        String name = nameField.getText().trim();
        String serving = servingField.getText().trim();
        String unit = unitComboBox.getValue();
        String calories = caloriesField.getText().trim();
        String protein = proteinField.getText().trim();
        String carbs = carbsField.getText().trim();
        String fat = fatField.getText().trim();

        // Reset previous
        resetFieldStyles();

        // Validation flag
        boolean isValid = true;

        // Check for empty required fields
        if (name.isEmpty()) {
            markInvalid(nameField);
            isValid = false;
        }
        if (serving.isEmpty()) {
            markInvalid(servingField);
            isValid = false;
        }
        if (unit == null || unit.isEmpty()) {
            unitComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            isValid = false;
        }
        if (calories.isEmpty()) {
            markInvalid(caloriesField);
            isValid = false;
        }
        if (protein.isEmpty()) {
            markInvalid(proteinField);
            isValid = false;
        }
        if (carbs.isEmpty()) {
            markInvalid(carbsField);
            isValid = false;
        }
        if (fat.isEmpty()) {
            markInvalid(fatField);
            isValid = false;
        }

        // Stop submission if invalid
        if (!isValid) {
            System.out.println("Please fill in all required fields before saving.");
            return;
        }

        // If valid, proceed
        // For this sprint, just print the data
        // LATER: data will be sent to service function to send it to firestore
        System.out.printf(
                "Food saved: %s (%s %s) - %s cal | P:%sg | C:%sg | F:%sg | Favorite: %s\n",
                name, // food name
                serving, // food serving size
                unit, // food serving unit
                calories, // food cals
                protein, // food protein
                carbs, // food carbs
                fat, // food fat
                favoriteCheckBox.isSelected() ? "Yes" : "No" // food is marked as a favorite
        );

        // Clear fields after successful save
        handleCancel();
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        // Clear all the fields
        nameField.clear();
        servingField.clear();
        unitComboBox.setValue("grams"); // grams by default
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatField.clear();
        favoriteCheckBox.setSelected(false);
        foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));
    }

    // Helper functions

    // Highlights invalid text fields
    private void markInvalid(TextField field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
    }

    // Clears validation styling before new checks
    private void resetFieldStyles() {
        TextField[] fields = {
                nameField, servingField, caloriesField, proteinField, carbsField, fatField
        };
        for (TextField field : fields) {
            field.setStyle(""); // Resets to default CSS
        }
        unitComboBox.setStyle(""); // reset combo box style
    }
}
