<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dateUtil" uri="http://app.spring.web/dateUtil" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">User Details</h1>
    <div>
        <a href="${pageContext.request.contextPath}/users/edit/${user.id}" class="btn btn-warning btn-sm">
            <i class="fas fa-edit"></i> Edit User
        </a>
        <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary btn-sm">
            <i class="fas fa-arrow-left"></i> Back to Users
        </a>
    </div>
</div>

<!-- User Information -->
<div class="row">
    <div class="col-lg-8">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">User Information</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>ID:</strong> ${user.id}</p>
                        <p><strong>Username:</strong> ${user.username}</p>
                        <p><strong>Full Name:</strong> <c:out value="${user.fullName}" default="-"/></p>
                        <p><strong>Email:</strong> <c:out value="${user.email}" default="-"/></p>
                        <p><strong>Phone:</strong> <c:out value="${user.phone}" default="-"/></p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Status:</strong> 
                            <c:choose>
                                <c:when test="${user.status == 1}">
                                    <span class="badge badge-success">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-danger">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>Last Login:</strong> 
                            <c:choose>
                                <c:when test="${not empty user.lastLogin}">
                                    ${dateUtil:formatLocalDateTime(user.lastLogin, 'yyyy-MM-dd HH:mm:ss')}
                                </c:when>
                                <c:otherwise>Never</c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>Created At:</strong> 
                            <c:if test="${not empty user.createdAt}">
                                ${dateUtil:formatLocalDateTime(user.createdAt, 'yyyy-MM-dd HH:mm:ss')}
                            </c:if>
                        </p>
                        <p><strong>Updated At:</strong> 
                            <c:if test="${not empty user.updatedAt}">
                                ${dateUtil:formatLocalDateTime(user.updatedAt, 'yyyy-MM-dd HH:mm:ss')}
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
                <h6 class="m-0 font-weight-bold text-primary">Assigned Roles</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty user.roles}">
                        <c:forEach var="role" items="${user.roles}">
                            <span class="badge badge-primary mb-2">${role.roleName}</span><br>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">No roles assigned</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>