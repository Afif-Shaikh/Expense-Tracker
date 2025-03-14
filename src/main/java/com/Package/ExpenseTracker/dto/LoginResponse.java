package com.Package.ExpenseTracker.dto;

public class LoginResponse {
    private String token;
    private String name;
    private String email;

    // Constructor
    public LoginResponse(String token, String name, String email) {
        this.token = token;
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
