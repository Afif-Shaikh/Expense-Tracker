package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.model.Expense;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // 1️⃣ Add Expense for a User
    public Expense addExpense(Expense expense, User user) {
        expense.setUser(user); // Associate expense with the given user
        return expenseRepository.save(expense);
    }

    // 2️⃣ Get All Expenses for a Specific User
    public List<Expense> getUserExpenses(User user) {
        return expenseRepository.findByUser(user);
    }

    // 3️⃣ Get a Single Expense by ID (Optional)
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // 4️⃣ Delete Expense by ID
    public boolean deleteExpense(Long id, User user) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            
            // Ensure the expense belongs to the user before deleting
            if (expense.getUser().getId().equals(user.getId())) {
                expenseRepository.deleteById(id);
                return true;
            }
        }
        return false; // Expense not found OR does not belong to user
    }
}
