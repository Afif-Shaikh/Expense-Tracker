package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Expense;
import com.Package.ExpenseTracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
@CrossOrigin(origins = "*") // Allow frontend requests
public class ExpenseController {

	@Autowired
	private ExpenseRepository expenseRepository;

	// API to add an expense
	@PostMapping("/addExpense")
	public Expense addExpense(@RequestBody Expense expense) {
		return expenseRepository.save(expense);
	}

	// API to get all expenses
	@GetMapping("/getExpense")
	public List<Expense> getExpense() {
		return expenseRepository.findAll();
	}
	
	@DeleteMapping("/deleteExpense/{id}")
	public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
	    if (!expenseRepository.existsById(id)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
	    }
	    expenseRepository.deleteById(id);
	    return ResponseEntity.ok("Expense deleted successfully");
	}


}
