<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Expense List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>Expense Tracker</h1>
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/expenses/add" class="btn btn-primary">Add New Expense</a>
            <a href="${pageContext.request.contextPath}/expenses/addRandom" class="btn btn-success ms-2">Add Random Expense</a>
            <a href="${pageContext.request.contextPath}/expenses/deleteAll" class="btn btn-danger ms-2" 
               onclick="return confirm('Are you sure you want to delete ALL expenses? This cannot be undone!')">Delete All Expenses</a>
        </div>
        
        <form method="get" class="mb-3">
            <div class="row">
                <div class="col-md-3">
                    <label for="startDate" class="form-label">Start Date:</label>
                    <input type="date" id="startDate" name="startDate" class="form-control" value="${startDate}">
                </div>
                <div class="col-md-3">
                    <label for="endDate" class="form-label">End Date:</label>
                    <input type="date" id="endDate" name="endDate" class="form-control" value="${endDate}">
                </div>
                <div class="col-md-3">
                    <label for="category" class="form-label">Category:</label>
                    <select id="category" name="category" class="form-control">
                        <option value="">All Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.displayName}" ${category == cat.displayName ? 'selected' : ''}>
                                ${cat.displayName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-info me-2">Filter</button>
                    <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">Clear</a>
                </div>
            </div>
        </form>
        
        <table class="table table-striped">
            <thead>
                <tr>
                    <th><a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=id" class="text-decoration-none text-dark">ID ↕</a></th>
                    <th><a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=description" class="text-decoration-none text-dark">Description ↕</a></th>
                    <th><a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=amount" class="text-decoration-none text-dark">Amount ↕</a></th>
                    <th><a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=category" class="text-decoration-none text-dark">Category ↕</a></th>
                    <th><a href="?startDate=${startDate}&endDate=${endDate}&category=${category}&sortBy=date" class="text-decoration-none text-dark">Date ↕</a></th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="expense" items="${expenses}">
                    <tr>
                        <td>${expense.id}</td>
                        <td>${expense.description}</td>
                        <td>$${expense.amount}</td>
                        <td>${expense.categoryDisplayName}</td>
                        <td>${expense.date}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/expenses/edit?id=${expense.id}" class="btn btn-sm btn-warning">Edit</a>
                            <a href="${pageContext.request.contextPath}/expenses/delete?id=${expense.id}" class="btn btn-sm btn-danger" 
                               onclick="return confirm('Are you sure?')">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <div class="mt-3">
            <h4>Total Amount: <span class="text-primary">$${totalAmount}</span></h4>
        </div>
    </div>
</body>
</html>