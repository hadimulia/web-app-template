<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dateUtil" uri="http://app.spring.web/dateUtil" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">Menu Details</h1>
    <div>
        <a href="${pageContext.request.contextPath}/menus/edit/${menu.id}" class="btn btn-warning btn-sm">
            <i class="fas fa-edit"></i> Edit Menu
        </a>
        <a href="${pageContext.request.contextPath}/menus" class="btn btn-secondary btn-sm">
            <i class="fas fa-arrow-left"></i> Back to Menus
        </a>
    </div>
</div>

<!-- Menu Information -->
<div class="row">
    <div class="col-lg-8">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Menu Information</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>ID:</strong> ${menu.id}</p>
                        <p><strong>Menu Name:</strong> ${menu.menuName}</p>
                        <p><strong>Menu Code:</strong> <code>${menu.menuCode}</code></p>
                        <p><strong>Menu URL:</strong> ${menu.menuUrl != null ? menu.menuUrl : '-'}</p>
                        <p><strong>Menu Icon:</strong> <i class="fas ${menu.menuIcon}"></i> ${menu.menuIcon}</p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Menu Type:</strong> 
                            <span class="badge badge-${menu.menuType == 'MENU' ? 'primary' : 'secondary'}">
                                ${menu.menuType}
                            </span>
                        </p>
                        <p><strong>Status:</strong> 
                            <c:choose>
                                <c:when test="${menu.status == 1}">
                                    <span class="badge badge-success">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-danger">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>Sort Order:</strong> ${menu.sortOrder}</p>
                        <p><strong>Created At:</strong> 
                            <c:if test="${not empty menu.createdAt}">
                                ${dateUtil:formatLocalDateTime(menu.createdAt, 'yyyy-MM-dd HH:mm:ss')}
                            </c:if>
                        </p>
                    </div>
                </div>
                <c:if test="${not empty menu.description}">
                    <div class="row">
                        <div class="col-12">
                            <p><strong>Description:</strong> ${menu.description}</p>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
    
    <div class="col-lg-4">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Quick Actions</h6>
            </div>
            <div class="card-body">
                <a href="${pageContext.request.contextPath}/menus/edit/${menu.id}" class="btn btn-primary btn-sm btn-block mb-2">
                    <i class="fas fa-edit"></i> Edit Menu
                </a>
                <a href="${pageContext.request.contextPath}/menus/delete/${menu.id}" class="btn btn-danger btn-sm btn-block" 
                   onclick="return confirm('Are you sure you want to delete this menu?')">
                    <i class="fas fa-trash"></i> Delete Menu
                </a>
            </div>
        </div>
    </div>
</div>