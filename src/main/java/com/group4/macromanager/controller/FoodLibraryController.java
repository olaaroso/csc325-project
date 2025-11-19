package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.session.CustomFoodSession;
import com.group4.macromanager.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class FoodLibraryController extends BaseController {

    @FXML private ComboBox<String> mealTypeComboBox;
    @FXML private TextField searchField;
    @FXML private TabPane foodTabPane;
    @FXML private FlowPane customFoodsContainer;
    @FXML private FlowPane recommendationsContainer;
    @FXML private FlowPane favoritesContainer;

    // Initialize function
    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        initializePage("foodLibrary"); // from BaseController

        // Setup filters for search
        mealTypeComboBox.getItems().addAll("All", "Breakfast", "Lunch", "Dinner", "Snack");
        mealTypeComboBox.setValue("All"); // Set to "All" by default

        // Load data for all tabs
        loadCustomFoods();
        loadRecommendations();
        loadFavorites();

        // Remove search results tab when the searchbar is cleared
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.isEmpty()) {
               foodTabPane.getTabs().removeIf(tab -> "Search Results".equals(tab.getText()));

               // Re-focus on custom foods tab (or any other one)
               foodTabPane.getSelectionModel().select(0);
           }
        });
    }

    // Handler functions - for button clicks

    // HandleSearch - handler for search button
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (ValidationUtil.isEmpty(query)) return; // Return if empty

        String type = mealTypeComboBox.getValue();

        // Search for foods using searchFoods() method
        List<Food> results = foodService.searchFoods(query, type); // uses inherited foodService from BaseController

        // Check if a tab already exists
        Tab searchTab = foodTabPane.getTabs().stream()
                .filter(tab -> "Search Results".equals(tab.getText()))
                .findFirst()
                .orElse(null);

        // Dynamically create a search tab and FlowPane container to show the results
        FlowPane searchContainer;
        if (searchTab == null) {
            // Create FlowPane
            searchContainer = new FlowPane();
            searchContainer.setPadding(new Insets(15, 15, 15, 15));
            searchContainer.setHgap(15);
            searchContainer.setVgap(15);
            searchContainer.getStyleClass().add("search-results-container");

            // Create ScrollPane
            ScrollPane scrollPane = new ScrollPane(searchContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            // Create Tab
            searchTab = new Tab("Search Results", scrollPane);
            searchTab.getStyleClass().add("food-tab");
            searchTab.setClosable(true); // Temp tab; therefore, it should be closable

            foodTabPane.getTabs().add(searchTab);
        } else {
            // Reuse existing one
            ScrollPane scrollPane = (ScrollPane) searchTab.getContent();
            searchContainer = (FlowPane) scrollPane.getContent();
            searchContainer.getChildren().clear();
        }

        renderFoods(results, searchContainer);

        // If there are no results, show message
        if (results.isEmpty()) {
            // Show "No results found" message
            showAlert("No results found for: " + query); // inherited alert method from BaseController
        }

        foodTabPane.getSelectionModel().select(searchTab); // Select the search tab automatically after searching
    }

    // HandleAddNewFood - handler for clicking add food button under the custom foods tab
    @FXML
    private void handleAddNewFood() {
        try {
            // Start new food creation
            CustomFoodSession.getInstance().startNewFood();
            PageNavigationManager.switchTo("customFoodFormPage.fxml");
        } catch (IOException e) {
            showAlert("Failed to navigate to food form: " + e.getMessage());
        }
    }

    // Loader functions - fetches food data for each category
    private void loadCustomFoods() {

        // Only load custom foods if user is logged in
        String userId = getCurrentUserId();
        if (userId != null) {
            List<Food> customFoodEntries = foodService.getCustomFoods(userId);
            renderFoods(customFoodEntries, customFoodsContainer, true);
        }
    }

    private void loadFavorites() {

        // Only load favorite foods if user is logged in
        String userId = getCurrentUserId();
        if (userId != null) {
            List<Food> favs = foodService.getFavorites(userId);
            renderFoods(favs, favoritesContainer, true);
        }
    }

    private void loadRecommendations() {
        List<Food> recs = foodService.getRecommendations();
        renderFoods(recs, recommendationsContainer, false); // No edit/delete
    }

    // Handlers for edit and delete actions
    private void handleEditFood(Food food) {
        try {
            // Fetch the complete food object from the service to ensure we have all data
            Food completeFood = foodService.getFoodById(food.getId());

            if (completeFood == null) {
                showAlert("Food not found. It may have been deleted.");
                return;
            }

            // Get current user ID
            String userId = getCurrentUserId();
            if (userId == null) {
                showAlert("User not logged in.");
                return;
            }

            // Load food into session for editing
            CustomFoodSession.getInstance().loadFoodForEditing(food);
            PageNavigationManager.switchTo("customFoodFormPage.fxml");
        } catch (IOException e) {
            showAlert("Failed to navigate to edit form: " + e.getMessage());
        }
    }

    private void handleDeleteFood(Food food, FlowPane container) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Food");
        alert.setHeaderText("Delete " + food.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    foodService.deleteFood(food.getId());
                    showSuccessAlert("Food deleted successfully!");
                    // Refresh the current tab
                    refreshCurrentTab();
                } catch (Exception e) {
                    showAlert("Failed to delete food: " + e.getMessage());
                }
            }
        });
    }

    // Refresh the currently selected tab
    private void refreshCurrentTab() {
        // Refresh the currently selected tab
        Tab selectedTab = foodTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            switch (selectedTab.getText()) {
                case "Custom Foods":
                    loadCustomFoods();
                    break;
                case "Favorites":
                    loadFavorites();
                    break;
                case "Recommendations":
                    loadRecommendations();
                    break;
            }
        }
    }

    // Render functions - renders each food item as a foodCard and dynamically adds them to their respective container
    private void renderFoods(List<Food> foodEntries, FlowPane container) {
        renderFoods(foodEntries, container, false);
    }

    private void renderFoods(List<Food> foodEntries, FlowPane container, boolean showEditDelete) {
        container.getChildren().clear();
        for (Food food : foodEntries) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/foodCard.fxml"));
                HBox card = loader.load();

                FoodCardController controller = loader.getController();
                controller.setFood(food);

                if (showEditDelete) {
                    controller.setEditDeleteMode(true,
                            () -> handleEditFood(food),
                            () -> handleDeleteFood(food, container));
                }

                controller.updateView();
                container.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
