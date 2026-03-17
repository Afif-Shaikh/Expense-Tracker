package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.repository.TransactionRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionExcelService {

    private static final Logger log = LoggerFactory.getLogger(TransactionExcelService.class);

    private final TransactionRepository transactionRepository;

    public TransactionExcelService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ByteArrayInputStream exportTransactionsToExcel() throws IOException {
        log.info("Starting Excel export");

        List<Transaction> transactions = transactionRepository.findAllByOrderByDateDesc();
        String[] columns = {"Name", "Amount", "Date", "Category", "Type", "Comments"};

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transactions");

            // Header row
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Data rows
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

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Excel export complete: {} transactions exported", transactions.size());
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public Map<String, Object> importTransactionsFromExcel(MultipartFile file) {
        log.info("Starting Excel import, file: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        List<String> errors = new ArrayList<>();
        List<Transaction> validTransactions = new ArrayList<>();
        int rowCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                rowCount++;
                if (rowCount == 1) continue; // Skip header

                try {
                    String name = row.getCell(0).getStringCellValue().trim();
                    double amountRaw = row.getCell(1).getNumericCellValue();
                    BigDecimal amount = BigDecimal.valueOf(amountRaw);

                    // Parse date
                    LocalDate date;
                    Cell dateCell = row.getCell(2);
                    if (dateCell.getCellType() == CellType.NUMERIC
                            && DateUtil.isCellDateFormatted(dateCell)) {
                        date = dateCell.getLocalDateTimeCellValue().toLocalDate();
                    } else {
                        date = LocalDate.parse(dateCell.getStringCellValue().trim());
                    }

                    String category = row.getCell(3).getStringCellValue().trim();
                    String type = row.getCell(4).getStringCellValue().trim().toUpperCase();

                    // Read optional comments
                    String comments = "";
                    Cell commentsCell = row.getCell(5);
                    if (commentsCell != null && commentsCell.getCellType() == CellType.STRING) {
                        comments = commentsCell.getStringCellValue().trim();
                    }

                    // Validate
                    if (name.isEmpty()) {
                        errors.add("Row " + rowCount + ": Name is empty");
                        continue;
                    }
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("Row " + rowCount + ": Amount must be greater than zero");
                        continue;
                    }
                    if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
                        errors.add("Row " + rowCount + ": Type must be INCOME or EXPENSE, got '" + type + "'");
                        continue;
                    }

                    Transaction transaction = new Transaction(
                            name, amount, date, category, type, comments
                    );
                    validTransactions.add(transaction);

                } catch (Exception e) {
                    errors.add("Row " + rowCount + ": " + e.getMessage());
                    log.warn("Error parsing row {}: {}", rowCount, e.getMessage());
                }
            }

            // Save all valid transactions at once
            transactionRepository.saveAll(validTransactions);
            log.info("Excel import complete: {} saved, {} errors",
                    validTransactions.size(), errors.size());

        } catch (Exception e) {
            errors.add("File processing failed: " + e.getMessage());
            log.error("Excel import failed", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("saved", validTransactions.size());
        result.put("errors", errors);
        return result;
    }
}