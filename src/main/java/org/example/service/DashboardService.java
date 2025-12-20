package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    
    private final ExpenseDAO expenseDAO;

    public DashboardService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public Map<String, BigDecimal> getSpendingByCategory() {
        List<Expense> expenses = expenseDAO.findAll();
        return expenses.stream()
                .collect(Collectors.groupingBy(
                    expense -> expense.getCategory().getDisplayName(),
                    LinkedHashMap::new,
                    Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> getSpendingByMonth() {
        List<Expense> expenses = expenseDAO.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        return expenses.stream()
                .collect(Collectors.groupingBy(
                    expense -> expense.getDate().format(formatter),
                    LinkedHashMap::new,
                    Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }

    public BigDecimal getTotalSpending() {
        return expenseDAO.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTotalExpenseCount() {
        return expenseDAO.findAll().size();
    }
}