package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.model.*;
import com.Package.ExpenseTracker.repository.ExpenseRepository;
import com.Package.ExpenseTracker.repository.IncomeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class TransactionExcelService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public ByteArrayInputStream exportTransactionsToExcel() throws IOException {
        List<Income> incomeList = incomeRepository.findAllIncomeSorted();
        List<Expense> expenseList = expenseRepository.findAllExpensesSorted();

        List<Transaction> transactions = new ArrayList<>();

        for (Income i : incomeList) {
            transactions.add(new Transaction(
                    i.getName(),
                    i.getAmount(),
                    LocalDate.parse(i.getDate()),
                    i.getCategory(),
                    "Income"
            ));
        }

        for (Expense e : expenseList) {
            transactions.add(new Transaction(
                    e.getName(),
                    e.getAmount(),
                    LocalDate.parse(e.getDate()),
                    e.getCategory(),
                    "Expense"
            ));
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        String[] columns = {"Name", "Amount", "Date", "Category", "Type"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.getName());
            row.createCell(1).setCellValue(t.getAmount());
            row.createCell(2).setCellValue(t.getDate().toString());
            row.createCell(3).setCellValue(t.getCategory());
            row.createCell(4).setCellValue(t.getType());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public Map<String, Object> importTransactionsFromExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int saved = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;

            for (Row row : sheet) {
                if (rowCount++ == 0) continue; // Skip header

                try {
                    String name = row.getCell(0).getStringCellValue();
                    double amount = row.getCell(1).getNumericCellValue();
//                    String dateStr = row.getCell(2).getStringCellValue();
                    Cell dateCell = row.getCell(2);
                    LocalDate date;
                    if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        date = dateCell.getLocalDateTimeCellValue().toLocalDate();
                    } else {
                        // Fallback if it's a string (e.g., if user typed manually)
                        String dateStr = dateCell.getStringCellValue();
                        date = LocalDate.parse(dateStr);
                    }
                    String category = row.getCell(3).getStringCellValue();
                    String type = row.getCell(4).getStringCellValue();

                    if (amount <= 0 || (!type.equalsIgnoreCase("Income") && !type.equalsIgnoreCase("Expense"))) {
                        errors.add("Invalid row at line " + rowCount);
                        continue;
                    }

//                    LocalDate date = LocalDate.parse(dateStr);

                    if (type.equalsIgnoreCase("Income")) {
                        Income income = new Income(name, amount, date, category); // Removed userId
                        incomeRepository.save(income);
                    } else {
                        Expense expense = new Expense(name, amount, date, category); // Removed userId
                        expenseRepository.save(expense);
                    }

                    saved++;
                } catch (Exception e) {
                    errors.add("Error at row " + rowCount + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            errors.add("File processing failed: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("saved", saved);
        result.put("errors", errors);
        return result;
    }

}
