<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Expense Tracker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="container mt-4">
        <div class="row mb-4">
            <div class="col-12 d-flex justify-content-between align-items-center">
                <div>
                    <h1 class="display-5">
                        <i class="bi bi-graph-up text-primary"></i>
                        Dashboard
                    </h1>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/expenses" class="text-decoration-none">
                                    <i class="bi bi-house"></i> Expenses
                                </a>
                            </li>
                            <li class="breadcrumb-item active">Dashboard</li>
                        </ol>
                    </nav>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </a>
            </div>
        </div>

        <!-- Summary Cards -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="card bg-primary text-white shadow">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col">
                                <h5 class="card-title mb-1">Total Spending</h5>
                                <h2 class="mb-0">$${totalSpending}</h2>
                            </div>
                            <div class="col-auto">
                                <i class="bi bi-currency-dollar display-4"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card bg-success text-white shadow">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col">
                                <h5 class="card-title mb-1">Total Expenses</h5>
                                <h2 class="mb-0">${totalExpenses}</h2>
                            </div>
                            <div class="col-auto">
                                <i class="bi bi-receipt display-4"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts -->
        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="card shadow">
                    <div class="card-header bg-light">
                        <h5 class="mb-0">
                            <i class="bi bi-pie-chart"></i> Spending by Category
                        </h5>
                    </div>
                    <div class="card-body">
                        <canvas id="categoryChart" width="400" height="400"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 mb-4">
                <div class="card shadow">
                    <div class="card-header bg-light">
                        <h5 class="mb-0">
                            <i class="bi bi-bar-chart"></i> Monthly Spending
                        </h5>
                    </div>
                    <div class="card-body">
                        <canvas id="monthlyChart" width="400" height="400"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Fetch and render category chart
        fetch('${pageContext.request.contextPath}/api/dashboard/category-data')
            .then(response => response.json())
            .then(data => {
                const ctx = document.getElementById('categoryChart').getContext('2d');
                const labels = Object.keys(data);
                const values = Object.values(data);
                
                if (labels.length === 0) {
                    document.getElementById('categoryChart').parentElement.innerHTML = 
                        '<div class="text-center py-5"><i class="bi bi-inbox display-1 text-muted"></i><h5 class="text-muted mt-3">No data available</h5></div>';
                    return;
                }

                new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: values,
                            backgroundColor: [
                                '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
                                '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
                            ],
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom'
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return context.label + ': $' + context.parsed.toFixed(2);
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => {
                console.error('Error fetching category data:', error);
                document.getElementById('categoryChart').parentElement.innerHTML = 
                    '<div class="text-center py-5 text-danger"><i class="bi bi-exclamation-triangle display-1"></i><h5 class="mt-3">Error loading chart</h5></div>';
            });

        // Fetch and render monthly chart
        fetch('${pageContext.request.contextPath}/api/dashboard/monthly-data')
            .then(response => response.json())
            .then(data => {
                const ctx = document.getElementById('monthlyChart').getContext('2d');
                const labels = Object.keys(data);
                const values = Object.values(data);
                
                if (labels.length === 0) {
                    document.getElementById('monthlyChart').parentElement.innerHTML = 
                        '<div class="text-center py-5"><i class="bi bi-inbox display-1 text-muted"></i><h5 class="text-muted mt-3">No data available</h5></div>';
                    return;
                }

                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Monthly Spending',
                            data: values,
                            backgroundColor: '#36A2EB',
                            borderColor: '#1E88E5',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return '$' + value.toFixed(2);
                                    }
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return 'Amount: $' + context.parsed.y.toFixed(2);
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => {
                console.error('Error fetching monthly data:', error);
                document.getElementById('monthlyChart').parentElement.innerHTML = 
                    '<div class="text-center py-5 text-danger"><i class="bi bi-exclamation-triangle display-1"></i><h5 class="mt-3">Error loading chart</h5></div>';
            });
    </script>
</body>
</html>