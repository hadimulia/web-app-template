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
                        <p><strong>Menu Name:</strong> 
                            <i class="fas ${menu.menuIcon}"></i> ${menu.menuName}
                        </p>
                        <p><strong>Menu Code:</strong> <code>${menu.menuCode}</code></p>
                        <p><strong>Menu URL:</strong> 
                            <c:choose>
                                <c:when test="${not empty menu.menuUrl}">
                                    <code>${menu.menuUrl}</code>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>Menu Icon:</strong> 
                            <c:if test="${not empty menu.menuIcon}">
                                <i class="fas ${menu.menuIcon}"></i> <code>${menu.menuIcon}</code>
                            </c:if>
                        </p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Parent Menu:</strong> 
                            <c:out value="${menu.parentName}" default="Root Menu"/>
                        </p>
                        <p><strong>Sort Order:</strong> ${menu.sortOrder}</p>
                        <p><strong>Menu Type:</strong> 
                            <c:choose>
                                <c:when test="${menu.menuType == 1}">
                                    <span class="badge badge-primary">Menu</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-info">Button</span>
                                </c:otherwise>
                            </c:choose>
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
                        <p><strong>Created At:</strong> 
                            <c:if test="${not empty menu.createdAt}">
                                ${dateUtil:formatLocalDateTime(menu.createdAt, 'yyyy-MM-dd HH:mm:ss')}
                            </c:if>
                        </p>
                        <p><strong>Updated At:</strong> 
                            <c:if test="${not empty menu.updatedAt}">
                                ${dateUtil:formatLocalDateTime(menu.updatedAt, 'yyyy-MM-dd HH:mm:ss')}
                            </c:if>
                        </p>
                    </div>
                </div>
                
                <c:if test="${not empty menu.description}">
                    <hr>
                    <p><strong>Description:</strong></p>
                    <p class="text-muted">${menu.description}</p>
                </c:if>
            </div>
        </div>
    </div>
    
    <div class="col-lg-4">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Child Menus</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty menu.children}">
                        <c:forEach var="child" items="${menu.children}">
                            <div class="mb-2">
                                <i class="fas ${child.menuIcon}"></i> ${child.menuName}
                                <c:if test="${not empty child.menuUrl}">
                                    <small class="text-muted d-block">${child.menuUrl}</small>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">No child menus</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>