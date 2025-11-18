package com.group4.macromanager.model;

public class User {
    private String id;
    private String email;
    private String username;

    // Required no-arg constructor for Firestore deserialization
    public User() {}

    // Parameterized constructor
    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.username = generateUsernameFromEmail(email);
    }

    // Helper method to generate username from email
    private String generateUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "User";
        }
        // i.e., joshiehle@example.com -> joshiehle
        return email.substring(0, email.indexOf("@"));
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        this.id = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        // Auto-generate username when email is set (important for Firestore deserialization)
        if (this.username == null || this.username.isEmpty()) {
            this.username = generateUsernameFromEmail(email);
        }
    }

    public String getUsername() {
        return username != null ? username : "User";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email=" + email + ", username=" + username + '}';
    }
}
