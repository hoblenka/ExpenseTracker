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
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Expense() {}

    public Expense(String description, BigDecimal amount, ExpenseCategory category, LocalDate date) {
        try {
            setDescription(description);
            setAmount(amount);
            setCategory(category);
            setDate(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expense data: " + e.getMessage(), e);
        }
    }

    public Expense(String description, BigDecimal amount, ExpenseCategory category, LocalDate date, Long userId) {
        this(description, amount, category, date);
        setUserId(userId);
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

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { 
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = category; 
    }

    public String getCategoryDisplayName() { 
        return category != null ? category.getDisplayName() : null; 
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { 
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date; 
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}