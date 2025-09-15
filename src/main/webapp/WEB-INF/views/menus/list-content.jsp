<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">Menu Management</h1>
    <a href="${pageContext.request.contextPath}/menus/create" class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm">
        <i class="fas fa-plus fa-sm text-white-50"></i> Add New Menu
    </a>
</div>

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

<!-- Simple Menu Table -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Menu List</h6>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Menu Name</th>
                        <th>Menu Code</th>
                        <th>Parent</th>
                        <th>URL</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th>Order</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="menuDisplay" items="${menus}">
                        <tr>
                            <td>${menuDisplay.menu.id}</td>
                            <td>
                                <%-- Add indentation based on level --%>
                                <c:forEach begin="1" end="${menuDisplay.level}">
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                </c:forEach>
                                <c:if test="${menuDisplay.level > 0}">
                                    <i class="fas fa-level-up-alt text-muted me-1" style="transform: rotate(90deg);"></i>
                                </c:if>
                                <i class="fas ${menuDisplay.menu.menuIcon != null ? menuDisplay.menu.menuIcon : 'fa-circle'} me-2"></i>
                                <strong>${menuDisplay.menu.menuName}</strong>
                            </td>
                            <td>
                                <span class="badge badge-secondary">${menuDisplay.menu.menuCode}</span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${menuDisplay.menu.parentId != null}">
                                        <small class="text-muted">Parent ID: ${menuDisplay.menu.parentId}</small>
                                    </c:when>
                                    <c:otherwise>
                                        <small class="text-muted">Root Menu</small>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty menuDisplay.menu.menuUrl}">
                                        <code>${menuDisplay.menu.menuUrl}</code>
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${menuDisplay.menu.menuType == 1}">
                                        <span class="badge badge-primary">Menu</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-info">Button</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${menuDisplay.menu.status == 1}">
                                        <span class="badge badge-success">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <span class="badge badge-light">${menuDisplay.menu.sortOrder != null ? menuDisplay.menu.sortOrder : 0}</span>
                            </td>
                            <td>
                                <div class="btn-group" role="group">
                                    <a href="${pageContext.request.contextPath}/menus/view/${menuDisplay.menu.id}" 
                                       class="btn btn-info btn-sm" title="View">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/menus/edit/${menuDisplay.menu.id}" 
                                       class="btn btn-warning btn-sm" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <button class="btn btn-danger btn-sm delete-btn" 
                                            data-menu-id="${menuDisplay.menu.id}"
                                            data-menu-name="${menuDisplay.menu.menuName}"
                                            title="Delete">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty menus}">
                        <tr>
                            <td colspan="9" class="text-center text-muted">No menus found</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Handle delete buttons
    document.querySelectorAll('.delete-btn').forEach(function(btn) {
        btn.addEventListener('click', function() {
            const menuId = this.getAttribute('data-menu-id');
            const menuName = this.getAttribute('data-menu-name');
            
            if (confirm('Are you sure you want to delete menu "' + menuName + '"? This action cannot be undone.')) {
                window.location.href = '${pageContext.request.contextPath}/menus/delete/' + menuId;
            }
        });
    });
});
</script>