package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseControllerAPITest {

    private final ExpenseController expenseController = new ExpenseController(null, null, null);

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
}