package com.group4.macromanager.controller;

// This is a base controller class for shared functionality among controllers.

import com.group4.macromanager.service.IFoodService;
import com.group4.macromanager.service.IMealService;
import com.group4.macromanager.service.InMemoryFoodService;
import com.group4.macromanager.service.InMemoryMealService;
import com.group4.macromanager.util.AlertUtil;
import com.group4.macromanager.util.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.io.File;

public abstract class BaseController {

    // FXML
    @FXML protected SidebarController sidebarIncludeController;
    @FXML protected ImageView foodImage;

    // Services
    protected IFoodService foodService = new InMemoryFoodService();
    protected IMealService mealService = new InMemoryMealService();
    protected File selectedImageFile;

    // Initialize page with active sidebar highlighting and placeholder image
    protected void initializePage(String activePage) {
        if (sidebarIncludeController != null) {
            sidebarIncludeController.setActivePage(activePage);
        }
        if (foodImage != null) {
            ImageUtil.setPlaceholderImage(foodImage);
        }
    }

    // HandleUpload - handles uploaded pictures
    @FXML
    protected void handleUpload() {
        selectedImageFile = ImageUtil.selectImageFile();
        if (selectedImageFile != null && foodImage != null) {
            ImageUtil.setImageFromFile(foodImage, selectedImageFile);
        }
    }

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
