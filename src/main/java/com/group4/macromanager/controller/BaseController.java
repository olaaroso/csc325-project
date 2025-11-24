package com.group4.macromanager.controller;

// This is a base controller class for shared functionality among controllers.

import com.group4.macromanager.model.Meal;
import com.group4.macromanager.model.User;
import com.group4.macromanager.service.*;
import com.group4.macromanager.session.AuthSessionManager;
import com.group4.macromanager.session.CustomFoodSession;
import com.group4.macromanager.session.MealBuilderSession;
import com.group4.macromanager.util.AlertUtil;
import com.group4.macromanager.util.ImageUtil;
import com.group4.macromanager.util.TableUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public abstract class BaseController {

    // FXML
    @FXML protected SidebarController sidebarIncludeController;
    @FXML protected ImageView foodImage;

    // -------------------- Services --------------------

    // Use FirestoreFoodService for persistent storage, InMemoryFoodService for testing
    // protected IFoodService foodService = new InMemoryFoodService();
    protected IFoodService foodService = new FirestoreFoodService();


    protected IMealService mealService = new FirestoreMealService();
    protected File selectedImageFile;

    // Session Management
    protected MealBuilderSession mealSession = MealBuilderSession.getInstance();
    protected CustomFoodSession foodSession = CustomFoodSession.getInstance();

    // -------------------- Initialization --------------------

    // Initialize page with active sidebar highlighting and placeholder image
    protected void initializePage(String activePage) {
        if (sidebarIncludeController != null) {
            sidebarIncludeController.setActivePage(activePage);
        }
        if (foodImage != null) {
            ImageUtil.setPlaceholderImage(foodImage);
        }

        // Restore meal builder session image if available
        if ("mealBuilder".equals(activePage) && mealSession.getSelectedImage() != null) {
            selectedImageFile = mealSession.getSelectedImage();
            ImageUtil.setImageFromFile(foodImage, selectedImageFile);
        }

        // Restore custom food session image if available
        if ("customFoods".equals(activePage) && foodSession.getSelectedImage() != null) {
            selectedImageFile = foodSession.getSelectedImage();
            ImageUtil.setImageFromFile(foodImage, selectedImageFile);
        }
    }

    // -------------------- Auth Session Methods --------------------

    // Get current user ID from auth session
    protected String getCurrentUserId() {
        return AuthSessionManager.getInstance().getCurrentUserId();
    }

    // Get current user from auth session
    protected User getCurrentUser() {
        return AuthSessionManager.getInstance().getCurrentUser();
    }

    // Check if user is logged in
    protected boolean isUserLoggedIn() {
        return AuthSessionManager.getInstance().isLoggedIn();
    }

    // -------------------- Common Methods --------------------

    // Save current form data to session (override in controllers that need it)
    protected void saveToSession() {
        // Default implementation - override in MealBuilderController, CustomFoodController
    }

    // Restore form data from session (override in controllers that need it)
    protected void restoreFromSession() {
        // Default implementation - override in MealBuilderController, CustomFoodController
    }

    // -------------------- Meal Methods --------------------

    // Common meal data loading method
    protected List<Meal> loadMealsForDate(LocalDate date) {
        try {
            // Get current user ID
            String userId = getCurrentUserId();

            // If not logged in, return empty list
            if (userId == null) {
                showAlert("User not logged in.");
                return List.of();
            }

            // Load meals for the specified date
            return mealService.getMealsForDate(userId, date);

        } catch (Exception e) {
            showAlert("Failed to load meals for " + date + ": " + e.getMessage());
            System.out.println("Error loading meals: " + e.getMessage());
            return List.of();
        }
    }

    // Common method to update summary labels
    protected void updateSummaryLabels(List<Meal> meals, Label caloriesLabel, Label proteinLabel,
                                       Label carbsLabel, Label fatLabel) {
        TableUtil.NutritionalSummary summary = TableUtil.calculateDailySummary(meals);


        caloriesLabel.setText(String.format("%.0f", summary.calories));
        proteinLabel.setText(String.format("%.1f", summary.protein));
        carbsLabel.setText(String.format("%.1f", summary.carbs));
        fatLabel.setText(String.format("%.1f", summary.fat));

    }

    // Common meal deletion method
    protected void deleteMealWithConfirmation(Meal meal, ObservableList<Meal> dataList, Runnable refreshCallback) {
        if (meal == null) return;

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Meal");
        confirmAlert.setHeaderText("Are you sure you want to delete this meal?");
        confirmAlert.setContentText("This action cannot be undone.");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                mealService.deleteMeal(meal.getId());
                dataList.remove(meal);
                refreshCallback.run();
                showSuccessAlert("Meal deleted successfully");
            } catch (Exception e) {
                showAlert("Failed to delete meal: " + e.getMessage());
            }
        }
    }

    // Common navigation to meal builder for editing existing meal
    protected void navigateToMealBuilderForEdit(Meal meal) {
        if (meal == null) {
            showAlert("No meal selected for editing.");
            return;
        }

        try {
            // Load meal into session for editing
            mealSession.loadMealForEditing(meal);
            PageNavigationManager.switchTo("mealBuilderPage.fxml");
        } catch (IOException e) {
            showAlert("Failed to open meal builder: " + e.getMessage());
        }
    }

    // Common navigation to meal builder for new meal
    protected void navigateToMealBuilder() {
        try {
            // Start new meal in session
            mealSession.startNewMeal();
            PageNavigationManager.switchTo("mealBuilderPage.fxml");
        } catch (IOException e) {
            showAlert("Failed to open meal builder: " + e.getMessage());
        }
    }

    // -------------------- Image Handling --------------------

    // HandleUpload - handles uploaded pictures
    @FXML
    protected void handleUpload() {
        selectedImageFile = ImageUtil.selectImageFile();
        if (selectedImageFile != null && foodImage != null) {
            ImageUtil.setImageFromFile(foodImage, selectedImageFile);

            // Save to session if in meal builder
            saveToSession();
        }
    }

    // -------------------- Utility Methods --------------------

    // Alert utilities
    protected void showAlert(String message) {
        AlertUtil.showWarning(message);
    }
    protected void showSuccessAlert(String message) {
        AlertUtil.showInfo(message);
    }

    // Reset image to placeholder
    protected void resetImageToPlaceholder() {
        selectedImageFile = null;
        if (foodImage != null) {
            ImageUtil.setPlaceholderImage(foodImage);
        }
    }
}
