package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.dao.UserDAO;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpenseSortService;
import org.example.service.ExpensePaginationService;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.example.util.SessionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class WebExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebExpenseController.class);
    private final ExpenseCrudService crudService;
    private final ExpenseFilterService filterService;
    private final ExpenseSortService sortService;
    private final ExpensePaginationService paginationService;
    private final ExpenseController expenseController;
    private final UserDAO userDAO;

    public WebExpenseController(ExpenseCrudService crudService, ExpenseFilterService filterService,
                                ExpenseSortService sortService, ExpensePaginationService paginationService,
                                ExpenseController expenseController, UserDAO userDAO) {
        this.crudService = crudService;
        this.filterService = filterService;
        this.sortService = sortService;
        this.paginationService = paginationService;
        this.expenseController = expenseController;
        this.userDAO = userDAO;
    }

    @GetMapping("/expenses")
    public String listExpenses(@RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false) String filterUserId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model,
                             HttpSession session) {
        logger.info("Accessing /expenses endpoint");
        
        Long userId = SessionHelper.getUserId(session);
        logger.info("Session userId: {}", userId);
        
        if (userId == null) {
            logger.warn("No userId in session, redirecting to login");
            return "redirect:/login";
        }
        
        try {
            var allExpenses = SessionHelper.isAdmin(session) ? 
                getAllExpenses(startDate, endDate, category, sortBy, filterUserId) : 
                getAllExpensesByUserId(startDate, endDate, category, sortBy, userId);
            logger.info("Found {} expenses for user {} (admin: {})", allExpenses.size(), userId, SessionHelper.isAdmin(session));

            // Apply pagination to filtered results
            ExpensePaginationService.PageResult<Expense> pageResult = paginationService.getPageFromList(allExpenses, page, size);
            
            BigDecimal totalAmount = allExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            model.addAttribute("expenses", pageResult.content());
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("pageResult", pageResult);
            model.addAttribute("isPaginated", true);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("category", category);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("filterUserId", filterUserId);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("categories", ExpenseCategory.values());
            model.addAttribute("isAdmin", SessionHelper.isAdmin(session));
            model.addAttribute("username", session.getAttribute("username"));
            
            if (SessionHelper.isAdmin(session)) {
                model.addAttribute("users", userDAO.findAll());
            }
            
            logger.info("Returning list view with {} expenses on page", pageResult.content().size());
            return "list";
        } catch (Exception e) {
            logger.error("Error loading expenses for user {}: {}", userId, e.getMessage(), e);
            model.addAttribute("error", "Error loading expenses: " + e.getMessage());
            return "list";
        }
    }

    private List<Expense> getAllExpensesByUserId(String startDate, String endDate, String category, String sortBy, Long userId) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        List<Expense> allExpenses;
        if (start != null || end != null || (category != null && !category.isEmpty())) {
            allExpenses = filterService.getFilteredExpensesByUserId(start, end, category, userId);
        } else {
            allExpenses = crudService.getAllExpensesByUserId(userId);
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            allExpenses = sortService.sortExpenses(allExpenses, sortBy.trim());
        }
        return allExpenses;
    }
    
    private List<Expense> getAllExpenses(String startDate, String endDate, String category, String sortBy, String filterUserId) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        List<Expense> allExpenses;
        if (filterUserId != null && !filterUserId.isEmpty()) {
            Long userId = Long.parseLong(filterUserId);
            if (start != null || end != null || (category != null && !category.isEmpty())) {
                allExpenses = filterService.getFilteredExpensesByUserId(start, end, category, userId);
            } else {
                allExpenses = crudService.getAllExpensesByUserId(userId);
            }
        } else {
            if (start != null || end != null || (category != null && !category.isEmpty())) {
                allExpenses = filterService.getFilteredExpenses(start, end, category);
            } else {
                allExpenses = crudService.getAllExpenses();
            }
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            allExpenses = sortService.sortExpenses(allExpenses, sortBy.trim());
        }
        return allExpenses;
    }

    @GetMapping("/expenses/add")
    public String showAddForm() {
        return "add";
    }

    @PostMapping("/expenses/add")
    public String addExpense(@RequestParam String description,
                           @RequestParam BigDecimal amount,
                           @RequestParam String category,
                           @RequestParam String date,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(required = false) String categoryFilter,
                           @RequestParam(required = false) String sortBy,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model,
                           HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(ExpenseCategory.fromString(category));
            expense.setDate(LocalDate.parse(date));
            expense.setUserId(userId);

            Expense savedExpense = crudService.saveExpense(expense);

            int targetPage = findExpensePage(savedExpense, startDate, endDate, categoryFilter, sortBy, size, userId);

            return "redirect:/expenses?" + buildQueryString(startDate, endDate, categoryFilter, sortBy, targetPage, size);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("description", description);
            model.addAttribute("amount", amount);
            model.addAttribute("category", category);
            model.addAttribute("date", date);
            return "add";
        }
    }

    @GetMapping("/expenses/edit")
    public String showEditForm(@RequestParam Long id,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) String sortBy,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model, HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        Expense expense = crudService.getExpenseByIdAndUserId(id, userId);
        model.addAttribute("expense", expense);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("category", category);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "edit";
    }

    @PostMapping("/expenses/edit")
    public String updateExpense(@RequestParam Long id,
                              @RequestParam String description,
                              @RequestParam BigDecimal amount,
                              @RequestParam String category,
                              @RequestParam String date,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              @RequestParam(required = false) String categoryFilter,
                              @RequestParam(required = false) String sortBy,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model, HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            Expense expense = new Expense();
            expense.setId(id);
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(ExpenseCategory.fromString(category));
            expense.setDate(LocalDate.parse(date));
            expense.setUserId(userId);

            crudService.updateExpense(expense);
            return "redirect:/expenses?" + buildQueryString(startDate, endDate, categoryFilter, sortBy, page, size);
        } catch (IllegalArgumentException e) {
            Expense expenseObj = crudService.getExpenseByIdAndUserId(id, userId);
            model.addAttribute("expense", expenseObj);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("category", categoryFilter);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            return "edit";
        }
    }

    @GetMapping("/expenses/delete")
    public String deleteExpense(@RequestParam Long id,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        crudService.deleteExpenseByIdAndUserId(id, userId);
        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, page, size);
    }



    @GetMapping("/test")
    public String test(Model model, HttpSession session) {
        logger.info("Test endpoint accessed");
        logger.info("Session ID: {}", session.getId());
        logger.info("Session userId: {}", session.getAttribute("userId"));
        
        model.addAttribute("message", "Test page works!");
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("userId", session.getAttribute("userId"));
        return "test";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/deleteAll")
    public String deleteAllExpenses(@RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        crudService.deleteAllExpensesByUserId(userId);
        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, 0, size);
    }



    @GetMapping("/expenses/addRandom")
    public String addRandomExpense(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        Expense savedExpense = crudService.addRandomExpenseForUser(userId);
        int targetPage = findExpensePage(savedExpense, startDate, endDate, category, sortBy, size, userId);
        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, targetPage, size);
    }



    @GetMapping("/expenses/addRandom30")
    public String add30RandomExpenses(@RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate,
                                     @RequestParam(required = false) String category,
                                     @RequestParam(required = false) String sortBy,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        for (int i = 0; i < 30; i++) {
            crudService.addRandomExpenseForUser(userId);
        }
        
        // Go to the last page that has the maximum number of items (size)
        var allExpenses = getAllExpensesByUserId(startDate, endDate, category, sortBy, userId);
        int totalPages = (int) Math.ceil((double) allExpenses.size() / size);
        int targetPage = Math.max(0, totalPages - 2); // Go to second-to-last page to show full page
        
        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, targetPage, size);
    }

    private int findExpensePage(Expense expense, String startDate, String endDate, String categoryFilter, String sortBy, int size, Long userId) {
        var allExpenses = getAllExpensesByUserId(startDate, endDate, categoryFilter, sortBy, userId);

        // Find the index of the expense
        int expenseIndex = -1;
        for (int i = 0; i < allExpenses.size(); i++) {
            if (allExpenses.get(i).getId().equals(expense.getId())) {
                expenseIndex = i;
                break;
            }
        }

        // Calculate the page number (0-based)
        return expenseIndex >= 0 ? expenseIndex / size : 0;
    }

    private String buildQueryString(String startDate, String endDate, String category, String sortBy, int page, int size) {
        StringBuilder query = new StringBuilder();
        if (startDate != null && !startDate.isEmpty()) {
            query.append("startDate=").append(startDate).append("&");
        }
        if (endDate != null && !endDate.isEmpty()) {
            query.append("endDate=").append(endDate).append("&");
        }
        if (category != null && !category.isEmpty()) {
            query.append("category=").append(category).append("&");
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            query.append("sortBy=").append(sortBy).append("&");
        }
        query.append("page=").append(page).append("&size=").append(size);
        return query.toString();
    }

    @GetMapping("/expenses/filter")
    public String filterExpensesByDateRange(@RequestParam String startDate,
                                          @RequestParam String endDate,
                                          Model model, HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Expense> expenses = filterService.getFilteredExpensesByUserId(
            LocalDate.parse(startDate), LocalDate.parse(endDate), null, userId);
        
        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "list";
    }

    @GetMapping("/expenses/api/all")
    @ResponseBody
    public List<Expense> getAllExpensesApi(HttpSession session) {
        return expenseController.getAllExpenses(null, null, session).getBody();
    }

    @GetMapping("/expenses/api/{id}")
    @ResponseBody
    public Expense getExpenseByIdApi(@PathVariable Long id, HttpSession session) {
        return expenseController.getExpenseById(id, session).getBody();
    }

    @PostMapping("/expenses/api/delete/{id}")
    @ResponseBody
    public String deleteExpenseApi(@PathVariable Long id, HttpSession session) {
        expenseController.deleteExpense(id, session);
        return "deleted";
    }

    @GetMapping("/expenses/api/total")
    @ResponseBody
    public BigDecimal getTotalAmountApi(HttpSession session) {
        return expenseController.getTotalAmount(session).getBody();
    }

    @GetMapping("/expenses/api/category/{category}")
    @ResponseBody
    public List<Expense> getExpensesByCategoryApi(@PathVariable String category, HttpSession session) {
        return expenseController.getExpensesByCategory(category, session).getBody();
    }

    @GetMapping("/expenses/api/filter")
    @ResponseBody
    public List<Expense> getExpensesByDateRangeApi(@RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate,
                                                   HttpSession session) {
        return expenseController.getExpensesByDateRange(startDate, endDate, session).getBody();
    }


}