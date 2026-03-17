package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.service.TransactionExcelService;
import com.Package.ExpenseTracker.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
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
    private final TransactionExcelService excelService;

    public TransactionController(TransactionService transactionService,
                                 TransactionExcelService excelService) {
        this.transactionService = transactionService;
        this.excelService = excelService;
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

    // Excel Export
    @GetMapping("/excel/download")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        log.info("GET /api/transactions/excel/download — exporting Excel");
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions.xlsx"
        );
        try (OutputStream out = response.getOutputStream()) {
            excelService.exportTransactionsToExcel().transferTo(out);
            out.flush();
        }
    }

    // Excel Import
    @PostMapping("/excel/upload")
    public ResponseEntity<Map<String, Object>> uploadExcel(
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/transactions/excel/upload — importing Excel");
        Map<String, Object> result = excelService.importTransactionsFromExcel(file);
        return ResponseEntity.ok(result);
    }
}