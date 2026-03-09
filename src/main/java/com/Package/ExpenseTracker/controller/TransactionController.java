package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        Transaction saved = transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.getAll();
    }

    @GetMapping("/type/{type}")
    public List<Transaction> getByType(@PathVariable String type) {
        return transactionService.getByType(type);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @GetMapping("/summary")
    public Map<String, BigDecimal> getSummary() {
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", transactionService.getTotalIncome());
        summary.put("totalExpense", transactionService.getTotalExpense());
        summary.put("balance", transactionService.getBalance());
        return summary;
    }
}