package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensePaginationServiceTest {

    @Mock
    private ExpenseDAO mockExpenseDAO;

    @InjectMocks
    private ExpensePaginationService paginationService;

    @Test
    void testGetExpensesPage() {
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Bus ticket", new BigDecimal("3.00"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(mockExpenseDAO.countAll()).thenReturn(25L);
        when(mockExpenseDAO.findPage(0, 10)).thenReturn(mockExpenses);

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPage(0, 10);

        assertEquals(0, result.currentPage());
        assertEquals(10, result.pageSize());
        assertEquals(25L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(2, result.content().size());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
        
        verify(mockExpenseDAO).countAll();
        verify(mockExpenseDAO).findPage(0, 10);
    }

    @Test
    void testGetExpensesPageOutOfRange() {
        when(mockExpenseDAO.countAll()).thenReturn(25L);
        when(mockExpenseDAO.findPage(2, 10)).thenReturn(Arrays.asList());

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPage(10, 10);

        assertEquals(2, result.currentPage()); // Should be adjusted to last page
        assertEquals(3, result.totalPages());
        
        verify(mockExpenseDAO).findPage(2, 10);
    }

    @Test
    void testGetExpensesPageNegativePage() {
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(mockExpenseDAO.countAll()).thenReturn(5L);
        when(mockExpenseDAO.findPage(0, 10)).thenReturn(mockExpenses);

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPage(-1, 10);

        assertEquals(0, result.currentPage()); // Should be adjusted to 0
        
        verify(mockExpenseDAO).findPage(0, 10);
    }

    @Test
    void testGetExpensesPageInvalidSize() {
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(mockExpenseDAO.countAll()).thenReturn(5L);
        when(mockExpenseDAO.findPage(0, 10)).thenReturn(mockExpenses);

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPage(0, 0);

        assertEquals(10, result.pageSize()); // Should use default size
        
        verify(mockExpenseDAO).findPage(0, 10);
    }
}