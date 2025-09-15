<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Customer Portal - Dashboard</title>

    <!-- Custom fonts for this template-->
    <link href="/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="/css/sb-admin-2.min.css" rel="stylesheet">
    
    <style>
        .portal-navbar {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
        }
        
        .sidebar-dark {
            background-color: #2c3e50;
        }
        
        .sidebar-dark .sidebar-brand {
            background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        }
        
        .card-header-primary {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            color: white;
        }
        
        .stat-card {
            transition: transform 0.2s;
        }
        
        .stat-card:hover {
            transform: translateY(-2px);
        }
        
        .quick-action-btn {
            border-radius: 0.5rem;
            padding: 0.75rem 1.5rem;
            margin: 0.25rem;
        }
    </style>
</head>

<body id="page-top">

    <!-- Page Wrapper -->
    <div id="wrapper">

        <!-- Sidebar -->
        <ul class="navbar-nav sidebar sidebar-dark accordion" id="accordionSidebar">

            <!-- Sidebar - Brand -->
            <a class="sidebar-brand d-flex align-items-center justify-content-center" href="/portal/dashboard">
                <div class="sidebar-brand-icon rotate-n-15">
                    <i class="fas fa-user-circle"></i>
                </div>
                <div class="sidebar-brand-text mx-3">Portal</div>
            </a>

            <!-- Divider -->
            <hr class="sidebar-divider my-0">

            <!-- Nav Item - Dashboard -->
            <li class="nav-item active">
                <a class="nav-link" href="/portal/dashboard">
                    <i class="fas fa-fw fa-tachometer-alt"></i>
                    <span>Dashboard</span>
                </a>
            </li>

            <!-- Divider -->
            <hr class="sidebar-divider">

            <!-- Nav Item - Invoices -->
            <li class="nav-item">
                <a class="nav-link" href="/portal/invoices">
                    <i class="fas fa-fw fa-file-invoice-dollar"></i>
                    <span>My Invoices</span>
                </a>
            </li>

            <!-- Nav Item - Payments -->
            <li class="nav-item">
                <a class="nav-link" href="/portal/payments">
                    <i class="fas fa-fw fa-credit-card"></i>
                    <span>Payment History</span>
                </a>
            </li>

            <!-- Nav Item - Profile -->
            <li class="nav-item">
                <a class="nav-link" href="/portal/profile">
                    <i class="fas fa-fw fa-user"></i>
                    <span>My Profile</span>
                </a>
            </li>

            <!-- Divider -->
            <hr class="sidebar-divider d-none d-md-block">

            <!-- Sidebar Toggler (Sidebar) -->
            <div class="text-center d-none d-md-inline">
                <button class="rounded-circle border-0" id="sidebarToggle"></button>
            </div>

        </ul>
        <!-- End of Sidebar -->

        <!-- Content Wrapper -->
        <div id="content-wrapper" class="d-flex flex-column">

            <!-- Main Content -->
            <div id="content">

                <!-- Topbar -->
                <nav class="navbar navbar-expand navbar-light portal-navbar topbar mb-4 static-top shadow">

                    <!-- Sidebar Toggle (Topbar) -->
                    <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                        <i class="fa fa-bars"></i>
                    </button>

                    <!-- Topbar Navbar -->
                    <ul class="navbar-nav ml-auto">

                        <!-- Nav Item - User Information -->
                        <li class="nav-item dropdown no-arrow">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="mr-2 d-none d-lg-inline text-white small" id="customerNameDisplay">${customerName}</span>
                                <i class="fas fa-user-circle fa-fw text-white"></i>
                            </a>
                            <!-- Dropdown - User Information -->
                            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                                aria-labelledby="userDropdown">
                                <a class="dropdown-item" href="/portal/profile">
                                    <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Profile
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="#" onclick="logout()">
                                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Logout
                                </a>
                            </div>
                        </li>

                    </ul>

                </nav>
                <!-- End of Topbar -->

                <!-- Begin Page Content -->
                <div class="container-fluid">

                    <!-- Page Heading -->
                    <div class="d-sm-flex align-items-center justify-content-between mb-4">
                        <h1 class="h3 mb-0 text-gray-800">Dashboard</h1>
                        <div class="d-sm-flex">
                            <button class="btn btn-primary btn-sm quick-action-btn" onclick="window.location.href='/portal/invoices'">
                                <i class="fas fa-file-invoice-dollar"></i> View Invoices
                            </button>
                        </div>
                    </div>

                    <!-- Content Row - Statistics -->
                    <div class="row">

                        <!-- Total Invoices Card -->
                        <div class="col-xl-3 col-md-6 mb-4">
                            <div class="card border-left-primary shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                                Total Invoices
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="totalInvoices">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-file-invoice fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Outstanding Amount Card -->
                        <div class="col-xl-3 col-md-6 mb-4">
                            <div class="card border-left-warning shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                                Outstanding Amount
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="outstandingAmount">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-exclamation-triangle fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Paid Amount Card -->
                        <div class="col-xl-3 col-md-6 mb-4">
                            <div class="card border-left-success shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                                Total Paid
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="totalPaid">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Overdue Invoices Card -->
                        <div class="col-xl-3 col-md-6 mb-4">
                            <div class="card border-left-danger shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
                                                Overdue Invoices
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="overdueInvoices">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-calendar-times fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                    <!-- Content Row -->
                    <div class="row">

                        <!-- Recent Invoices -->
                        <div class="col-lg-8 mb-4">
                            <div class="card shadow mb-4">
                                <div class="card-header card-header-primary py-3 d-flex flex-row align-items-center justify-content-between">
                                    <h6 class="m-0 font-weight-bold">Recent Invoices</h6>
                                    <a href="/portal/invoices" class="text-white">
                                        <i class="fas fa-arrow-right"></i> View All
                                    </a>
                                </div>
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-bordered" width="100%" cellspacing="0">
                                            <thead>
                                                <tr>
                                                    <th>Invoice #</th>
                                                    <th>Date</th>
                                                    <th>Amount</th>
                                                    <th>Status</th>
                                                </tr>
                                            </thead>
                                            <tbody id="recentInvoicesTable">
                                                <tr>
                                                    <td colspan="4" class="text-center">Loading...</td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Overdue Alerts -->
                        <div class="col-lg-4 mb-4">
                            <div class="card shadow mb-4">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-danger">
                                        <i class="fas fa-exclamation-triangle"></i> Overdue Invoices
                                    </h6>
                                </div>
                                <div class="card-body">
                                    <div id="overdueInvoicesList">
                                        <p class="text-center text-muted">Loading...</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
                <!-- /.container-fluid -->

            </div>
            <!-- End of Main Content -->

            <!-- Footer -->
            <footer class="sticky-footer bg-white">
                <div class="container my-auto">
                    <div class="copyright text-center my-auto">
                        <span>Copyright &copy; Customer Portal 2024</span>
                    </div>
                </div>
            </footer>
            <!-- End of Footer -->

        </div>
        <!-- End of Content Wrapper -->

    </div>
    <!-- End of Page Wrapper -->

    <!-- Scroll to Top Button-->
    <a class="scroll-to-top rounded" href="#page-top">
        <i class="fas fa-angle-up"></i>
    </a>

    <!-- Bootstrap core JavaScript-->
    <script src="/vendor/jquery/jquery.min.js"></script>
    <script src="/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

    <!-- Core plugin JavaScript-->
    <script src="/vendor/jquery-easing/jquery.easing.min.js"></script>

    <!-- Custom scripts for all pages-->
    <script src="/js/sb-admin-2.min.js"></script>

    <script>
        $(document).ready(function() {
            // Check authentication status
            checkAuthStatus();
            
            // Load dashboard data
            loadDashboardData();
        });
        
        function checkAuthStatus() {
            $.get('/api/portal/auth-status')
                .done(function(response) {
                    if (!response.authenticated) {
                        window.location.href = '/portal/login';
                    } else {
                        $('#customerNameDisplay').text(response.customerName || 'Customer');
                    }
                })
                .fail(function() {
                    window.location.href = '/portal/login';
                });
        }
        
        function loadDashboardData() {
            $.get('/api/portal/dashboard')
                .done(function(response) {
                    if (response.success && response.data) {
                        const data = response.data;
                        
                        // Update statistics
                        updateStatistics(data.statistics);
                        
                        // Update recent invoices
                        updateRecentInvoices(data.recentInvoices);
                        
                        // Update overdue invoices
                        updateOverdueInvoices(data.overdueInvoices);
                    }
                })
                .fail(function(xhr) {
                    console.error('Failed to load dashboard data:', xhr);
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    }
                });
        }
        
        function updateStatistics(stats) {
            if (stats) {
                $('#totalInvoices').text(stats.total_invoices || 0);
                $('#outstandingAmount').text(formatCurrency(stats.total_outstanding || 0));
                $('#totalPaid').text(formatCurrency(stats.total_paid || 0));
                $('#overdueInvoices').text(stats.overdue_invoices || 0);
            }
        }
        
        function updateRecentInvoices(invoices) {
            const tbody = $('#recentInvoicesTable');
            tbody.empty();
            
            if (!invoices || invoices.length === 0) {
                tbody.append('<tr><td colspan="4" class="text-center text-muted">No recent invoices</td></tr>');
                return;
            }
            
            invoices.forEach(function(invoice) {
                const row = `
                    <tr onclick="window.location.href='/portal/invoices/${invoice.id}'" style="cursor: pointer;">
                        <td><strong>${invoice.invoiceNumber}</strong></td>
                        <td>${formatDate(invoice.invoiceDate)}</td>
                        <td>${formatCurrency(invoice.totalAmount)}</td>
                        <td>
                            <span class="badge badge-${getStatusColor(invoice.status)} badge-pill">
                                ${getStatusText(invoice.status)}
                            </span>
                        </td>
                    </tr>
                `;
                tbody.append(row);
            });
        }
        
        function updateOverdueInvoices(invoices) {
            const container = $('#overdueInvoicesList');
            container.empty();
            
            if (!invoices || invoices.length === 0) {
                container.append('<p class="text-center text-muted">No overdue invoices</p>');
                return;
            }
            
            invoices.forEach(function(invoice) {
                const item = `
                    <div class="mb-3 p-3 border-left-danger border-left">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="font-weight-bold text-danger">${invoice.invoiceNumber}</h6>
                                <p class="text-sm text-muted mb-1">Due: ${formatDate(invoice.dueDate)}</p>
                                <p class="text-sm font-weight-bold mb-0">${formatCurrency(invoice.totalAmount)}</p>
                            </div>
                            <span class="badge badge-danger">${invoice.daysPastDue} days</span>
                        </div>
                        <a href="/portal/invoices/${invoice.id}" class="btn btn-sm btn-outline-danger mt-2">
                            View Details
                        </a>
                    </div>
                `;
                container.append(item);
            });
        }
        
        function formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleDateString('id-ID');
        }
        
        function formatCurrency(amount) {
            if (!amount) return 'IDR 0';
            return 'IDR ' + new Intl.NumberFormat('id-ID').format(amount);
        }
        
        function getStatusColor(status) {
            const colors = {
                'PENDING': 'warning',
                'SENT': 'info',
                'PAID': 'success',
                'OVERDUE': 'danger',
                'CANCELLED': 'secondary'
            };
            return colors[status] || 'secondary';
        }
        
        function getStatusText(status) {
            const texts = {
                'PENDING': 'Pending',
                'SENT': 'Sent',
                'PAID': 'Paid',
                'OVERDUE': 'Overdue',
                'CANCELLED': 'Cancelled'
            };
            return texts[status] || status;
        }
        
        function logout() {
            if (confirm('Are you sure you want to logout?')) {
                $.post('/api/portal/logout')
                    .always(function() {
                        window.location.href = '/portal/login';
                    });
            }
        }
    </script>

</body>

</html>
