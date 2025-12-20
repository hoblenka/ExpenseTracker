package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.dao.UserDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.model.UserRole;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpensePaginationService;
import org.example.service.ExpenseSortService;
import org.example.util.SessionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class WebExpenseControllerAdminTest {

    @Mock
    private ExpenseCrudService crudService;
    @Mock
    private ExpenseFilterService filterService;
    @Mock
    private ExpenseSortService sortService;
    @Mock
    private ExpensePaginationService paginationService;
    @Mock
    private ExpenseController expenseController;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;
    @Mock
    private UserDAO userDAO;

    private WebExpenseController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new WebExpenseController(crudService, filterService, sortService, paginationService, expenseController, userDAO);
    }

    @Test
    void testListExpenses_AdminUserSeesAllExpenses() {
        Long adminUserId = 1L;

        List<Expense> allExpenses = Arrays.asList(
                createExpense(1L, "Admin expense", new BigDecimal("10.00"), adminUserId),
                createExpense(2L, "User1 expense", new BigDecimal("20.00"), 2L),
                createExpense(3L, "User2 expense", new BigDecimal("30.00"), 3L)
        );

        try (MockedStatic<SessionHelper> sessionHelper = mockStatic(SessionHelper.class)) {

            // ✅ Mock everything the controller might use
            sessionHelper.when(() -> SessionHelper.getUserId(session))
                    .thenReturn(adminUserId);

            sessionHelper.when(() -> SessionHelper.isAdmin(session))
                    .thenReturn(true);

            sessionHelper.when(() -> SessionHelper.getUserRole(session))
                    .thenReturn(UserRole.ADMIN);

            when(crudService.getAllExpenses()).thenReturn(allExpenses);

            when(paginationService.getPageFromList(any(), eq(0), eq(10)))
                    .thenReturn(new ExpensePaginationService.PageResult<>(allExpenses, 0, 10, 3, 1));

            String result = controller.listExpenses(
                    null, null, null, null,
                    "",
                    0, 10,
                    model, session
            );

            assertEquals("list", result);

            // ✅ Admin branch
            verify(crudService).getAllExpenses();

            // ✅ User branch must not be called
            verify(crudService, never()).getAllExpensesByUserId(any());
        }
    }



    @Test
    void testListExpenses_RegularUserSeesOnlyOwnExpenses() {
        Long regularUserId = 2L;
        List<Expense> userExpenses = Arrays.asList(
            createExpense(2L, "User expense 1", new BigDecimal("15.00"), regularUserId),
            createExpense(4L, "User expense 2", new BigDecimal("25.00"), regularUserId)
        );

        try (MockedStatic<SessionHelper> sessionHelper = mockStatic(SessionHelper.class)) {
            sessionHelper.when(() -> SessionHelper.getUserId(session)).thenReturn(regularUserId);
            sessionHelper.when(() -> SessionHelper.isAdmin(session)).thenReturn(false);

            when(crudService.getAllExpensesByUserId(regularUserId)).thenReturn(userExpenses);
            when(paginationService.getPageFromList(any(), eq(0), eq(10)))
                .thenReturn(new ExpensePaginationService.PageResult<>(userExpenses, 0, 10, 2, 1));
            String userId= String.valueOf(regularUserId);
            String result = controller.listExpenses(null, null, null, null, userId,0, 10, model, session);

            assertEquals("list", result);
            verify(crudService).getAllExpensesByUserId(regularUserId);
            verify(crudService, never()).getAllExpenses();
        }
    }

    @Test
    void testListExpenses_AdminWithFilters() {
        Long adminUserId = 1L;
        String category = "Food";
        List<Expense> filteredExpenses = Arrays.asList(
            createExpense(1L, "Admin food", new BigDecimal("10.00"), adminUserId),
            createExpense(2L, "User1 food", new BigDecimal("20.00"), 2L)
        );

        try (MockedStatic<SessionHelper> sessionHelper = mockStatic(SessionHelper.class)) {
            sessionHelper.when(() -> SessionHelper.getUserId(session)).thenReturn(adminUserId);
            sessionHelper.when(() -> SessionHelper.isAdmin(session)).thenReturn(true);

            when(filterService.getFilteredExpenses(null, null, category)).thenReturn(filteredExpenses);
            when(paginationService.getPageFromList(any(), eq(0), eq(10)))
                .thenReturn(new ExpensePaginationService.PageResult<>(filteredExpenses, 0, 10, 2, 1));
            String userId= "";
            String result = controller.listExpenses(null, null, category, null, userId,0, 10, model, session);

            assertEquals("list", result);
            verify(filterService).getFilteredExpenses(null, null, category);
            verify(filterService, never()).getFilteredExpensesByUserId(any(), any(), any(), any());
        }
    }

    @Test
    void testListExpenses_RegularUserWithFilters() {
        Long regularUserId = 2L;
        String category = "Transport";
        List<Expense> filteredUserExpenses = List.of(
                createExpense(3L, "User transport", new BigDecimal("5.00"), regularUserId)
        );

        try (MockedStatic<SessionHelper> sessionHelper = mockStatic(SessionHelper.class)) {
            sessionHelper.when(() -> SessionHelper.getUserId(session)).thenReturn(regularUserId);
            sessionHelper.when(() -> SessionHelper.isAdmin(session)).thenReturn(false);

            when(filterService.getFilteredExpensesByUserId(null, null, category, regularUserId))
                .thenReturn(filteredUserExpenses);
            when(paginationService.getPageFromList(any(), eq(0), eq(10)))
                .thenReturn(new ExpensePaginationService.PageResult<>(filteredUserExpenses, 0, 10, 1, 1));
            String userId= String.valueOf(regularUserId);
            String result = controller.listExpenses(null, null, category, null, userId,0, 10, model, session);

            assertEquals("list", result);
            verify(filterService).getFilteredExpensesByUserId(null, null, category, regularUserId);
            verify(filterService, never()).getFilteredExpenses(any(), any(), any());
        }
    }

    private Expense createExpense(Long id, String description, BigDecimal amount, Long userId) {
        Expense expense = new Expense(description, amount, ExpenseCategory.FOOD, LocalDate.now(), userId);
        expense.setId(id);
        return expense;
    }
}