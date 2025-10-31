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

        // Pre-fill static demo values for now
        nameField.setText("Sample Oatmeal");
        servingField.setText("100");
        unitComboBox.setValue("grams");
        caloriesField.setText("120");
        proteinField.setText("5");
        carbsField.setText("20");
        fatField.setText("2");
        favoriteCheckBox.setSelected(false);
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
        // For this sprint, just print the sample data
        // LATER: data will be sent to service function to send it to firestore
        System.out.printf(
                "Food saved: %s (%s %s) - %s cal | P:%sg | C:%sg | F:%sg | Favorite: %s\n",
                nameField.getText(), // food name
                servingField.getText(), // food serving size
                unitComboBox.getValue(), // food serving unit
                caloriesField.getText(), // food cals
                proteinField.getText(), // food protein
                carbsField.getText(), // food carbs
                fatField.getText(), // food fat
                favoriteCheckBox.isSelected() ? "Yes" : "No" // food is marked as a favorite
        );
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        // Clear all the fields
        nameField.clear();
        servingField.clear();
        unitComboBox.setValue(null);
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatField.clear();
        favoriteCheckBox.setSelected(false);
        foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));
    }
}
