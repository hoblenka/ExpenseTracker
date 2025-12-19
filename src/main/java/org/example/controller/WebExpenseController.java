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
        
        List<Expense> expenses;
        if (start != null || end != null || (category != null && !category.isEmpty())) {
            expenses = filterService.getFilteredExpenses(start, end, category);
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                expenses = sortService.sortExpenses(expenses, sortBy.trim());
            }
            
            BigDecimal totalAmount = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            model.addAttribute("expenses", expenses);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("isPaginated", false);
        } else {
            ExpensePaginationService.PageResult<Expense> pageResult = paginationService.getExpensesPage(page, size);
            expenses = pageResult.content();
            
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                expenses = sortService.sortExpenses(expenses, sortBy.trim());
            }
            
            BigDecimal totalAmount = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            model.addAttribute("expenses", expenses);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("pageResult", pageResult);
            model.addAttribute("isPaginated", true);
        }
        
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
                           Model model) {
        try {
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(ExpenseCategory.fromString(category));
            expense.setDate(LocalDate.parse(date));
            
            crudService.saveExpense(expense);
            return "redirect:/expenses";
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
    public String showEditForm(@RequestParam Long id, Model model) {
        Expense expense = crudService.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "edit";
    }

    @PostMapping("/expenses/edit")
    public String updateExpense(@RequestParam Long id,
                              @RequestParam String description,
                              @RequestParam BigDecimal amount,
                              @RequestParam String category,
                              @RequestParam String date,
                              Model model) {
        try {
            Expense expense = new Expense();
            expense.setId(id);
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(ExpenseCategory.fromString(category));
            expense.setDate(LocalDate.parse(date));
            
            crudService.updateExpense(expense);
            return "redirect:/expenses";
        } catch (IllegalArgumentException e) {
            Expense expense = crudService.getExpenseById(id);
            model.addAttribute("expense", expense);
            model.addAttribute("error", e.getMessage());
            return "edit";
        }
    }

    @GetMapping("/expenses/delete")
    public String deleteExpense(@RequestParam Long id) {
        crudService.deleteExpense(id);
        return "redirect:/expenses";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/deleteAll")
    public String deleteAllExpenses() {
        crudService.deleteAllExpenses();
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/addRandom")
    public String addRandomExpense() {
        crudService.addRandomExpense();
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/addRandom30")
    public String add30RandomExpenses() {
        crudService.addMultipleRandomExpenses(30);
        return "redirect:/expenses";
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