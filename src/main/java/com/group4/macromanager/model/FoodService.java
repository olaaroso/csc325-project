package com.group4.macromanager.model;

import java.util.List;
import java.util.ArrayList;

public class FoodService {

    public List<Food> getCustomFoods() {
        // FOR NOW: static sample food data is added for styling and structure
        List<Food> customFoods = new ArrayList<>();
        customFoods.add(new Food("1", "Grilled Chicken Breast", 4, "oz", 165, 31, 0, 3.6, null, "Lunch", false));
        customFoods.add(new Food("2", "Greek Yogurt", 6, "oz", 140, 13, 8, 4, "/images/hero-img.png", "Breakfast", false));
        return customFoods;
    }

    public List<Food> getRecommendations() {
        // FOR NOW: static sample food data is added for styling and structure
        List<Food> recommendedFoods = new ArrayList<>();
        recommendedFoods.add(new Food("3", "Salmon", 3, "oz", 140, 29, 0, 6.3, "/images/hero-img.png", "Dinner", false));
        recommendedFoods.add(new Food("4", "Avocado Toast", 97, "g", 189, 3.8, 20, 11, null, "Breakfast", false));
        recommendedFoods.add(new Food("5", "Protein Shake", 11, "fl oz", 280, 30, 5, 3, "/images/hero-img.png", "Snack", false));
        return recommendedFoods;
    }

    public List<Food> getFavorites() {
        // FOR NOW: static sample food data is added for styling and structure
        List<Food> favoriteFoods = new ArrayList<>();
        favoriteFoods.add(new Food("6", "Steak", 6, "oz", 195, 34, 0, 5.6, "/images/hero-img.png", "Dinner", true));
        favoriteFoods.add(new Food("7", "Banana", 4.2, "oz", 118, 1, 27, 0, null, "Snack", true));
        return favoriteFoods;
    }

    public List<Food> searchFoods(String query, String mealType) {
        // Just return recommendations for now (fake search)
        return getRecommendations();
    }
}
