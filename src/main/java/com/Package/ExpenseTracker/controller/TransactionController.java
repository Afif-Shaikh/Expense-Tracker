package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.service.TransactionExcelService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionExcelService excelService;

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, Principal principal) throws IOException {
        // You can fetch userId from DB using principal.getName() in real implementation
        Long userId = 1L;

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

        try (OutputStream out = response.getOutputStream()) {
            excelService.exportTransactionsToExcel().transferTo(out);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file, Principal principal) {
        Long userId = 1L;
        Map<String, Object> result = excelService.importTransactionsFromExcel(file);
        return ResponseEntity.ok(result);
    }
}
