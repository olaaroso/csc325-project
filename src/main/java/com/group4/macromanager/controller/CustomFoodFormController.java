package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.service.IFoodService;
import com.group4.macromanager.service.InMemoryFoodService;
import com.group4.macromanager.util.ValidationUtil;
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

        // Default placeholder image
        // BaseController handles this:
        // foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));

        // Set default values for combo boxes
        mealTypeComboBox.setValue("Breakfast");
        unitComboBox.setValue("grams");
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

        try {
            Food created = createFoodFromForm();
            Food saved = foodService.saveCustomFood(created);

            System.out.println(
                    "Saved custom food: " + saved.getName() + " ID: " + saved.getId()
                    + "\nServing Size: " + saved.getServingSize() + " " + saved.getServingUnit()
                    + "\nCalories: " + saved.getCalories()
                    + "\nProtein: " + saved.getProtein()
                    + "\nCarbs: " + saved.getCarbs()
                    + "\nFat: " + saved.getFat()
                    + "\nMeal Type: " + saved.getMealType()
                    + "\nFavorite: " + saved.isFavorite()
                    + "\nImage URL: " + saved.getImageUrl()
            );
            showSuccessAlert("Custom food saved successfully!");
            handleCancel();
        }
        catch (NumberFormatException e) {
            showAlert("Please fill in all required fields correctly.");
        }
        catch (Exception e) {
            showAlert("Error saving food: " + e.getMessage());
        }
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        clearForm(); // Clear form fields
        resetImageToPlaceholder(); // from BaseController
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
        return new Food(
                null,
                nameField.getText().trim(),
                Double.parseDouble(servingField.getText().trim()),
                unitComboBox.getValue(),
                Double.parseDouble(caloriesField.getText().trim()),
                Double.parseDouble(proteinField.getText().trim()),
                Double.parseDouble(carbsField.getText().trim()),
                Double.parseDouble(fatField.getText().trim()),
                selectedImageFile != null ? selectedImageFile.toURI().toString() : null,
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
    }

    // Reset field styles
    private void resetFieldStyles() {
        ValidationUtil.resetFieldStyles(nameField, servingField, caloriesField, proteinField, carbsField, fatField);
        ValidationUtil.resetFieldStyles(mealTypeComboBox, unitComboBox);
    }
}
