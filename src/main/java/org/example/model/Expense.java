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
        setDescription(description);
        setAmount(amount);
        setCategory(category);
        setDate(date);
    }

    // Getters and Setters
    public Long getId() { return id; }
    void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { 
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description; 
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { 
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        this.amount = amount; 
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { 
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category; 
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { 
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date; 
    }
}