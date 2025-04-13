package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.service.TransactionExcelService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/excel")
public class TransactionExcelController {

    @Autowired
    private TransactionExcelService transactionExcelService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return transactionExcelService.importTransactionsFromExcel(file);
    }


    @GetMapping("/download")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");
        response.getOutputStream().write(transactionExcelService.exportTransactionsToExcel().readAllBytes());
        response.getOutputStream().flush();
    }

}
