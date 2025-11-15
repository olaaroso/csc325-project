package com.group4.macromanager.controller;

import com.group4.macromanager.model.Food;
import com.group4.macromanager.model.Meal;
import com.group4.macromanager.service.InMemoryMealService;
import com.group4.macromanager.util.ChartUtil;
import com.group4.macromanager.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryReportsController extends BaseController {

    // UI Components - Date Selection
    @FXML private DatePicker datePicker;
    @FXML private Button todayButton;
    @FXML private Button exportCsvButton;

    // UI Components - Table Actions
    @FXML private Button editEntryButton;
    @FXML private Button deleteEntryButton;

    // UI Components - Daily Summary Labels
    @FXML private Label totalCaloriesLabel;
    @FXML private Label totalProteinLabel;
    @FXML private Label totalCarbsLabel;
    @FXML private Label totalFatLabel;

    // UI Components - Data Table
    @FXML private TableView<Meal> dailyEntriesTable;
    @FXML private TableColumn<Meal, String> foodNameColumn;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, Integer> servingColumn;
    @FXML private TableColumn<Meal, Double> caloriesColumn;
    @FXML private TableColumn<Meal, Double> proteinColumn;
    @FXML private TableColumn<Meal, Double> carbsColumn;
    @FXML private TableColumn<Meal, Double> fatColumn;

    // UI Components - Chart
    @FXML private BarChart<String, Number> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Data Model
    private ObservableList<Meal> currentDayMeals = FXCollections.observableArrayList();

    // Initialization
    @FXML
    public void initialize() {
        // initialize page
        initializePage("history");

        // Load sample data if using in-memory service
        if (mealService instanceof InMemoryMealService) {
            ((InMemoryMealService) mealService).addSampleMeals();
        }

        // Setup UI components
        setupTable();
        setupDatePicker();
        setupChart();

        // Load today's data by default
        loadTodaysData();
    }

    // Setup table columns and bindings
    private void setupTable() {
        TableUtil.setupMealTableColumns(
                foodNameColumn, mealTypeColumn, servingColumn, caloriesColumn,
                proteinColumn, carbsColumn, fatColumn, dailyEntriesTable,
                currentDayMeals, editEntryButton, deleteEntryButton
        );
    }

    // Setup date picker listener
    private void setupDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadDataForDate(newDate);
            }
        });
    }

    // Setup weekly chart
    private void setupChart() {
        xAxis.setLabel("Date");
        yAxis.setLabel("Calories");
        weeklyChart.setTitle("Weekly Calorie Intake");
        weeklyChart.setLegendVisible(false);
        loadWeeklyChart();
    }

    // Load today's meal data and update UI
    private void loadTodaysData() {
        loadDataForDate(LocalDate.now());
    }

    // Load meal data for selected date
    private void loadDataForDate(LocalDate date) {
        List<Meal> meals = loadMealsForDate(date);
        currentDayMeals.setAll(meals);
        updateSummaryLabels(meals, totalCaloriesLabel, totalProteinLabel,
                totalCarbsLabel, totalFatLabel);
    }

    // Load weekly chart data
    private void loadWeeklyChart() {
        try {
            double[] weeklyCalories = new double[7];
            LocalDate startDate = LocalDate.now().minusDays(6);

            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                List<Meal> dayMeals = loadMealsForDate(date);
                weeklyCalories[i] = dayMeals.stream().mapToDouble(Meal::getTotalCalories).sum();
            }

            ChartUtil.setupWeeklyCalorieChart(weeklyChart, weeklyCalories);
        } catch (Exception e) {
            showAlert("Failed to load weekly chart data: " + e.getMessage());
        }
    }

    // Set date picker to today
    @FXML
    private void handleTodayButton() {
        datePicker.setValue(LocalDate.now());
    }

    // Handler for Exporting nutrition data to CSV
    @FXML
    private void handleExportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Nutrition Data");
        fileChooser.setInitialFileName("nutrition_data_" + LocalDate.now().toString() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
        if (file != null) {
            exportDataToCsv(file);
        }
    }

    // Export data to CSV file
    private void exportDataToCsv(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Date,Meal Name,Meal Type,Foods,Total Calories,Total Protein,Total Carbs,Total Fat\n");

            List<Meal> allMeals = mealService.getAllMealsForUser("123");

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

            showSuccessAlert("Data exported successfully to " + file.getName());
        } catch (IOException e) {
            showAlert("Failed to export data: " + e.getMessage());
        }
    }

    // Edit selected meal entry
    @FXML
    private void handleEditEntry() {
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();
        navigateToMealBuilderForEdit(selectedMeal);
    }

    // Delete selected meal entry with confirmation
    @FXML
    private void handleDeleteEntry() {
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();
        deleteMealWithConfirmation(selectedMeal, currentDayMeals, this::refreshCurrentView);
    }

    // Refresh current view after deletion or updates
    private void refreshCurrentView() {
        loadDataForDate(datePicker.getValue());
        loadWeeklyChart();
    }
}