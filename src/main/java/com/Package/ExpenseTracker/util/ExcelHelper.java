package com.Package.ExpenseTracker.util;

import com.Package.ExpenseTracker.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    static String SHEET = "Transactions";
    static String[] HEADERS = {"Name", "Amount", "Date", "Category", "Type", "Comments"};

    public static ByteArrayInputStream transactionsToExcel(List<Transaction> transactions)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            // Data
            int rowIdx = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getName());
                row.createCell(1).setCellValue(t.getAmount().doubleValue());
                row.createCell(2).setCellValue(t.getDate().toString());
                row.createCell(3).setCellValue(t.getCategory());
                row.createCell(4).setCellValue(t.getType());
                row.createCell(5).setCellValue(t.getComments() != null ? t.getComments() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public static List<Transaction> excelToTransactions(MultipartFile file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet(SHEET);

            if (sheet == null) {
                throw new IOException("Sheet '" + SHEET + "' not found. "
                        + "Make sure the sheet is named 'Transactions'");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String name = row.getCell(0).getStringCellValue().trim();
                    BigDecimal amount = BigDecimal.valueOf(row.getCell(1).getNumericCellValue());
                    LocalDate date = LocalDate.parse(
                            row.getCell(2).getStringCellValue().trim(), formatter
                    );
                    String category = row.getCell(3).getStringCellValue().trim();
                    String type = row.getCell(4).getStringCellValue().trim().toUpperCase();

                    if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
                        throw new IllegalArgumentException("Invalid Type: " + type);
                    }

                    // Optional comments
                    String comments = "";
                    Cell commentsCell = row.getCell(5);
                    if (commentsCell != null && commentsCell.getCellType() == CellType.STRING) {
                        comments = commentsCell.getStringCellValue().trim();
                    }

                    Transaction transaction = new Transaction(
                            name, amount, date, category, type, comments
                    );
                    transactions.add(transaction);

                } catch (Exception e) {
                    // Skip invalid row
                }
            }
        }

        return transactions;
    }
}