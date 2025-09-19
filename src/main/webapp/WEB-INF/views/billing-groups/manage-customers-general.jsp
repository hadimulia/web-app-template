<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container-fluid">
    <!-- Page Heading -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">
            <i class="fas fa-users-cog me-2"></i>${pageTitle}
        </h1>
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="/dashboard">Dashboard</a></li>
                <li class="breadcrumb-item"><a href="/billing-groups">Billing Groups</a></li>
                <li class="breadcrumb-item active" aria-current="page">Manage Customers</li>
            </ol>
        </nav>
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

    <!-- Instructions Card -->
    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-info-circle me-2"></i>Billing Group Customer Management
            </h6>
        </div>
        <div class="card-body">
            <p class="mb-3">
                <strong>Welcome to the Billing Group Customer Management center.</strong> 
                Here you can manage customer assignments across all billing groups. 
                Select a billing group below to view and manage its customer assignments.
            </p>
            
            <div class="row">
                <div class="col-md-6">
                    <h6 class="text-primary mb-2">
                        <i class="fas fa-tasks me-1"></i>Available Actions:
                    </h6>
                    <ul class="list-unstyled ms-3">
                        <li><i class="fas fa-check text-success me-2"></i>View customer assignments per billing group</li>
                        <li><i class="fas fa-check text-success me-2"></i>Assign new customers to billing groups</li>
                        <li><i class="fas fa-check text-success me-2"></i>Update existing assignments</li>
                        <li><i class="fas fa-check text-success me-2"></i>Remove customer assignments</li>
                        <li><i class="fas fa-check text-success me-2"></i>Set custom pricing and discounts</li>
                    </ul>
                </div>
                <div class="col-md-6">
                    <h6 class="text-info mb-2">
                        <i class="fas fa-lightbulb me-1"></i>Quick Stats:
                    </h6>
                    <div class="row">
                        <div class="col-6">
                            <div class="text-center">
                                <div class="h4 text-primary mb-0">${billingGroups.size()}</div>
                                <small class="text-muted">Active Groups</small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="text-center">
                                <div class="h4 text-success mb-0">${customers.size()}</div>
                                <small class="text-muted">Active Customers</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Billing Groups Selection -->
    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-layer-group me-2"></i>Select Billing Group to Manage
            </h6>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${empty billingGroups}">
                    <div class="text-center py-4">
                        <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                        <h5 class="text-muted">No Active Billing Groups Found</h5>
                        <p class="text-muted mb-3">
                            You need to create at least one active billing group before you can manage customer assignments.
                        </p>
                        <a href="/billing-groups/create" class="btn btn-primary">
                            <i class="fas fa-plus me-1"></i>Create Billing Group
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <c:forEach items="${billingGroups}" var="group" varStatus="status">
                            <div class="col-md-6 col-lg-4 mb-4">
                                <div class="card border-left-primary h-100">
                                    <div class="card-body">
                                        <div class="d-flex align-items-center justify-content-between">
                                            <div class="flex-grow-1">
                                                <h6 class="text-primary font-weight-bold mb-1">${group.groupName}</h6>
                                                <small class="text-muted d-block mb-2">
                                                    <code>${group.groupCode}</code>
                                                </small>
                                                <div class="small text-muted mb-2">
                                                    <i class="fas fa-money-bill-wave me-1"></i>
                                                    <fmt:formatNumber value="${group.basePrice}" type="currency" currencyCode="${group.currency}"/>
                                                </div>
                                                <div class="small text-muted mb-3">
                                                    <i class="fas fa-calendar me-1"></i>
                                                    ${group.billingCycle}
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="d-grid gap-2">
                                            <a href="/billing-groups/customers/${group.id}" 
                                               class="btn btn-primary btn-sm">
                                                <i class="fas fa-users me-1"></i>Manage Customers
                                            </a>
                                            <div class="btn-group btn-group-sm">
                                                <a href="/billing-groups/view/${group.id}" 
                                                   class="btn btn-outline-info">
                                                    <i class="fas fa-eye me-1"></i>View
                                                </a>
                                                <a href="/billing-groups/edit/${group.id}" 
                                                   class="btn btn-outline-secondary">
                                                    <i class="fas fa-edit me-1"></i>Edit
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row">
        <div class="col-md-6">
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-success">
                        <i class="fas fa-plus me-2"></i>Quick Actions
                    </h6>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="/billing-groups/create" class="btn btn-success">
                            <i class="fas fa-layer-group me-1"></i>Create New Billing Group
                        </a>
                        <a href="/customers/create" class="btn btn-info">
                            <i class="fas fa-user-plus me-1"></i>Add New Customer
                        </a>
                        <a href="/billing-groups" class="btn btn-outline-primary">
                            <i class="fas fa-list me-1"></i>View All Billing Groups
                        </a>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6">
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-info">
                        <i class="fas fa-chart-bar me-2"></i>Recent Activity
                    </h6>
                </div>
                <div class="card-body">
                    <div class="text-center py-3">
                        <i class="fas fa-chart-line fa-2x text-muted mb-2"></i>
                        <p class="text-muted mb-2">Activity tracking coming soon</p>
                        <small class="text-muted">
                            Recent customer assignments and modifications will be displayed here.
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
