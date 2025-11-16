package com.group4.macromanager.util;

// Utility class for chart-related functionalities

import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class ChartUtil {

    // Setup macro distribution pie chart
    public static void setupMacroPieChart(PieChart chart, double protein, double carbs, double fats) {
        chart.getData().clear();
        chart.getData().addAll(
                new PieChart.Data("Protein", protein),
                new PieChart.Data("Carbs", carbs),
                new PieChart.Data("Fats", fats)
        );
    }

    // Setup weekly calorie bar chart
    public static void setupWeeklyCalorieChart(BarChart<String, Number> chart, double[] weeklyCalories) {
        chart.getData().clear();

        XYChart.Series<String, Number> calorieSeries = new XYChart.Series<>();
        calorieSeries.setName("Calories");

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < Math.min(days.length, weeklyCalories.length); i++) {
            calorieSeries.getData().add(new XYChart.Data<>(days[i], weeklyCalories[i]));
        }

        chart.getData().add(calorieSeries);
    }

}
