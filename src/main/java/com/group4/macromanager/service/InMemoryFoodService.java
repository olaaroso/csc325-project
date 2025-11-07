package com.group4.macromanager.service;

import com.group4.macromanager.model.Food;

import java.util.ArrayList;
import java.util.List;

public class InMemoryFoodService implements IFoodService {

    @Override
    public Food saveCustomFood(Food food) {
        food.setId(generateId());
        // persist later to Firestore; for now return with generated id
        return food;
    }

    @Override
    public List<Food> getCustomFoods(String userId) {
        List<Food> custom = new ArrayList<>();
        custom.add(new Food("1", "Grilled Chicken Breast", 4, "ounces", 165, 31, 0, 3.6, null, "Lunch", false));
        custom.add(new Food("2", "Greek Yogurt", 6, "ounces", 140, 13, 8, 4, "/images/hero-img.png", "Breakfast", false));
        return custom;
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
