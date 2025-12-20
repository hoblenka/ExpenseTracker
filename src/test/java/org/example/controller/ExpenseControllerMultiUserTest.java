package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpensePaginationService;
import org.example.service.ExpenseSortService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExpenseControllerMultiUserTest {

    @Mock
    private ExpenseCrudService crudService;
    
    @Mock
    private ExpenseFilterService filterService;
    
    @Mock
    private ExpenseSortService sortService;
    
    @Mock
    private ExpensePaginationService paginationService;
    
    @Mock
    private HttpSession session;

    private ExpenseController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ExpenseController(crudService, filterService, sortService, paginationService);
    }

    @Test
    void testGetAllExpenses_WithValidSession_ReturnsUserExpenses() {
        Long userId = 1L;
        List<Expense> userExpenses = Arrays.asList(
            createExpenseForUser("User expense", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(session.getAttribute("userId")).thenReturn(userId);
        when(crudService.getAllExpensesByUserId(userId)).thenReturn(userExpenses);

        ResponseEntity<List<Expense>> response = controller.getAllExpenses(null, null, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());
        verify(crudService).getAllExpensesByUserId(userId);
        verify(crudService, never()).getAllExpenses();
    }

    @Test
    void testGetAllExpenses_WithoutSession_ReturnsUnauthorized() {
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<List<Expense>> response = controller.getAllExpenses(null, null, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(crudService, never()).getAllExpenses();
        verify(crudService, never()).getAllExpensesByUserId(any());
    }

    @Test
    void testGetAllExpenses_WithCategoryFilter_UsesUserSpecificFilter() {
        Long userId = 1L;
        String category = "Food";
        List<Expense> filteredExpenses = Arrays.asList(
            createExpenseForUser("Food expense", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(session.getAttribute("userId")).thenReturn(userId);
        when(filterService.filterExpensesByCategoryAndUserId(category, userId)).thenReturn(filteredExpenses);

        ResponseEntity<List<Expense>> response = controller.getAllExpenses(category, null, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(filterService).filterExpensesByCategoryAndUserId(category, userId);
        verify(filterService, never()).filterExpensesByCategory(category);
    }

    @Test
    void testCreateExpense_WithValidSession_CreatesExpenseForUser() {
        Long userId = 1L;
        Expense inputExpense = new Expense("New expense", new BigDecimal("30.00"), ExpenseCategory.TRANSPORT, LocalDate.now());
        
        when(session.getAttribute("userId")).thenReturn(userId);

        ResponseEntity<Void> response = controller.createExpense(inputExpense, session);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(crudService).saveExpense(argThat(expense -> 
            expense.getUserId().equals(userId) && 
            expense.getDescription().equals("New expense")
        ));
    }

    @Test
    void testCreateExpense_WithoutSession_ReturnsUnauthorized() {
        Expense inputExpense = new Expense("New expense", new BigDecimal("30.00"), ExpenseCategory.TRANSPORT, LocalDate.now());
        
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<Void> response = controller.createExpense(inputExpense, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(crudService, never()).saveExpense(any());
    }

    @Test
    void testUserIsolation_DifferentSessionsGetDifferentData() {
        Long user1Id = 1L;
        Long user2Id = 2L;
        
        HttpSession session1 = mock(HttpSession.class);
        HttpSession session2 = mock(HttpSession.class);
        
        List<Expense> user1Expenses = Arrays.asList(
            createExpenseForUser("User 1 expense", new BigDecimal("20.00"), ExpenseCategory.FOOD, LocalDate.now(), user1Id)
        );
        List<Expense> user2Expenses = Arrays.asList(
            createExpenseForUser("User 2 expense", new BigDecimal("40.00"), ExpenseCategory.TRANSPORT, LocalDate.now(), user2Id)
        );
        
        when(session1.getAttribute("userId")).thenReturn(user1Id);
        when(session2.getAttribute("userId")).thenReturn(user2Id);
        when(crudService.getAllExpensesByUserId(user1Id)).thenReturn(user1Expenses);
        when(crudService.getAllExpensesByUserId(user2Id)).thenReturn(user2Expenses);

        ResponseEntity<List<Expense>> response1 = controller.getAllExpenses(null, null, session1);
        ResponseEntity<List<Expense>> response2 = controller.getAllExpenses(null, null, session2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(user1Id, response1.getBody().get(0).getUserId());
        assertEquals(user2Id, response2.getBody().get(0).getUserId());
        assertNotEquals(response1.getBody().get(0).getDescription(), response2.getBody().get(0).getDescription());
    }

    @Test
    void testSessionValidation_AllEndpointsRequireValidSession() {
        // Test multiple endpoints without valid session
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<List<Expense>> getAllResponse = controller.getAllExpenses(null, null, session);
        assertEquals(HttpStatus.UNAUTHORIZED, getAllResponse.getStatusCode());

        Expense testExpense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        ResponseEntity<Void> createResponse = controller.createExpense(testExpense, session);
        assertEquals(HttpStatus.UNAUTHORIZED, createResponse.getStatusCode());

        // Verify no service calls were made
        verify(crudService, never()).getAllExpensesByUserId(any());
        verify(crudService, never()).saveExpense(any());
    }

    private Expense createExpenseForUser(String description, BigDecimal amount, ExpenseCategory category, LocalDate date, Long userId) {
        Expense expense = new Expense(description, amount, category, date, userId);
        expense.setId(1L);
        return expense;
    }
}