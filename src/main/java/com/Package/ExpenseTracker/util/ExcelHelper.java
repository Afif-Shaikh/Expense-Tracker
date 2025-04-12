package com.Package.ExpenseTracker.util;

import com.Package.ExpenseTracker.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = { "Name", "Amount", "Date", "Category", "Type" }; // Type = Income / Expense
    static String SHEET = "Transactions";

    // ✅ Convert list of transactions to Excel
    public static ByteArrayInputStream transactionsToExcel(List<Transaction> transactions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            int rowIdx = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(transaction.getName());
                row.createCell(1).setCellValue(transaction.getAmount());
                row.createCell(2).setCellValue(transaction.getDate().toString());
                row.createCell(3).setCellValue(transaction.getCategory());
                row.createCell(4).setCellValue(transaction.getType());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ✅ Parse Excel to list of transactions
    public static List<Transaction> excelToTransactions(MultipartFile file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) {
                throw new IOException("Invalid sheet name");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String name = row.getCell(0).getStringCellValue();
                    double amount = row.getCell(1).getNumericCellValue();
                    LocalDate date = LocalDate.parse(row.getCell(2).getStringCellValue(), formatter);
                    String category = row.getCell(3).getStringCellValue();
                    String type = row.getCell(4).getStringCellValue();

                    if (!type.equalsIgnoreCase("Income") && !type.equalsIgnoreCase("Expense")) {
                        throw new IllegalArgumentException("Invalid Type: " + type);
                    }

                    Transaction transaction = new Transaction();
                    transaction.setName(name);
                    transaction.setAmount(amount);
                    transaction.setDate(date);
                    transaction.setCategory(category);
                    transaction.setType(type);

                    transactions.add(transaction);
                } catch (Exception e) {
                    // Skip invalid row (log or report later)
                    continue;
                }
            }
        }

        return transactions;
    }
}
