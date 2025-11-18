package com.group4.macromanager.model;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import javafx.application.HostServices;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class GoogleOAuthManager {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri = "http://localhost:8080/auth/callback";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private static HostServices hostServices;

    // GoogleOAuthManager Constructor
    public GoogleOAuthManager() throws Exception {
        // Load client ID and client secret from firebase-config
        InputStream input = getClass().getResourceAsStream("/firebase-config.properties");
        if (input == null) throw new RuntimeException("firebase-config.properties missing from resources");

        Properties properties = new Properties();
        properties.load(input);

        this.clientId = properties.getProperty("GOOGLE_WEB_CLIENT_ID");
        this.clientSecret = properties.getProperty("GOOGLE_WEB_CLIENT_SECRET");

        if (clientId == null || clientSecret == null) {
            throw new RuntimeException("Google OAuth credentials not configured");
        }
    }

    // Set HostServices for opening URLs
    public static void setHostServices(HostServices services) {
        hostServices = services;
    }

    // Sign in with Google method
    public CompletableFuture<GoogleTokenResponse> signInWithGoogle() throws Exception {
        CompletableFuture<GoogleTokenResponse> future = new CompletableFuture<>();

        // Start local server to handle Oauth callback
        OAuthCallbackServer server = new OAuthCallbackServer(8080, future);

        try {
            server.start();

            // build OAuth URL
            String authUrl = buildAuthUrl();

            // Open browser
            if (hostServices != null) {
                hostServices.showDocument(authUrl);
            }
            else {
                // Fallback for opening browser
                java.awt.Desktop.getDesktop().browse(URI.create(authUrl));
            }
        }
        catch (Exception e) {
            future.completeExceptionally(e);
            server.stop();
        }

        return future;
    }

    // Build Google OAuth URL
    private String buildAuthUrl() {
        String scope = URLEncoder.encode("openid email profile", StandardCharsets.UTF_8);

        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + scope +
                "&response_type=code" +
                "&access_type=offline";
    }

    // Exchange authorization code for tokens
    public GoogleTokenResponse exchangeCodeForTokens(String authCode) throws Exception {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        String body = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&code=" + authCode;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to exchange code for tokens: " + response.body());
        }

        return gson.fromJson(response.body(), GoogleTokenResponse.class);
    }

    // Get user info from access token
    public GoogleUserInfo getUserInfo(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get user info: " + response.body());
        }

        return gson.fromJson(response.body(), GoogleUserInfo.class);
    }

    public static class GoogleTokenResponse {
        @SerializedName("access_token") public String accessToken;
        @SerializedName("id_token") public String idToken;
        @SerializedName("refresh_token") public String refreshToken;
    }

    public static class GoogleUserInfo {
        @SerializedName("id") public String id;
        @SerializedName("email") public String email;
        @SerializedName("name") public String name;
        @SerializedName("picture") public String picture;
    }
}
