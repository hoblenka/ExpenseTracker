<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Expense Tracker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h1 class="mb-4">
                    <i class="bi bi-wallet2 text-primary"></i>
                    Expense Tracker
                </h1>
            </div>
        </div>
        
        <!-- Filter Form -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="bi bi-funnel"></i>
                    Filter Expenses
                </h5>
            </div>
            <div class="card-body">
                <form method="get" action="/expenses">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label for="category" class="form-label">Category</label>
                            <input type="text" class="form-control" id="category" name="category" 
                                   value="${param.category}" placeholder="Enter category...">
                        </div>
                        <div class="col-md-3">
                            <label for="startDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="startDate" name="startDate" 
                                   value="${param.startDate}">
                        </div>
                        <div class="col-md-3">
                            <label for="endDate" class="form-label">End Date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" 
                                   value="${param.endDate}">
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <div class="btn-group w-100" role="group">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-search"></i> Filter
                                </button>
                                <a href="/expenses" class="btn btn-outline-secondary">
                                    <i class="bi bi-x-circle"></i> Clear
                                </a>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Action Buttons -->
        <div class="row mb-3">
            <div class="col-12">
                <div class="btn-group" role="group">
                    <a href="/expenses/export?category=${param.category}&startDate=${param.startDate}&endDate=${param.endDate}" 
                       class="btn btn-success">
                        <i class="bi bi-download"></i> Export to CSV
                    </a>
                </div>
            </div>
        </div>
        
        <!-- Expenses Table -->
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="bi bi-table"></i>
                    Expenses List
                </h5>
                <span class="badge bg-primary">${expenses.size()} expenses</span>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${not empty expenses}">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover mb-0">
                                <thead class="table-dark">
                                    <tr>
                                        <th scope="col">
                                            <i class="bi bi-hash"></i> ID
                                        </th>
                                        <th scope="col">
                                            <i class="bi bi-card-text"></i> Description
                                        </th>
                                        <th scope="col">
                                            <i class="bi bi-currency-dollar"></i> Amount
                                        </th>
                                        <th scope="col">
                                            <i class="bi bi-tags"></i> Category
                                        </th>
                                        <th scope="col">
                                            <i class="bi bi-calendar"></i> Date
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="expense" items="${expenses}">
                                        <tr>
                                            <td class="fw-bold text-muted">${expense.id}</td>
                                            <td>${expense.description}</td>
                                            <td class="fw-bold text-success">$${expense.amount}</td>
                                            <td>
                                                <span class="badge bg-secondary">${expense.category.displayName}</span>
                                            </td>
                                            <td class="text-muted">${expense.date}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5">
                            <i class="bi bi-inbox display-1 text-muted"></i>
                            <h4 class="text-muted mt-3">No expenses found</h4>
                            <p class="text-muted">Try adjusting your filter criteria or add some expenses.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>