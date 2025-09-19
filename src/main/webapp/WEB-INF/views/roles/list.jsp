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

<!-- Search and Pagination Table using generic taglib -->
<pageUtil:searchTable 
    id="roles"
    apiUrl="${pageContext.request.contextPath}/roles/api/list"
    searchUrl="${pageContext.request.contextPath}/roles/api/search"
    title="Role Management"
    createUrl="${pageContext.request.contextPath}/roles/create"
    createLabel="Add New Role"
    pageSize="10"
    sortBy="id"
    sortDir="asc">
    
    <!-- Table Headers -->
    <tr>
        <th data-sortable="true" data-column="id">ID</th>
        <th data-sortable="true" data-column="role_name">Role Name</th>
        <th data-sortable="true" data-column="role_code">Role Code</th>
        <th data-sortable="true" data-column="description">Description</th>
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
                    <i class="fas fa-exclamation-triangle me-2"></i>Confirm Delete
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="text-center mb-3">
                    <i class="fas fa-trash-alt text-danger" style="font-size: 3rem;"></i>
                </div>
                <h6 class="text-center mb-3">Are you sure you want to delete this role?</h6>
                <div class="alert alert-info">
                    <strong>Role Name:</strong> <span id="roleNameToDelete"></span><br>
                    <strong>Role ID:</strong> <span id="roleIdToDelete"></span>
                </div>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <strong>Warning:</strong> This action cannot be undone. All users assigned to this role will lose their permissions.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="cancelDeleteBtn">
                    <i class="fas fa-times me-1"></i>Cancel
                </button>
                <button type="button" class="btn btn-danger" id="confirmDeleteBtn">
                    <i class="fas fa-trash me-1"></i>Delete Role
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Include search table JS after jQuery is loaded -->
<script src="${pageContext.request.contextPath}/js/search-table.js"></script>
<script src="${pageContext.request.contextPath}/js/roles/list-data.js"></script>