package org.example.controller;

import org.example.model.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// No mocks are needed in this basic test
class ExpenseControllerResponseErrorTest {

    private final ExpenseController expenseController = new ExpenseController(null, null, null, null);

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
    void testGetExpensesByCategoryWithEmptyCategory() {
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory("");
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetExpensesByCategoryWithNullCategory() {
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory(null);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetExpensesByCategoryWithWhitespaceCategory() {
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory("   ");
        assertEquals(400, response.getStatusCode().value());
    }
}