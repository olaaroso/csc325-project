package com.group4.macromanager.tests;

import com.group4.macromanager.model.AuthManager;
import com.group4.macromanager.model.FirestoreContext;
import com.google.cloud.firestore.Firestore;

public class TestAuth {

    public static void main(String[] args) {
        try {
            // Initialize Firestore
            FirestoreContext.initialize();
            Firestore db = FirestoreContext.getDb();
            System.out.println("Firestore connection successful");

            // Initialize AuthManager
            AuthManager authManager = new AuthManager();

            // Test registration
            System.out.println("Testing registration...");
            var session = authManager.registerUser("testuser@example.com", "Test1234");
            System.out.println("Registered: " + session.email);

            // (optional) Try login after registering
            System.out.println("Testing login...");
            var loginSession = authManager.login("testuser@example.com", "Test1234");
            System.out.println("Logged in: " + loginSession.email);

            // Logout
            authManager.logout();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Auth test failed: " + e.getMessage());
        }
    }
}
