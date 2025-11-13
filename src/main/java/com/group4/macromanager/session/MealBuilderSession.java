package com.group4.macromanager.session;

import com.group4.macromanager.model.Food;
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
}
