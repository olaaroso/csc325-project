package com.group4.macromanager.model;
// Handles signup/login

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class AuthManager {
    private final String apiKey;
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Session currentSession;

    // Session represents the currently signed-in user session
    public static class Session {
        public final String idToken;
        public final String refreshToken;
        public final String uid;
        public final String email;

        Session(String idToken, String refreshToken, String uid, String email) {
            this.idToken = idToken;
            this.refreshToken = refreshToken;
            this.uid = uid;
            this.email = email;
        }
    }

    // AuthManager Constructor
    public AuthManager() throws Exception {
        // Load API key from firebase-config
        // - NOTE: you must create your own firebase-config.properties file in the root of the resources dir
        //         MAKE SURE IT IS NOT BEING TRACKED BY GIT
        InputStream input = getClass().getResourceAsStream("/firebase-config.properties");
        if (input == null) throw new RuntimeException("firebase-config.properties missing from resources");
        Properties properties = new Properties();
        properties.load(input);
        this.apiKey = properties.getProperty("FIREBASE_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("FIREBASE_API_KEY is not set");
        }
    }

    // Sign Request Class
    private static class SignRequest {
        String email;
        String password;
        boolean returnSecureToken = true;
        SignRequest(String email, String password) { this.email = email; this.password = password; }
    }

    // Sign Response Class
    private static class SignResponse {
        @SerializedName("idToken") String idToken;
        @SerializedName("refreshToken")  String refreshToken;
        @SerializedName("localId") String localId;
        @SerializedName("email") String email;
    }

    // Sign Up URL
    private String signUpUrl() {
        return "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + apiKey;
    }

    // Login URL
    private String loginUrl() {
        return "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
    }

    // Register a new user
    public Session registerUser(String email, String password) throws Exception {
        // Build Request
        var body = gson.toJson(new SignRequest(email, password));
        var req = HttpRequest.newBuilder()
                .uri(URI.create(signUpUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // Store response in a res variable
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());

        // Check for ok status code
        if (res.statusCode() >= 200 &&  res.statusCode() < 300) {
            // Create and return the session if successful
            SignResponse response = gson.fromJson(res.body(), SignResponse.class);
            currentSession = new Session(response.idToken, response.refreshToken, response.localId, response.email);

            // Create user document in Firestore
            Firestore db = FirestoreContext.getDb();
            DocumentReference userRef = db.collection("users").document(response.localId);

            // Create user record
            User newUser = new User(response.localId,  response.email);
            userRef.set(newUser).get(); // Waits fore Firestore to complete

            // DEBUG PRINT: System.out.println("Registered user: " + response.email);
            return currentSession;
        }
        else {
            throw new RuntimeException("Failed to register user: " + res.body());
        }
    }

    // Login an existing user
    public Session login(String email, String password) throws Exception {
        // Build Request
        var body = gson.toJson(new SignRequest(email, password));
        var req = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // Store response in a variable
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());

        // Check for ok status code
        if (res.statusCode() >= 200 &&  res.statusCode() < 300) {
            // Create and return the session if successful
            SignResponse response = gson.fromJson(res.body(), SignResponse.class);
            currentSession = new Session(response.idToken, response.refreshToken, response.localId, response.email);

            // Fetch existing user document from Firestore
            Firestore db = FirestoreContext.getDb();
            DocumentSnapshot snapshot = db.collection("users").document(response.localId).get().get();

            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                System.out.println("Fetched user: " + user);
            }
            else {
                System.out.println("Fetched user not found for " + response.email);
            }

            // DEBUG PRINT: System.out.println("Logged In: " + response.email);
            return currentSession;
        }
        else {
            throw new RuntimeException("Failed to login: " + res.body());
        }
    }

    // Logout
    public void logout() {
        currentSession = null; // Set session to null
        // DEBUG PRINT: System.out.println("Logged out");
    }
}

