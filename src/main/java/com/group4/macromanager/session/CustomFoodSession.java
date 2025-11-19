package com.group4.macromanager.session;

import com.group4.macromanager.model.Food;

import java.io.File;

public class CustomFoodSession {
    private static CustomFoodSession instance;

    // Form data
    private String foodName = "";
    private String mealType = "Breakfast";
    private String servingSize = "";
    private String servingUnit = "grams";
    private String calories = "";
    private String protein = "";
    private String carbs = "";
    private String fat = "";
    private boolean isFavorite = false;
    private File selectedImage = null;

    // Edit mode tracking
    private boolean isEditMode = false;
    private String editingFoodId = null;

    // Private constructor for singleton
    private CustomFoodSession() {}

    // Get singleton instance
    public static CustomFoodSession getInstance() {
        if (instance == null) {
            instance = new CustomFoodSession();
        }
        return instance;
    }

    // Start new food creation
    public void startNewFood() {
        clearSession();
        isEditMode = false;
        editingFoodId = null;
    }

    // Load food for editing
    public void loadFoodForEditing(Food food) {
        if (food == null) return;

        this.isEditMode = true;
        this.editingFoodId = food.getId();

        // Populate all form fields with the food's data
        this.foodName = food.getName() != null ? food.getName() : "";
        this.mealType = food.getMealType() != null ? food.getMealType() : "Breakfast";
        this.servingSize = food.getServingSize() > 0 ? String.valueOf(food.getServingSize()) : "";
        this.servingUnit = food.getServingUnit() != null ? food.getServingUnit() : "grams";
        this.calories = food.getCalories() > 0 ? String.valueOf(food.getCalories()) : "";
        this.protein = food.getProtein() > 0 ? String.valueOf(food.getProtein()) : "";
        this.carbs = food.getCarbs() > 0 ? String.valueOf(food.getCarbs()) : "";
        this.fat = food.getFat() > 0 ? String.valueOf(food.getFat()) : "";
        this.isFavorite = food.isFavorite();

        // Handle image URL if it exists
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            this.selectedImage = new File(food.getImageUrl());
        } else {
            this.selectedImage = null;
        }
    }

    // Save form data
    public void saveFormData(String name, String type, String serving, String unit,
                             String cal, String prot, String carb, String ft,
                             boolean favorite, File image) {
        this.foodName = name != null ? name : "";
        this.mealType = type != null ? type : "Breakfast";
        this.servingSize = serving != null ? serving : "";
        this.servingUnit = unit != null ? unit : "grams";
        this.calories = cal != null ? cal : "";
        this.protein = prot != null ? prot : "";
        this.carbs = carb != null ? carb : "";
        this.fat = ft != null ? ft : "";
        this.isFavorite = favorite;
        this.selectedImage = image;
    }

    // Clear session
    public void clearSession() {
        foodName = "";
        mealType = "Breakfast";
        servingSize = "";
        servingUnit = "grams";
        calories = "";
        protein = "";
        carbs = "";
        fat = "";
        isFavorite = false;
        selectedImage = null;
        isEditMode = false;
        editingFoodId = null;
    }

    // Getters
    public String getFoodName() { return foodName; }
    public String getMealType() { return mealType; }
    public String getServingSize() { return servingSize; }
    public String getServingUnit() { return servingUnit; }
    public String getCalories() { return calories; }
    public String getProtein() { return protein; }
    public String getCarbs() { return carbs; }
    public String getFat() { return fat; }
    public boolean isFavorite() { return isFavorite; }
    public File getSelectedImage() { return selectedImage; }
    public boolean isEditMode() { return isEditMode; }
    public String getEditingFoodId() { return editingFoodId; }
}
