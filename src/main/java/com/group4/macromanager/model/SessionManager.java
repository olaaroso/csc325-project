package com.group4.macromanager.model;
// This class is used to track sessions across the whole app

import com.group4.macromanager.model.AuthManager.Session;

public class SessionManager {
    private static Session currentSession;

    public static void setCurrentSession(Session session) {
        currentSession = session;
    }

    public static Session getCurrentSession() {
        return currentSession;
    }

    // May not be used, same logic as logging out
    public static void clearCurrentSession() {
        currentSession = null;
    }
}
