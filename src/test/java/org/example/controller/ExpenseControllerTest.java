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

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void testGetAllExpenses() {
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Bus ticket", new BigDecimal("3.00"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(mockExpenseCrudService.getAllExpenses()).thenReturn(mockExpenses);

        ResponseEntity<List<Expense>> response = expenseController.getAllExpenses();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Coffee", response.getBody().get(0).getDescription());
        
        verify(mockExpenseCrudService).getAllExpenses();
    }

    @Test
    void testGetExpenseById() {
        Expense mockExpense = new Expense("Lunch", new BigDecimal("12.50"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(mockExpenseCrudService.getExpenseById(1L)).thenReturn(mockExpense);

        ResponseEntity<Expense> response = expenseController.getExpenseById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Lunch", response.getBody().getDescription());
        
        verify(mockExpenseCrudService).getExpenseById(1L);
    }

    @Test
    void testGetExpenseByIdNotFound() {
        when(mockExpenseCrudService.getExpenseById(999L)).thenReturn(null);

        ResponseEntity<Expense> response = expenseController.getExpenseById(999L);

        assertEquals(404, response.getStatusCode().value());
        
        verify(mockExpenseCrudService).getExpenseById(999L);
    }

    @Test
    void testDeleteExpense() {
        doNothing().when(mockExpenseCrudService).deleteExpense(1L);

        ResponseEntity<Void> response = expenseController.deleteExpense(1L);

        assertEquals(200, response.getStatusCode().value());
        
        verify(mockExpenseCrudService).deleteExpense(1L);
    }

    @Test
    void testGetTotalAmount() {
        BigDecimal mockTotal = new BigDecimal("150.75");
        
        when(mockExpenseCrudService.getTotalAmount()).thenReturn(mockTotal);

        ResponseEntity<BigDecimal> response = expenseController.getTotalAmount();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockTotal, response.getBody());
        
        verify(mockExpenseCrudService).getTotalAmount();
    }

    @Test
    void testGetExpensesByCategory() {
        List<Expense> mockExpenses = List.of(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(mockFilterService.filterExpensesByCategory("Food")).thenReturn(mockExpenses);

        ResponseEntity<List<Expense>> response = expenseController.getExpensesByCategory("Food");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(mockFilterService).filterExpensesByCategory("Food");
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