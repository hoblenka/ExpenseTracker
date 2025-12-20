<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Expense</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow">
                    <div class="card-header bg-warning text-dark d-flex justify-content-between align-items-center">
                        <h2 class="mb-0">
                            <i class="bi bi-pencil-square"></i> Edit Expense
                        </h2>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-dark btn-sm">
                            <i class="bi bi-box-arrow-right"></i> Logout
                        </a>
                    </div>
                    <div class="card-body">
                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i>
                                <%= request.getAttribute("error") %>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        <% } %>
                        
                        <form method="post" action="${pageContext.request.contextPath}/expenses/edit" novalidate>
                            <input type="hidden" name="id" value="${expense.id}">
                            <input type="hidden" name="startDate" value="${param.startDate}">
                            <input type="hidden" name="endDate" value="${param.endDate}">
                            <input type="hidden" name="categoryFilter" value="${param.category}">
                            <input type="hidden" name="sortBy" value="${param.sortBy}">
                            <input type="hidden" name="page" value="${param.page}">
                            <input type="hidden" name="size" value="${param.size}">
                            
                            <div class="mb-3">
                                <label for="description" class="form-label">
                                    <i class="bi bi-card-text"></i> Description <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="description" name="description" 
                                       value="${expense.description}" 
                                       pattern=".*\S.*" title="Description cannot be empty or contain only spaces" 
                                       placeholder="Enter expense description..." required>
                                <div class="invalid-feedback">
                                    Please provide a valid description.
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="amount" class="form-label">
                                    <i class="bi bi-currency-dollar"></i> Amount <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" step="0.01" min="0.01" max="99999999.99" 
                                           class="form-control" id="amount" name="amount" 
                                           value="${expense.amount}" 
                                           placeholder="0.00" required>
                                </div>
                                <div class="invalid-feedback">
                                    Please provide a valid amount (minimum $0.01).
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="category" class="form-label">
                                    <i class="bi bi-tags"></i> Category <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="category" name="category" required>
                                    <option value="Food" ${expense.categoryDisplayName == 'Food' ? 'selected' : ''}>
                                        Food
                                    </option>
                                    <option value="Transport" ${expense.categoryDisplayName == 'Transport' ? 'selected' : ''}>
                                        Transport
                                    </option>
                                    <option value="Utilities" ${expense.categoryDisplayName == 'Utilities' ? 'selected' : ''}>
                                        Utilities
                                    </option>
                                    <option value="Entertainment" ${expense.categoryDisplayName == 'Entertainment' ? 'selected' : ''}>
                                        Entertainment
                                    </option>
                                    <option value="Shopping" ${expense.categoryDisplayName == 'Shopping' ? 'selected' : ''}>
                                        Shopping
                                    </option>
                                    <option value="Rent" ${expense.categoryDisplayName == 'Rent' ? 'selected' : ''}>
                                        Rent
                                    </option>
                                    <option value="Other" ${expense.categoryDisplayName == 'Other' ? 'selected' : ''}>
                                        Other
                                    </option>
                                </select>
                                <div class="invalid-feedback">
                                    Please select a category.
                                </div>
                            </div>
                            
                            <div class="mb-4">
                                <label for="date" class="form-label">
                                    <i class="bi bi-calendar"></i> Date <span class="text-danger">*</span>
                                </label>
                                <input type="date" class="form-control" id="date" name="date" 
                                       value="${expense.date}" required>
                                <div class="invalid-feedback">
                                    Please provide a valid date.
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/expenses?startDate=${param.startDate}&endDate=${param.endDate}&category=${param.category}&sortBy=${param.sortBy}&page=${param.page}&size=${param.size}" class="btn btn-secondary me-md-2">
                                    <i class="bi bi-x-circle"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-warning">
                                    <i class="bi bi-check-circle"></i> Update Expense
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Expense Info Card -->
                <div class="card mt-3 shadow-sm">
                    <div class="card-header bg-light">
                        <h6 class="mb-0">
                            <i class="bi bi-info-circle"></i> Expense Information
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-sm-6">
                                <strong>ID:</strong> ${expense.id}
                            </div>
                            <div class="col-sm-6">
                                <strong>Current Amount:</strong> 
                                <span class="text-success fw-bold">$${expense.amount}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Bootstrap form validation
        (function() {
            'use strict';
            window.addEventListener('load', function() {
                var forms = document.getElementsByClassName('needs-validation');
                var validation = Array.prototype.filter.call(forms, function(form) {
                    form.addEventListener('submit', function(event) {
                        if (form.checkValidity() === false) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
            }, false);
        })();
    </script>
</body>
</html>