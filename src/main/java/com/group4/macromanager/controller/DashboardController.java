package com.group4.macromanager.controller;

import com.group4.macromanager.model.Meal;
import com.group4.macromanager.service.InMemoryMealService;
import com.group4.macromanager.util.ChartUtil;
import com.group4.macromanager.util.TableUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class DashboardController extends BaseController {

    // UI Components
    @FXML private Label totalCaloriesLabel;
    @FXML private Label totalProteinLabel;
    @FXML private Label totalCarbsLabel;
    @FXML private Label totalFatLabel;
    @FXML private PieChart macroPieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private TableView<Meal> dailyEntriesTable;
    @FXML private TableColumn<Meal, String> foodNameColumn;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, Integer> servingColumn;
    @FXML private TableColumn<Meal, Double> caloriesColumn;
    @FXML private TableColumn<Meal, Double> proteinColumn;
    @FXML private TableColumn<Meal, Double> carbsColumn;
    @FXML private TableColumn<Meal, Double> fatColumn;
    @FXML private Button editEntryButton;
    @FXML private Button deleteEntryButton;

    // Data
    private ObservableList<Meal> todaysMeals = FXCollections.observableArrayList();

    // Initialization
    @FXML
    public void initialize() {
        // initialize page
        initializePage("dashboard");

        // Load sample data if using in-memory service
        if (mealService instanceof InMemoryMealService) {
            ((InMemoryMealService) mealService).addSampleMeals();
        }

        // Setup table and load data
        setupTable();
        loadTodaysData();
    }

    // Setup table columns and bindings
    private void setupTable() {
        TableUtil.setupMealTableColumns(
                foodNameColumn, mealTypeColumn, servingColumn, caloriesColumn,
                proteinColumn, carbsColumn, fatColumn, dailyEntriesTable,
                todaysMeals, editEntryButton, deleteEntryButton
        );
    }

    // Load today's meal data and update UI
    private void loadTodaysData() {
        List<Meal> meals = loadMealsForDate(LocalDate.now());
        todaysMeals.setAll(meals);
        updateSummaryLabels(meals, totalCaloriesLabel, totalProteinLabel,
                totalCarbsLabel, totalFatLabel);
        updateMacroPieChart(meals);
        updateWeeklyChart();
    }

    // Update macro distribution pie chart
    private void updateMacroPieChart(List<Meal> meals) {
        TableUtil.NutritionalSummary summary = TableUtil.calculateDailySummary(meals);
        ChartUtil.setupMacroPieChart(macroPieChart, summary.protein, summary.carbs, summary.fat);
    }

    // Update weekly calorie bar chart
    private void updateWeeklyChart() {
        try {
            double[] weeklyCalories = new double[7];
            LocalDate startDate = LocalDate.now().minusDays(6);

            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                List<Meal> dayMeals = loadMealsForDate(date);
                weeklyCalories[i] = TableUtil.calculateDailySummary(dayMeals).calories;
            }

            ChartUtil.setupWeeklyCalorieChart(barChart, weeklyCalories);
        } catch (Exception e) {
            showAlert("Failed to load weekly chart: " + e.getMessage());
        }
    }

    // Navigation to Meal Builder page to edit selected meal
    @FXML
    private void handleEditEntry() {
        navigateToMealBuilder();
    }

    // Delete selected meal entry with confirmation
    @FXML
    private void handleDeleteEntry() {
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();
        deleteMealWithConfirmation(selectedMeal, todaysMeals, this::loadTodaysData);
    }
}