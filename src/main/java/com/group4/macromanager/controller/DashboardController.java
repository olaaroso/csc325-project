package com.group4.macromanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;

import java.io.IOException;

public class DashboardController {

    // FXML elements
    @FXML private ListView<String> recentEntries;
    @FXML private PieChart macroPieChart;
    @FXML private BarChart<String, Number> barChart;

    @FXML
    public void initialize() {
        // Placeholder recent entries
        recentEntries.getItems().addAll("Protein Shake", "Grilled Chicken", "Rice", "Broccoli");

        // Placeholder Pie Chart Data
        macroPieChart.getData().addAll(
                new PieChart.Data("Protein", 56),
                new PieChart.Data("Carbs", 175),
                new PieChart.Data("Fats", 31)
        );

        // Placeholder Bar Chart data
        XYChart.Series<String, Number> calorieSeries = new XYChart.Series<>();
        calorieSeries.setName("Calories");

        calorieSeries.getData().add(new XYChart.Data<>("Sun", 2000));
        calorieSeries.getData().add(new XYChart.Data<>("Mon", 2350));
        calorieSeries.getData().add(new XYChart.Data<>("Tue", 1775));
        calorieSeries.getData().add(new XYChart.Data<>("Wed", 1950));
        calorieSeries.getData().add(new XYChart.Data<>("Thu", 2075));
        calorieSeries.getData().add(new XYChart.Data<>("Fri", 2320));
        calorieSeries.getData().add(new XYChart.Data<>("Sat", 1950));

        barChart.getData().add(calorieSeries);
    }
    public void goSettings(ActionEvent event) throws IOException {
        PageNavigationManager.switchTo("settingsPage.fxml");
    }

    // Event handler functions
    public void handleLogout(ActionEvent event) throws IOException {
        // This handler will contain logout functionality

        // FOR NOW: redirects straight to the login page
        PageNavigationManager.switchTo("loginPage.fxml");
    }
}
