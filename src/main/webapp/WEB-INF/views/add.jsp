<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Expense</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h2 class="mb-0">
                            <i class="bi bi-plus-circle"></i> Add New Expense
                        </h2>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
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
                        
                        <form method="post" action="${pageContext.request.contextPath}/expenses/add" novalidate>
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
                                       value="${description != null ? description : ''}" 
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
                                           value="${amount != null ? amount : ''}" 
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
                                    <option value="">Select a category</option>
                                    <option value="Food" ${category == 'Food' ? 'selected' : ''}>
                                        Food
                                    </option>
                                    <option value="Transport" ${category == 'Transport' ? 'selected' : ''}>
                                        Transport
                                    </option>
                                    <option value="Utilities" ${category == 'Utilities' ? 'selected' : ''}>
                                        Utilities
                                    </option>
                                    <option value="Entertainment" ${category == 'Entertainment' ? 'selected' : ''}>
                                        Entertainment
                                    </option>
                                    <option value="Shopping" ${category == 'Shopping' ? 'selected' : ''}>
                                        Shopping
                                    </option>
                                    <option value="Rent" ${category == 'Rent' ? 'selected' : ''}>
                                        Rent
                                    </option>
                                    <option value="Other" ${category == 'Other' ? 'selected' : ''}>
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
                                       value="${date != null ? date : ''}" required>
                                <div class="invalid-feedback">
                                    Please provide a valid date.
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/expenses?startDate=${param.startDate}&endDate=${param.endDate}&category=${param.category}&sortBy=${param.sortBy}&page=${param.page}&size=${param.size}" class="btn btn-secondary me-md-2">
                                    <i class="bi bi-x-circle"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check-circle"></i> Add Expense
                                </button>
                            </div>
                        </form>
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