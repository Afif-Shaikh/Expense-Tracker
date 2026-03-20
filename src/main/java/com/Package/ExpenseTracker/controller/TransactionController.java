package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.service.TransactionExcelService;
import com.Package.ExpenseTracker.service.TransactionService;
import com.Package.ExpenseTracker.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    private final UserService userService;

    public TransactionController(TransactionService transactionService,
                                 TransactionExcelService excelService,
                                 UserService userService) {
        this.transactionService = transactionService;
        this.excelService = excelService;
        this.userService = userService;
    }

    // Helper to get current user from session
    private User getCurrentUser(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            throw new RuntimeException("Not authenticated");
        }
        return userService.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody Transaction transaction,
                                              HttpSession session) {
        User user = getCurrentUser(session);
        Transaction saved = transactionService.create(transaction, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(HttpSession session) {
        User user = getCurrentUser(session);
        return ResponseEntity.ok(transactionService.getAll(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id,
                                               HttpSession session) {
        User user = getCurrentUser(session);
        return ResponseEntity.ok(transactionService.getById(id, user));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getByType(@PathVariable String type,
                                                       HttpSession session) {
        User user = getCurrentUser(session);
        return ResponseEntity.ok(transactionService.getByType(user, type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id,
                                              @Valid @RequestBody Transaction transaction,
                                              HttpSession session) {
        User user = getCurrentUser(session);
        Transaction updated = transactionService.update(id, transaction, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id,
                                                      HttpSession session) {
        User user = getCurrentUser(session);
        transactionService.delete(id, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transaction deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, BigDecimal>> getSummary(HttpSession session) {
        User user = getCurrentUser(session);
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", transactionService.getTotalIncome(user));
        summary.put("totalExpense", transactionService.getTotalExpense(user));
        summary.put("balance", transactionService.getBalance(user));
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/excel/download")
    public void downloadExcel(HttpServletResponse response,
                              HttpSession session) throws IOException {
        User user = getCurrentUser(session);
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            excelService.exportTransactionsToExcel(user).transferTo(out);
            out.flush();
        }
    }

    @PostMapping("/excel/upload")
    public ResponseEntity<Map<String, Object>> uploadExcel(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        User user = getCurrentUser(session);
        Map<String, Object> result = excelService.importTransactionsFromExcel(file, user);
        return ResponseEntity.ok(result);
    }
}