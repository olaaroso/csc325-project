package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.service.IFoodService;
import com.group4.macromanager.service.InMemoryFoodService;
import com.group4.macromanager.session.CustomFoodSession;
import com.group4.macromanager.util.ValidationUtil;
import com.group4.macromanager.util.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class CustomFoodFormController extends BaseController {

    // FXML elements
    @FXML private TextField nameField;
    @FXML private ComboBox<String> mealTypeComboBox;
    @FXML private TextField servingField;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private CheckBox favoriteCheckBox;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        initializePage("customFoods"); // from BaseController

        // Restore form data from session
        restoreFromSession();

        // If not in edit mode and no session data, set defaults
        if (!foodSession.isEditMode() && (mealTypeComboBox.getValue() == null || mealTypeComboBox.getValue().isEmpty())) {
            mealTypeComboBox.setValue("Breakfast");
            unitComboBox.setValue("grams");
        }

        // Setup auto-save on form changes
        setupAutoSave();
    }

    // Override the saveToSession method
    @Override
    protected void saveToSession() {
        // Ensure session instance exists
        if (foodSession == null) return;

        // Save current form data to session
        foodSession.saveFormData(
                nameField.getText(),
                mealTypeComboBox.getValue(),
                servingField.getText(),
                unitComboBox.getValue(),
                caloriesField.getText(),
                proteinField.getText(),
                carbsField.getText(),
                fatField.getText(),
                favoriteCheckBox.isSelected(),
                selectedImageFile
        );
    }

    // Override the restoreFromSession method
    @Override
    protected void restoreFromSession() {
        nameField.setText(foodSession.getFoodName());
        mealTypeComboBox.setValue(foodSession.getMealType());
        servingField.setText(foodSession.getServingSize());
        unitComboBox.setValue(foodSession.getServingUnit());
        caloriesField.setText(foodSession.getCalories());
        proteinField.setText(foodSession.getProtein());
        carbsField.setText(foodSession.getCarbs());
        fatField.setText(foodSession.getFat());
        favoriteCheckBox.setSelected(foodSession.isFavorite());

        if (foodSession.getSelectedImage() != null) {
            selectedImageFile = foodSession.getSelectedImage();
            ImageUtil.setImageFromFile(foodImage, selectedImageFile);
        }
    }

    // Handler functions

    // HandleUpload - handles uploaded pictures
    // Inherited from BaseController

    // HandleSave - handler for when the user saves the entered form data
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showAlert("Please fill in all required fields correctly.");
            return;
        }

        // Validate image file exists if one was selected
        if (selectedImageFile != null && !selectedImageFile.exists()) {
            showAlert("Selected image file is not available. Please select a new image.");
            return;
        }

        try {
            Food food = createFoodFromForm();
            Food saved;

            if (foodSession.isEditMode()) {
                // Update existing food
                food.setId(foodSession.getEditingFoodId());
                saved = foodService.updateFood(food);
                showSuccessAlert("Food updated successfully!");
            } else {
                // Create new food
                saved = foodService.saveCustomFood(food);
                showSuccessAlert("Custom food saved successfully!");
            }

            handleCancel();
        } catch (Exception e) {
            showAlert("Error saving food: " + e.getMessage());
        }
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        foodSession.clearSession(); // Clear session data
        clearForm(); // Clear form fields

        // Navigate back to food library
        try {
            PageNavigationManager.switchTo("foodLibraryPage.fxml");
        } catch (Exception e) {
            showAlert("Failed to navigate back: " + e.getMessage());
        }
    }

    // Helper functions

    // Validate form fields
    private boolean validateForm() {
        // Reset previous styles
        resetFieldStyles();

        // Validation flag
        boolean isValid = true;

        if (ValidationUtil.isEmpty(nameField.getText())) {
            ValidationUtil.markInvalid(nameField);
            isValid = false;
        }
        if (ValidationUtil.isComboBoxEmpty(mealTypeComboBox)) {
            ValidationUtil.markInvalid(mealTypeComboBox);
            isValid = false;
        }
        if (ValidationUtil.isEmpty(servingField.getText()) || !ValidationUtil.isValidDouble(servingField.getText())) {
            ValidationUtil.markInvalid(servingField);
            isValid = false;
        }
        if (ValidationUtil.isComboBoxEmpty(unitComboBox)) {
            ValidationUtil.markInvalid(unitComboBox);
            isValid = false;
        }
        if (ValidationUtil.isEmpty(caloriesField.getText()) || !ValidationUtil.isValidDouble(caloriesField.getText())) {
            ValidationUtil.markInvalid(caloriesField);
            isValid = false;
        }
        if (ValidationUtil.isEmpty(proteinField.getText()) || !ValidationUtil.isValidDouble(proteinField.getText())) {
            ValidationUtil.markInvalid(proteinField);
            isValid = false;
        }
        if (ValidationUtil.isEmpty(carbsField.getText()) || !ValidationUtil.isValidDouble(carbsField.getText())) {
            ValidationUtil.markInvalid(carbsField);
            isValid = false;
        }
        if (ValidationUtil.isEmpty(fatField.getText()) || !ValidationUtil.isValidDouble(fatField.getText())) {
            ValidationUtil.markInvalid(fatField);
            isValid = false;
        }

        // Return overall validation result
        return isValid;
    }

    // Create Food object from form data
    private Food createFoodFromForm() {
        // Get current user ID
        String userId = getCurrentUserId();

        // Convert file path to resource path for storage
        String imageResourcePath = ImageUtil.getResourcePath(selectedImageFile);

        return new Food(
                null,
                nameField.getText().trim(),
                Double.parseDouble(servingField.getText().trim()),
                unitComboBox.getValue(),
                Double.parseDouble(caloriesField.getText().trim()),
                Double.parseDouble(proteinField.getText().trim()),
                Double.parseDouble(carbsField.getText().trim()),
                Double.parseDouble(fatField.getText().trim()),
                imageResourcePath, // stores /uploads/filename.ext
                mealTypeComboBox.getValue(),
                favoriteCheckBox.isSelected()
        );
    }

    // Clear form fields
    private void clearForm() {
        nameField.clear();
        mealTypeComboBox.setValue("Breakfast");
        servingField.clear();
        unitComboBox.setValue("grams");
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatField.clear();
        favoriteCheckBox.setSelected(false);

        // Reset image
        selectedImageFile = null;
        resetImageToPlaceholder();
    }

    // Reset field styles
    private void resetFieldStyles() {
        ValidationUtil.resetFieldStyles(nameField, servingField, caloriesField, proteinField, carbsField, fatField);
        ValidationUtil.resetFieldStyles(mealTypeComboBox, unitComboBox);
    }

    // auto-save setup method
    private void setupAutoSave() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        mealTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        servingField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        unitComboBox.valueProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        caloriesField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        proteinField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        carbsField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        fatField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        favoriteCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> saveToSession());
    }
}
