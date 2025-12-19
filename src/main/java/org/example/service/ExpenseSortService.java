package org.example.service;

import org.example.model.Expense;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseSortService {

    public List<Expense> sortExpenses(List<Expense> expenses, String sortBy) {
        return expenses.stream().sorted((e1, e2) -> switch (sortBy.toLowerCase()) {
            case "id" -> e1.getId().compareTo(e2.getId());
            case "description" -> e1.getDescription().compareToIgnoreCase(e2.getDescription());
            case "amount" -> e1.getAmount().compareTo(e2.getAmount());
            case "category" -> e1.getCategoryDisplayName().compareToIgnoreCase(e2.getCategoryDisplayName());
            case "date" -> e1.getDate().compareTo(e2.getDate());
            default -> 0;
        }).collect(Collectors.toList());
    }
}