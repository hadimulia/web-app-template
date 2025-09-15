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

    <title>Customer Portal - Payment History</title>

    <!-- Custom fonts for this template-->
    <link href="/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="/css/sb-admin-2.min.css" rel="stylesheet">
    
    <!-- DataTables CSS -->
    <link href="/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
    
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
        
        .payment-row:hover {
            background-color: #f8f9fc;
            cursor: pointer;
        }
        
        .filter-card {
            border-left: 4px solid #28a745;
        }
        
        .payment-summary-card {
            border-left: 4px solid #17a2b8;
        }
        
        .stat-card {
            transition: transform 0.2s;
        }
        
        .stat-card:hover {
            transform: translateY(-2px);
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
            <li class="nav-item">
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
            <li class="nav-item active">
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
                        <h1 class="h3 mb-0 text-gray-800">Payment History</h1>
                    </div>

                    <!-- Payment Statistics -->
                    <div class="row mb-4">
                        <div class="col-xl-4 col-md-6 mb-4">
                            <div class="card border-left-success shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                                Total Payments
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="totalPayments">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-credit-card fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-xl-4 col-md-6 mb-4">
                            <div class="card border-left-info shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                                Total Amount Paid
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="totalAmountPaid">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-xl-4 col-md-6 mb-4">
                            <div class="card border-left-primary shadow h-100 py-2 stat-card">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                                Last Payment
                                            </div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800" id="lastPaymentDate">-</div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-calendar fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Filter Card -->
                    <div class="card shadow mb-4 filter-card">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-success">
                                <i class="fas fa-filter"></i> Filter Payments
                            </h6>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-3">
                                    <label for="statusFilter">Payment Status</label>
                                    <select id="statusFilter" class="form-control">
                                        <option value="">All Statuses</option>
                                        <option value="COMPLETED">Completed</option>
                                        <option value="PENDING">Pending</option>
                                        <option value="FAILED">Failed</option>
                                        <option value="CANCELLED">Cancelled</option>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label for="paymentMethodFilter">Payment Method</label>
                                    <select id="paymentMethodFilter" class="form-control">
                                        <option value="">All Methods</option>
                                        <option value="BANK_TRANSFER">Bank Transfer</option>
                                        <option value="CREDIT_CARD">Credit Card</option>
                                        <option value="CASH">Cash</option>
                                        <option value="CHECK">Check</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <label for="dateFromFilter">From Date</label>
                                    <input type="date" id="dateFromFilter" class="form-control">
                                </div>
                                <div class="col-md-2">
                                    <label for="dateToFilter">To Date</label>
                                    <input type="date" id="dateToFilter" class="form-control">
                                </div>
                                <div class="col-md-2">
                                    <label>&nbsp;</label>
                                    <div>
                                        <button id="applyFilter" class="btn btn-success btn-sm">
                                            <i class="fas fa-search"></i> Apply
                                        </button>
                                        <button id="clearFilter" class="btn btn-secondary btn-sm">
                                            <i class="fas fa-times"></i> Clear
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Payments Table -->
                    <div class="card shadow mb-4">
                        <div class="card-header card-header-primary py-3">
                            <h6 class="m-0 font-weight-bold">Payment History</h6>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="paymentsTable" width="100%" cellspacing="0">
                                    <thead>
                                        <tr>
                                            <th>Payment Date</th>
                                            <th>Invoice Number</th>
                                            <th>Amount</th>
                                            <th>Payment Method</th>
                                            <th>Reference</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
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

    <!-- Payment Detail Modal -->
    <div class="modal fade" id="paymentDetailModal" tabindex="-1" role="dialog" aria-labelledby="paymentDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title" id="paymentDetailModalLabel">
                        <i class="fas fa-credit-card"></i> Payment Details
                    </h5>
                    <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body" id="paymentDetailContent">
                    <!-- Payment details will be loaded here -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" id="downloadReceiptBtn" onclick="downloadReceipt()" style="display: none;">
                        <i class="fas fa-download"></i> Download Receipt
                    </button>
                </div>
            </div>
        </div>
    </div>

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

    <!-- DataTables JavaScript-->
    <script src="/vendor/datatables/jquery.dataTables.min.js"></script>
    <script src="/vendor/datatables/dataTables.bootstrap4.min.js"></script>

    <script>
        let paymentsTable;
        let currentPaymentId = null;
        
        $(document).ready(function() {
            // Check authentication status
            checkAuthStatus();
            
            // Load payment statistics
            loadPaymentStatistics();
            
            // Initialize DataTable
            initializePaymentsTable();
            
            // Bind filter events
            bindFilterEvents();
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
        
        function loadPaymentStatistics() {
            $.get('/api/portal/payments/statistics')
                .done(function(response) {
                    if (response.success && response.data) {
                        const stats = response.data;
                        $('#totalPayments').text(stats.total_payments || 0);
                        $('#totalAmountPaid').text(formatCurrency(stats.total_amount || 0));
                        $('#lastPaymentDate').text(stats.last_payment_date ? formatDate(stats.last_payment_date) : 'N/A');
                    }
                })
                .fail(function(xhr) {
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    }
                });
        }
        
        function initializePaymentsTable() {
            paymentsTable = $('#paymentsTable').DataTable({
                "processing": true,
                "serverSide": true,
                "ajax": {
                    "url": "/api/portal/payments/datatable",
                    "type": "POST",
                    "data": function(d) {
                        d.status = $('#statusFilter').val();
                        d.paymentMethod = $('#paymentMethodFilter').val();
                        d.dateFrom = $('#dateFromFilter').val();
                        d.dateTo = $('#dateToFilter').val();
                    },
                    "error": function(xhr) {
                        if (xhr.status === 401) {
                            window.location.href = '/portal/login';
                        }
                    }
                },
                "columns": [
                    {
                        "data": "paymentDate",
                        "render": function(data) {
                            return formatDate(data);
                        }
                    },
                    {
                        "data": "invoiceNumber",
                        "render": function(data, type, row) {
                            return '<strong>' + data + '</strong>';
                        }
                    },
                    {
                        "data": "amount",
                        "render": function(data) {
                            return '<strong>' + formatCurrency(data) + '</strong>';
                        }
                    },
                    {
                        "data": "paymentMethod",
                        "render": function(data) {
                            const methodLabels = {
                                'BANK_TRANSFER': 'Bank Transfer',
                                'CREDIT_CARD': 'Credit Card',
                                'CASH': 'Cash',
                                'CHECK': 'Check',
                                'OTHER': 'Other'
                            };
                            return methodLabels[data] || data;
                        }
                    },
                    {
                        "data": "reference",
                        "render": function(data) {
                            return data || '-';
                        }
                    },
                    {
                        "data": "status",
                        "render": function(data) {
                            const statusColors = {
                                'COMPLETED': 'success',
                                'PENDING': 'warning',
                                'FAILED': 'danger',
                                'CANCELLED': 'secondary'
                            };
                            const statusTexts = {
                                'COMPLETED': 'Completed',
                                'PENDING': 'Pending',
                                'FAILED': 'Failed',
                                'CANCELLED': 'Cancelled'
                            };
                            return '<span class="badge badge-' + (statusColors[data] || 'secondary') + '">' + 
                                   (statusTexts[data] || data) + '</span>';
                        }
                    },
                    {
                        "data": null,
                        "orderable": false,
                        "render": function(data, type, row) {
                            let actions = '<button class="btn btn-sm btn-primary" onclick="viewPaymentDetail(' + row.id + ')">';
                            actions += '<i class="fas fa-eye"></i> View</button>';
                            
                            if (row.status === 'COMPLETED') {
                                actions += ' <button class="btn btn-sm btn-info ml-1" onclick="downloadReceiptDirect(' + row.id + ')">';
                                actions += '<i class="fas fa-download"></i> Receipt</button>';
                            }
                            
                            return actions;
                        }
                    }
                ],
                "order": [[0, "desc"]],
                "pageLength": 25,
                "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
                "language": {
                    "emptyTable": "No payments found",
                    "processing": "Loading payments..."
                },
                "rowCallback": function(row, data) {
                    $(row).addClass('payment-row');
                }
            });
        }
        
        function bindFilterEvents() {
            $('#applyFilter').on('click', function() {
                paymentsTable.ajax.reload();
            });
            
            $('#clearFilter').on('click', function() {
                $('#statusFilter').val('');
                $('#paymentMethodFilter').val('');
                $('#dateFromFilter').val('');
                $('#dateToFilter').val('');
                paymentsTable.ajax.reload();
            });
        }
        
        function viewPaymentDetail(paymentId) {
            currentPaymentId = paymentId;
            
            $.get('/api/portal/payments/' + paymentId)
                .done(function(response) {
                    if (response.success && response.data) {
                        showPaymentDetail(response.data);
                    } else {
                        alert('Failed to load payment details');
                    }
                })
                .fail(function(xhr) {
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    } else {
                        alert('Failed to load payment details');
                    }
                });
        }
        
        function showPaymentDetail(payment) {
            let content = '<div class="payment-summary-card p-4 mb-4" style="border: 2px solid #e3e6f0; border-radius: 0.35rem; background: #f8f9fc;">';
            content += '<div class="row">';
            content += '<div class="col-md-6">';
            content += '<h5><strong>Payment Information</strong></h5>';
            content += '<p><strong>Payment Date:</strong> ' + formatDateTime(payment.paymentDate) + '</p>';
            content += '<p><strong>Invoice:</strong> ' + payment.invoiceNumber + '</p>';
            content += '<p><strong>Payment Method:</strong> ' + getPaymentMethodLabel(payment.paymentMethod) + '</p>';
            if (payment.reference) {
                content += '<p><strong>Reference:</strong> ' + payment.reference + '</p>';
            }
            content += '<p><strong>Status:</strong> ' + getStatusBadge(payment.status) + '</p>';
            content += '</div>';
            content += '<div class="col-md-6 text-right">';
            content += '<h5><strong>Amount</strong></h5>';
            content += '<h4 class="text-success">' + formatCurrency(payment.amount) + '</h4>';
            content += '</div>';
            content += '</div>';
            content += '</div>';
            
            if (payment.notes) {
                content += '<div class="mb-3">';
                content += '<h6><strong>Notes:</strong></h6>';
                content += '<p>' + payment.notes + '</p>';
                content += '</div>';
            }
            
            // Transaction details if available
            if (payment.transactionId || payment.gateway) {
                content += '<div class="mb-3">';
                content += '<h6><strong>Transaction Details:</strong></h6>';
                if (payment.transactionId) {
                    content += '<p><strong>Transaction ID:</strong> ' + payment.transactionId + '</p>';
                }
                if (payment.gateway) {
                    content += '<p><strong>Gateway:</strong> ' + payment.gateway + '</p>';
                }
                content += '</div>';
            }
            
            $('#paymentDetailContent').html(content);
            
            // Show/hide receipt button
            $('#downloadReceiptBtn').toggle(payment.status === 'COMPLETED');
            
            $('#paymentDetailModal').modal('show');
        }
        
        function downloadReceipt() {
            if (currentPaymentId) {
                window.open('/api/portal/payments/' + currentPaymentId + '/receipt', '_blank');
            }
        }
        
        function downloadReceiptDirect(paymentId) {
            window.open('/api/portal/payments/' + paymentId + '/receipt', '_blank');
        }
        
        function formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleDateString('id-ID');
        }
        
        function formatDateTime(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleDateString('id-ID') + ' ' + date.toLocaleTimeString('id-ID', {
                hour: '2-digit',
                minute: '2-digit'
            });
        }
        
        function formatCurrency(amount) {
            if (!amount) return 'IDR 0';
            return 'IDR ' + new Intl.NumberFormat('id-ID').format(amount);
        }
        
        function getPaymentMethodLabel(method) {
            const methodLabels = {
                'BANK_TRANSFER': 'Bank Transfer',
                'CREDIT_CARD': 'Credit Card',
                'CASH': 'Cash',
                'CHECK': 'Check',
                'OTHER': 'Other'
            };
            return methodLabels[method] || method;
        }
        
        function getStatusBadge(status) {
            const statusColors = {
                'COMPLETED': 'success',
                'PENDING': 'warning',
                'FAILED': 'danger',
                'CANCELLED': 'secondary'
            };
            const statusTexts = {
                'COMPLETED': 'Completed',
                'PENDING': 'Pending',
                'FAILED': 'Failed',
                'CANCELLED': 'Cancelled'
            };
            return '<span class="badge badge-' + (statusColors[status] || 'secondary') + '">' + 
                   (statusTexts[status] || status) + '</span>';
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
