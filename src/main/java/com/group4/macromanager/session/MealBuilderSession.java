package com.group4.macromanager.session;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

// Singleton class to manage meal builder sessions for data persistence
public class MealBuilderSession {
    private static MealBuilderSession instance;

    // Form Data
    private String mealName = "";
    private String mealType = "Breakfast"; // Default meal type
    private String notes = "";
    private boolean isFavorite = false;
    private File selectedImage = null;
    private ObservableList<Food> selectedFoods = FXCollections.observableArrayList();

    // Edit mode tracking
    private boolean isEditMode = false;
    private String editingMealId = null;

    private MealBuilderSession() {
        // Private constructor to prevent instantiation
    }

    // Get the singleton instance
    public static MealBuilderSession getInstance() {
        if (instance == null) {
            instance = new MealBuilderSession();
        }
        return instance;
    }

    // Load existing meal for editing
    public void loadMealForEditing(Meal meal) {
        if (meal == null) return;

        // Update edit mode status
        this.isEditMode = true;
        this.editingMealId = meal.getId();

        // Load meal data into session
        this.mealName = meal.getName() != null ? meal.getName() : "";
        this.mealType = meal.getMealType() != null ? meal.getMealType() : "Breakfast";
        this.notes = meal.getNotes() != null ? meal.getNotes() : "";
        this.isFavorite = meal.isFavorite();

        // Handle image loading
        if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
            this.selectedImage = new File(meal.getImageUrl());
        } else {
            this.selectedImage = null;
        }

        // Load foods
        this.selectedFoods.clear();
        if (meal.getFoods() != null) {
            this.selectedFoods.addAll(meal.getFoods());
        }
    }

    // Start creating new meal
    public void startNewMeal() {
        this.isEditMode = false;
        this.editingMealId = null;
        clearSession();
    }

    // Save current form state
    public void saveFormData(String mealName, String mealType, String notes,
                             boolean isFavorite, File selectedImage) {
        this.mealName = mealName != null ? mealName : "";
        this.mealType = mealType != null ? mealType : "Breakfast";
        this.notes = notes != null ? notes : "";
        this.isFavorite = isFavorite;
        this.selectedImage = selectedImage;
    }

    // Add selected food
    public void addFood(Food food) {
        if (food != null && !selectedFoods.contains(food)) {
            selectedFoods.add(food);
        }
    }

    // Remove selected food
    public void removeFood(Food food) {
        selectedFoods.remove(food);
    }

    // Clear session (after save/cancel)
    public void clearSession() {
        mealName = "";
        mealType = "Breakfast";
        notes = "";
        isFavorite = false;
        selectedImage = null;
        selectedFoods.clear();
    }

    // Getters
    public String getMealName() { return mealName; }
    public String getMealType() { return mealType; }
    public String getNotes() { return notes; }
    public boolean isFavorite() { return isFavorite; }
    public File getSelectedImage() { return selectedImage; }
    public ObservableList<Food> getSelectedFoods() { return selectedFoods; }
    public boolean isEditMode() { return isEditMode; }
    public String getEditingMealId() { return editingMealId; }
}
