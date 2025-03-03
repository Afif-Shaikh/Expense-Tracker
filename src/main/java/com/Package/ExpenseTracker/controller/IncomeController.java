package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Income;
import com.Package.ExpenseTracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
