<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Expense List</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <div class="row mb-4">
            <div class="col-12">
                <h1 class="display-5">
                    <i class="bi bi-wallet2 text-primary"></i>
                    Expense Tracker
                </h1>
            </div>
        </div>
        
        <!-- Action Buttons -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex flex-wrap gap-2">
                    <a href="${pageContext.request.contextPath}/expenses/add" class="btn btn-primary">
                        <i class="bi bi-plus-circle"></i> Add New Expense
                    </a>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-info">
                        <i class="bi bi-graph-up"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/expenses/addRandom?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=${sortBy}&page=${page}&size=${size}" 
                       class="btn btn-success">
                        <i class="bi bi-shuffle"></i> Add Random
                    </a>
                    <a href="${pageContext.request.contextPath}/expenses/addRandom30?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=${sortBy}&page=${page}&size=${size}" 
                       class="btn btn-success">
                        <i class="bi bi-collection"></i> Add 30
                    </a>
                    <a href="${pageContext.request.contextPath}/expenses/export?category=${category}&startDate=${startDate}&endDate=${endDate}" 
                       class="btn btn-warning">
                        <i class="bi bi-download"></i> Export CSV
                    </a>
                    <a href="${pageContext.request.contextPath}/expenses/deleteAll?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=${sortBy}&page=${page}&size=${size}" 
                       class="btn btn-danger" 
                       onclick="return confirm('Are you sure you want to delete ALL expenses? This cannot be undone!')">
                        <i class="bi bi-trash"></i> Delete All
                    </a>
                </div>
            </div>
        </div>
        
        <!-- Filter Form -->
        <div class="card mb-4 shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="bi bi-funnel"></i> Filter Expenses
                </h5>
            </div>
            <div class="card-body">
                <form method="get">
                    <div class="row g-3">
                        <div class="col-md-3">
                            <label for="startDate" class="form-label">
                                <i class="bi bi-calendar-event"></i> Start Date
                            </label>
                            <input type="date" id="startDate" name="startDate" class="form-control" value="${startDate}">
                        </div>
                        <div class="col-md-3">
                            <label for="endDate" class="form-label">
                                <i class="bi bi-calendar-check"></i> End Date
                            </label>
                            <input type="date" id="endDate" name="endDate" class="form-control" value="${endDate}">
                        </div>
                        <div class="col-md-4">
                            <label for="category" class="form-label">
                                <i class="bi bi-tags"></i> Category
                            </label>
                            <select id="category" name="category" class="form-select">
                                <option value="">All Categories</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.displayName}" ${category == cat.displayName ? 'selected' : ''}>
                                        ${cat.displayName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <div class="btn-group w-100" role="group">
                                <button type="submit" class="btn btn-info">
                                    <i class="bi bi-search"></i> Filter
                                </button>
                                <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">
                                    <i class="bi bi-x-circle"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Expenses Table -->
        <div class="card shadow-sm">
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="bi bi-table"></i> Expenses
                </h5>
                <span class="badge bg-primary rounded-pill">${expenses.size()} items</span>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover table-striped mb-0">
                        <thead class="table-dark">
                            <tr>
                                <th>
                                    <a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=id" 
                                       class="text-decoration-none text-white">
                                        <i class="bi bi-hash"></i> ID <i class="bi bi-arrow-down-up"></i>
                                    </a>
                                </th>
                                <th>
                                    <a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=description" 
                                       class="text-decoration-none text-white">
                                        <i class="bi bi-card-text"></i> Description <i class="bi bi-arrow-down-up"></i>
                                    </a>
                                </th>
                                <th>
                                    <a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=amount" 
                                       class="text-decoration-none text-white">
                                        <i class="bi bi-currency-dollar"></i> Amount <i class="bi bi-arrow-down-up"></i>
                                    </a>
                                </th>
                                <th>
                                    <a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=category" 
                                       class="text-decoration-none text-white">
                                        <i class="bi bi-tags"></i> Category <i class="bi bi-arrow-down-up"></i>
                                    </a>
                                </th>
                                <th>
                                    <a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=date" 
                                       class="text-decoration-none text-white">
                                        <i class="bi bi-calendar"></i> Date <i class="bi bi-arrow-down-up"></i>
                                    </a>
                                </th>
                                <th class="text-center">
                                    <i class="bi bi-gear"></i> Actions
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
                                        <span class="badge bg-info text-dark">${expense.categoryDisplayName}</span>
                                    </td>
                                    <td class="text-muted">
                                        <i class="bi bi-calendar3"></i> ${expense.date}
                                    </td>
                                    <td class="text-center">
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="${pageContext.request.contextPath}/expenses/edit?id=${expense.id}&startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=${sortBy}&page=${page}&size=${size}" 
                                               class="btn btn-outline-warning" title="Edit">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/expenses/delete?id=${expense.id}&startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=${sortBy}&page=${page}&size=${size}" 
                                               class="btn btn-outline-danger" 
                                               onclick="return confirm('Are you sure?')" title="Delete">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        
        <!-- Pagination -->
        <c:if test="${isPaginated}">
            <nav aria-label="Page navigation" class="mt-4">
                <ul class="pagination justify-content-center">
                    <c:if test="${pageResult.hasPrevious()}">
                        <li class="page-item">
                            <a class="page-link" href="?page=${pageResult.currentPage() - 1}&size=${size}&sortBy=${sortBy}&startDate=${startDate}&endDate=${endDate}&category=${category}">
                                <i class="bi bi-chevron-left"></i> Previous
                            </a>
                        </li>
                    </c:if>
                    
                    <c:if test="${pageResult.totalPages() > 0}">
                        <c:forEach begin="0" end="${pageResult.totalPages() - 1}" var="i">
                            <li class="page-item ${i == pageResult.currentPage() ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}&size=${size}&sortBy=${sortBy}&startDate=${startDate}&endDate=${endDate}&category=${category}">${i + 1}</a>
                            </li>
                        </c:forEach>
                    </c:if>
                    
                    <c:if test="${pageResult.hasNext()}">
                        <li class="page-item">
                            <a class="page-link" href="?page=${pageResult.currentPage() + 1}&size=${size}&sortBy=${sortBy}&startDate=${startDate}&endDate=${endDate}&category=${category}">
                                Next <i class="bi bi-chevron-right"></i>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </nav>
            
            <div class="text-center mb-3">
                <small class="text-muted">
                    <c:choose>
                        <c:when test="${pageResult.totalPages() > 0}">
                            <i class="bi bi-info-circle"></i>
                            Showing page ${pageResult.currentPage() + 1} of ${pageResult.totalPages()} 
                            (${pageResult.totalElements()} total expenses)
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-inbox"></i> No expenses found
                        </c:otherwise>
                    </c:choose>
                </small>
            </div>
        </c:if>
        
        <!-- Total Amount -->
        <div class="card mt-4 shadow-sm">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <h4 class="mb-0">
                            <i class="bi bi-calculator"></i> Total Amount:
                        </h4>
                    </div>
                    <div class="col-md-6 text-md-end">
                        <h3 class="mb-0 text-primary fw-bold">
                            <i class="bi bi-currency-dollar"></i>${totalAmount}
                        </h3>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>