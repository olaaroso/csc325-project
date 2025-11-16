package com.group4.macromanager.model;

public class User {
    private String id;
    private String email;

    // Required no-arg constructor for Firestore deserialization
    public User() {}

    // Parameterized constructor
    public User(String id, String email) {
        this.id = id;
        this.email = email;
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
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email=" + email + '}';
    }
}
