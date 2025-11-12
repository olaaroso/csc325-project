package com.group4.macromanager.service;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryMealService implements IMealService{
    // In-memory storage for meals (in real app, this would be database/Firestore)
    private List<Meal> meals = new ArrayList<>();

    @Override
    public List<Meal> getMealsForDate(String userId, LocalDate date) {
        return meals.stream()
                .filter(meal -> userId.equals(meal.getUserId()))
                .filter(meal -> meal.getDateOnly().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllMealsForUser(String userId) {
        return meals.stream()
                .filter(meal -> userId.equals(meal.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMeal(String mealId) {
        meals.removeIf(meal -> mealId.equals(meal.getId()));
    }

    // Helper method to add sample meals for testing
    public void addSampleMeals() {
        // Create some sample meals for testing
        List<Food> breakfastFoods = new ArrayList<>();
        breakfastFoods.add(new Food("1", "Egg", 1, "large", 70, 6, 1, 5, null, "Breakfast", false));
        breakfastFoods.add(new Food("2", "Toast", 1, "slice", 80, 3, 15, 1, null, "Breakfast", false));

        Meal breakfastMeal = new Meal("meal1", "Morning Breakfast", "Breakfast", "Healthy start",
                breakfastFoods, false, null);
        breakfastMeal.setUserId("123");

        meals.add(breakfastMeal);

        List<Food> lunchFoods = new ArrayList<>();
        lunchFoods.add(new Food("3", "Chicken Breast", 4, "ounces", 185, 35, 0, 4, null, "Lunch", false));
        lunchFoods.add(new Food("4", "Brown Rice", 0.5, "cup", 110, 2.5, 23, 0.9, null, "Lunch", false));

        Meal lunchMeal = new Meal("meal2", "Power Lunch", "Lunch", "High protein meal",
                lunchFoods, true, null);
        lunchMeal.setUserId("123");

        meals.add(lunchMeal);
    }

    @Override
    public Meal saveMeal(Meal meal) {
        if (meal.getId() == null) {
            meal.setId(generateId());
        }
        meals.add(meal);
        return meal;
    }

    // Simple ID generator for temporary use
    private String generateId() {
        return "meal_" + System.currentTimeMillis();
    }
}
