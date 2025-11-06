package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;

public class MealBuilderController {

    // FXML elements
    @FXML private SidebarController sidebarIncludeController;
    @FXML private ComboBox<String> mealTypeCombo;
    @FXML private TextField mealNameField;
    @FXML private TextArea notesField;
    @FXML private TextField searchFoodField;
    @FXML private ListView<Food> foodsListView;
    @FXML private Label caloriesLabel, proteinLabel, carbsLabel, fatLabel;
    @FXML private CheckBox favoriteCheckBox;

    private ObservableList<Food> foods = FXCollections.observableArrayList();
    private File attachedImage;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        sidebarIncludeController.setActivePage("mealBuilder");

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

    // Handlers
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

    @FXML
    private void handleClearFoods() {
        foods.clear();
        updateTotals();
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        attachedImage = fileChooser.showOpenDialog(null);
    }

    @FXML
    private void handleSave() {
        if (mealTypeCombo.getValue() == null || mealNameField.getText().isEmpty()) {
            showAlert("Please fill out Meal Type and Meal Name before saving.");
            return;
        }

        System.out.printf(
                "Saved meal: %s (%s) with %d foods. Favorite: %s%n",
                mealNameField.getText(),
                mealTypeCombo.getValue(),
                foods.size(),
                favoriteCheckBox.isSelected() ? "Yes" : "No"
        );

        // Clear form
        handleCancel();
    }

    @FXML
    private void handleCancel() {
        mealTypeCombo.setValue(null);
        mealNameField.clear();
        notesField.clear();
        favoriteCheckBox.setSelected(false);
        foods.clear();
        attachedImage = null;
        updateTotals();
    }

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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
