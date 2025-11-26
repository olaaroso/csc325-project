package com.group4.macromanager.model;

public class UserSettings {
    private String userId;
    private double calorieTarget;
    private double proteinTarget;
    private double carbTarget;
    private double fatTarget;
    private String theme;

    // Constructor
    public UserSettings(String userId, double calorieTarget, double proteinTarget,
                        double carbTarget, double fatTarget, String theme) {
        this.userId = userId;
        this.calorieTarget = calorieTarget;
        this.proteinTarget = proteinTarget;
        this.carbTarget = carbTarget;
        this.fatTarget = fatTarget;
        this.theme = theme != null ? theme : "light";
    }

    // Default constructor with sensible defaults
    public UserSettings() {
        this.calorieTarget = 2000.0;
        this.proteinTarget = 150.0;
        this.carbTarget = 250.0;
        this.fatTarget = 67.0;
        this.theme = "light";
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getCalorieTarget() { return calorieTarget; }
    public void setCalorieTarget(double calorieTarget) { this.calorieTarget = calorieTarget; }

    public double getProteinTarget() { return proteinTarget; }
    public void setProteinTarget(double proteinTarget) { this.proteinTarget = proteinTarget; }

    public double getCarbTarget() { return carbTarget; }
    public void setCarbTarget(double carbTarget) { this.carbTarget = carbTarget; }

    public double getFatTarget() { return fatTarget; }
    public void setFatTarget(double fatTarget) { this.fatTarget = fatTarget; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

}
