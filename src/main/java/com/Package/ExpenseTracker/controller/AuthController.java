package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.dto.LoginRequest;
import com.Package.ExpenseTracker.dto.LoginResponse;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.UserRepository;
import com.Package.ExpenseTracker.service.JwtTokenProvider;
import com.Package.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("🔍 LOGIN REQUEST RECEIVED FOR: " + loginRequest.getEmail());

        Optional<User> user = userService.getUserByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            System.out.println("❌ User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        boolean authenticated = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (!authenticated) {
            System.out.println("❌ Incorrect password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // ✅ Generate JWT Token
        String token = jwtTokenProvider.generateToken(user.get());

        return ResponseEntity.ok(new LoginResponse(token, user.get().getName(), user.get().getEmail()));
    }
}
