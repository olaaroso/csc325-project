package com.group4.macromanager.service;

import com.group4.macromanager.model.Food;
import java.util.List;

public interface IFoodService {
    Food saveCustomFood(Food food);
    List<Food> getCustomFoods(String userId);
    List<Food> getRecommendations();
    List<Food> getFavorites(String userId);
    List<Food> searchFoods(String query, String mealType);
}
