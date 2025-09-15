<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="layout" %>

<layout:main>
    <jsp:attribute name="title">Invoice Management</jsp:attribute>
    <jsp:attribute name="content">
        <div class="container-fluid">
            
            <!-- Page Heading -->
            <div class="d-sm-flex align-items-center justify-content-between mb-4">
                <h1 class="h3 mb-0 text-gray-800">
                    <i class="fas fa-file-invoice-dollar"></i> Invoice Management
                </h1>
                <div>
                    <button class="btn btn-success btn-sm" onclick="openGenerateModal()">
                        <i class="fas fa-plus"></i> Generate Invoice
                    </button>
                    <button class="btn btn-primary btn-sm" onclick="openCreateModal()">
                        <i class="fas fa-plus"></i> Create Invoice
                    </button>
                </div>
            </div>
            
            <!-- Statistics Cards -->
            <div class="row mb-4">
                <div class="col-xl-3 col-md-6 mb-4">
                    <div class="card border-left-primary shadow h-100 py-2">
                        <div class="card-body">
                            <div class="row no-gutters align-items-center">
                                <div class="col mr-2">
                                    <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                        Total Invoices
                                    </div>
                                    <div class="h5 mb-0 font-weight-bold text-gray-800" id="total-invoices">0</div>
                                </div>
                                <div class="col-auto">
                                    <i class="fas fa-file-invoice fa-2x text-gray-300"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="col-xl-3 col-md-6 mb-4">
                    <div class="card border-left-success shadow h-100 py-2">
                        <div class="card-body">
                            <div class="row no-gutters align-items-center">
                                <div class="col mr-2">
                                    <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                        Paid Invoices
                                    </div>
                                    <div class="h5 mb-0 font-weight-bold text-gray-800" id="paid-invoices">0</div>
                                </div>
                                <div class="col-auto">
                                    <i class="fas fa-check-circle fa-2x text-gray-300"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="col-xl-3 col-md-6 mb-4">
                    <div class="card border-left-warning shadow h-100 py-2">
                        <div class="card-body">
                            <div class="row no-gutters align-items-center">
                                <div class="col mr-2">
                                    <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                        Pending Amount
                                    </div>
                                    <div class="h5 mb-0 font-weight-bold text-gray-800" id="pending-amount">IDR 0</div>
                                </div>
                                <div class="col-auto">
                                    <i class="fas fa-clock fa-2x text-gray-300"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="col-xl-3 col-md-6 mb-4">
                    <div class="card border-left-danger shadow h-100 py-2">
                        <div class="card-body">
                            <div class="row no-gutters align-items-center">
                                <div class="col mr-2">
                                    <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
                                        Overdue Invoices
                                    </div>
                                    <div class="h5 mb-0 font-weight-bold text-gray-800" id="overdue-invoices">0</div>
                                </div>
                                <div class="col-auto">
                                    <i class="fas fa-exclamation-triangle fa-2x text-gray-300"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Filter Panel -->
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-filter"></i> Filter & Search
                    </h6>
                </div>
                <div class="card-body">
                    <form id="filterForm" class="row">
                        <div class="col-md-3 mb-3">
                            <label for="searchTerm" class="form-label">Search</label>
                            <input type="text" class="form-control" id="searchTerm" name="searchTerm"
                                   placeholder="Invoice number, customer name...">
                        </div>
                        
                        <div class="col-md-2 mb-3">
                            <label for="status" class="form-label">Status</label>
                            <select class="form-control" id="status" name="status">
                                <option value="">All Statuses</option>
                                <option value="PENDING">Pending</option>
                                <option value="SENT">Sent</option>
                                <option value="PAID">Paid</option>
                                <option value="OVERDUE">Overdue</option>
                                <option value="CANCELLED">Cancelled</option>
                            </select>
                        </div>
                        
                        <div class="col-md-2 mb-3">
                            <label for="paymentStatus" class="form-label">Payment Status</label>
                            <select class="form-control" id="paymentStatus" name="paymentStatus">
                                <option value="">All Payment Status</option>
                                <option value="UNPAID">Unpaid</option>
                                <option value="PARTIAL">Partially Paid</option>
                                <option value="PAID">Fully Paid</option>
                            </select>
                        </div>
                        
                        <div class="col-md-2 mb-3">
                            <label for="startDate" class="form-label">From Date</label>
                            <input type="date" class="form-control" id="startDate" name="startDate">
                        </div>
                        
                        <div class="col-md-2 mb-3">
                            <label for="endDate" class="form-label">To Date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate">
                        </div>
                        
                        <div class="col-md-1 mb-3 d-flex align-items-end">
                            <button type="button" class="btn btn-primary" onclick="applyFilters()">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            
            <!-- DataTable -->
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-table"></i> Invoice List
                    </h6>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-bordered" id="invoicesTable" width="100%" cellspacing="0">
                            <thead>
                                <tr>
                                    <th>Invoice #</th>
                                    <th>Customer</th>
                                    <th>Invoice Date</th>
                                    <th>Due Date</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Payment Status</th>
                                    <th>Overdue</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="invoicesTableBody">
                                <!-- Data will be loaded via AJAX -->
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Pagination -->
                    <nav aria-label="Page navigation" class="mt-3">
                        <ul class="pagination justify-content-center" id="pagination">
                            <!-- Pagination will be generated dynamically -->
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
        
        <!-- Generate Invoice Modal -->
        <div class="modal fade" id="generateInvoiceModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Generate Invoice</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form id="generateForm">
                        <div class="modal-body">
                            <div class="form-group">
                                <label for="generateCustomerId">Customer *</label>
                                <select class="form-control" id="generateCustomerId" name="customerId" required>
                                    <option value="">Select Customer...</option>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="generateBillingGroupId">Billing Group *</label>
                                <select class="form-control" id="generateBillingGroupId" name="billingGroupId" required>
                                    <option value="">Select Billing Group...</option>
                                </select>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="periodStart">Period Start *</label>
                                        <input type="date" class="form-control" id="periodStart" name="periodStart" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="periodEnd">Period End *</label>
                                        <input type="date" class="form-control" id="periodEnd" name="periodEnd" required>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Generate Invoice</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <!-- Create Invoice Modal -->
        <div class="modal fade" id="createInvoiceModal" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Create Invoice</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form id="createForm">
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="createCustomerId">Customer *</label>
                                        <select class="form-control" id="createCustomerId" name="customerId" required>
                                            <option value="">Select Customer...</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="createBillingGroupId">Billing Group *</label>
                                        <select class="form-control" id="createBillingGroupId" name="billingGroupId" required>
                                            <option value="">Select Billing Group...</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="invoiceDate">Invoice Date *</label>
                                        <input type="date" class="form-control" id="invoiceDate" name="invoiceDate" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="dueDate">Due Date *</label>
                                        <input type="date" class="form-control" id="dueDate" name="dueDate" required>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="currency">Currency</label>
                                        <select class="form-control" id="currency" name="currency">
                                            <option value="IDR">IDR</option>
                                            <option value="USD">USD</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="notes">Notes</label>
                                <textarea class="form-control" id="notes" name="notes" rows="3"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Create Invoice</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <!-- Record Payment Modal -->
        <div class="modal fade" id="recordPaymentModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Record Payment</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form id="paymentForm">
                        <div class="modal-body">
                            <input type="hidden" id="paymentInvoiceId" name="invoiceId">
                            
                            <div class="form-group">
                                <label>Invoice Number</label>
                                <input type="text" class="form-control" id="paymentInvoiceNumber" readonly>
                            </div>
                            
                            <div class="form-group">
                                <label>Total Amount</label>
                                <input type="text" class="form-control" id="paymentTotalAmount" readonly>
                            </div>
                            
                            <div class="form-group">
                                <label>Remaining Amount</label>
                                <input type="text" class="form-control" id="paymentRemainingAmount" readonly>
                            </div>
                            
                            <div class="form-group">
                                <label for="paymentAmount">Payment Amount *</label>
                                <input type="number" class="form-control" id="paymentAmount" name="amount" 
                                       step="0.01" min="0.01" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-success">Record Payment</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
    </jsp:attribute>
    
    <jsp:attribute name="scripts">
        <script src="/js/invoices/list-data.js"></script>
    </jsp:attribute>
</layout:main>
