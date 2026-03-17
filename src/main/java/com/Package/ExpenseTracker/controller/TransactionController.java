package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody Transaction transaction) {
        log.info("POST /api/transactions — creating transaction");
        Transaction saved = transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        log.info("GET /api/transactions — fetching all");
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        log.info("GET /api/transactions/{} — fetching by id", id);
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getByType(@PathVariable String type) {
        log.info("GET /api/transactions/type/{} — fetching by type", type);
        return ResponseEntity.ok(transactionService.getByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(
            @PathVariable Long id,
            @Valid @RequestBody Transaction transaction) {
        log.info("PUT /api/transactions/{} — updating", id);
        Transaction updated = transactionService.update(id, transaction);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        log.info("DELETE /api/transactions/{} — deleting", id);
        transactionService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transaction deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, BigDecimal>> getSummary() {
        log.info("GET /api/transactions/summary — fetching summary");
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", transactionService.getTotalIncome());
        summary.put("totalExpense", transactionService.getTotalExpense());
        summary.put("balance", transactionService.getBalance());
        return ResponseEntity.ok(summary);
    }
}