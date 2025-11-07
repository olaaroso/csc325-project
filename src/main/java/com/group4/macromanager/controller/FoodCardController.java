package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class FoodCardController {

    @FXML private HBox cardRoot;
    @FXML private ImageView foodImage;
    @FXML private Label foodName;
    @FXML private Label foodMacros;
    @FXML private Button addButton;

    private Food food;

    public void setFood(Food food) {
        this.food = food;
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
