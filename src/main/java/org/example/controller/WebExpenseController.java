package org.example.controller;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class WebExpenseController {
    
    private final ExpenseCrudService crudService;
    private final ExpenseFilterService filterService;
    private final ExpenseSortService sortService;
    private final ExpensePaginationService paginationService;
    private final ExpenseController expenseController;

    public WebExpenseController(ExpenseCrudService crudService, ExpenseFilterService filterService,
                                ExpenseSortService sortService, ExpensePaginationService paginationService,
                                ExpenseController expenseController) {
        this.crudService = crudService;
        this.filterService = filterService;
        this.sortService = sortService;
        this.paginationService = paginationService;
        this.expenseController = expenseController;
    }

    @GetMapping("/expenses")
    public String listExpenses(@RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;
        
        List<Expense> allExpenses;
        if (start != null || end != null || (category != null && !category.isEmpty())) {
            allExpenses = filterService.getFilteredExpenses(start, end, category);
        } else {
            allExpenses = crudService.getAllExpenses();
        }
        
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            allExpenses = sortService.sortExpenses(allExpenses, sortBy.trim());
        }
        
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
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("categories", ExpenseCategory.values());
        return "list";
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
                           Model model) {
        try {
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(ExpenseCategory.fromString(category));
            expense.setDate(LocalDate.parse(date));

            Expense savedExpense = crudService.saveExpense(expense);

            int targetPage = findExpensePage(savedExpense, startDate, endDate, categoryFilter, sortBy, size);

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
                              Model model) {
        Expense expense = crudService.getExpenseById(id);
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
                              Model model) {
        try {
            Expense expense = new Expense();
            expense.setId(id);
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(ExpenseCategory.fromString(category));
            expense.setDate(LocalDate.parse(date));

            crudService.updateExpense(expense);
            return "redirect:/expenses?" + buildQueryString(startDate, endDate, categoryFilter, sortBy, page, size);
        } catch (IllegalArgumentException e) {
            Expense expenseObj = crudService.getExpenseById(id);
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
                               @RequestParam(defaultValue = "10") int size) {
        crudService.deleteExpense(id);
        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, page, size);
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
                                   @RequestParam(defaultValue = "10") int size) {
        crudService.deleteAllExpenses();
        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, page, size);
    }

    @GetMapping("/expenses/addRandom")
    public String addRandomExpense(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Expense savedExpense = crudService.addRandomExpense();

        int targetPage = findExpensePage(savedExpense, startDate, endDate, category, sortBy, size);

        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, targetPage, size);
    }

    @GetMapping("/expenses/addRandom30")
    public String add30RandomExpenses(@RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate,
                                     @RequestParam(required = false) String category,
                                     @RequestParam(required = false) String sortBy,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Expense lastExpense = crudService.addMultipleRandomExpenses(30);

        int targetPage = findExpensePage(lastExpense, startDate, endDate, category, sortBy, size);

        return "redirect:/expenses?" + buildQueryString(startDate, endDate, category, sortBy, targetPage, size);
    }

    private int findExpensePage(Expense expense, String startDate, String endDate, String categoryFilter, String sortBy, int size) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        List<Expense> allExpenses;
        if (start != null || end != null || (categoryFilter != null && !categoryFilter.isEmpty())) {
            allExpenses = filterService.getFilteredExpenses(start, end, categoryFilter);
        } else {
            allExpenses = crudService.getAllExpenses();
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            allExpenses = sortService.sortExpenses(allExpenses, sortBy.trim());
        }

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
                                          Model model) {
        List<Expense> expenses = filterService.getFilteredExpenses(
            LocalDate.parse(startDate), LocalDate.parse(endDate), null);
        
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
    public List<Expense> getAllExpensesApi() {
        return expenseController.getAllExpenses().getBody();
    }

    @GetMapping("/expenses/api/{id}")
    @ResponseBody
    public Expense getExpenseByIdApi(@PathVariable Long id) {
        return expenseController.getExpenseById(id).getBody();
    }

    @PostMapping("/expenses/api/delete/{id}")
    @ResponseBody
    public String deleteExpenseApi(@PathVariable Long id) {
        expenseController.deleteExpense(id);
        return "deleted";
    }

    @GetMapping("/expenses/api/total")
    @ResponseBody
    public BigDecimal getTotalAmountApi() {
        return expenseController.getTotalAmount().getBody();
    }

    @GetMapping("/expenses/api/category/{category}")
    @ResponseBody
    public List<Expense> getExpensesByCategoryApi(@PathVariable String category) {
        return expenseController.getExpensesByCategory(category).getBody();
    }

    @GetMapping("/expenses/api/filter")
    @ResponseBody
    public List<Expense> getExpensesByDateRangeApi(@RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate) {
        return expenseController.getExpensesByDateRange(startDate, endDate).getBody();
    }


}