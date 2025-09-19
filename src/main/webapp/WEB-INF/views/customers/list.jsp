<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dateUtil" uri="http://app.spring.web/dateUtil" %>
<%@ taglib prefix="pageUtil" uri="http://app.spring.web/pageUtil" %>

<!-- Include search table CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/search-table.css">

<!-- Messages for flash attributes -->
<c:if test="${not empty success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ${success}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${error}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

<!-- Customer Statistics Cards -->
<div class="row mb-4">
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-primary shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                            Active Customers</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800" id="activeCustomersCount">--</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-users fa-2x text-gray-300"></i>
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
                            Suspended</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800" id="suspendedCustomersCount">--</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-user-times fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-info shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                            New Today</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800" id="newCustomersToday">--</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-user-plus fa-2x text-gray-300"></i>
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
                            New This Month</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800" id="newCustomersThisMonth">--</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-calendar-plus fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Search and Pagination Table using generic taglib -->
<pageUtil:searchTable 
    id="customers"
    apiUrl="${pageContext.request.contextPath}/customers/api/list"
    searchUrl="${pageContext.request.contextPath}/customers/api/search"
    title="Customer Management"
    createUrl="${pageContext.request.contextPath}/customers/create"
    createLabel="Add New Customer"
    pageSize="10"
    sortBy="id"
    sortDir="asc">
    
    <!-- Table Headers -->
    <tr>
        <th data-sortable="true" data-column="id">ID</th>
        <th data-sortable="true" data-column="customer_code">Customer Code</th>
        <th data-sortable="true" data-column="company_name">Company Name</th>
        <th data-sortable="true" data-column="contact_person">Contact Person</th>
        <th data-sortable="true" data-column="email">Email</th>
        <th data-sortable="true" data-column="customer_type">Type</th>
        <th data-sortable="true" data-column="status">Status</th>
        <th data-sortable="true" data-column="customer_since">Customer Since</th>
        <th>Outstanding</th>
        <th>Actions</th>
    </tr>
    
</pageUtil:searchTable>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="deleteModalLabel">
                    <i class="fas fa-exclamation-triangle me-2"></i>Confirm Deactivation
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="text-center mb-3">
                    <i class="fas fa-user-times text-warning" style="font-size: 3rem;"></i>
                </div>
                <h6 class="text-center mb-3">Are you sure you want to deactivate this customer?</h6>
                <div class="alert alert-info">
                    <strong>Company:</strong> <span id="customerNameToDelete"></span><br>
                    <strong>Customer Code:</strong> <span id="customerCodeToDelete"></span><br>
                    <strong>Email:</strong> <span id="customerEmailToDelete"></span>
                </div>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <strong>Note:</strong> Customer will be deactivated but data will be preserved. You can reactivate the customer later.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="cancelDeleteBtn">
                    <i class="fas fa-times me-1"></i>Cancel
                </button>
                <button type="button" class="btn btn-warning" id="confirmDeleteBtn">
                    <i class="fas fa-user-times me-1"></i>Deactivate Customer
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Include search table JS after jQuery is loaded -->
<script src="${pageContext.request.contextPath}/js/search-table.js"></script>

<script>
// Load customer statistics
$(document).ready(function() {
    $.get('${pageContext.request.contextPath}/customers/api/stats')
        .done(function(data) {
            $('#activeCustomersCount').text(data.activeCustomers || 0);
            $('#suspendedCustomersCount').text(data.suspendedCustomers || 0);
            $('#newCustomersToday').text(data.newCustomersToday || 0);
            $('#newCustomersThisMonth').text(data.newCustomersThisMonth || 0);
        })
        .fail(function() {
            console.error('Failed to load customer statistics');
        });
});
</script>

<script src="${pageContext.request.contextPath}/js/customers/list-data.js"></script>
