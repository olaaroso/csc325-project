package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.FoodService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class FoodLibraryController {

    @FXML private ComboBox<String> mealTypeComboBox;
    @FXML private TextField searchField;
    @FXML private TabPane foodTabPane;
    @FXML private FlowPane customFoodsContainer;
    @FXML private FlowPane recommendationsContainer;
    @FXML private FlowPane favoritesContainer;

    // Initialize foodService instance for making calls to the backend
    private final FoodService foodService = new FoodService();

    // Initialize function
    @FXML
    public void initialize() {
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
        String type = mealTypeComboBox.getValue();

        // Return out of the handler if the entered query is empty
        if (query.isEmpty()) return;

        // Search for foods using searchFoods() method
        List<Food> results = foodService.searchFoods(query, type);

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
            Label noResults = new Label("No results found");
            noResults.getStyleClass().add("no-results-label");
            searchContainer.getChildren().add(noResults);
        }

        foodTabPane.getSelectionModel().select(searchTab); // Select the search tab automatically after searching
    }

    // HandleAddNewFood - handler for clicking add food button under the custom foods tab
    @FXML
    private void handleAddNewFood() {
        try {
            PageNavigationManager.switchTo("customFoodFormPage.fxml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loader functions - fetches food data for each category
    private void loadCustomFoods() {
        List<Food> customFoods = foodService.getCustomFoods();
        renderFoods(customFoods, customFoodsContainer);
    }

    private void loadRecommendations() {
        List<Food> recs = foodService.getRecommendations();
        renderFoods(recs, recommendationsContainer);
    }

    private void loadFavorites() {
        List<Food> favs = foodService.getFavorites();
        renderFoods(favs, favoritesContainer);
    }

    // Render functions - renders each food item as a foodCard and dynamically adds them to their respective container
    private void renderFoods(List<Food> foods, FlowPane container) {
        container.getChildren().clear();
        for (Food food : foods) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/foodCard.fxml"));
                HBox card = loader.load();

                FoodCardController controller = loader.getController();
                controller.setFood(food);
                controller.updateView();

                container.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
