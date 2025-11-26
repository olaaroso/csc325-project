package com.group4.macromanager.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.group4.macromanager.model.FirestoreContext;
import com.group4.macromanager.model.UserSettings;
import com.group4.macromanager.util.UserValidationUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreSettingsService implements ISettingsService {

    // DB instance
    private final Firestore db;
    private final CollectionReference settingsCollection;

    // Constructor
    public FirestoreSettingsService() {
        this.db = FirestoreContext.getDb();
        this.settingsCollection = db.collection("userSettings");
    }

    // -------------------- Implement ISettingsService methods --------------------

    // Get user settings
    @Override
    public UserSettings getUserSettings(String requestedUserId) {
        String currentUserId = UserValidationUtil.validateUserAccess(requestedUserId);

        try {
            DocumentSnapshot document = settingsCollection.document(currentUserId).get().get();

            if (document.exists()) {
                return documentToSettings(document);
            } else {
                // Return default settings if none exist
                UserSettings defaults = getDefaultSettings();
                defaults.setUserId(currentUserId);
                return defaults;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get user settings: " + e.getMessage(), e);
        }
    }

    // Save user settings
    @Override
    public UserSettings saveUserSettings(UserSettings settings) {
        String userId = UserValidationUtil.validateUserAccess();
        settings.setUserId(userId);

        try {
            Map<String, Object> settingsData = settingsToMap(settings);
            settingsCollection.document(userId).set(settingsData).get();
            return settings;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save user settings: " + e.getMessage(), e);
        }
    }

    // Get default settings
    @Override
    public UserSettings getDefaultSettings() {
        return new UserSettings();
    }

    // -------------------- Helper methods --------------------

    // Convert UserSettings to Map for Firestore
    private Map<String, Object> settingsToMap(UserSettings settings) {
        Map<String, Object> data = new HashMap<>();
        data.put("calorieTarget", settings.getCalorieTarget());
        data.put("proteinTarget", settings.getProteinTarget());
        data.put("carbTarget", settings.getCarbTarget());
        data.put("fatTarget", settings.getFatTarget());
        data.put("theme", settings.getTheme());
        data.put("updatedAt", FieldValue.serverTimestamp());
        return data;
    }

    // Convert Firestore DocumentSnapshot to UserSettings
    private UserSettings documentToSettings(DocumentSnapshot document) {
        return new UserSettings(
                document.getId(),
                document.getDouble("calorieTarget") != null ? document.getDouble("calorieTarget") : 2000.0,
                document.getDouble("proteinTarget") != null ? document.getDouble("proteinTarget") : 150.0,
                document.getDouble("carbTarget") != null ? document.getDouble("carbTarget") : 250.0,
                document.getDouble("fatTarget") != null ? document.getDouble("fatTarget") : 67.0,
                document.getString("theme") != null ? document.getString("theme") : "light"
        );
    }
}
