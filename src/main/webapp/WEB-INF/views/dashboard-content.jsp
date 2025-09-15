<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Heading -->
<h1 class="mt-4">Dashboard</h1>
<ol class="breadcrumb mb-4">
    <li class="breadcrumb-item active">Dashboard</li>
</ol>

<!-- Content Row -->
<div class="row">

    <!-- Users Card -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-start border-primary shadow h-100 py-2">
            <div class="card-body">
                <div class="row g-0 align-items-center">
                    <div class="col me-2">
                        <div class="text-xs fw-bold text-primary text-uppercase mb-1">
                            Total Users</div>
                        <div class="h5 mb-0 fw-bold text-gray-800">0</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-users fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Roles Card -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-start border-success shadow h-100 py-2">
            <div class="card-body">
                <div class="row g-0 align-items-center">
                    <div class="col me-2">
                        <div class="text-xs fw-bold text-success text-uppercase mb-1">
                            Total Roles</div>
                        <div class="h5 mb-0 fw-bold text-gray-800">0</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-user-shield fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Menus Card -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-start border-info shadow h-100 py-2">
            <div class="card-body">
                <div class="row g-0 align-items-center">
                    <div class="col me-2">
                        <div class="text-xs fw-bold text-info text-uppercase mb-1">
                            Total Menus</div>
                        <div class="h5 mb-0 fw-bold text-gray-800">0</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-list fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Active Sessions Card -->
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-start border-warning shadow h-100 py-2">
            <div class="card-body">
                <div class="row g-0 align-items-center">
                    <div class="col me-2">
                        <div class="text-xs fw-bold text-warning text-uppercase mb-1">
                            Active Sessions</div>
                        <div class="h5 mb-0 fw-bold text-gray-800">1</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-globe fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<!-- Content Row -->
<div class="row">

    <!-- Welcome Message -->
    <div class="col-lg-12 mb-4">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 fw-bold text-primary">Welcome to Admin Panel</h6>
            </div>
            <div class="card-body">
                <p>Welcome, <strong><c:out value="${fullName}" default="${username}"/>!</strong></p>
                <p>You are successfully logged in. Use the sidebar to navigate through the system management features:</p>
                <ul>
                    <li><strong>User Management:</strong> Create, view, edit, and delete users</li>
                    <li><strong>Role Management:</strong> Manage user roles and permissions</li>
                    <li><strong>Menu Management:</strong> Configure dynamic menu structure</li>
                </ul>
                <p>All menu items are dynamically loaded from the database and cached in your session for optimal performance.</p>
            </div>
        </div>
    </div>

</div>

<!-- Quick Actions -->
<div class="row">
    <div class="col-lg-12">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Quick Actions</h6>
            </div>
            <div class="card-body">
                <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary btn-icon-split">
                    <span class="icon text-white-50">
                        <i class="fas fa-user-plus"></i>
                    </span>
                    <span class="text">Add New User</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/roles/create" class="btn btn-success btn-icon-split">
                    <span class="icon text-white-50">
                        <i class="fas fa-shield-alt"></i>
                    </span>
                    <span class="text">Add New Role</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/menus/create" class="btn btn-info btn-icon-split">
                    <span class="icon text-white-50">
                        <i class="fas fa-plus"></i>
                    </span>
                    <span class="text">Add New Menu</span>
                </a>
            </div>
        </div>
    </div>
</div>