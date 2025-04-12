package com.Package.ExpenseTracker.model;

import java.time.LocalDate;

public class Transaction {
    private String name;
    private double amount;
    private LocalDate date;
    private String category;
    private String type; // Income or Expense

    public Transaction() {}

    public Transaction(String name, double amount, LocalDate date, String category, String type) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.type = type;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
