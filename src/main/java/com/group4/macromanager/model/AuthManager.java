package com.group4.macromanager.model;
// Handles signup/login

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.group4.macromanager.session.AuthSessionManager;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class AuthManager {
    private final String apiKey;
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Session currentSession;
    private GoogleOAuthManager googleOAuth;

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

        // Initialize GoogleOAuthManager
        this.googleOAuth = new GoogleOAuthManager();
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

    // Get current user
    public User getCurrentUser() {
        if (currentSession == null) return null;

        try {
            // Fetch user document from Firestore
            Firestore db = FirestoreContext.getDb();
            DocumentSnapshot snapshot = db.collection("users").document(currentSession.uid).get().get();

            // Return user object if exists
            if (snapshot.exists()) {
                return snapshot.toObject(User.class);
            }
            // User not found
            return null;
        }
        catch (Exception e) {
            System.err.println("Failed to fetch current user: " + e.getMessage());
            return null;
        }
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

            // Set session in AuthSessionManager
            AuthSessionManager.getInstance().setSession(currentSession, newUser);

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

            User user = null;
            if (snapshot.exists()) {
                user = snapshot.toObject(User.class);
                System.out.println("Fetched user: " + user);
            }

            // Set session in AuthSessionManager
            AuthSessionManager.getInstance().setSession(currentSession, user);

            return currentSession;
        }
        else {
            throw new RuntimeException("Failed to login: " + res.body());
        }
    }

    // Google sign-in method
    public Session signInWithGoogle() throws Exception {
        CompletableFuture<GoogleOAuthManager.GoogleTokenResponse> future = googleOAuth.signInWithGoogle();

        // Wait for OAuth completion
        GoogleOAuthManager.GoogleTokenResponse tokenResponse = future.get();

        // Get user info from Google
        GoogleOAuthManager.GoogleUserInfo userInfo = googleOAuth.getUserInfo(tokenResponse.accessToken);

        // Create or get user in Firestore
        Firestore db = FirestoreContext.getDb();
        DocumentSnapshot userDoc = db.collection("users").document(userInfo.id).get().get();

        User user;
        if (!userDoc.exists()) {
            // Create new user
            user = new User(userInfo.id, userInfo.email);
            if (userInfo.name != null && !userInfo.name.isEmpty()) {
                user.setUsername(userInfo.name);
            }
            db.collection("users").document(userInfo.id).set(user).get();
        } else {
            // User exists, fetch user data
            user = userDoc.toObject(User.class);
        }

        // Create session
        currentSession = new Session(tokenResponse.idToken, tokenResponse.refreshToken, userInfo.id, userInfo.email);

        // Set session in AuthSessionManager
        AuthSessionManager.getInstance().setSession(currentSession, user);

        return currentSession;
    }

    // Logout
    public void logout() {
        currentSession = null; // Set session to null
        AuthSessionManager.getInstance().clearSession();
    }
}

