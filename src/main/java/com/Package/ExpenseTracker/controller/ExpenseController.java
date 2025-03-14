package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Expense;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.service.ExpenseService;
import com.Package.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;  // ✅ FIX: Import UUID

@RestController
@RequestMapping("/api/expense")
@CrossOrigin(origins = "*") // Allow frontend requests
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    // 1️⃣ Add Expense for a User
    @PostMapping("/addExpense/{userId}")
    public ResponseEntity<?> addExpense(@PathVariable UUID userId, @RequestBody Expense expense) { // ✅ FIXED
        Optional<User> user = userService.getUserById(userId); // ✅ FIXED
        
        if (user.isPresent()) {
            Expense savedExpense = expenseService.addExpense(expense, user.get());
            return ResponseEntity.ok(savedExpense);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    // 2️⃣ Get All Expenses for a Specific User
    @GetMapping("/getExpenses/{userId}")
    public ResponseEntity<?> getUserExpenses(@PathVariable UUID userId) {
        Optional<User> user = userService.getUserById(userId);

        if (user.isPresent()) {
            List<Expense> expenses = expenseService.getUserExpenses(user.get());
            return ResponseEntity.ok(expenses);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    // 3️⃣ Delete Expense by ID (Ensuring it belongs to the user)
    @DeleteMapping("/deleteExpense/{userId}/{expenseId}")
    public ResponseEntity<String> deleteExpense(@PathVariable UUID userId, @PathVariable Long expenseId) {
        Optional<User> user = userService.getUserById(userId);

        if (user.isPresent()) {
            boolean deleted = expenseService.deleteExpense(expenseId, user.get());

            if (deleted) {
                return ResponseEntity.ok("Expense deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Expense not found or does not belong to user");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}
