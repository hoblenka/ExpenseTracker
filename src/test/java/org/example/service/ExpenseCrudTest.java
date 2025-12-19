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
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = crudService.getAllExpenses();

        assertEquals(2, result.size());
        verify(expenseDAO).findAll();
    }

    @Test
    void testGetExpenseById() {
        Expense expense = createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(expenseDAO.findById(1L)).thenReturn(expense);

        Expense result = crudService.getExpenseById(1L);

        assertEquals("Lunch", result.getDescription());
        verify(expenseDAO).findById(1L);
    }

    @Test
    void testSaveExpense() {
        Expense expense = createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now());
        when(idService.getNextAvailableId()).thenReturn(1L);
        
        crudService.saveExpense(expense);

        assertEquals(1L, expense.getId());
        verify(idService).getNextAvailableId();
        verify(expenseDAO).save(expense);
    }

    @Test
    void testUpdateExpense() {
        Expense expense = createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        crudService.updateExpense(expense);

        verify(expenseDAO).update(expense);
    }

    @Test
    void testDeleteExpense() {
        crudService.deleteExpense(1L);

        verify(expenseDAO).deleteById(1L);
    }

    @Test
    void testDeleteAllExpenses() {
        crudService.deleteAllExpenses();

        verify(expenseDAO).deleteAll();
    }

    @Test
    void testGetTotalAmount() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        BigDecimal total = crudService.getTotalAmount();

        assertEquals(new BigDecimal("17.50"), total);
        verify(expenseDAO).findAll();
    }

    @Test
    void testAddRandomExpense() {
        crudService.addRandomExpense();

        verify(expenseDAO).save(any(Expense.class));
    }

    private Expense createExpense(String description, BigDecimal amount, ExpenseCategory category, LocalDate date) {
        return new Expense(description, amount, category, date);
    }
}