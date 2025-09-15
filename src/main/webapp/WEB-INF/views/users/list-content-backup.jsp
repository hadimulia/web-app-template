<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<!-- Include search table CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/search-table.css">
<!-- Include search table JS after jQuery is loaded -->
<script src="${pageContext.request.contextPath}/js/search-table.js"></script>

<!-- Page Heading -->
<h1 class="mt-4">User Management</h1>
<ol class="breadcrumb mb-4">
    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
    <li class="breadcrumb-item active">Users</li>
</ol>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div></div>
    <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary">
        <i class="fas fa-plus fa-sm me-1"></i> Add New User
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

<!-- Simple User Table -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">User List</h6>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.id}</td>
                            <td><strong>${user.username}</strong></td>
                            <td>${user.fullName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty user.email}">
                                        <a href="mailto:${user.email}">${user.email}</a>
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${user.phone != null ? user.phone : '-'}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.status == 1}">
                                        <span class="badge badge-success">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="btn-group" role="group">
                                    <a href="${pageContext.request.contextPath}/users/view/${user.id}" 
                                       class="btn btn-info btn-sm" title="View">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/users/edit/${user.id}" 
                                       class="btn btn-warning btn-sm" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <button class="btn btn-danger btn-sm" 
                                            onclick="confirmDelete('${pageContext.request.contextPath}/users/delete/${user.id}', '${user.username}')" 
                                            title="Delete">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty users}">
                        <tr>
                            <td colspan="7" class="text-center text-muted">No users found</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
function confirmDelete(deleteUrl, username) {
    if (confirm('Are you sure you want to delete user "' + username + '"? This action cannot be undone.')) {
        window.location.href = deleteUrl;
    }
}
</script>