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

    <title>Customer Portal - My Invoices</title>

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
        
        .invoice-row:hover {
            background-color: #f8f9fc;
            cursor: pointer;
        }
        
        .filter-card {
            border-left: 4px solid #4e73df;
        }
        
        .invoice-detail-modal .modal-dialog {
            max-width: 800px;
        }
        
        .invoice-summary {
            border: 2px solid #e3e6f0;
            border-radius: 0.35rem;
            background: #f8f9fc;
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
            <li class="nav-item active">
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
                        <h1 class="h3 mb-0 text-gray-800">My Invoices</h1>
                    </div>

                    <!-- Filter Card -->
                    <div class="card shadow mb-4 filter-card">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">
                                <i class="fas fa-filter"></i> Filter Invoices
                            </h6>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-3">
                                    <label for="statusFilter">Status</label>
                                    <select id="statusFilter" class="form-control">
                                        <option value="">All Statuses</option>
                                        <option value="PENDING">Pending</option>
                                        <option value="SENT">Sent</option>
                                        <option value="PAID">Paid</option>
                                        <option value="OVERDUE">Overdue</option>
                                        <option value="CANCELLED">Cancelled</option>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label for="dateFromFilter">From Date</label>
                                    <input type="date" id="dateFromFilter" class="form-control">
                                </div>
                                <div class="col-md-3">
                                    <label for="dateToFilter">To Date</label>
                                    <input type="date" id="dateToFilter" class="form-control">
                                </div>
                                <div class="col-md-3">
                                    <label>&nbsp;</label>
                                    <div>
                                        <button id="applyFilter" class="btn btn-primary btn-sm">
                                            <i class="fas fa-search"></i> Apply Filter
                                        </button>
                                        <button id="clearFilter" class="btn btn-secondary btn-sm">
                                            <i class="fas fa-times"></i> Clear
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Invoices Table -->
                    <div class="card shadow mb-4">
                        <div class="card-header card-header-primary py-3">
                            <h6 class="m-0 font-weight-bold">Invoice List</h6>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="invoicesTable" width="100%" cellspacing="0">
                                    <thead>
                                        <tr>
                                            <th>Invoice Number</th>
                                            <th>Invoice Date</th>
                                            <th>Due Date</th>
                                            <th>Total Amount</th>
                                            <th>Paid Amount</th>
                                            <th>Outstanding</th>
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

    <!-- Invoice Detail Modal -->
    <div class="modal fade invoice-detail-modal" id="invoiceDetailModal" tabindex="-1" role="dialog" aria-labelledby="invoiceDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="invoiceDetailModalLabel">
                        <i class="fas fa-file-invoice-dollar"></i> Invoice Details
                    </h5>
                    <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body" id="invoiceDetailContent">
                    <!-- Invoice details will be loaded here -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" id="downloadInvoiceBtn" onclick="downloadInvoice()">
                        <i class="fas fa-download"></i> Download PDF
                    </button>
                    <button type="button" class="btn btn-success" id="payInvoiceBtn" onclick="payInvoice()" style="display: none;">
                        <i class="fas fa-credit-card"></i> Pay Now
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
        let invoicesTable;
        let currentInvoiceId = null;
        
        $(document).ready(function() {
            // Check authentication status
            checkAuthStatus();
            
            // Initialize DataTable
            initializeInvoicesTable();
            
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
        
        function initializeInvoicesTable() {
            invoicesTable = $('#invoicesTable').DataTable({
                "processing": true,
                "serverSide": true,
                "ajax": {
                    "url": "/api/portal/invoices/datatable",
                    "type": "POST",
                    "data": function(d) {
                        d.status = $('#statusFilter').val();
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
                        "data": "invoiceNumber",
                        "render": function(data, type, row) {
                            return '<strong>' + data + '</strong>';
                        }
                    },
                    {
                        "data": "invoiceDate",
                        "render": function(data) {
                            return formatDate(data);
                        }
                    },
                    {
                        "data": "dueDate",
                        "render": function(data, type, row) {
                            const dueDateFormatted = formatDate(data);
                            const isOverdue = row.status === 'OVERDUE';
                            return isOverdue ? 
                                '<span class="text-danger font-weight-bold">' + dueDateFormatted + '</span>' :
                                dueDateFormatted;
                        }
                    },
                    {
                        "data": "totalAmount",
                        "render": function(data) {
                            return formatCurrency(data);
                        }
                    },
                    {
                        "data": "paidAmount",
                        "render": function(data) {
                            return formatCurrency(data || 0);
                        }
                    },
                    {
                        "data": "outstandingAmount",
                        "render": function(data, type, row) {
                            const amount = formatCurrency(data || 0);
                            return data > 0 ? 
                                '<span class="text-warning font-weight-bold">' + amount + '</span>' :
                                amount;
                        }
                    },
                    {
                        "data": "status",
                        "render": function(data) {
                            const statusColors = {
                                'PENDING': 'warning',
                                'SENT': 'info',
                                'PAID': 'success',
                                'OVERDUE': 'danger',
                                'CANCELLED': 'secondary'
                            };
                            const statusTexts = {
                                'PENDING': 'Pending',
                                'SENT': 'Sent',
                                'PAID': 'Paid',
                                'OVERDUE': 'Overdue',
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
                            let actions = '<div class="btn-group">';
                            actions += '<button class="btn btn-sm btn-primary" onclick="viewInvoiceDetail(' + row.id + ')">';
                            actions += '<i class="fas fa-eye"></i> View</button>';
                            
                            if (row.status !== 'PAID' && row.status !== 'CANCELLED') {
                                actions += '<button class="btn btn-sm btn-success ml-1" onclick="payInvoiceQuick(' + row.id + ')">';
                                actions += '<i class="fas fa-credit-card"></i> Pay</button>';
                            }
                            
                            actions += '</div>';
                            return actions;
                        }
                    }
                ],
                "order": [[1, "desc"]],
                "pageLength": 25,
                "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
                "language": {
                    "emptyTable": "No invoices found",
                    "processing": "Loading invoices..."
                },
                "rowCallback": function(row, data) {
                    $(row).addClass('invoice-row');
                    $(row).attr('onclick', 'viewInvoiceDetail(' + data.id + ')');
                }
            });
        }
        
        function bindFilterEvents() {
            $('#applyFilter').on('click', function() {
                invoicesTable.ajax.reload();
            });
            
            $('#clearFilter').on('click', function() {
                $('#statusFilter').val('');
                $('#dateFromFilter').val('');
                $('#dateToFilter').val('');
                invoicesTable.ajax.reload();
            });
            
            // Apply filter on Enter key
            $('#statusFilter, #dateFromFilter, #dateToFilter').on('keypress', function(e) {
                if (e.which === 13) {
                    invoicesTable.ajax.reload();
                }
            });
        }
        
        function viewInvoiceDetail(invoiceId) {
            currentInvoiceId = invoiceId;
            
            $.get('/api/portal/invoices/' + invoiceId)
                .done(function(response) {
                    if (response.success && response.data) {
                        showInvoiceDetail(response.data);
                    } else {
                        alert('Failed to load invoice details');
                    }
                })
                .fail(function(xhr) {
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    } else {
                        alert('Failed to load invoice details');
                    }
                });
        }
        
        function showInvoiceDetail(invoice) {
            let content = '<div class="invoice-summary p-4 mb-4">';
            content += '<div class="row">';
            content += '<div class="col-md-6">';
            content += '<h5><strong>Invoice #' + invoice.invoiceNumber + '</strong></h5>';
            content += '<p><strong>Date:</strong> ' + formatDate(invoice.invoiceDate) + '</p>';
            content += '<p><strong>Due Date:</strong> ' + formatDate(invoice.dueDate) + '</p>';
            content += '<p><strong>Status:</strong> ' + getStatusBadge(invoice.status) + '</p>';
            content += '</div>';
            content += '<div class="col-md-6 text-right">';
            content += '<h5><strong>Total Amount</strong></h5>';
            content += '<h4 class="text-primary">' + formatCurrency(invoice.totalAmount) + '</h4>';
            if (invoice.paidAmount > 0) {
                content += '<p><strong>Paid:</strong> ' + formatCurrency(invoice.paidAmount) + '</p>';
                content += '<p><strong>Outstanding:</strong> ' + formatCurrency(invoice.outstandingAmount || 0) + '</p>';
            }
            content += '</div>';
            content += '</div>';
            content += '</div>';
            
            if (invoice.description) {
                content += '<div class="mb-3">';
                content += '<h6><strong>Description:</strong></h6>';
                content += '<p>' + invoice.description + '</p>';
                content += '</div>';
            }
            
            // Invoice Items
            if (invoice.items && invoice.items.length > 0) {
                content += '<h6><strong>Invoice Items:</strong></h6>';
                content += '<div class="table-responsive">';
                content += '<table class="table table-sm table-bordered">';
                content += '<thead class="thead-light">';
                content += '<tr><th>Description</th><th>Quantity</th><th>Unit Price</th><th>Total</th></tr>';
                content += '</thead><tbody>';
                
                invoice.items.forEach(function(item) {
                    content += '<tr>';
                    content += '<td>' + item.description + '</td>';
                    content += '<td class="text-center">' + item.quantity + '</td>';
                    content += '<td class="text-right">' + formatCurrency(item.unitPrice) + '</td>';
                    content += '<td class="text-right"><strong>' + formatCurrency(item.totalAmount) + '</strong></td>';
                    content += '</tr>';
                });
                
                content += '</tbody></table>';
                content += '</div>';
            }
            
            $('#invoiceDetailContent').html(content);
            
            // Show/hide payment button
            const canPay = invoice.status !== 'PAID' && invoice.status !== 'CANCELLED';
            $('#payInvoiceBtn').toggle(canPay);
            
            $('#invoiceDetailModal').modal('show');
        }
        
        function downloadInvoice() {
            if (currentInvoiceId) {
                window.open('/api/portal/invoices/' + currentInvoiceId + '/pdf', '_blank');
            }
        }
        
        function payInvoice() {
            if (currentInvoiceId) {
                window.location.href = '/portal/payments/new?invoiceId=' + currentInvoiceId;
            }
        }
        
        function payInvoiceQuick(invoiceId) {
            window.location.href = '/portal/payments/new?invoiceId=' + invoiceId;
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
        
        function getStatusBadge(status) {
            const statusColors = {
                'PENDING': 'warning',
                'SENT': 'info',
                'PAID': 'success',
                'OVERDUE': 'danger',
                'CANCELLED': 'secondary'
            };
            const statusTexts = {
                'PENDING': 'Pending',
                'SENT': 'Sent',
                'PAID': 'Paid',
                'OVERDUE': 'Overdue',
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
