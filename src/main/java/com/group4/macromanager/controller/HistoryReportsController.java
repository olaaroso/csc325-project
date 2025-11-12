package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;
import com.group4.macromanager.service.InMemoryFoodService;
import com.group4.macromanager.service.InMemoryMealService;
import com.group4.macromanager.util.ChartUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryReportsController extends BaseController {

    // FXML UI components

    // Date picker and today button
    @FXML private DatePicker datePicker;
    @FXML private Button todayButton;

    // Export and entry management buttons
    @FXML private Button exportCsvButton;
    @FXML private Button editEntryButton;
    @FXML private Button deleteEntryButton;

    // Daily summary labels
    @FXML private Label totalCaloriesLabel;
    @FXML private Label totalProteinLabel;
    @FXML private Label totalCarbsLabel;
    @FXML private Label totalFatLabel;

    // Daily entries table and columns
    @FXML private TableView<Meal> dailyEntriesTable;
    @FXML private TableColumn<Meal, String> foodNameColumn;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, Integer> servingColumn;
    @FXML private TableColumn<Meal, Double> caloriesColumn;
    @FXML private TableColumn<Meal, Double> proteinColumn;
    @FXML private TableColumn<Meal, Double> carbsColumn;
    @FXML private TableColumn<Meal, Double> fatColumn;

    // Weekly chart
    @FXML private BarChart<String, Number> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Data model
    private ObservableList<Meal> currentDayMeals = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Call base controller initialization
        initializePage("history");

        // Add sample meals for testing if using in-memory service
        if (mealService instanceof InMemoryMealService) {
            ((InMemoryMealService) mealService).addSampleMeals();
        }

        // Setup UI components
        setupTableColumns();
        setupDatePicker();
        setupChart();

        // Load today's data by default
        loadTodaysData();
    }

    // Setup table columns with appropriate cell value factories
    private void setupTableColumns() {
        // Map meal name to the Meal Name column
        foodNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        // Map meal type to the Meal Type column
        mealTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMealType()));

        // Calculate and display number of foods in each meal
        servingColumn.setCellValueFactory(cellData -> {
            int foodCount = cellData.getValue().getFoods() != null ?
                    cellData.getValue().getFoods().size() : 0;
            return new SimpleIntegerProperty(foodCount).asObject();
        });

        // Use meal's total nutrition values for respective columns
        caloriesColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalCalories()).asObject());

        proteinColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalProtein()).asObject());

        carbsColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalCarbs()).asObject());

        fatColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalFat()).asObject());

        // Bind the observable list to the table
        dailyEntriesTable.setItems(currentDayMeals);

        // Set up selection listener to enable/disable edit and delete buttons
        dailyEntriesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean hasSelection = newSelection != null;
                    editEntryButton.setDisable(!hasSelection);
                    deleteEntryButton.setDisable(!hasSelection);
                }
        );
    }

    // Setup date picker to load data for selected date
    private void setupDatePicker() {
        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Add listener to load data when date changes
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadDataForDate(newDate);
            }
        });
    }

    // Setup weekly chart
    private void setupChart() {
        // Configure axes and chart properties
        xAxis.setLabel("Date");
        yAxis.setLabel("Calories");
        weeklyChart.setTitle("Weekly Calorie Intake");
        weeklyChart.setLegendVisible(false);

        // Load initial chart data
        loadWeeklyChart();
    }

    // Load today's data into the table and summary
    private void loadTodaysData() {
        loadDataForDate(LocalDate.now());
    }

    // Load data for the specified date
    private void loadDataForDate(LocalDate date) {
        try {
            // Fetch meals for the selected date (using hardcoded user ID "123" for demo)
            List<Meal> meals = mealService.getMealsForDate("123", date);

            // Update observable list and daily summary
            currentDayMeals.clear();
            currentDayMeals.addAll(meals);
            updateDailySummary(meals);
        } catch (Exception e) {
            // Show error alert if data loading fails
            showAlert("Failed to load data for selected date: " + e.getMessage());
        }
    }

    // Update daily summary labels based on the meals
    private void updateDailySummary(List<Meal> meals) {
        double totalCalories = meals.stream().mapToDouble(Meal::getTotalCalories).sum();
        double totalProtein = meals.stream().mapToDouble(Meal::getTotalProtein).sum();
        double totalCarbs = meals.stream().mapToDouble(Meal::getTotalCarbs).sum();
        double totalFat = meals.stream().mapToDouble(Meal::getTotalFat).sum();

        totalCaloriesLabel.setText(String.format("%.0f kcal", totalCalories));
        totalProteinLabel.setText(String.format("%.1f g", totalProtein));
        totalCarbsLabel.setText(String.format("%.1f g", totalCarbs));
        totalFatLabel.setText(String.format("%.1f g", totalFat));
    }

    // Load weekly chart data
    private void loadWeeklyChart() {
        try {
            // Initialize array to hold daily calorie totals
            double[] weeklyCalories = new double[7];

            // Calculate start date (6 days ago)
            LocalDate startDate = LocalDate.now().minusDays(6);

            // Load calorie data for each day of the week
            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                List<Meal> dayMeals = mealService.getMealsForDate("123", date);
                weeklyCalories[i] = dayMeals.stream().mapToDouble(Meal::getTotalCalories).sum();
            }

            // Use utility to set up the chart with the data
            ChartUtil.setupWeeklyCalorieChart(weeklyChart, weeklyCalories);

        } catch (Exception e) {
            // Show error alert if chart loading fails
            showAlert("Failed to load weekly chart data: " + e.getMessage());
        }
    }

    // Handle "Today" button click to reset date picker to today
    @FXML
    private void handleTodayButton() {
        datePicker.setValue(LocalDate.now());
    }

    // Handle export to CSV button click
    @FXML
    private void handleExportCsv() {
        // Open file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Nutrition Data");
        fileChooser.setInitialFileName("nutrition_data_" + LocalDate.now().toString() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Show save dialog
        File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());

        // If a file was selected, export data
        if (file != null) {
            exportDataToCsv(file);
        }
    }

    // Export all meal data to the specified CSV file
    private void exportDataToCsv(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("Date,Meal Name,Meal Type,Foods,Total Calories,Total Protein,Total Carbs,Total Fat\n");

            // Fetch all meals for the user (using hardcoded user ID "123" for demo)
            List<Meal> allMeals = mealService.getAllMealsForUser("123");

            // Write each meal's data as a CSV row
            for (Meal meal : allMeals) {
                String foodNames = meal.getFoods() != null ?
                        meal.getFoods().stream()
                                .map(Food::getName)
                                .collect(Collectors.joining("; ")) : "";

                writer.append(String.format("%s,\"%s\",%s,\"%s\",%.2f,%.2f,%.2f,%.2f\n",
                        meal.getCreatedDate() != null ? meal.getCreatedDate().toLocalDate().toString() : "Unknown",
                        meal.getName() != null ? meal.getName() : "Unnamed Meal",
                        meal.getMealType() != null ? meal.getMealType() : "Unknown",
                        foodNames,
                        meal.getTotalCalories(),
                        meal.getTotalProtein(),
                        meal.getTotalCarbs(),
                        meal.getTotalFat()));
            }

            // Show success alert
            showSuccessAlert("Data exported successfully to " + file.getName());

        } catch (IOException e) {
            // Show error alert if export fails
            showAlert("Failed to export data: " + e.getMessage());
        }
    }

    // Handle edit entry button click
    @FXML
    private void handleEditEntry() {
        // Get selected meal from the table
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();

        // If a meal is selected, navigate to meal builder for editing
        if (selectedMeal != null) {
            try {
                // Navigate to meal builder to edit the meal
                PageNavigationManager.switchTo("mealBuilderPage.fxml");
            } catch (IOException e) {
                showAlert("Failed to open meal builder: " + e.getMessage());
            }
        }
    }

    // Handle delete entry button click
    @FXML
    private void handleDeleteEntry() {
        // Get selected meal from the table
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();

        // If a meal is selected, confirm deletion
        if (selectedMeal != null) {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Delete Meal");
            confirmAlert.setHeaderText("Are you sure you want to delete this meal?");
            confirmAlert.setContentText("This action cannot be undone.");

            // If user confirms, delete the meal
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                try {
                    // Delete the meal using the meal service
                    mealService.deleteMeal(selectedMeal.getId());

                    // Refresh the view to reflect deletion
                    refreshCurrentView();

                    // Show success alert
                    showSuccessAlert("Meal deleted successfully");
                } catch (Exception e) {
                    // Show error alert if deletion fails
                    showAlert("Failed to delete meal: " + e.getMessage());
                }
            }
        }
    }

    // Refresh the current view by reloading data and chart
    private void refreshCurrentView() {
        loadDataForDate(datePicker.getValue());
        loadWeeklyChart();
    }
}