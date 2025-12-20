package org.example.controller;

import org.example.model.Expense;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpenseSortService;
import org.example.service.ExpensePaginationService;
import org.example.util.SessionHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    private final ExpenseCrudService crudService;
    private final ExpenseFilterService filterService;
    private final ExpenseSortService sortService;
    private final ExpensePaginationService paginationService;
    
    public ExpenseController(ExpenseCrudService crudService, ExpenseFilterService filterService, 
                           ExpenseSortService sortService, ExpensePaginationService paginationService) {
        this.crudService = crudService;
        this.filterService = filterService;
        this.sortService = sortService;
        this.paginationService = paginationService;
    }
    
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(@RequestParam(required = false) String category,
                                                       @RequestParam(required = false) String sortBy,
                                                       HttpSession session) {
        try {
            ResponseEntity<List<Expense>> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            Long userId = SessionHelper.getUserId(session);
            List<Expense> expenses;
            if (category != null && !category.trim().isEmpty()) {
                expenses = filterService.filterExpensesByCategoryAndUserId(category.trim(), userId);
            } else {
                expenses = crudService.getAllExpensesByUserId(userId);
            }
            
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                expenses = sortService.sortExpenses(expenses, sortBy.trim());
            }
            
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            logger.error("Failed to get expenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Void> createExpense(@Valid @RequestBody Expense expense, HttpSession session) {
        try {
            ResponseEntity<Void> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            if (!isValidExpense(expense)) {
                return ResponseEntity.badRequest().build();
            }
            
            Long userId = SessionHelper.getUserId(session);
            Expense cleanExpense = new Expense(
                HtmlUtils.htmlEscape(expense.getDescription()),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate(),
                userId
            );
            
            crudService.saveExpense(cleanExpense);
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            logger.error("Failed to create expense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id, HttpSession session) {
        try {
            ResponseEntity<Expense> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Long userId = SessionHelper.getUserId(session);
            Expense expense = crudService.getExpenseByIdAndUserId(id, userId);
            return expense != null ? ResponseEntity.ok(expense) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get expense by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, HttpSession session) {
        try {
            ResponseEntity<Void> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Long userId = SessionHelper.getUserId(session);
            crudService.deleteExpenseByIdAndUserId(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete expense by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category, HttpSession session) {
        try {
            ResponseEntity<List<Expense>> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            if (category == null || category.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Long userId = SessionHelper.getUserId(session);
            String trimmedCategory = category.trim();
            return ResponseEntity.ok(filterService.filterExpensesByCategoryAndUserId(trimmedCategory, userId));
        } catch (Exception e) {
            logger.error("Failed to get expenses by category '{}': {}", category, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllExpenses(HttpSession session) {
        try {
            ResponseEntity<Void> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            Long userId = SessionHelper.getUserId(session);
            crudService.deleteAllExpensesByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete all expenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/random")
    public ResponseEntity<Void> addRandomExpense(HttpSession session) {
        try {
            ResponseEntity<Void> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            Long userId = SessionHelper.getUserId(session);
            crudService.addRandomExpenseForUser(userId);
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            logger.error("Failed to add random expense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/random/{count}")
    public ResponseEntity<Void> addMultipleRandomExpenses(@PathVariable int count, HttpSession session) {
        try {
            ResponseEntity<Void> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            if (count <= 0 || count > 100) {
                return ResponseEntity.badRequest().build();
            }
            Long userId = SessionHelper.getUserId(session);
            for (int i = 0; i < count; i++) {
                crudService.addRandomExpenseForUser(userId);
            }
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            logger.error("Failed to add {} random expenses: {}", count, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmount(HttpSession session) {
        try {
            ResponseEntity<BigDecimal> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            Long userId = SessionHelper.getUserId(session);
            return ResponseEntity.ok(crudService.getTotalAmountByUserId(userId));
        } catch (Exception e) {
            logger.error("Failed to get total amount: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        try {
            ResponseEntity<List<Expense>> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            Long userId = SessionHelper.getUserId(session);
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            return ResponseEntity.ok(filterService.getFilteredExpensesByUserId(start, end, null, userId));
        } catch (Exception e) {
            logger.error("Failed to filter expenses by date range: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/page")
    public ResponseEntity<ExpensePaginationService.PageResult<Expense>> getExpensesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        try {
            ResponseEntity<ExpensePaginationService.PageResult<Expense>> validationResponse = SessionHelper.validateSession(session);
            if (validationResponse != null) return validationResponse;
            
            Long userId = SessionHelper.getUserId(session);
            return ResponseEntity.ok(paginationService.getExpensesPageByUserId(page, size, userId));
        } catch (Exception e) {
            logger.error("Failed to get expenses page: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isValidExpense(Expense expense) {
        return expense.getDescription() != null && expense.getCategory() != null && 
               expense.getAmount() != null && expense.getDate() != null;
    }
}