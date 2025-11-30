package com.group4.macromanager.service;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.group4.macromanager.BaseServiceTest;
import com.group4.macromanager.model.Food;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.cloud.firestore.WriteResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FirestoreFoodServiceTest extends BaseServiceTest {

    private FirestoreFoodService foodService;

    @BeforeEach
    void setup() {
        foodService = new FirestoreFoodService();
    }

    @Test
    void testSaveCustomFood() throws Exception {
        Food testFood = new Food(null, "Test Food", 100.0, "grams", 200.0, 20.0, 30.0, 10.0, "/test/image.jpg", "Lunch", false);

        when(mockDocument.getId()).thenReturn("generated-id");
        when(mockDocument.set(any())).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(null);

        Food savedFood = foodService.saveCustomFood(testFood);

        assertNotNull(savedFood);
        assertEquals("Test Food", savedFood.getName());
        assertEquals("generated-id", savedFood.getId());
        verify(mockDocument).set(any());
    }

    @Test
    void testDeleteFood_SecurityException() throws Exception {
        when(mockDocument.get()).thenReturn(mockDocumentFuture);
        when(mockDocumentFuture.get()).thenReturn(mockSnapshot);
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.getString("userId")).thenReturn("different-user-id");

        assertThrows(SecurityException.class, () -> foodService.deleteFood("test-food-1"));
        verify(mockDocument, never()).delete();
    }

    @Test
    void testGetCustomFoods() throws Exception {
        when(mockCollection.whereEqualTo("userId", TEST_USER_ID)).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("isCustom", true)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryFuture);
        when(mockQueryFuture.get()).thenReturn(mockQuerySnapshot);

        setupMockFoodDocument();

        // Forward all food field getters
        when(mockQueryDocumentSnapshot.getId()).thenAnswer(i -> mockSnapshot.getId());
        when(mockQueryDocumentSnapshot.getString(anyString()))
                .thenAnswer(i -> mockSnapshot.getString(i.getArgument(0)));
        when(mockQueryDocumentSnapshot.getDouble(anyString()))
                .thenAnswer(i -> mockSnapshot.getDouble(i.getArgument(0)));
        when(mockQueryDocumentSnapshot.getBoolean(anyString()))
                .thenAnswer(i -> mockSnapshot.getBoolean(i.getArgument(0)));
        when(mockQueryDocumentSnapshot.get(anyString()))
                .thenAnswer(i -> mockSnapshot.get((String) i.getArgument(0)));

        // Mock the list of documents returned
        List<QueryDocumentSnapshot> documents = Arrays.asList(mockQueryDocumentSnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documents);

        List<Food> foods = foodService.getCustomFoods(TEST_USER_ID);

        assertNotNull(foods);
        assertEquals(1, foods.size());
        assertEquals("Test Food", foods.get(0).getName());
    }

    @Test
    void testGetFoodById() throws Exception {
        when(mockDocument.get()).thenReturn(mockDocumentFuture);
        when(mockDocumentFuture.get()).thenReturn(mockSnapshot);
        when(mockSnapshot.exists()).thenReturn(true);
        setupMockFoodDocument();

        Food food = foodService.getFoodById("test-food-1");

        assertNotNull(food);
        assertEquals("Test Food", food.getName());
        assertEquals(200.0, food.getCalories());
    }

    private void setupMockFoodDocument() {
        when(mockSnapshot.getId()).thenReturn("test-food-1");
        when(mockSnapshot.getString("name")).thenReturn("Test Food");
        when(mockSnapshot.getDouble("servingSize")).thenReturn(100.0);
        when(mockSnapshot.getString("servingUnit")).thenReturn("grams");
        when(mockSnapshot.getDouble("calories")).thenReturn(200.0);
        when(mockSnapshot.getDouble("protein")).thenReturn(20.0);
        when(mockSnapshot.getDouble("carbs")).thenReturn(30.0);
        when(mockSnapshot.getDouble("fat")).thenReturn(10.0);
        when(mockSnapshot.getString("imageUrl")).thenReturn("/test/image.jpg");
        when(mockSnapshot.getString("mealType")).thenReturn("Lunch");
        when(mockSnapshot.getBoolean("isFavorite")).thenReturn(false);
    }
}
