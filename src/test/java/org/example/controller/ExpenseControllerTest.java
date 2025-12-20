package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpenseSortService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseCrudService mockExpenseCrudService;
    
    @Mock
    private ExpenseFilterService mockFilterService;
    
    @Mock
    private ExpenseSortService mockSortService;
    
    @Mock
    private HttpSession mockSession;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void testExpenseValidation() {
        Expense invalidExpense = new Expense();
        assertFalse(isValidExpense(invalidExpense));

        Expense validExpense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        assertTrue(isValidExpense(validExpense));
    }

    @Test
    void testGetAllExpenses() {
        Long userId = 1L;
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Bus ticket", new BigDecimal("3.00"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockExpenseCrudService.getAllExpensesByUserId(userId)).thenReturn(mockExpenses);

        ResponseEntity<List<Expense>> response = expenseController.getAllExpenses(null, null, mockSession);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Coffee", response.getBody().get(0).getDescription());
        
        verify(mockExpenseCrudService).getAllExpensesByUserId(userId);
    }

    @Test
    void testGetExpenseById() {
        Long userId = 1L;
        Expense mockExpense = new Expense("Lunch", new BigDecimal("12.50"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockExpenseCrudService.getExpenseByIdAndUserId(1L, userId)).thenReturn(mockExpense);

        ResponseEntity<Expense> response = expenseController.getExpenseById(1L, mockSession);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Lunch", response.getBody().getDescription());
        
        verify(mockExpenseCrudService).getExpenseByIdAndUserId(1L, userId);
    }

    @Test
    void testGetExpenseByIdNotFound() {
        Long userId = 1L;
        
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockExpenseCrudService.getExpenseByIdAndUserId(999L, userId)).thenReturn(null);

        ResponseEntity<Expense> response = expenseController.getExpenseById(999L, mockSession);

        assertEquals(404, response.getStatusCode().value());
        
        verify(mockExpenseCrudService).getExpenseByIdAndUserId(999L, userId);
    }

    @Test
    void testDeleteExpense() {
        Long userId = 1L;
        
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        doNothing().when(mockExpenseCrudService).deleteExpenseByIdAndUserId(1L, userId);

        ResponseEntity<Void> response = expenseController.deleteExpense(1L, mockSession);

        assertEquals(200, response.getStatusCode().value());
        
        verify(mockExpenseCrudService).deleteExpenseByIdAndUserId(1L, userId);
    }

    @Test
    void testGetTotalAmount() {
        Long userId = 1L;
        BigDecimal mockTotal = new BigDecimal("150.75");
        
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockExpenseCrudService.getTotalAmountByUserId(userId)).thenReturn(mockTotal);

        ResponseEntity<BigDecimal> response = expenseController.getTotalAmount(mockSession);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockTotal, response.getBody());
        
        verify(mockExpenseCrudService).getTotalAmountByUserId(userId);
    }

    @Test
    void testGetExpensesByCategory() {
        Long userId = 1L;
        List<Expense> mockExpenses = List.of(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockFilterService.filterExpensesByCategoryAndUserId("Food", userId)).thenReturn(mockExpenses);

        ResponseEntity<List<Expense>> response = expenseController.getAllExpenses("Food", null, mockSession);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(mockFilterService).filterExpensesByCategoryAndUserId("Food", userId);
    }

    private boolean isValidExpense(Expense expense) {
        return expense.getDescription() != null && expense.getCategory() != null &&
                expense.getAmount() != null && expense.getDate() != null;
    }
}