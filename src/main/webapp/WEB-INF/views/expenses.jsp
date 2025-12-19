<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Expense Tracker</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .form-group { margin: 10px 0; }
        .btn { padding: 8px 16px; margin: 5px; text-decoration: none; background: #007bff; color: white; border: none; cursor: pointer; }
        .btn:hover { background: #0056b3; }
        .export-btn { background: #28a745; }
        .export-btn:hover { background: #1e7e34; }
    </style>
</head>
<body>
    <h1>Expense Tracker</h1>
    
    <div class="form-group">
        <form method="get" action="/expenses">
            <label>Category: <input type="text" name="category" value="${param.category}"></label>
            <label>Start Date: <input type="date" name="startDate" value="${param.startDate}"></label>
            <label>End Date: <input type="date" name="endDate" value="${param.endDate}"></label>
            <button type="submit" class="btn">Filter</button>
            <a href="/expenses" class="btn">Clear</a>
        </form>
    </div>
    
    <div class="form-group">
        <a href="/expenses/export?category=${param.category}&startDate=${param.startDate}&endDate=${param.endDate}" 
           class="btn export-btn">Export to CSV</a>
    </div>
    
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Description</th>
                <th>Amount</th>
                <th>Category</th>
                <th>Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="expense" items="${expenses}">
                <tr>
                    <td>${expense.id}</td>
                    <td>${expense.description}</td>
                    <td>$${expense.amount}</td>
                    <td>${expense.category.displayName}</td>
                    <td>${expense.date}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <c:if test="${empty expenses}">
        <p>No expenses found.</p>
    </c:if>
</body>
</html>