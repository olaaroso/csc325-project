package com.group4.macromanager.service;

import com.group4.macromanager.model.Meal;

import java.time.LocalDate;
import java.util.List;

public interface IMealService {

    List<Meal> getMealsForDate(String userId, LocalDate date);
    List<Meal> getAllMealsForUser(String userId);
    void deleteMeal(String mealId);
    Meal saveMeal(Meal meal);
}
