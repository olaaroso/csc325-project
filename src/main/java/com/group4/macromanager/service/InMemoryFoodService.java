package com.group4.macromanager.service;

import com.group4.macromanager.model.Food;

import java.util.ArrayList;
import java.util.List;

public class InMemoryFoodService implements IFoodService {

    // Temporary in-memory storage for custom foods
    private List<Food> customFoods = new ArrayList<>();

    // Initial foods
    public InMemoryFoodService() {
        // Add some initial custom foods for testing
        customFoods.add(new Food("1", "Chicken Breast", 6, "ounces", 280, 33, 0, 6, null, "Lunch", false));
        customFoods.add(new Food("2", "Brown Rice", 1, "cup", 215, 5, 45, 1.6, null, "Dinner", false));
    }

    @Override
    public Food saveCustomFood(Food food) {
        if (food.getId() == null) {
            // New food
            food.setId(generateId());
            customFoods.add(food);
        } else {
            // Update existing
            customFoods.removeIf(f -> food.getId().equals(f.getId()));
            customFoods.add(food);
        }
        return food;
    }

    @Override
    public Food updateFood(Food food) {
        // Not implemented in this in-memory version
        return null;
    }

    @Override
    public void deleteFood(String foodId) {
        customFoods.removeIf(food -> foodId.equals(food.getId()));
    }

    @Override
    public Food getFoodById(String foodId) {
        return customFoods.stream()
                .filter(food -> foodId.equals(food.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Food> getCustomFoods(String userId) {
        return new ArrayList<>(customFoods); // Return actual custom foods
    }

    @Override
    public List<Food> getRecommendations() {
        List<Food> rec = new ArrayList<>();
        rec.add(new Food("3", "Salmon", 3, "ounces", 140, 29, 0, 6.3, "/images/hero-img.png", "Dinner", false));
        rec.add(new Food("4", "Avocado Toast", 97, "grams", 189, 3.8, 20, 11, null, "Breakfast", false));
        return rec;
    }

    @Override
    public List<Food> getFavorites(String userId) {
        List<Food> fav = new ArrayList<>();
        fav.add(new Food("6", "Steak", 6, "ounces", 195, 34, 0, 5.6, "/images/hero-img.png", "Dinner", true));
        fav.add(new Food("7", "Banana", 4.2, "ounces", 118, 1, 27, 0, null, "Snack", true));
        return fav;
    }

    @Override
    public List<Food> searchFoods(String query, String mealType) {
        // temporary: return recommendations (replace with real search later)
        return getRecommendations();
    }

    // Simple ID generator for temporary use
    private String generateId() {
        return "food_" + System.currentTimeMillis();
    }
}