package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.service.IFoodService;
import com.group4.macromanager.service.InMemoryFoodService;
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
    @FXML private ComboBox<String> mealTypeComboBox;
    @FXML private TextField servingField;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private ImageView foodImage;
    @FXML private CheckBox favoriteCheckBox;

    private File selectedImageFile;

    // Initialize FoodService instance
    private IFoodService foodService = new InMemoryFoodService();

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        sidebarIncludeController.setActivePage("customFoods");

        // Default placeholder image
        foodImage.setImage(new Image(getClass().getResource("/images/placeholder.png").toExternalForm()));

        // Pre-fill meal type combo box so validation doesn't initially fail
        mealTypeComboBox.setValue("Breakfast");

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
        String mealType = mealTypeComboBox.getValue();
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
        if (mealType == null || mealType.isEmpty()) {
            mealTypeComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
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

        // Save the food data (in memory for now; will be connected to firestore later)
        Food created = new Food(
                null,
                name,
                Double.parseDouble(serving),
                unit,
                Double.parseDouble(calories),
                Double.parseDouble(protein),
                Double.parseDouble(carbs),
                Double.parseDouble(fat),
                selectedImageFile != null ? selectedImageFile.toURI().toString() : null,
                mealType,
                favoriteCheckBox.isSelected()
        );

        // Persist the custom food (using in-memory service for now, which just generates an ID)
        Food saved = foodService.saveCustomFood(created);
        System.out.println("Saved custom food id: " + saved.getId()
                + "\nname: " + saved.getName()
                + "\nmeal type: " + saved.getMealType()
                + "\nserving: " + saved.getServingSize() + " " + saved.getServingUnit()
                + "\ncalories: " + saved.getCalories()
                + "\nprotein: " + saved.getProtein()
                + "\ncarbs: " + saved.getCarbs()
                + "\nfat: " + saved.getFat()
                + "\nfavorite: " + saved.isFavorite()
                + "\nimage url: " + saved.getImageUrl()
        );

        // Clear the form after saving
        handleCancel();
    }

    // HandleCancel - handler for when the user cancels the entered data
    @FXML
    private void handleCancel() {
        // Clear all the fields
        nameField.clear();
        mealTypeComboBox.setValue("Breakfast"); // Breakfast by default
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
        mealTypeComboBox.setStyle(""); // reset combo box style
        unitComboBox.setStyle(""); // reset combo box style
    }
}
