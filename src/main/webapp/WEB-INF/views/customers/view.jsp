<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">Customer Details</h1>
    <div>
        <a href="${pageContext.request.contextPath}/customers" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
            <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Customers
        </a>
        <a href="${pageContext.request.contextPath}/customers/edit/${customer.id}" class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm">
            <i class="fas fa-edit fa-sm text-white-50"></i> Edit Customer
        </a>
    </div>
</div>

<!-- Customer Information Card with basic details -->
<div class="row">
    <div class="col-lg-8">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Customer Information</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>Customer Code:</strong> <code>${customer.customerCode}</code></p>
                        <p><strong>Company Name:</strong> ${customer.companyName != null ? customer.companyName : '-'}</p>
                        <p><strong>Contact Person:</strong> ${customer.contactPerson != null ? customer.contactPerson : '-'}</p>
                        <p><strong>Email:</strong> ${customer.email}</p>
                        <p><strong>Phone:</strong> ${customer.phone != null ? customer.phone : '-'}</p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Customer Type:</strong> 
                            <span class="badge badge-secondary">
                                ${customer.customerType == 'CORPORATE' ? 'Corporate' : 'Individual'}
                            </span>
                        </p>
                        <p><strong>Status:</strong> 
                            <c:choose>
                                <c:when test="${customer.status == 1}">
                                    <span class="badge badge-success">Active</span>
                                </c:when>
                                <c:when test="${customer.status == 2}">
                                    <span class="badge badge-warning">Suspended</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-danger">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <p><strong>Customer Since:</strong> 
                            <c:if test="${not empty customer.customerSince}">
                                <fmt:formatDate value="${customer.customerSince}" pattern="yyyy-MM-dd"/>
                            </c:if>
                        </p>
                        <p><strong>Credit Limit:</strong> 
                            <c:if test="${not empty customer.creditLimit}">
                                <fmt:formatNumber value="${customer.creditLimit}" type="currency" currencySymbol="IDR "/>
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
                <h6 class="m-0 font-weight-bold text-primary">Quick Actions</h6>
            </div>
            <div class="card-body">
                <a href="${pageContext.request.contextPath}/customers/edit/${customer.id}" class="btn btn-primary btn-sm btn-block mb-2">
                    <i class="fas fa-edit"></i> Edit Customer
                </a>
                <c:choose>
                    <c:when test="${customer.status == 1}">
                        <form method="POST" action="${pageContext.request.contextPath}/customers/suspend/${customer.id}" style="margin: 0;">
                            <button type="submit" class="btn btn-warning btn-sm btn-block" onclick="return confirm('Suspend this customer?')">
                                <i class="fas fa-pause"></i> Suspend Customer
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form method="POST" action="${pageContext.request.contextPath}/customers/activate/${customer.id}" style="margin: 0;">
                            <button type="submit" class="btn btn-success btn-sm btn-block" onclick="return confirm('Activate this customer?')">
                                <i class="fas fa-play"></i> Activate Customer
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
