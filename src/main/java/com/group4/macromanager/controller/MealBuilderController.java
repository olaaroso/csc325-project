package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;
import com.group4.macromanager.service.IFoodService;
import com.group4.macromanager.service.InMemoryFoodService;
import com.group4.macromanager.session.MealBuilderSession;
import com.group4.macromanager.util.ImageUtil;
import com.group4.macromanager.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MealBuilderController extends BaseController {

    // FXML elements
    @FXML private ComboBox<String> mealTypeCombo;
    @FXML private TextField mealNameField;
    @FXML private TextField notesField;
    @FXML private Label caloriesLabel, proteinLabel, carbsLabel, fatLabel;
    @FXML private CheckBox favoriteCheckBox;
    @FXML private FlowPane selectedFoodsContainer;
    @FXML private Button selectFoodsButton;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        initializePage("mealBuilder"); // from BaseController

        // Initialize session
        // BaseController handles this:
        // session = MealBuilderSession.getInstance();

        // Restore form data from session
        restoreFromSession();

        // If not in edit mode and no session data, set defaults
        if (!session.isEditMode() && (mealTypeCombo.getValue() == null || mealTypeCombo.getValue().isEmpty())) {
            mealTypeCombo.setValue("Breakfast");
        }

        // Default placeholder image
        // BaseController handles this:
        // foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));

        // Setup auto-save on form changes
        setupAutoSave();

        // Listen for changes to selected foods
        session.getSelectedFoods().addListener((javafx.collections.ListChangeListener<Food>) change -> {
            updateFoodsDisplay();
            updateTotals();
        });

        // Initial display update
        updateFoodsDisplay();
        updateTotals();

        // Update UI based on edit mode
        updateUiForEditMode();
    }

    // Handler functions

    // HandleSelectFoods - handler for when the user wants to select foods
    @FXML
    private void handleSelectFoods() {
        // Save current form data to session before navigating
        saveToSession();

        try {
            PageNavigationManager.switchTo("foodLibraryPage.fxml");
        }
        catch (IOException e) {
            showAlert("Failed to navigate to Food Library: " + e.getMessage());
        }
    }

    // HandleSave - handler for when the user saves the entered meal data
    @FXML
    private void handleSave() {
        // Validate form
        if (!validateForm()) {
            showAlert("Please fill out all the required fields.");
            return;
        }

        // Get image path safely - handle null case
        String imagePath = null;
        if (selectedImageFile != null) {
            imagePath = selectedImageFile.getAbsolutePath();
        }

        Meal meal;
        if (session.isEditMode()) {
            // Update existing meal
            meal = new Meal(
                    session.getEditingMealId(), // Use existing ID
                    mealNameField.getText(),
                    mealTypeCombo.getValue(),
                    notesField.getText(),
                    new ArrayList<>(session.getSelectedFoods()),
                    favoriteCheckBox.isSelected(),
                    imagePath
            );
        } else {
            // Create new meal
            meal = new Meal(
                    null, // ID will be generated
                    mealNameField.getText(),
                    mealTypeCombo.getValue(),
                    notesField.getText(),
                    new ArrayList<>(session.getSelectedFoods()),
                    favoriteCheckBox.isSelected(),
                    imagePath
            );
        }

        try {
            mealService.saveMeal(meal);
            showSuccessAlert(session.isEditMode() ? "Meal updated successfully!" : "Meal saved successfully!");
            handleCancel(); // Clear form after saving
        }
        catch (Exception e) {
            showAlert("Failed to save meal: " + e.getMessage());
        }
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        session.clearSession(); // Clear session data
        clearForm(); // Clear form fields
        resetImageToPlaceholder(); // from BaseController
    }

    // Ui update method for edit mode
    private void updateUiForEditMode() {
        if (session.isEditMode()) {
            // Update UI elements if needed for edit mode
            // e.g., change button text from "Save" to "Update"
        }
    }

    // Save current form data to session
    @Override
    protected void saveToSession() {
        session.saveFormData(
                mealNameField.getText(),
                mealTypeCombo.getValue(),
                notesField.getText(),
                favoriteCheckBox.isSelected(),
                selectedImageFile
        );
    }

    // Restore form data from session
    @Override
    protected void restoreFromSession() {
        mealNameField.setText(session.getMealName());
        mealTypeCombo.setValue(session.getMealType());
        notesField.setText(session.getNotes());
        favoriteCheckBox.setSelected(session.isFavorite());

        if (session.getSelectedImage() != null) {
            selectedImageFile = session.getSelectedImage();
            ImageUtil.setImageFromFile(foodImage, selectedImageFile);
        }
    }

    // Update the display of selected foods
    private void updateFoodsDisplay() {
        selectedFoodsContainer.getChildren().clear();

        for (Food food : session.getSelectedFoods()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/foodCard.fxml"));
                HBox card = loader.load();

                FoodCardController controller = loader.getController();
                controller.setFood(food);
                controller.setMealBuilderMode(true); // Show remove button
                controller.updateView();

                selectedFoodsContainer.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Helper functions
    private boolean validateForm() {
        // Reset Previous styles
        resetFieldStyles();

        // Validation flag
        boolean isValid = true;

        if (ValidationUtil.isEmpty(mealNameField.getText())) {
            ValidationUtil.markInvalid(mealNameField);
            isValid = false;
        }
        if (ValidationUtil.isComboBoxEmpty(mealTypeCombo)) {
            ValidationUtil.markInvalid(mealTypeCombo);
            isValid = false;
        }
        // No validation for notes (optional)

        // Validate that at least one food item is added
        if (session.getSelectedFoods().isEmpty()) {
            showAlert("Please add at least one food item to the meal.");
            isValid = false;
        }

        return isValid; // Return overall validation result
    }

    // Clear form fields
    private void clearForm() {
        mealTypeCombo.setValue("Breakfast"); // Reset to default
        mealNameField.clear(); // Clear meal name
        notesField.clear(); // Clear notes
        favoriteCheckBox.setSelected(false); // Uncheck favorite
        selectedFoodsContainer.getChildren().clear();
        updateTotals(); // Update totals display
    }

    // Reset field styles
    private void resetFieldStyles() {
        ValidationUtil.resetFieldStyles(mealNameField);
        ValidationUtil.resetFieldStyles(mealTypeCombo);
    }

    // Update total nutritional values
    private void updateTotals() {
        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;
        for (Food f : session.getSelectedFoods()) {
            totalCalories += f.getCalories();
            totalProtein += f.getProtein();
            totalCarbs += f.getCarbs();
            totalFat += f.getFat();
        }
        caloriesLabel.setText("Calories: " + (int) totalCalories);
        proteinLabel.setText("Protein (g): " + (int) totalProtein);
        carbsLabel.setText("Carbs (g): " + (int) totalCarbs);
        fatLabel.setText("Fats (g): " + (int) totalFat);
    }

    // Add listeners to form fields to auto-save on changes
    private void setupAutoSave() {
        mealNameField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        mealTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        notesField.textProperty().addListener((obs, oldVal, newVal) -> saveToSession());
        favoriteCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> saveToSession());
    }
}
