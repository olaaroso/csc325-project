package com.group4.macromanager.model;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FirestoreContext {

    public Firestore firebase() {
        try {

            // NOTE: create your own firebase-key.json file and put it in the root of the resources dir
            //       - make sure to insert your own private key data into the json file
            //       - NAME MUST MATCH (for .gitignore). MAKE SURE THAT YOU DO NOT PUSH THE KEY FILE TO THE GITHUB
            InputStream serviceAccount = getClass().getResourceAsStream("/firebase-key.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            // Print statement for confirmation
            System.out.println("Firestore Initialized");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return FirestoreClient.getFirestore();
    }
}
