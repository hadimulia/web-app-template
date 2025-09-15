<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dateUtil" uri="http://app.spring.web/dateUtil" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">Role Details</h1>
    <div>
        <a href="${pageContext.request.contextPath}/roles/edit/${role.id}" class="btn btn-warning btn-sm">
            <i class="fas fa-edit"></i> Edit Role
        </a>
        <a href="${pageContext.request.contextPath}/roles" class="btn btn-secondary btn-sm">
            <i class="fas fa-arrow-left"></i> Back to Roles
        </a>
    </div>
</div>

<!-- Role Information -->
<div class="row">
    <div class="col-lg-8">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Role Information</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>ID:</strong> ${role.id}</p>
                        <p><strong>Role Name:</strong> ${role.roleName}</p>
                        <p><strong>Role Code:</strong> <code>${role.roleCode}</code></p>
                        <p><strong>Description:</strong> <c:out value="${role.description}" default="-"/></p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Status:</strong> 
                            <c:choose>
                                <c:when test="${role.status == 1}">
                                    <span class="badge badge-success">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-danger">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>Created At:</strong> 
                            <c:if test="${not empty role.createdAt}">
                                ${dateUtil:formatLocalDateTime(role.createdAt, 'yyyy-MM-dd HH:mm:ss')}
                            </c:if>
                        </p>
                        <p><strong>Updated At:</strong> 
                            <c:if test="${not empty role.updatedAt}">
                                ${dateUtil:formatLocalDateTime(role.updatedAt, 'yyyy-MM-dd HH:mm:ss')}
                            </c:if>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div class="col-lg-4">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Assigned Menus</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty role.menus}">
                        <c:forEach var="menu" items="${role.menus}">
                            <div class="mb-2">
                                <i class="fas ${menu.menuIcon}"></i> ${menu.menuName}
                                <c:if test="${not empty menu.menuUrl}">
                                    <small class="text-muted d-block">${menu.menuUrl}</small>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">No menus assigned</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>