package com.group4.macromanager.service;

import com.google.cloud.firestore.*;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;
import com.group4.macromanager.util.ImageUtil;
import com.group4.macromanager.util.UserValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirestoreMealService implements IMealService {

    // Firestore instance and meals collection reference
    private final Firestore db;
    private final CollectionReference mealsCollection;

    // Constructor
    public FirestoreMealService() {
        this.db = FirestoreContext.getDb();
        this.mealsCollection = db.collection("meals");
    }

    // -------------------- Implement IMealService methods here --------------------

    // Get meals for a specific date
    @Override
    public List<Meal> getMealsForDate(String requestedUserId, LocalDate date) {
        String currentUserId = UserValidationUtil.validateUserAccess(requestedUserId);

        try {
            /* Convert LocalDate to start and end of day timestamps (Used for Firestore range queries)

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
            */


            Query query = mealsCollection
                    .whereEqualTo("userId", currentUserId);

            QuerySnapshot querySnapshot = query.get().get();

            // Filter by date and sort in memory
            LocalDate targetDate = date;
            return querySnapshot.getDocuments().stream()
                    .map(this::documentToMeal)
                    .filter(meal -> meal.getCreatedDate() != null &&
                            meal.getCreatedDate().toLocalDate().equals(targetDate))
                    .sorted((m1, m2) -> m2.getCreatedDate().compareTo(m1.getCreatedDate()))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get meals for date: " + e.getMessage(), e);
        }
    }

    // Get all meals for a user
    @Override
    public List<Meal> getAllMealsForUser(String requestedUserId) {
        String currentUserId = UserValidationUtil.validateUserAccess(requestedUserId);

        try {
            Query query = mealsCollection
                    .whereEqualTo("userId", currentUserId);

            QuerySnapshot querySnapshot = query.get().get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToMeal)
                    .sorted((m1, m2) -> m2.getCreatedDate().compareTo(m1.getCreatedDate())) // Sort newest first
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get all meals for user: " + e.getMessage(), e);
        }
    }

    // Delete a meal by ID
    @Override
    public void deleteMeal(String mealId) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            DocumentSnapshot document = mealsCollection.document(mealId).get().get();

            if (document.exists()) {
                // Verify ownership
                String mealUserId = document.getString("userId");
                if (!userId.equals(mealUserId)) {
                    throw new SecurityException("Cannot delete meal owned by another user");
                }

                String imageUrl = document.getString("imageUrl");

                // Delete the meal from Firestore
                mealsCollection.document(mealId).delete().get();

                // Clean up the image file if it's an uploaded image
                if (imageUrl != null && imageUrl.startsWith("file:uploads/")) {
                    ImageUtil.deleteUploadedImage(imageUrl);
                }
            } else {
                // Meal doesn't exist, just try to delete anyway
                mealsCollection.document(mealId).delete().get();
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete meal: " + e.getMessage(), e);
        }
    }

    // Save or update a meal
    @Override
    public Meal saveMeal(Meal meal) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            // Generate ID if creating a new meal
            if (meal.getId() == null || meal.getId().isEmpty()) {
                DocumentReference docRef = mealsCollection.document();
                meal.setId(docRef.getId());
            }

            // Set user ID and creation date
            meal.setUserId(userId);
            if (meal.getCreatedDate() == null) {
                meal.setCreatedDate(LocalDateTime.now());
            }

            // Convert Meal to Map for Firestore
            Map<String, Object> mealData = mealToMap(meal, userId);

            // Save to Firestore
            mealsCollection.document(meal.getId()).set(mealData).get();

            return meal;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save meal: " + e.getMessage(), e);
        }
    }

    // Get a meal by ID
    @Override
    public Meal getMealById(String mealId) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            DocumentSnapshot document = mealsCollection.document(mealId).get().get();

            if (document.exists()) {
                // Verify ownership
                String mealUserId = document.getString("userId");
                if (!userId.equals(mealUserId)) {
                    throw new SecurityException("Cannot access meal owned by another user");
                }

                return documentToMeal(document);
            }

            return null;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get meal by ID: " + e.getMessage(), e);
        }
    }

    // -------------------- Helper Methods --------------------

    // Helper method to convert Meal object to Firestore Map
    private Map<String, Object> mealToMap(Meal meal, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", meal.getName());
        data.put("mealType", meal.getMealType());
        data.put("notes", meal.getNotes());
        data.put("isFavorite", meal.isFavorite());
        data.put("imageUrl", meal.getImageUrl());
        data.put("userId", userId);
        data.put("createdAt", FieldValue.serverTimestamp());

        // Convert LocalDateTime to Date for Firestore
        Date createdDate = Date.from(meal.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
        data.put("createdDate", createdDate);

        // Convert foods to list of maps
        if (meal.getFoods() != null) {
            List<Map<String, Object>> foodsList = meal.getFoods().stream()
                    .map(this::foodToMap)
                    .collect(Collectors.toList());
            data.put("foods", foodsList);
        } else {
            data.put("foods", new ArrayList<>());
        }

        return data;
    }

    // Helper method to convert Food to Map
    private Map<String, Object> foodToMap(Food food) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", food.getId());
        data.put("name", food.getName());
        data.put("servingSize", food.getServingSize());
        data.put("servingUnit", food.getServingUnit());
        data.put("calories", food.getCalories());
        data.put("protein", food.getProtein());
        data.put("carbs", food.getCarbs());
        data.put("fat", food.getFat());
        data.put("imageUrl", food.getImageUrl());
        data.put("mealType", food.getMealType());
        data.put("isFavorite", food.isFavorite());
        return data;
    }

    // Helper method to convert Firestore document to Meal object
    private Meal documentToMeal(DocumentSnapshot document) {
        String id = document.getId();
        String name = document.getString("name");
        String mealType = document.getString("mealType");
        String notes = document.getString("notes");
        Boolean isFavorite = document.getBoolean("isFavorite");
        String imageUrl = document.getString("imageUrl");

        // Convert Firestore Timestamp back to LocalDateTime
        Date createdDate = document.getDate("createdDate");
        LocalDateTime localCreatedDate = createdDate != null ?
                createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() :
                LocalDateTime.now();

        // Convert foods from list of maps back to Food objects
        List<Food> foods = new ArrayList<>();
        List<Map<String, Object>> foodsList = (List<Map<String, Object>>) document.get("foods");
        if (foodsList != null) {
            foods = foodsList.stream()
                    .map(this::mapToFood)
                    .collect(Collectors.toList());
        }

        Meal meal = new Meal(id, name, mealType, notes, foods, isFavorite != null && isFavorite, imageUrl);
        meal.setCreatedDate(localCreatedDate);
        meal.setUserId(document.getString("userId"));

        return meal;
    }

    // Helper method to convert Map back to Food object
    private Food mapToFood(Map<String, Object> foodMap) {
        return new Food(
                (String) foodMap.get("id"),
                (String) foodMap.get("name"),
                ((Number) foodMap.get("servingSize")).doubleValue(),
                (String) foodMap.get("servingUnit"),
                ((Number) foodMap.get("calories")).doubleValue(),
                ((Number) foodMap.get("protein")).doubleValue(),
                ((Number) foodMap.get("carbs")).doubleValue(),
                ((Number) foodMap.get("fat")).doubleValue(),
                (String) foodMap.get("imageUrl"),
                (String) foodMap.get("mealType"),
                Boolean.TRUE.equals(foodMap.get("isFavorite"))
        );
    }
}
