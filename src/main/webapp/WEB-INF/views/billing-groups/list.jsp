<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dateUtil" uri="http://app.spring.web/dateUtil" %>
<%@ taglib prefix="pageUtil" uri="http://app.spring.web/pageUtil" %>

<!-- Include search table CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/search-table.css">

<!-- Messages -->
<c:if test="${not empty success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ${success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<!-- Search and Pagination Table using generic taglib -->
<pageUtil:searchTable 
    id="billing-groups"
    apiUrl="${pageContext.request.contextPath}/billing-groups/api/list"
    searchUrl="${pageContext.request.contextPath}/billing-groups/api/search"
    title="Billing Group Management"
    createUrl="${pageContext.request.contextPath}/billing-groups/create"
    createLabel="Add New Billing Group"
    pageSize="10"
    sortBy="id"
    sortDir="asc">
    
    <!-- Table Headers -->
    <tr>
        <th data-sortable="true" data-column="id">ID</th>
        <th data-sortable="true" data-column="group_code">Group Code</th>
        <th data-sortable="true" data-column="group_name">Group Name</th>
        <th data-sortable="true" data-column="base_price">Base Price</th>
        <th data-sortable="true" data-column="currency">Currency</th>
        <th data-sortable="true" data-column="billing_cycle">Billing Cycle</th>
        <th data-sortable="true" data-column="auto_generate">Auto Generate</th>
        <th data-sortable="true" data-column="status">Status</th>
        <th data-sortable="true" data-column="created_at">Created At</th>
        <th>Actions</th>
    </tr>
    
</pageUtil:searchTable>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="deleteModalLabel">
                    <i class="fas fa-exclamation-triangle me-2"></i>Confirm Deactivate
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="text-center mb-3">
                    <i class="fas fa-layer-group text-danger" style="font-size: 3rem;"></i>
                </div>
                <h6 class="text-center mb-3">Are you sure you want to deactivate this billing group?</h6>
                <div class="alert alert-info">
                    <strong>Group ID:</strong> <span id="groupIdToDelete"></span><br>
                    <strong>Group Name:</strong> <span id="groupNameToDelete"></span><br>
                    <strong>Group Code:</strong> <span id="groupCodeToDelete"></span><br>
                </div>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <strong>Warning:</strong> This will deactivate the billing group and may affect customer billing.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="cancelDeleteBtn">
                    <i class="fas fa-times me-1"></i>Cancel
                </button>
                <button type="button" class="btn btn-danger" id="confirmDeleteBtn">
                    <i class="fas fa-ban me-1"></i>Deactivate
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Statistics Modal -->
<div class="modal fade" id="statsModal" tabindex="-1" role="dialog" aria-labelledby="statsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="statsModalLabel">
                    <i class="fas fa-chart-bar me-2"></i>Billing Group Statistics
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div id="statsContent">
                    <div class="text-center">
                        <div class="spinner-border text-primary" role="status">
                            <span class="sr-only">Loading...</span>
                        </div>
                        <p class="mt-2">Loading statistics...</p>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/search-table.js"></script>
<script src="${pageContext.request.contextPath}/js/billing/list-data.js"></script>
<script>


function showStatistics() {
    $('#statsModal').modal('show');
    
    const contextPath = '${pageContext.request.contextPath}';
    
    // Load statistics
    $.ajax({
        url: contextPath + '/billing-groups/api/stats',
        type: 'GET',
        success: function(data) {
            const content = `
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <div class="card text-white bg-primary">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h4 class="mb-0">\${data.activeBillingGroups || 0}</h4>
                                        <p class="mb-0">Active Billing Groups</p>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-layer-group fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <div class="card text-white bg-success">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h4 class="mb-0">\${data.autoGenerateGroups || 0}</h4>
                                        <p class="mb-0">Auto-Generate Groups</p>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-magic fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <div class="card text-white bg-info">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h4 class="mb-0">\${data.activeAssignments || 0}</h4>
                                        <p class="mb-0">Active Assignments</p>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-link fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <div class="card text-white bg-warning">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h4 class="mb-0">\${data.customersWithAssignments || 0}</h4>
                                        <p class="mb-0">Customers Assigned</p>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-users fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            $('#statsContent').html(content);
        },
        error: function() {
            $('#statsContent').html('<div class="alert alert-danger">Failed to load statistics</div>');
        }
    });
}
</script>
