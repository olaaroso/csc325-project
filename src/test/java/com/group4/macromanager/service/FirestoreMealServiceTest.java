package com.group4.macromanager.service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.group4.macromanager.BaseServiceTest;
import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.cloud.firestore.WriteResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FirestoreMealServiceTest extends BaseServiceTest {

    private FirestoreMealService mealService;

    @BeforeEach
    void setup() {
        mealService = new FirestoreMealService();
    }

    @Test
    void testSaveMeal() throws Exception {
        Food testFood = new Food("food-1", "Test Food", 100.0, "grams", 200.0, 20.0, 30.0, 10.0, "/test/image.jpg", "Lunch", false);
        Meal testMeal = new Meal("meal-1", "Test Meal", "Lunch", "Test notes", Arrays.asList(testFood), false, "/meal/image.jpg");

        when(mockDocument.getId()).thenReturn("generated-meal-id");
        when(mockDocument.set(any())).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(null);

        Meal savedMeal = mealService.saveMeal(testMeal);

        assertNotNull(savedMeal);
        assertEquals("Test Meal", savedMeal.getName());
        assertEquals(TEST_USER_ID, savedMeal.getUserId());
        verify(mockDocument).set(any());
    }

    @Test
    void testDeleteMeal() throws Exception {
        when(mockDocument.get()).thenReturn(mockDocumentFuture);
        when(mockDocumentFuture.get()).thenReturn(mockSnapshot);
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.getString("userId")).thenReturn(TEST_USER_ID);
        when(mockSnapshot.getString("imageUrl")).thenReturn("/test/image.jpg");
        when(mockDocument.delete()).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(null);

        assertDoesNotThrow(() -> mealService.deleteMeal("test-meal-1"));
        verify(mockDocument).delete();
    }

    @Test
    void testGetMealsForDate() throws Exception {
        LocalDate testDate = LocalDate.now();
        LocalDateTime testDateTime = testDate.atStartOfDay();
        Date firestoreDate = Date.from(testDateTime.atZone(ZoneId.systemDefault()).toInstant());

        when(mockCollection.whereEqualTo("userId", TEST_USER_ID)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryFuture);
        when(mockQueryFuture.get()).thenReturn(mockQuerySnapshot);

        setupMockMealDocument(firestoreDate);

        // Forward all meal field getters
        when(mockQueryDocumentSnapshot.getId()).thenAnswer(i -> mockSnapshot.getId());
        when(mockQueryDocumentSnapshot.getString(anyString()))
                .thenAnswer(i -> mockSnapshot.getString(i.getArgument(0)));
        when(mockQueryDocumentSnapshot.getBoolean(anyString()))
                .thenAnswer(i -> mockSnapshot.getBoolean(i.getArgument(0)));
        when(mockQueryDocumentSnapshot.getDate(anyString()))
                .thenAnswer(i -> mockSnapshot.getDate(i.getArgument(0)));
        when(mockQueryDocumentSnapshot.get(anyString()))
                .thenAnswer(i -> mockSnapshot.get((String) i.getArgument(0)));

        List<QueryDocumentSnapshot> documents = Arrays.asList(mockQueryDocumentSnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documents);

        List<Meal> meals = mealService.getMealsForDate(TEST_USER_ID, testDate);

        assertNotNull(meals);
        assertEquals(1, meals.size());
        assertEquals("Test Meal", meals.get(0).getName());
    }

    private void setupMockMealDocument(Date createdDate) {
        when(mockSnapshot.getId()).thenReturn("test-meal-1");
        when(mockSnapshot.getString("name")).thenReturn("Test Meal");
        when(mockSnapshot.getString("mealType")).thenReturn("Lunch");
        when(mockSnapshot.getString("notes")).thenReturn("Test notes");
        when(mockSnapshot.getBoolean("isFavorite")).thenReturn(false);
        when(mockSnapshot.getString("imageUrl")).thenReturn("/meal/image.jpg");
        when(mockSnapshot.getString("userId")).thenReturn(TEST_USER_ID);
        when(mockSnapshot.getDate("createdDate")).thenReturn(createdDate);

        // Mock foods list
        Map<String, Object> foodMap = new HashMap<>();
        foodMap.put("id", "food-1");
        foodMap.put("name", "Test Food");
        foodMap.put("servingSize", 100.0);
        foodMap.put("servingUnit", "grams");
        foodMap.put("calories", 200.0);
        foodMap.put("protein", 20.0);
        foodMap.put("carbs", 30.0);
        foodMap.put("fat", 10.0);
        foodMap.put("imageUrl", "/test/image.jpg");
        foodMap.put("mealType", "Lunch");
        foodMap.put("isFavorite", false);

        when(mockSnapshot.get("foods")).thenReturn(Arrays.asList(foodMap));
    }
}
