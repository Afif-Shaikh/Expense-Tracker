package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Income;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.service.IncomeService;
import com.Package.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;  // ✅ FIX: Import UUID

@RestController
@RequestMapping("/api/income")
@CrossOrigin(origins = "*") // Allow frontend requests
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private UserService userService;

    // 1️⃣ Add Income for a User
    @PostMapping("/addIncome/{userId}")
    public ResponseEntity<?> addIncome(@PathVariable UUID userId, @RequestBody Income income) { // ✅ FIXED
        Optional<User> user = userService.getUserById(userId); // ✅ FIXED
        
        if (user.isPresent()) {
            Income savedIncome = incomeService.addIncome(income, user.get());
            return ResponseEntity.ok(savedIncome);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    // 2️⃣ Get All Income for a Specific User
    @GetMapping("/getIncome/{userId}")
    public ResponseEntity<?> getUserIncome(@PathVariable UUID userId) {
        Optional<User> user = userService.getUserById(userId);

        if (user.isPresent()) {
            List<Income> incomeList = incomeService.getUserIncome(user.get());
            return ResponseEntity.ok(incomeList);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    // 3️⃣ Delete Income by ID (Ensuring it belongs to the user)
    @DeleteMapping("/deleteIncome/{userId}/{incomeId}")
    public ResponseEntity<String> deleteIncome(@PathVariable UUID userId, @PathVariable Long incomeId) {
        Optional<User> user = userService.getUserById(userId);

        if (user.isPresent()) {
            boolean deleted = incomeService.deleteIncome(incomeId, user.get());

            if (deleted) {
                return ResponseEntity.ok("Income deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Income not found or does not belong to user");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}
