package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class MealBuilderController {

    // FXML elements
    @FXML private SidebarController sidebarIncludeController;
    @FXML private ComboBox<String> mealTypeCombo;
    @FXML private TextField mealNameField;
    @FXML private TextField notesField;
    @FXML private TextField searchFoodField;
    @FXML private ListView<Food> foodsListView;
    @FXML private Label caloriesLabel, proteinLabel, carbsLabel, fatLabel;
    @FXML private CheckBox favoriteCheckBox;
    @FXML private ImageView foodImage;

    private ObservableList<Food> foods = FXCollections.observableArrayList();
    private File selectedImageFile;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        sidebarIncludeController.setActivePage("mealBuilder");

        // Default placeholder image
        foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));

        // Pre-fill meal type combo box so validation doesn't initially fail
        mealTypeCombo.setValue("Breakfast"); // Default value

        // Initialize foodListView with list of foods
        foodsListView.setItems(foods);
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
    }

    // Handler functions

    // handleAddFood - adds a food item based on search input
    @FXML
    private void handleAddFood() {
        String name = searchFoodField.getText().trim();
        if (name.isEmpty()) return;

        // Simple placeholder food item
        Food item = new Food("11", name, 5, "grams", 230, 21, 25, 4, "/images/hero-img.png", "Snack", false);
        foods.add(item);
        searchFoodField.clear();
        updateTotals();
    }

    // handleClearFoods - clears all food items from the meal
    @FXML
    private void handleClearFoods() {
        foods.clear();
        updateTotals();
    }

    // Handle Upload - handles uploaded pictures
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

    // Handle Save - handler for when the user saves the entered form data
    @FXML
    private void handleSave() {
        // Trimmed field values
        String mealName = mealNameField.getText().trim();
        String mealType = mealTypeCombo.getValue();
        String notes = notesField.getText().trim();

        // Reset previous
        resetFieldStyles();

        // Validation flag
        boolean isValid = true;

        // Check for empty required fields
        if (mealName.isEmpty()) {
            markInvalid(mealNameField);
            isValid = false;
        }
        if (mealType == null || mealType.isEmpty()) {
            mealTypeCombo.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            isValid = false;
        }
        // At least one food item must be added
        if (foods.isEmpty()) {
            showAlert("Please add at least one food item to the meal.");
            isValid = false;
        }

        // Stop processing if validation failed
        if (!isValid) {
            showAlert("Please fill out all the required fields.");
            return;
        }

        System.out.printf(
                "Saved meal: %s (%s) with %d foods. Favorite: %s%n",
                mealNameField.getText(),
                mealTypeCombo.getValue(),
                foods.size(),
                favoriteCheckBox.isSelected() ? "Yes" : "No"
        );

        // Clear form after saving
        handleCancel();
    }

    // Handle Cancel - clears the form inputs
    @FXML
    private void handleCancel() {
        mealTypeCombo.setValue("Breakfast"); // Reset to default
        mealNameField.clear();
        notesField.clear();
        favoriteCheckBox.setSelected(false);
        foods.clear();
        selectedImageFile = null;
        updateTotals(); // Reset totals
        foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm())); // Reset image
    }

    // Utility functions

    // updateTotals - updates the nutritional totals based on current foods
    private void updateTotals() {
        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;
        for (Food f : foods) {
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

    // showAlert - shows an alert dialog with the given message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // markInvalid - marks a TextField as invalid with red border
    private void markInvalid(TextField field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
    }

    // resetFieldStyles - clears validation styling before new checks
    private void resetFieldStyles() {
        TextField[] fields = {
                mealNameField,
        };
        for (TextField field : fields) {
            field.setStyle("");
        }
        mealTypeCombo.setStyle(""); // Reset combo box style
    }
}
