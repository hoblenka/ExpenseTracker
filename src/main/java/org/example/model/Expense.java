package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@SuppressWarnings("unused")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private LocalDate date;

    public Expense() {}

    public Expense(String description, BigDecimal amount, String category, LocalDate date) {
        try {
            setDescription(description);
            setAmount(amount);
            setCategory(category);
            setDate(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expense data: " + e.getMessage(), e);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { 
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description.trim(); 
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { 
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (amount.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("Amount cannot exceed 99,999,999.99");
        }
        this.amount = amount; 
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { 
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (!ExpenseCategory.isValid(category.trim())) {
            throw new IllegalArgumentException("Invalid category. Must be one of: Food, Transport, Utilities, Entertainment, Shopping, Rent, Other");
        }
        this.category = category.trim(); 
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { 
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date; 
    }
}