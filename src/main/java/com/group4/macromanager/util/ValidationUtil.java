package com.group4.macromanager.util;

// This class contains utility methods for validating user input across the application.

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ValidationUtil {

    // Mark TextField as invalid
    public static void markInvalid(TextField field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
    }

    // Mark ComboBox as invalid
    public static void markInvalid(ComboBox<?> comboBox) {
        comboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
    }

    // Reset styles for a single TextField
    public static void resetFieldStyle(TextField field) {
        field.setStyle("");
    }

    // Reset styles for a single ComboBox
    public static void resetFieldStyle(ComboBox<?> comboBox) {
        comboBox.setStyle("");
    }

    // Reset styles for multiple TextFields
    public static void resetFieldStyles(TextField... fields) {
        for (TextField field : fields) {
            resetFieldStyle(field);
        }
    }

    // Reset styles for multiple ComboBoxes
    public static void resetFieldStyles(ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.setStyle("");
        }
    }

    // Checks for valid double input
    public static boolean isValidDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if field is empty
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Checks if ComboBox has a selected value
    public static boolean isComboBoxEmpty(ComboBox<?> comboBox) {
        return comboBox.getValue() == null || comboBox.getValue().toString().trim().isEmpty();
    }
}
