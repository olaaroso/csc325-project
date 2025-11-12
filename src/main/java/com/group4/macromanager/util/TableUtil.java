package com.group4.macromanager.util;

import com.group4.macromanager.model.Meal;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

// Utility class for common table operations across the application
public class TableUtil {



    // Sets up daily entries table columns with standard meal data bindings
    public static void setupMealTableColumns(
            TableColumn<Meal, String> foodNameColumn,
            TableColumn<Meal, String> mealTypeColumn,
            TableColumn<Meal, Integer> servingColumn,
            TableColumn<Meal, Double> caloriesColumn,
            TableColumn<Meal, Double> proteinColumn,
            TableColumn<Meal, Double> carbsColumn,
            TableColumn<Meal, Double> fatColumn,
            TableView<Meal> tableView,
            ObservableList<Meal> dataList,
            Button editButton,
            Button deleteButton) {

        // Set up cell value factories
        foodNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        mealTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMealType()));

        servingColumn.setCellValueFactory(cellData -> {
            int foodCount = cellData.getValue().getFoods() != null ?
                    cellData.getValue().getFoods().size() : 0;
            return new SimpleIntegerProperty(foodCount).asObject();
        });

        caloriesColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalCalories()).asObject());

        proteinColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalProtein()).asObject());

        carbsColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalCarbs()).asObject());

        fatColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalFat()).asObject());

        // Bind table to data list
        tableView.setItems(dataList);

        // Set up selection listener for buttons
        if (editButton != null && deleteButton != null) {
            tableView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        boolean hasSelection = newSelection != null;
                        editButton.setDisable(!hasSelection);
                        deleteButton.setDisable(!hasSelection);
                    }
            );
        }
    }

    // Calculates total nutritional summary from a list of meals
    public static NutritionalSummary calculateDailySummary(List<Meal> meals) {
        double totalCalories = meals.stream().mapToDouble(Meal::getTotalCalories).sum();
        double totalProtein = meals.stream().mapToDouble(Meal::getTotalProtein).sum();
        double totalCarbs = meals.stream().mapToDouble(Meal::getTotalCarbs).sum();
        double totalFat = meals.stream().mapToDouble(Meal::getTotalFat).sum();

        return new NutritionalSummary(totalCalories, totalProtein, totalCarbs, totalFat);
    }

    // Data class to hold nutritional summary totals
    public static class NutritionalSummary {
        public final double calories;
        public final double protein;
        public final double carbs;
        public final double fat;

        public NutritionalSummary(double calories, double protein, double carbs, double fat) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }
}
