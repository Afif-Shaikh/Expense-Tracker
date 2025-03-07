package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Income;
import com.Package.ExpenseTracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
@CrossOrigin(origins = "*")
public class IncomeController {

	@Autowired
	private IncomeRepository incomeRepository;

	// API to add income
	@PostMapping("/addIncome")
	public Income addIncome(@RequestBody Income income) {
		return incomeRepository.save(income);
	}

	// API to get all income
	@GetMapping("/getIncome")
	public List<Income> getIncome() {
		return incomeRepository.findAll();
	}
	
	@DeleteMapping("/deleteIncome/{id}")
	public ResponseEntity<String> deleteIncome(@PathVariable Long id) {
	    if (!incomeRepository.existsById(id)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Income not found");
	    }
	    incomeRepository.deleteById(id);
	    return ResponseEntity.ok("Income deleted successfully");
	}

}
