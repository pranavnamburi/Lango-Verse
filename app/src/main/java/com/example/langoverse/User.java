package com.example.langoverse;

import androidx.appcompat.app.AppCompatActivity;

public class User {
    private static User instance;
    private String email;

    private User() {}  // Private constructor to prevent instantiation

    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

