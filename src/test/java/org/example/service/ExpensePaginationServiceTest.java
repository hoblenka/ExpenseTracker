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
        Long userId = 1L;
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            new Expense("Bus ticket", new BigDecimal("3.00"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(mockExpenseDAO.countAllByUserId(userId)).thenReturn(25L);
        when(mockExpenseDAO.findPageByUserId(0, 10, userId)).thenReturn(mockExpenses);

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPageByUserId(0, 10, userId);

        assertEquals(0, result.currentPage());
        assertEquals(10, result.pageSize());
        assertEquals(25L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(2, result.content().size());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
        
        verify(mockExpenseDAO).countAllByUserId(userId);
        verify(mockExpenseDAO).findPageByUserId(0, 10, userId);
    }

    @Test
    void testGetExpensesPageOutOfRange() {
        Long userId = 1L;
        when(mockExpenseDAO.countAllByUserId(userId)).thenReturn(25L);
        when(mockExpenseDAO.findPageByUserId(2, 10, userId)).thenReturn(List.of());

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPageByUserId(10, 10, userId);

        assertEquals(2, result.currentPage()); // Should be adjusted to last page
        assertEquals(3, result.totalPages());
        
        verify(mockExpenseDAO).findPageByUserId(2, 10, userId);
    }

    @Test
    void testGetExpensesPageNegativePage() {
        Long userId = 1L;
        List<Expense> mockExpenses = List.of(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(mockExpenseDAO.countAllByUserId(userId)).thenReturn(5L);
        when(mockExpenseDAO.findPageByUserId(0, 10, userId)).thenReturn(mockExpenses);

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPageByUserId(-1, 10, userId);

        assertEquals(0, result.currentPage()); // Should be adjusted to 0
        
        verify(mockExpenseDAO).findPageByUserId(0, 10, userId);
    }

    @Test
    void testGetExpensesPageInvalidSize() {
        Long userId = 1L;
        List<Expense> mockExpenses = List.of(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(mockExpenseDAO.countAllByUserId(userId)).thenReturn(5L);
        when(mockExpenseDAO.findPageByUserId(0, 10, userId)).thenReturn(mockExpenses);

        ExpensePaginationService.PageResult<Expense> result = paginationService.getExpensesPageByUserId(0, 0, userId);

        assertEquals(10, result.pageSize()); // Should use default size
        
        verify(mockExpenseDAO).findPageByUserId(0, 10, userId);
    }

    @Test
    void testGetPageFromList() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            new Expense("Bus", new BigDecimal("3.00"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId),
            new Expense("Lunch", new BigDecimal("12.00"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );

        ExpensePaginationService.PageResult<Expense> result = paginationService.getPageFromList(expenses, 0, 2);

        assertEquals(0, result.currentPage());
        assertEquals(2, result.pageSize());
        assertEquals(3L, result.totalElements());
        assertEquals(2, result.totalPages());
        assertEquals(2, result.content().size());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
    }

    @Test
    void testGetPageFromListEmptyList() {
        ExpensePaginationService.PageResult<Expense> result = paginationService.getPageFromList(List.of(), 0, 10);

        assertEquals(0, result.currentPage());
        assertEquals(10, result.pageSize());
        assertEquals(0L, result.totalElements());
        assertEquals(0, result.totalPages());
        assertEquals(0, result.content().size());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
    }

    @Test
    void testGetPageFromListPageOutOfRange() {
        Long userId = 1L;
        List<Expense> expenses = List.of(
                new Expense("Coffee", new BigDecimal("5.50"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );

        ExpensePaginationService.PageResult<Expense> result = paginationService.getPageFromList(expenses, 10, 10);

        assertEquals(0, result.currentPage()); // Should be adjusted to last page
        assertEquals(1, result.totalPages());
        assertEquals(1, result.content().size());
    }
}