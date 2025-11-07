package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.service.IFoodService;
import com.group4.macromanager.service.InMemoryFoodService;
import com.group4.macromanager.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class MealBuilderController extends BaseController {

    // FXML elements
    @FXML private ComboBox<String> mealTypeCombo;
    @FXML private TextField mealNameField;
    @FXML private TextField notesField;
    @FXML private TextField searchFoodField;
    @FXML private ListView<Food> foodsListView;
    @FXML private Label caloriesLabel, proteinLabel, carbsLabel, fatLabel;
    @FXML private CheckBox favoriteCheckBox;

    private ObservableList<Food> foodEntries = FXCollections.observableArrayList();

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        initializePage("mealBuilder"); // from BaseController

        // Default placeholder image
        // BaseController handles this:
        // foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));

        // Setup meal type combo box
        mealTypeCombo.setValue("Breakfast"); // Default value

        // Setup ListView
        foodsListView.setItems(foodEntries);
        foodsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Food food, boolean empty) {
                super.updateItem(food, empty);
                if (empty || food == null) {
                    setText(null);
                } else {
                    setText(food.getName() + " — " + food.getServingSize() + " " + food.getServingUnit());
                }
            }
        });

        updateTotals();
    }

    // Handler functions

    @FXML
    private void handleAddFood() {
        // Search for food and add the first matching result
        String query = searchFoodField.getText().trim();
        if (ValidationUtil.isEmpty(query)) return;

        // Search for food
        var results = foodService.searchFoods(query, mealTypeCombo.getValue());
        if (!results.isEmpty()) {
            foodEntries.add(results.get(0));
            updateTotals();
        } else {
            showAlert("No foods found matching: " + query);
        }
        searchFoodField.clear(); // Clear search field after adding
    }

    // HandleClearFoods - clears all food entries from the meal
    @FXML
    private void handleClearFoods() {
        foodEntries.clear(); // Clear all food entries
        updateTotals(); // Update totals display
    }

    // HandleSave - handler for when the user saves the entered meal data
    @FXML
    private void handleSave() {
        // Validate form
        if (!validateForm()) {
            showAlert("Please fill out all the required fields.");
            return;
        }

        // Simulate saving meal (in a real app, save to database or service)
        System.out.printf(
                "Saved meal: %s (%s) with %d food items. Favorite: %s%n",
                mealNameField.getText(),
                mealTypeCombo.getValue(),
                foodEntries.size(),
                favoriteCheckBox.isSelected() ? "Yes" : "No"
        );

        showSuccessAlert("Meal saved successfully!"); // from BaseController
        handleCancel(); // Clear form after saving
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        clearForm(); // Clear form fields
        resetImageToPlaceholder(); // from BaseController
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
        if (foodEntries.isEmpty()) {
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
        foodEntries.clear(); // Clear food entries
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
        for (Food f : foodEntries) {
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
}
