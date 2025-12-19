package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseControllerTest {

    private final ExpenseController expenseController = new ExpenseController(null);

    @Test
    void testGetExpenseByIdWithInvalidId() {
        ResponseEntity<Expense> response = expenseController.getExpenseById(-1L);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetExpenseByIdWithNullId() {
        ResponseEntity<Expense> response = expenseController.getExpenseById(null);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testDeleteExpenseWithInvalidId() {
        ResponseEntity<Void> response = expenseController.deleteExpense(-1L);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testDeleteExpenseWithNullId() {
        ResponseEntity<Void> response = expenseController.deleteExpense(null);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testExpenseValidation() {
        Expense invalidExpense = new Expense();
        assertFalse(isValidExpense(invalidExpense));
        
        Expense validExpense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        assertTrue(isValidExpense(validExpense));
    }

    private boolean isValidExpense(Expense expense) {
        return expense.getDescription() != null && expense.getCategory() != null && 
               expense.getAmount() != null && expense.getDate() != null;
    }

    @Test
    public void testExpenseCreation() {
        Expense expense = new Expense("Test expense", new BigDecimal("100.00"), ExpenseCategory.FOOD, LocalDate.now());

        assertNotNull(expense);
        assertEquals("Test expense", expense.getDescription());
        assertEquals(new BigDecimal("100.00"), expense.getAmount());
        assertEquals(ExpenseCategory.FOOD, expense.getCategory());
        assertNotNull(expense.getDate());
    }

    @Test
    public void testExpenseModification() {
        Expense expense = new Expense("Original", new BigDecimal("50.00"), ExpenseCategory.TRANSPORT, LocalDate.now());

        expense.setDescription("Updated");
        expense.setAmount(new BigDecimal("75.00"));

        assertEquals("Updated", expense.getDescription());
        assertEquals(new BigDecimal("75.00"), expense.getAmount());
    }

    @Test
    public void testInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> ExpenseCategory.fromString("InvalidCategory"));
    }

    @Test
    public void testValidCategories() {
        // Test all valid categories from enum
        for (ExpenseCategory category : ExpenseCategory.values()) {
            Expense expense = new Expense("Test", new BigDecimal("10.00"), category, LocalDate.now());
            assertEquals(category, expense.getCategory());
        }
    }

    @Test
    public void testAmountCalculation() {
        Expense expense1 = new Expense("Test 1", new BigDecimal("10.50"), ExpenseCategory.FOOD, LocalDate.now());
        Expense expense2 = new Expense("Test 2", new BigDecimal("25.75"), ExpenseCategory.TRANSPORT, LocalDate.now());
        Expense expense3 = new Expense("Test 3", new BigDecimal("5.25"), ExpenseCategory.OTHER, LocalDate.now());

        BigDecimal total = expense1.getAmount()
                .add(expense2.getAmount())
                .add(expense3.getAmount());

        assertEquals(new BigDecimal("41.50"), total);
    }
}