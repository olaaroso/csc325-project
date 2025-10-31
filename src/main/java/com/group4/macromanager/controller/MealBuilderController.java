package com.group4.macromanager.controller;

import javafx.fxml.FXML;

public class MealBuilderController {

    // FXML elements
    @FXML
    private SidebarController sidebarIncludeController;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        sidebarIncludeController.setActivePage("mealBuilder");
    }
}
