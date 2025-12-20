package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerResponseErrorTest {

    @Mock
    private HttpSession mockSession;

    private final ExpenseController expenseController = new ExpenseController(null, null, null, null);

    @Test
    void testGetExpenseByIdWithInvalidId() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<Expense> response = expenseController.getExpenseById(-1L, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetExpenseByIdWithNullId() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<Expense> response = expenseController.getExpenseById(null, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testDeleteExpenseWithInvalidId() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<Void> response = expenseController.deleteExpense(-1L, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testDeleteExpenseWithNullId() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<Void> response = expenseController.deleteExpense(null, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUnauthorizedAccess() {
        ResponseEntity<Expense> response = expenseController.getExpenseById(1L, null);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetExpensesByCategoryWithEmptyCategory() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory("", mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetExpensesByCategoryWithWhitespaceCategory() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory("   ", mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetExpensesByCategoryWithoutSession() {
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory("Food", null);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testCreateExpenseWithInvalidData() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        Expense invalidExpense = new Expense();
        ResponseEntity<Void> response = expenseController.createExpense(invalidExpense, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testCreateExpenseWithoutSession() {
        Expense expense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        ResponseEntity<Void> response = expenseController.createExpense(expense, null);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testAddMultipleRandomExpensesWithInvalidCount() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<Void> response = expenseController.addMultipleRandomExpenses(-1, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testAddMultipleRandomExpensesWithExcessiveCount() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<Void> response = expenseController.addMultipleRandomExpenses(101, mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testAddMultipleRandomExpensesWithoutSession() {
        ResponseEntity<Void> response = expenseController.addMultipleRandomExpenses(5, null);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testGetExpensesByDateRangeWithInvalidDate() {
        when(mockSession.getAttribute("userId")).thenReturn(1L);
        ResponseEntity<List<Expense>> response = expenseController.getExpensesByDateRange("invalid-date", "2024-01-31", mockSession);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testUnauthorizedAccessToTotalAmount() {
        ResponseEntity<BigDecimal> response = expenseController.getTotalAmount(null);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUnauthorizedAccessToDeleteAll() {
        ResponseEntity<Void> response = expenseController.deleteAllExpenses(null);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUnauthorizedAccessToAddRandom() {
        ResponseEntity<Void> response = expenseController.addRandomExpense(null);
        assertEquals(401, response.getStatusCode().value());
    }
}