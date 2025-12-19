<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Expense</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>Add New Expense</h1>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <form method="post" action="${pageContext.request.contextPath}/expenses/add">
            <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <input type="text" class="form-control" id="description" name="description" 
                       value="${description != null ? description : ''}" 
                       pattern=".*\S.*" title="Description cannot be empty or contain only spaces" required>
            </div>
            
            <div class="mb-3">
                <label for="amount" class="form-label">Amount</label>
                <input type="number" step="0.01" min="0.01" max="99999999.99" class="form-control" id="amount" name="amount" 
                       value="${amount != null ? amount : ''}" required>
            </div>
            
            <div class="mb-3">
                <label for="category" class="form-label">Category</label>
                <select class="form-control" id="category" name="category" required>
                    <option value="">Select a category</option>
                    <option value="Food" ${category == 'Food' ? 'selected' : ''}>Food</option>
                    <option value="Transport" ${category == 'Transport' ? 'selected' : ''}>Transport</option>
                    <option value="Utilities" ${category == 'Utilities' ? 'selected' : ''}>Utilities</option>
                    <option value="Entertainment" ${category == 'Entertainment' ? 'selected' : ''}>Entertainment</option>
                    <option value="Shopping" ${category == 'Shopping' ? 'selected' : ''}>Shopping</option>
                    <option value="Rent" ${category == 'Rent' ? 'selected' : ''}>Rent</option>
                    <option value="Other" ${category == 'Other' ? 'selected' : ''}>Other</option>
                </select>
            </div>
            
            <div class="mb-3">
                <label for="date" class="form-label">Date</label>
                <input type="date" class="form-control" id="date" name="date" 
                       value="${date != null ? date : ''}" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Add Expense</button>
            <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
</body>
</html>