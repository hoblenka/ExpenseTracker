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

public class ExpenseCrudServiceMultiUserTest {

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
    void testGetAllExpensesByUserId_ReturnsOnlyUserExpenses() {
        long userId = 1L;
        List<Expense> userExpenses = Arrays.asList(
            createExpenseForUser("User 1 Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("User 1 Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(userExpenses);

        List<Expense> result = crudService.getAllExpensesByUserId(userId);

        assertEquals(2, result.size());
        result.forEach(expense -> assertEquals(userId, expense.getUserId()));
        verify(expenseDAO).findAllByUserId(userId);
        verify(expenseDAO, never()).findAll();
    }

    @Test
    void testGetExpenseByIdAndUserId_ReturnsExpenseForCorrectUser() {
        Long expenseId = 1L;
        Long userId = 1L;
        Expense userExpense = createExpenseForUser("User expense", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now(), userId);
        
        when(expenseDAO.findByIdAndUserId(expenseId, userId)).thenReturn(userExpense);

        Expense result = crudService.getExpenseByIdAndUserId(expenseId, userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(expenseDAO).findByIdAndUserId(expenseId, userId);
        verify(expenseDAO, never()).findById(expenseId);
    }

    @Test
    void testGetExpenseByIdAndUserId_ReturnsNullForWrongUser() {
        Long expenseId = 1L;
        Long userId = 2L;
        
        when(expenseDAO.findByIdAndUserId(expenseId, userId)).thenReturn(null);

        Expense result = crudService.getExpenseByIdAndUserId(expenseId, userId);

        assertNull(result);
        verify(expenseDAO).findByIdAndUserId(expenseId, userId);
    }

    @Test
    void testDeleteExpenseByIdAndUserId_OnlyDeletesUserExpense() {
        Long expenseId = 1L;
        Long userId = 1L;

        crudService.deleteExpenseByIdAndUserId(expenseId, userId);

        verify(expenseDAO).deleteByIdAndUserId(expenseId, userId);
        verify(expenseDAO, never()).deleteById(expenseId);
    }

    @Test
    void testDeleteAllExpensesByUserId_OnlyDeletesUserExpenses() {
        Long userId = 1L;

        crudService.deleteAllExpensesByUserId(userId);

        verify(expenseDAO).deleteAllByUserId(userId);
        verify(expenseDAO, never()).deleteAll();
    }

    @Test
    void testGetTotalAmountByUserId_CalculatesOnlyUserExpenses() {
        Long userId = 1L;
        List<Expense> userExpenses = Arrays.asList(
            createExpenseForUser("Expense 1", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Expense 2", new BigDecimal("15.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(userExpenses);

        BigDecimal total = crudService.getTotalAmountByUserId(userId);

        assertEquals(new BigDecimal("25.50"), total);
        verify(expenseDAO).findAllByUserId(userId);
    }

    @Test
    void testAddRandomExpenseForUser_CreatesExpenseWithCorrectUserId() {
        Long userId = 2L;
        when(idService.getNextAvailableId()).thenReturn(1L);

        Expense result = crudService.addRandomExpenseForUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(expenseDAO).save(any(Expense.class));
    }

    @Test
    void testUserIsolation_DifferentUsersGetDifferentData() {
        Long user1Id = 1L;
        Long user2Id = 2L;
        
        List<Expense> user1Expenses = List.of(
            createExpenseForUser("User 1 Expense", new BigDecimal("20.00"), ExpenseCategory.FOOD, LocalDate.now(), user1Id)
        );
        List<Expense> user2Expenses = List.of(
                createExpenseForUser("User 2 Expense", new BigDecimal("30.00"), ExpenseCategory.TRANSPORT, LocalDate.now(), user2Id)
        );
        
        when(expenseDAO.findAllByUserId(user1Id)).thenReturn(user1Expenses);
        when(expenseDAO.findAllByUserId(user2Id)).thenReturn(user2Expenses);

        List<Expense> user1Result = crudService.getAllExpensesByUserId(user1Id);
        List<Expense> user2Result = crudService.getAllExpensesByUserId(user2Id);

        assertEquals(1, user1Result.size());
        assertEquals(1, user2Result.size());
        assertEquals(user1Id, user1Result.get(0).getUserId());
        assertEquals(user2Id, user2Result.get(0).getUserId());
        assertNotEquals(user1Result.get(0).getDescription(), user2Result.get(0).getDescription());
    }

    private Expense createExpenseForUser(String description, BigDecimal amount, ExpenseCategory category, LocalDate date, Long userId) {
        Expense expense = new Expense(description, amount, category, date, userId);
        expense.setId(1L);
        return expense;
    }
}