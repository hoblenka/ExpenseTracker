package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseCrudTest {

    @Mock
    private ExpenseDAO expenseDAO;
    
    @Mock
    private ExpenseIdService idService;
    
    private ExpenseCrudService crudService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        crudService = new ExpenseCrudService(expenseDAO, idService);
    }

    @Test
    void testGetAllExpenses() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = crudService.getAllExpensesByUserId(userId);

        assertEquals(2, result.size());
        verify(expenseDAO).findAllByUserId(userId);
    }

    @Test
    void testGetExpenseById() {
        Long userId = 1L;
        Expense expense = createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId);
        
        when(expenseDAO.findByIdAndUserId(1L, userId)).thenReturn(expense);

        Expense result = crudService.getExpenseByIdAndUserId(1L, userId);

        assertEquals("Lunch", result.getDescription());
        verify(expenseDAO).findByIdAndUserId(1L, userId);
    }

    @Test
    void testSaveExpense() {
        Long userId = 1L;
        Expense expense = createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId);
        
        crudService.saveExpense(expense);

        verify(expenseDAO).save(expense);
    }

    @Test
    void testUpdateExpense() {
        Long userId = 1L;
        Expense expense = createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId);
        
        crudService.updateExpense(expense);

        verify(expenseDAO).update(expense);
    }

    @Test
    void testDeleteExpense() {
        Long userId = 1L;
        crudService.deleteExpenseByIdAndUserId(1L, userId);

        verify(expenseDAO).deleteByIdAndUserId(1L, userId);
    }

    @Test
    void testDeleteAllExpenses() {
        Long userId = 1L;
        crudService.deleteAllExpensesByUserId(userId);

        verify(expenseDAO).deleteAllByUserId(userId);
    }

    @Test
    void testGetTotalAmount() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        BigDecimal total = crudService.getTotalAmountByUserId(userId);

        assertEquals(new BigDecimal("17.50"), total);
        verify(expenseDAO).findAllByUserId(userId);
    }

    @Test
    void testAddRandomExpense() {
        Long userId = 1L;
        crudService.addRandomExpenseForUser(userId);

        verify(expenseDAO).save(any(Expense.class));
    }

    private Expense createExpenseForUser(String description, BigDecimal amount, ExpenseCategory category, LocalDate date, Long userId) {
        return new Expense(description, amount, category, date, userId);
    }
}