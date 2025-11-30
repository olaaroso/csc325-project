package com.group4.macromanager.session;
// This class is used to track sessions across the whole app

import com.group4.macromanager.model.AuthManager;
import com.group4.macromanager.model.User;

public class AuthSessionManager {
    private static AuthSessionManager instance;
    private AuthManager.Session currentSession;
    private User currentUser;

    private AuthSessionManager() {
        // Private constructor to prevent instantiation
    }

    // Get the singleton instance
    public static AuthSessionManager getInstance() {
        if (instance == null) {
            instance = new AuthSessionManager();
        }
        return instance;
    }

    // Set current session
    public void setSession(AuthManager.Session session, User user) {
        this.currentSession = session;
        this.currentUser = user;

        // Load user settings when session is set
        if (session != null) {
            SettingsManager.getInstance().loadUserSettings(session.uid);
        }
    }

    // Get current session
    public AuthManager.Session getCurrentSession() {
        return currentSession;
    }

    // Get current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Get current user ID
    public String getCurrentUserId() {
        return currentSession != null ? currentSession.uid : null;
    }

    // Get current username
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "User";
    }

    // Get current user email
    public String getCurrentUserEmail() {
        return currentSession != null ? currentSession.email : null;
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return currentSession != null && currentUser != null;
    }

    // Clear session
    public void clearSession() {
        this.currentSession = null;
        this.currentUser = null;
        SettingsManager.getInstance().clearSettings();
    }
}
