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
        
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Description</th>
                    <th>Amount</th>
                    <th>Category</th>
                    <th>Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="expense" items="${expenses}">
                    <tr>
                        <td>${expense.id}</td>
                        <td>${expense.description}</td>
                        <td>$${expense.amount}</td>
                        <td>${expense.category}</td>
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