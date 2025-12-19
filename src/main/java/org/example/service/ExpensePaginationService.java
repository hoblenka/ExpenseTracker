package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpensePaginationService {
    
    private final ExpenseDAO expenseDAO;
    private static final int DEFAULT_PAGE_SIZE = 10;
    
    public ExpensePaginationService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }
    
    public PageResult<Expense> getExpensesPage(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        
        long totalElements = expenseDAO.countAll();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        if (page >= totalPages && totalPages > 0) {
            page = totalPages - 1;
        }
        
        List<Expense> expenses = expenseDAO.findPage(page, size);
        
        return new PageResult<>(expenses, page, size, totalElements, totalPages);
    }

    public record PageResult<T>(List<T> content, int currentPage, int pageSize, long totalElements, int totalPages) {
        public boolean hasNext() {
            return currentPage < totalPages - 1;
        }

        public boolean hasPrevious() {
            return currentPage > 0;
        }
        }
}