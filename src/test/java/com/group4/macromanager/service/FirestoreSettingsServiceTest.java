package com.group4.macromanager.service;

import com.group4.macromanager.BaseServiceTest;
import com.group4.macromanager.model.UserSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.cloud.firestore.WriteResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FirestoreSettingsServiceTest extends BaseServiceTest {

    private FirestoreSettingsService settingsService;

    @BeforeEach
    void setup() {
        settingsService = new FirestoreSettingsService();
    }

    @Test
    void testSaveUserSettings() throws Exception {
        UserSettings testSettings = new UserSettings(TEST_USER_ID, 2500.0, 180.0, 300.0, 80.0, "dark");

        when(mockDocument.set(any())).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(null);

        UserSettings savedSettings = settingsService.saveUserSettings(testSettings);

        assertNotNull(savedSettings);
        assertEquals(2500.0, savedSettings.getCalorieTarget());
        assertEquals("dark", savedSettings.getTheme());
        assertEquals(TEST_USER_ID, savedSettings.getUserId());
        verify(mockDocument).set(any());
    }

    @Test
    void testGetUserSettings_Existing() throws Exception {
        when(mockDocument.get()).thenReturn(mockDocumentFuture);
        when(mockDocumentFuture.get()).thenReturn(mockSnapshot);
        when(mockSnapshot.exists()).thenReturn(true);
        setupMockSettingsDocument();

        UserSettings settings = settingsService.getUserSettings(TEST_USER_ID);

        assertNotNull(settings);
        assertEquals(2500.0, settings.getCalorieTarget());
        assertEquals("dark", settings.getTheme());
    }

    @Test
    void testGetUserSettings_DefaultWhenNotExists() throws Exception {
        when(mockDocument.get()).thenReturn(mockDocumentFuture);
        when(mockDocumentFuture.get()).thenReturn(mockSnapshot);
        when(mockSnapshot.exists()).thenReturn(false);

        UserSettings settings = settingsService.getUserSettings(TEST_USER_ID);

        assertNotNull(settings);
        assertEquals(2000.0, settings.getCalorieTarget());
        assertEquals("light", settings.getTheme());
        assertEquals(TEST_USER_ID, settings.getUserId());
    }

    @Test
    void testGetDefaultSettings() {
        UserSettings defaults = settingsService.getDefaultSettings();

        assertNotNull(defaults);
        assertEquals(2000.0, defaults.getCalorieTarget());
        assertEquals(150.0, defaults.getProteinTarget());
        assertEquals(250.0, defaults.getCarbTarget());
        assertEquals(67.0, defaults.getFatTarget());
        assertEquals("light", defaults.getTheme());
    }

    private void setupMockSettingsDocument() {
        when(mockSnapshot.getId()).thenReturn(TEST_USER_ID);
        when(mockSnapshot.getDouble("calorieTarget")).thenReturn(2500.0);
        when(mockSnapshot.getDouble("proteinTarget")).thenReturn(180.0);
        when(mockSnapshot.getDouble("carbTarget")).thenReturn(300.0);
        when(mockSnapshot.getDouble("fatTarget")).thenReturn(80.0);
        when(mockSnapshot.getString("theme")).thenReturn("dark");
    }
}
