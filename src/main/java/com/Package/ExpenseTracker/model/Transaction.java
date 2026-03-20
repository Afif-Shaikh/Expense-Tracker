package com.Package.ExpenseTracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Type must be INCOME or EXPENSE")
    @Column(nullable = false)
    private String type;

    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Transaction() {}

    public Transaction(String name, BigDecimal amount, LocalDate date,
                       String category, String type, String comments) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.type = type;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}