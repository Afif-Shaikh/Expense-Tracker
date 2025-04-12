package com.Package.ExpenseTracker.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "income")
public class Income {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private double amount;
	private String category;
	private String date;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Income(Long id, String name, double amount, String category, String date) {
		this.id = id;
		this.name = name;
		this.amount = amount;
		this.category = category;
		this.date = date;
	}
	public Income(String name, double amount, LocalDate date, String category) {
	    this.name = name;
	    this.amount = amount;
	    this.date = date.toString(); // Convert LocalDate to String
	    this.category = category;
	}

	public Income() {
	}
	
}
