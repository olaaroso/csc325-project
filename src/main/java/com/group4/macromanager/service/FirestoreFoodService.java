package com.group4.macromanager.service;

import com.google.cloud.firestore.*;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.model.Food;
import com.group4.macromanager.session.AuthSessionManager;
import com.group4.macromanager.util.ImageUtil;
import com.group4.macromanager.util.UserValidationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirestoreFoodService implements IFoodService {

    // Firestore instance and foods collection reference
    private final Firestore db;
    private final CollectionReference foodsCollection;

    // Constructor
    public FirestoreFoodService() {
        this.db = FirestoreContext.getDb();
        this.foodsCollection = db.collection("foods");
    }

    // -------------------- Implement IFoodService methods --------------------

    // Save custom food method
    @Override
    public Food saveCustomFood(Food food) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            // Generate ID if creating a new food
            if (food.getId() == null || food.getId().isEmpty()) {
                DocumentReference docRef = foodsCollection.document();
                food.setId(docRef.getId());
            }

            // Convert Food to Map for Firestore (key-value pairs)
            Map<String, Object> foodData = foodToMap(food, userId);

            // Save to Firestore
            foodsCollection.document(food.getId()).set(foodData).get();

            return food;
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save food: " + e.getMessage(), e);
        }
    }

    // Update existing food method
    @Override
    public Food updateFood(Food food) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            if (food.getId() == null) {
                throw new RuntimeException("Food ID is required for update");
            }

            Map<String, Object> foodData = foodToMap(food, userId);

            DocumentReference docRef = foodsCollection.document(food.getId());
            docRef.set(foodData).get();

            food.setId(food.getId());
            return food;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to update food: " + e.getMessage(), e);
        }
    }

    // Get list of custom foods for a user
    @Override
    public List<Food> getCustomFoods(String requestedUserId) {
        String currentUserId = UserValidationUtil.validateUserAccess(requestedUserId);

        try {
            // Query for foods where userId matches and isCustom is true
            Query query = foodsCollection
                    .whereEqualTo("userId", currentUserId)
                    .whereEqualTo("isCustom", true);

            QuerySnapshot querySnapshot = query.get().get();

            // Convert documents to Food objects
            return querySnapshot.getDocuments().stream()
                    .map(this::documentToFood)
                    .collect(Collectors.toList());
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch custom foods: " + e.getMessage(), e);
        }
    }

    // Get recommendations (predefined foods)
    @Override
    public List<Food> getRecommendations() {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            // Query for foods where isRecommendation is true, limit to 20
            Query query = foodsCollection
                    .whereEqualTo("isRecommendation", true)
                    .limit(20);

            QuerySnapshot querySnapshot = query.get().get();

            // Convert documents to Food objects
            return querySnapshot.getDocuments().stream()
                    .map(this::documentToFood)
                    .collect(Collectors.toList());
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get recommendations: " + e.getMessage(), e);
        }
    }

    // Get favorite foods for a user
    @Override
    public List<Food> getFavorites(String requestedUserId) {
        String currentUserId = UserValidationUtil.validateUserAccess(requestedUserId);
        try {
            // Query for foods where userId matches and isFavorite is true
            Query query = foodsCollection
                    .whereEqualTo("userId", currentUserId)
                    .whereEqualTo("isFavorite", true);

            QuerySnapshot querySnapshot = query.get().get();

            // Convert documents to Food objects
            return querySnapshot.getDocuments().stream()
                    .map(this::documentToFood)
                    .collect(Collectors.toList());
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get favorites: " + e.getMessage(), e);
        }
    }

    // Search foods by query and meal type
    @Override
    public List<Food> searchFoods(String query, String mealType) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            // Start with base query filtered by user
            Query firestoreQuery = foodsCollection
                    .whereEqualTo("userId", userId);

            // Apply meal type filter if not "All"
            if (mealType != null && !mealType.equals("All")) {
                firestoreQuery = firestoreQuery.whereEqualTo("mealType", mealType);
            }

            QuerySnapshot querySnapshot = firestoreQuery.get().get();

            // Filter by name contains query (case-insensitive)
            return querySnapshot.getDocuments().stream()
                    .map(this::documentToFood)
                    .filter(food -> food.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to search foods: " + e.getMessage(), e);
        }
    }

    // Delete food by ID
    @Override
    public void deleteFood(String foodId) {
        String userId = UserValidationUtil.validateUserAccess();
        try {
            // First, get the food to check if it has an uploaded image
            DocumentSnapshot document = foodsCollection.document(foodId).get().get();

            if (document.exists()) {
                // Verify ownership
                String foodUserId = document.getString("userId");
                if (!userId.equals(foodUserId)) {
                    throw new SecurityException("Cannot delete food owned by another user");
                }

                String imageUrl = document.getString("imageUrl");

                // Delete the food from Firestore
                foodsCollection.document(foodId).delete().get();

                // Clean up the image file if it's an uploaded image
                if (imageUrl != null && imageUrl.startsWith("file:uploads/")) {
                    ImageUtil.deleteUploadedImage(imageUrl);
                }
            } else {
                // Food doesn't exist, just try to delete anyway
                foodsCollection.document(foodId).delete().get();
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete food: " + e.getMessage(), e);
        }
    }

    // Get food by ID
    @Override
    public Food getFoodById(String foodId) {
        String userId = UserValidationUtil.validateUserAccess();

        try {
            // Fetch document by ID
            DocumentSnapshot document = foodsCollection.document(foodId).get().get();

            // Convert to Food object if exists
            if (document.exists()) {
                return documentToFood(document);
            }

            // Not found
            return null;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get food by ID: " + e.getMessage(), e);
        }
    }

    // -------------------- Helper Methods --------------------

    // Helper method to convert Food object to Firestore Map
    private Map<String, Object> foodToMap(Food food, String userId) {
        Map<String, Object> data = new HashMap<>();
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

        // Add metadata fields
        data.put("userId", userId);
        data.put("isCustom", true);
        data.put("isRecommendation", false);
        data.put("createdAt", FieldValue.serverTimestamp());

        return data;
    }

    // Helper method to convert Firestore document to Food object
    private Food documentToFood(DocumentSnapshot document) {
        return new Food(
                document.getId(),
                document.getString("name"),
                document.getDouble("servingSize"),
                document.getString("servingUnit"),
                document.getDouble("calories"),
                document.getDouble("protein"),
                document.getDouble("carbs"),
                document.getDouble("fat"),
                document.getString("imageUrl"),
                document.getString("mealType"),
                Boolean.TRUE.equals(document.getBoolean("isFavorite"))
        );
    }

    // Method to initialize sample recommendation foods
    public void initializeRecommendations() {
        try {
            // Check if recommendations already exist
            Query query = foodsCollection.whereEqualTo("isRecommendation", true).limit(1);
            QuerySnapshot existing = query.get().get();

            if (!existing.isEmpty()) {
                return; // Already initialized
            }

            // Add sample recommendations
            List<Food> recommendations = createSampleRecommendations();

            for (Food food : recommendations) {
                Map<String, Object> foodData = new HashMap<>();
                foodData.put("name", food.getName());
                foodData.put("servingSize", food.getServingSize());
                foodData.put("servingUnit", food.getServingUnit());
                foodData.put("calories", food.getCalories());
                foodData.put("protein", food.getProtein());
                foodData.put("carbs", food.getCarbs());
                foodData.put("fat", food.getFat());
                foodData.put("imageUrl", food.getImageUrl());
                foodData.put("mealType", food.getMealType());
                foodData.put("isFavorite", false);
                foodData.put("isCustom", false);
                foodData.put("isRecommendation", true);
                foodData.put("createdAt", FieldValue.serverTimestamp());

                foodsCollection.add(foodData).get();
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to initialize recommendations: " + e.getMessage());
        }
    }

    private List<Food> createSampleRecommendations() {
        List<Food> recommendations = new ArrayList<>();

        recommendations.add(new Food(null, "Grilled Chicken Breast", 100.0, "grams", 165, 31, 0, 3.6, "/images/placeholder.png", "Lunch", false));
        recommendations.add(new Food(null, "Brown Rice", 100.0, "grams", 111, 2.6, 23, 0.9, "/images/placeholder.png", "Lunch", false));
        recommendations.add(new Food(null, "Banana", 1.0, "medium", 105, 1.3, 27, 0.3, "/images/placeholder.png", "Snack", false));
        recommendations.add(new Food(null, "Greek Yogurt", 150.0, "grams", 100, 10, 6, 0.4, "/images/placeholder.png", "Breakfast", false));
        recommendations.add(new Food(null, "Almonds", 30.0, "grams", 174, 6.4, 6.1, 15.2, "/images/placeholder.png", "Snack", false));

        return recommendations;
    }
}
