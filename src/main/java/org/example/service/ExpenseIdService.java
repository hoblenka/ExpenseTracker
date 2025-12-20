package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class ExpenseIdService {
    
    private final ExpenseDAO expenseDAO;

    public ExpenseIdService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public synchronized Long getNextAvailableId() {
        List<Long> existingIds = expenseDAO.findAll().stream()
            .map(Expense::getId)
            .sorted()
            .toList();

        if (existingIds.isEmpty()) {
            return 1L;
        }

        // Find first gap in IDs
        for (long i = 1; i <= existingIds.size(); i++) {
            if (!existingIds.contains(i)) {
                return i;
            }
        }

        // No gaps found, return next sequential ID
        return existingIds.get(existingIds.size() - 1) + 1;
    }

    public synchronized Long getNextAvailableIdForUser(Long userId) {
        List<Long> existingIds = expenseDAO.findAllByUserId(userId).stream()
            .map(Expense::getId)
            .sorted()
            .toList();

        if (existingIds.isEmpty()) {
            return 1L;
        }

        // Find first gap in IDs
        for (long i = 1; i <= existingIds.size(); i++) {
            if (!existingIds.contains(i)) {
                return i;
            }
        }

        // No gaps found, return next sequential ID
        return existingIds.get(existingIds.size() - 1) + 1;
    }
}