package org.example.controller;

import org.example.model.Expense;
import org.example.service.ExpenseService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    private final ExpenseService expenseService;
    
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        try {
            return ResponseEntity.ok(expenseService.getAllExpenses());
        } catch (Exception e) {
            logger.error("Failed to get all expenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Void> createExpense(@Valid @RequestBody Expense expense) {
        try {
            if (!isValidExpense(expense)) {
                return ResponseEntity.badRequest().build();
            }
            
            // Create new clean expense with sanitized data
            Expense cleanExpense = new Expense(
                HtmlUtils.htmlEscape(expense.getDescription()),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate()
            );
            
            expenseService.saveExpense(cleanExpense);
            //Changing return type to ResponseEntity<Void> to avoid returning any potentially tainted data
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            logger.error("Failed to create expense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Expense expense = expenseService.getExpenseById(id);
            return expense != null ? ResponseEntity.ok(expense) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get expense by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            expenseService.deleteExpense(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete expense by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            String trimmedCategory = category.trim();
            return ResponseEntity.ok(expenseService.getExpensesByCategory(trimmedCategory));
        } catch (Exception e) {
            logger.error("Failed to get expenses by category '{}': {}", category, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllExpenses() {
        try {
            expenseService.deleteAllExpenses();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete all expenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/random")
    public ResponseEntity<Void> addRandomExpense() {
        try {
            expenseService.addRandomExpense();
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            logger.error("Failed to add random expense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmount() {
        try {
            return ResponseEntity.ok(expenseService.getTotalAmount());
        } catch (Exception e) {
            logger.error("Failed to get total amount: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isValidExpense(Expense expense) {
        return expense.getDescription() != null && expense.getCategory() != null && 
               expense.getAmount() != null && expense.getDate() != null;
    }
}