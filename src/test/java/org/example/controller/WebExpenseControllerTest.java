package org.example.controller;

import org.example.dao.UserDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpensePaginationService;
import org.example.service.ExpenseSortService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebExpenseControllerTest {

    @Mock
    private ExpenseCrudService expenseCrudService;
    
    @Mock
    private ExpenseFilterService filterService;
    
    @Mock
    private ExpenseSortService sortService;

    @Mock
    private ExpensePaginationService expensePaginationService;

    @Mock
    private ExpenseController expenseController;

    @Mock
    private Model model;
    
    @Mock
    private HttpSession session;

    @Mock
    private UserDAO user;

    private WebExpenseController webController;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webController = new WebExpenseController(expenseCrudService, filterService, sortService, expensePaginationService, expenseController, user);
    }

    @Test
    void testListExpensesWithDateRange() {
        when(session.getAttribute("userId")).thenReturn(userId);
        
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        ExpensePaginationService.PageResult<Expense> pageResult = 
            new ExpensePaginationService.PageResult<>(List.of(expense), 0, 10, 1, 1);
        
        when(filterService.getFilteredExpensesByUserId(LocalDate.parse(startDate), LocalDate.parse(endDate), null, userId))
                .thenReturn(List.of(expense));
        when(expensePaginationService.getPageFromList(List.of(expense), 0, 10)).thenReturn(pageResult);
        String userIdstr = String.valueOf(this.userId);
        String result = webController.listExpenses(startDate, endDate, null, null, userIdstr, 0, 10, model, session);

        assertEquals("list", result);
        verify(filterService).getFilteredExpensesByUserId(LocalDate.parse(startDate), LocalDate.parse(endDate), null, userId);
        verify(model).addAttribute("expenses", List.of(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
        verify(model).addAttribute("startDate", startDate);
        verify(model).addAttribute("endDate", endDate);
    }

    @Test
    void testListExpensesWithoutDateRange() {
        when(session.getAttribute("userId")).thenReturn(userId);
        
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.now());
        ExpensePaginationService.PageResult<Expense> pageResult = 
            new ExpensePaginationService.PageResult<>(List.of(expense), 0, 10, 1, 1);
        
        when(expenseCrudService.getAllExpensesByUserId(userId)).thenReturn(List.of(expense));
        when(expensePaginationService.getPageFromList(List.of(expense), 0, 10)).thenReturn(pageResult);
        String userIdstr = String.valueOf(this.userId);
        String result = webController.listExpenses(null, null, null, null,userIdstr, 0, 10, model, session);

        assertEquals("list", result);
        verify(expenseCrudService).getAllExpensesByUserId(userId);
        verify(model).addAttribute("expenses", List.of(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
    }

    @Test
    void testShowAddForm() {
        String result = webController.showAddForm();
        assertEquals("add", result);
    }

    @Test
    void testDeleteExpense() {
        when(session.getAttribute("userId")).thenReturn(userId);
        
        String result = webController.deleteExpense(1L, null, null, null, null, 0, 10, session);
        assertTrue(result.startsWith("redirect:/expenses?"));
        verify(expenseCrudService).deleteExpenseByIdAndUserId(1L, userId);
    }

    @Test
    void testHome() {
        String result = webController.home();
        assertEquals("redirect:/expenses", result);
    }

    @Test
    void testDeleteAllExpenses() {
        when(session.getAttribute("userId")).thenReturn(userId);
        
        String result = webController.deleteAllExpenses(null, null, null, null, 0, 10, session);
        assertTrue(result.startsWith("redirect:/expenses?"));
        verify(expenseCrudService).deleteAllExpensesByUserId(userId);
    }

    @Test
    void testAddRandomExpense() {
        when(session.getAttribute("userId")).thenReturn(userId);
        
        Expense mockExpense = new Expense("Random", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now());
        mockExpense.setId(1L);
        when(expenseCrudService.addRandomExpenseForUser(userId)).thenReturn(mockExpense);
        when(expenseCrudService.getAllExpensesByUserId(userId)).thenReturn(List.of(mockExpense));
        
        String result = webController.addRandomExpense(null, null, null, null, 0, 10, session);
        assertTrue(result.startsWith("redirect:/expenses?"));
        verify(expenseCrudService).addRandomExpenseForUser(userId);
    }

    @Test
    void testRedirectToLoginWhenNoSession() {
        when(session.getAttribute("userId")).thenReturn(null);
        
        String result = webController.deleteExpense(1L, null, null, null, null, 0, 10, session);
        assertEquals("redirect:/login", result);
    }
}