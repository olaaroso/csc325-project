package com.group4.macromanager.controller;

import com.group4.macromanager.util.ChartUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;

import java.io.IOException;

public class DashboardController extends BaseController {

    // FXML elements
    @FXML private ListView<String> recentEntries;
    @FXML private PieChart macroPieChart;
    @FXML private BarChart<String, Number> barChart;

    @FXML
    public void initialize() {
        // Highlight current page in the sidebar
        initializePage("dashboard"); // Call to BaseController method

        // Placeholder recent entries
        recentEntries.getItems().addAll("Protein Shake", "Grilled Chicken", "Rice", "Broccoli");

        // Setup charts using ChartUtil
        ChartUtil.setupMacroPieChart(macroPieChart, 76, 175, 31);
        ChartUtil.setupWeeklyCalorieChart(barChart, new double[]{2200, 1800, 2000, 2500, 2300, 1900, 2100});
    }
}
