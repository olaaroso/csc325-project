package com.group4.macromanager.util;

// Utility class for chart-related functionalities

import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;


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

    // Setup weekly calorie bar chart with explicit labels and highlight date
    public static void setupWeeklyCalorieChart(BarChart<String, Number> chart, double[] weeklyCalories, LocalDate startDate, LocalDate highlightDate) {
        chart.setAnimated(false); // Disable animation for immediate rendering
        chart.getData().clear();

        XYChart.Series<String, Number> calorieSeries = new XYChart.Series<>();
        calorieSeries.setName("Calories");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE M/d", Locale.getDefault());

        // Build labels based on the provided startDate so categories align with data
        for (int i = 0; i < Math.min(weeklyCalories.length, 7); i++) {
            LocalDate d = startDate.plusDays(i);
            String label = d.format(formatter);
            calorieSeries.getData().add(new XYChart.Data<>(label, weeklyCalories[i]));
        }

        chart.getData().add(calorieSeries);

        // Highlight the tick label that corresponds to the provided highlightDate (if any)
        if (highlightDate != null) {
            String labelToHighlight = highlightDate.format(formatter);

            // Use multiple Platform.runLater calls to ensure chart is fully rendered
            Platform.runLater(() -> {
                Platform.runLater(() -> {
                    try {
                        chart.applyCss();
                        chart.layout();

                        if (chart.getXAxis() instanceof CategoryAxis) {
                            CategoryAxis axis = (CategoryAxis) chart.getXAxis();
                            for (Node node : axis.lookupAll(".axis-tick-mark")) {
                                Node parent = node.getParent();
                                if (parent instanceof Parent) {
                                    Parent parentNode = (Parent) parent;
                                    for (Node child : parentNode.getChildrenUnmodifiable()) {
                                        if (child instanceof Text) {
                                            Text t = (Text) child;
                                            if (labelToHighlight.equals(t.getText().trim())) {
                                                t.setStyle("-fx-text-fill: #7DB6F6; -fx-font-weight: bold;");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error styling chart labels: " + e.getMessage());
                    }
                });
            });
        }
    }

    // Backwards-compatible overload (keeps previous behavior but uses last 7 days, no highlight)
    public static void setupWeeklyCalorieChart(BarChart<String, Number> chart, double[] weeklyCalories) {
        LocalDate startDate = LocalDate.now().minusDays(6);
        setupWeeklyCalorieChart(chart, weeklyCalories, startDate, null);
    }

}
