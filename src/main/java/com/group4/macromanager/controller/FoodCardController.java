package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.session.MealBuilderSession;
import com.group4.macromanager.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class FoodCardController {

    // FXML injected UI components
    @FXML private HBox cardRoot;
    @FXML private ImageView foodImage;
    @FXML private Label foodName;
    @FXML private Label foodMacros;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private HBox buttonContainer;

    // Food data model
    private Food food;

    // flag to indicate if in meal builder context
    private boolean isInMealBuilder = false;

    // flag to control visibility of edit/delete buttons
    private boolean showEditDeleteButtons = false;

    // Callbacks for edit/delete actions
    private Runnable onEditCallback;
    private Runnable onDeleteCallback;

    // Set food data
    public void setFood(Food food) {
        this.food = food;
    }

    // Set meal builder mode
    public void setMealBuilderMode(boolean isInMealBuilder) {
        // Update internal flag
        this.isInMealBuilder = isInMealBuilder;

        // Update button visibility
        updateButtonVisibility();
    }

    // Set edit/delete button visibility
    public void setEditDeleteMode(boolean showEditDelete, Runnable onEdit, Runnable onDelete) {
        this.showEditDeleteButtons = showEditDelete;
        this.onEditCallback = onEdit;
        this.onDeleteCallback = onDelete;
        updateButtonVisibility();
    }

    // Handle add/remove button action
    @FXML
    public void initialize() {
        if (addButton != null) {
            addButton.setOnAction(e -> handleAddButton());
        }
        if (editButton != null) {
            editButton.setOnAction(e -> handleEditButton());
        }
        if (deleteButton != null) {
            deleteButton.setOnAction(e -> handleDeleteButton());
        }
        updateButtonVisibility();
    }

    // Update button visibility based on mode
    private void updateButtonVisibility() {
        if (addButton == null) return;

        if (isInMealBuilder) {
            // Meal builder mode: replace add with remove
            addButton.setVisible(true);
            addButton.setText("Remove");
            if (editButton != null) editButton.setVisible(false);
            if (deleteButton != null) deleteButton.setVisible(false);
        }
        else if (showEditDeleteButtons) {
            // Edit/delete mode for custom/favorite foods
            addButton.setVisible(true);
            addButton.setText("Add");
            if (editButton != null) editButton.setVisible(true);
            if (deleteButton != null) deleteButton.setVisible(true);
        }
        else {
            // Default mode: only show add button
            addButton.setVisible(true);
            addButton.setText("Add");
            if (editButton != null) editButton.setVisible(false);
            if (deleteButton != null) deleteButton.setVisible(false);
        }
    }

    // Button action handlers

    // Handle add/remove button logic
    private void handleAddButton() {
        if (isInMealBuilder) {
            // Remove food from meal builder session
            MealBuilderSession.getInstance().removeFood(food);
        }
        else {
            // Add to meal builder and navigate back
            MealBuilderSession.getInstance().addFood(food);
            try {
                PageNavigationManager.switchTo("mealBuilderPage.fxml");
            }
            catch (IOException e) {
                AlertUtil.showWarning("Failed to return to meal builder: " + e.getMessage());
            }
        }
    }

    // Handle edit button logic
    private void handleEditButton() {
        if (onEditCallback != null) {
            onEditCallback.run();
        }
    }

    // Handle delete button logic
    private void handleDeleteButton() {
        if (onDeleteCallback != null) {
            onDeleteCallback.run();
        }
    }

            @FXML
    public void updateView() {
        if (food == null) return;

        // Set basic info
        foodName.setText(food.getName());
        foodMacros.setText(String.format("%.0f cal | P: %.0fg | C: %.0fg | F: %.0fg",
                food.getCalories(), food.getProtein(), food.getCarbs(), food.getFat()));

        // Handle image with fallbacks
        Image image = null;
        String imageUrl = food.getImageUrl();

        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Try loading from resources (e.g. /images/hero-img.jpg)
                var resource = getClass().getResource(imageUrl);
                if (resource != null) {
                    image = new Image(resource.toExternalForm(), true);
                } else {
                    // Try external URL
                    image = new Image(imageUrl, true);
                }
            }

            // Fallback if image failed or not found
            if (image == null || image.isError()) {
                var fallback = getClass().getResource("/images/placeholder.png");
                if (fallback != null) {
                    image = new Image(fallback.toExternalForm(), true);
                } else {
                    System.err.println("No fallback image found at /images/placeholder.png");
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to load image: " + imageUrl + " — " + e.getMessage());
            var fallback = getClass().getResource("/images/placeholder.png");
            if (fallback != null) {
                image = new Image(fallback.toExternalForm(), true);
            }
        }

        // Apply the image
        foodImage.setImage(image);
    }

    public HBox getView() {
        return cardRoot;
    }
}
