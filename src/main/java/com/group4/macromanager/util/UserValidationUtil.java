package com.group4.macromanager.util;

import com.group4.macromanager.session.AuthSessionManager;

public class UserValidationUtil {

    /**
     * Validates that a user is logged in and authorized
     * @return The current user ID if valid
     * @throws SecurityException if user is not logged in or not authorized
     */
    public static String validateUserAccess() {
        String userId = AuthSessionManager.getInstance().getCurrentUserId();

        if (userId == null || userId.isEmpty()) {
            throw new SecurityException("User not authenticated. Please log in.");
        }

        return userId;
    }

    /**
     * Validates that a user is logged in and matches the expected user ID
     * @param expectedUserId The user ID to validate against
     * @return The current user ID if valid
     * @throws SecurityException if user is not logged in or IDs don't match
     */
    public static String validateUserAccess(String expectedUserId) {
        String currentUserId = validateUserAccess();

        if (!currentUserId.equals(expectedUserId)) {
            throw new SecurityException("Access denied. User ID mismatch.");
        }

        return currentUserId;
    }
}
