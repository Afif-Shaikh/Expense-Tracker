package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")  // Standard naming for authentication routes
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // ✅ Register User (Manually)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ Manual Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {
        Optional<User> user = userService.getUserByEmail(loginUser.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginUser.getPassword(), user.get().getPassword())) {
            return ResponseEntity.ok(user.get()); // Send user data if authentication is successful
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
