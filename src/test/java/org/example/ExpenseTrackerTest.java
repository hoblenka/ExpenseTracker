package org.example;

import org.example.model.Expense;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTrackerTest {

    @Test
    public void testExpenseCreation() {
        Expense expense = new Expense("Test expense", new BigDecimal("100.00"), "Food", LocalDate.now());
        
        assertNotNull(expense);
        assertEquals("Test expense", expense.getDescription());
        assertEquals(new BigDecimal("100.00"), expense.getAmount());
        assertEquals("Food", expense.getCategory());
        assertNotNull(expense.getDate());
    }

    @Test
    public void testExpenseModification() {
        Expense expense = new Expense("Original", new BigDecimal("50.00"), "Transport", LocalDate.now());
        
        expense.setDescription("Updated");
        expense.setAmount(new BigDecimal("75.00"));
        
        assertEquals("Updated", expense.getDescription());
        assertEquals(new BigDecimal("75.00"), expense.getAmount());
    }

    @Test
    public void testInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("Test", new BigDecimal("10.00"), "InvalidCategory", LocalDate.now()));
    }

    @Test
    public void testValidCategories() {
        // Test all valid categories
        String[] validCategories = {"Food", "Transport", "Utilities", "Entertainment", "Shopping", "Rent", "Other"};
        
        for (String category : validCategories) {
            Expense expense = new Expense("Test", new BigDecimal("10.00"), category, LocalDate.now());
            assertEquals(category, expense.getCategory());
        }
    }

    @Test
    public void testAmountCalculation() {
        Expense expense1 = new Expense("Test 1", new BigDecimal("10.50"), "Food", LocalDate.now());
        Expense expense2 = new Expense("Test 2", new BigDecimal("25.75"), "Transport", LocalDate.now());
        Expense expense3 = new Expense("Test 3", new BigDecimal("5.25"), "Other", LocalDate.now());
        
        BigDecimal total = expense1.getAmount()
                .add(expense2.getAmount())
                .add(expense3.getAmount());
        
        assertEquals(new BigDecimal("41.50"), total);
    }
}