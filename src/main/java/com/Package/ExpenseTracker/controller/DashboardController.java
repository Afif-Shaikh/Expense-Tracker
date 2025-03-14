package com.Package.ExpenseTracker.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User user) {
//        return "Welcome, " + user.getAttribute("name") + "! Your email: " + user.getAttribute("email");
    	return "Dashboard.html";
    }	
}
