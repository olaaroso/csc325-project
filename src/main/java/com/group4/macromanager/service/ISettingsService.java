package com.group4.macromanager.service;

import com.group4.macromanager.model.UserSettings;

public interface ISettingsService {
    UserSettings getUserSettings(String userId);
    UserSettings saveUserSettings(UserSettings settings);
    UserSettings getDefaultSettings();
}
