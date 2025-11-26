package com.group4.macromanager.controller;

import com.group4.macromanager.model.Meal;
import com.group4.macromanager.model.UserSettings;
import com.group4.macromanager.service.FirestoreMealService;
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
    @FXML private Label calorieTargetLabel;
    @FXML private Label proteinTargetLabel;
    @FXML private Label carbTargetLabel;
    @FXML private Label fatTargetLabel;
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

        // Setup table and load data
        setupTable();
        loadTodaysData();

        // Update macro targets
        updateMacroTargets();
    }


    // method to update the target displays
    private void updateMacroTargets() {
        UserSettings settings = getCurrentUserSettings();
        if (settings != null) {
            // Update the sublabels in your dashboard FXML
            // You'll need to update your FXML to have fx:id for these labels
            if (calorieTargetLabel != null) {
                calorieTargetLabel.setText("/ " + (int)settings.getCalorieTarget());
            }
            if (proteinTargetLabel != null) {
                proteinTargetLabel.setText("/ " + (int)settings.getProteinTarget() + "g");
            }
            if (carbTargetLabel != null) {
                carbTargetLabel.setText("/ " + (int)settings.getCarbTarget() + "g");
            }
            if (fatTargetLabel != null) {
                fatTargetLabel.setText("/ " + (int)settings.getFatTarget() + "g");
            }
        }
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

            ChartUtil.setupWeeklyCalorieChart(barChart, weeklyCalories, startDate, LocalDate.now());
        } catch (Exception e) {
            showAlert("Failed to load weekly chart: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    // Navigation to Meal Builder page to edit selected meal
    @FXML
    private void handleEditEntry() {
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();
        navigateToMealBuilderForEdit(selectedMeal);
    }

    // Delete selected meal entry with confirmation
    @FXML
    private void handleDeleteEntry() {
        Meal selectedMeal = dailyEntriesTable.getSelectionModel().getSelectedItem();
        deleteMealWithConfirmation(selectedMeal, todaysMeals, this::loadTodaysData);
    }
}