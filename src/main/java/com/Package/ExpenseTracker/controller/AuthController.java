package com.Package.ExpenseTracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "<h1>Welcome to Expense Tracker</h1> <a href='/oauth2/authorization/google'>Login with Google</a>";
    }

    @GetMapping("/user")
    public Principal user(Principal principal) {
        return principal;  // Returns logged-in user details
    }
}
