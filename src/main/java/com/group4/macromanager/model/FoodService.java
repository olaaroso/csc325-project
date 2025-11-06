package com.group4.macromanager.model;

import java.util.List;
import java.util.ArrayList;

public class FoodService {

    public List<Food> getCustomFoods() {
        // FOR NOW: static sample food data is added for styling and structure
        List<Food> customFoods = new ArrayList<>();
        customFoods.add(new Food("1", "Grilled Chicken Breast", 4, "ounces", 165, 31, 0, 3.6, null, "Lunch", false));
        customFoods.add(new Food("2", "Greek Yogurt", 6, "ounces", 140, 13, 8, 4, "/images/hero-img.png", "Breakfast", false));
        return customFoods;
    }

    public List<Food> getRecommendations() {
        // FOR NOW: static sample food data is added for styling and structure
        List<Food> recommendedFoods = new ArrayList<>();
        recommendedFoods.add(new Food("3", "Salmon", 3, "ounces", 140, 29, 0, 6.3, "/images/hero-img.png", "Dinner", false));
        recommendedFoods.add(new Food("4", "Avocado Toast", 97, "grams", 189, 3.8, 20, 11, null, "Breakfast", false));
        return recommendedFoods;
    }

    public List<Food> getFavorites() {
        // FOR NOW: static sample food data is added for styling and structure
        List<Food> favoriteFoods = new ArrayList<>();
        favoriteFoods.add(new Food("6", "Steak", 6, "ounces", 195, 34, 0, 5.6, "/images/hero-img.png", "Dinner", true));
        favoriteFoods.add(new Food("7", "Banana", 4.2, "ounces", 118, 1, 27, 0, null, "Snack", true));
        return favoriteFoods;
    }

    public List<Food> searchFoods(String query, String mealType) {
        // Just return recommendations for now (fake search)
        return getRecommendations();
    }
}
