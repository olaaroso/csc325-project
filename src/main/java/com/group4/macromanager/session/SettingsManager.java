package com.group4.macromanager.session;

import com.group4.macromanager.Main;
import com.group4.macromanager.controller.PageNavigationManager;
import com.group4.macromanager.model.UserSettings;
import com.group4.macromanager.service.FirestoreSettingsService;
import com.group4.macromanager.service.ISettingsService;

public class SettingsManager {
    private static SettingsManager instance;
    private UserSettings currentSettings;
    private ISettingsService settingsService;

    private SettingsManager() {
        this.settingsService = new FirestoreSettingsService();
    }

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public UserSettings getCurrentSettings() {
        return currentSettings;
    }

    public void loadUserSettings(String userId) {
        try {
            currentSettings = settingsService.getUserSettings(userId);
            // Apply theme immediately after loading
            if (currentSettings != null) {
                PageNavigationManager.setCurrentTheme(currentSettings.getTheme());
                Main.applyTheme(currentSettings.getTheme());
            }
        } catch (Exception e) {
            System.err.println("Failed to load user settings: " + e.getMessage());
            currentSettings = settingsService.getDefaultSettings();
            Main.applyTheme("light"); // Default to light theme
        }
    }

    public void saveSettings(UserSettings settings) {
        try {
            currentSettings = settingsService.saveUserSettings(settings);
            // Apply theme immediately after saving
            PageNavigationManager.setCurrentTheme(currentSettings.getTheme());
            Main.applyTheme(currentSettings.getTheme());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save settings: " + e.getMessage(), e);
        }
    }

    public void clearSettings() {
        currentSettings = null;
        // Reset to light theme when clearing settings
        PageNavigationManager.setCurrentTheme("light");
        Main.applyTheme("light");
    }
}