package com.group4.macromanager.model;
// Firestore initialization
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;

public class FirestoreContext {

    // Create firestore db instance
    private static Firestore db;

    public static void initialize() {
        if (db != null) return; // Prevent double initialization

        // NOTE: create your own firebase-key.json file and put it in the root of the resources dir
        //       - make sure to insert your own private key data into the json file
        //       - NAME MUST MATCH (for .gitignore). MAKE SURE THAT YOU DO NOT PUSH THE KEY FILE TO THE GITHUB
        try (InputStream serviceAccount = FirestoreContext.class.getResourceAsStream("/firebase-key.json")) {
            // Guard clause for if firebase key is not found
            if (serviceAccount == null) {
                throw new IOException("firebase-key.json not found in resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();

            System.out.println("Firestore initialized successfully");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to initialize Firestore");
        }
    }

    // Method to get the DB instance
    public static Firestore getDb() {
        if (db == null) initialize();
        return db;
    }
}
