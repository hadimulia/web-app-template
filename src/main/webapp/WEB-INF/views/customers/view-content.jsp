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

<!-- Messages -->
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

<div class="row">
    <!-- Customer Information -->
    <div class="col-lg-8">
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Customer Information</h6>
                <div class="dropdown no-arrow">
                    <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink"
                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right shadow animated--fade-in"
                        aria-labelledby="dropdownMenuLink">
                        <div class="dropdown-header">Actions:</div>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/customers/edit/${customer.id}">
                            <i class="fas fa-edit fa-sm fa-fw mr-2 text-gray-400"></i>
                            Edit Customer
                        </a>
                        <div class="dropdown-divider"></div>
                        <c:choose>
                            <c:when test="${customer.status == 1}">
                                <form method="POST" action="${pageContext.request.contextPath}/customers/suspend/${customer.id}" style="margin: 0;">
                                    <button type="submit" class="dropdown-item" onclick="return confirm('Are you sure you want to suspend this customer?')">
                                        <i class="fas fa-pause fa-sm fa-fw mr-2 text-gray-400"></i>
                                        Suspend Customer
                                    </button>
                                </form>
                            </c:when>
                            <c:when test="${customer.status == 0 || customer.status == 2}">
                                <form method="POST" action="${pageContext.request.contextPath}/customers/activate/${customer.id}" style="margin: 0;">
                                    <button type="submit" class="dropdown-item" onclick="return confirm('Are you sure you want to activate this customer?')">
                                        <i class="fas fa-play fa-sm fa-fw mr-2 text-gray-400"></i>
                                        Activate Customer
                                    </button>
                                </form>
                            </c:when>
                        </c:choose>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <table class="table table-borderless">
                            <tr>
                                <th width="40%">Customer Code:</th>
                                <td><code>${customer.customerCode}</code></td>
                            </tr>
                            <tr>
                                <th>Customer Type:</th>
                                <td>
                                    <span class="badge badge-secondary">
                                        ${customer.customerType == 'CORPORATE' ? 'Corporate' : 'Individual'}
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <th>Company Name:</th>
                                <td>${customer.companyName != null ? customer.companyName : '-'}</td>
                            </tr>
                            <tr>
                                <th>Contact Person:</th>
                                <td>${customer.contactPerson != null ? customer.contactPerson : '-'}</td>
                            </tr>
                            <tr>
                                <th>Email:</th>
                                <td><a href="mailto:${customer.email}">${customer.email}</a></td>
                            </tr>
                            <tr>
                                <th>Phone:</th>
                                <td>${customer.phone != null ? customer.phone : '-'}</td>
                            </tr>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <table class="table table-borderless">
                            <tr>
                                <th width="40%">Status:</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${customer.status == 1}">
                                            <span class="badge badge-success">Active</span>
                                        </c:when>
                                        <c:when test="${customer.status == 2}">
                                            <span class="badge badge-warning">Suspended</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-secondary">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <th>Customer Since:</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${customer.customerSince != null}">
                                            <fmt:formatDate value="${customer.customerSince}" pattern="dd MMM yyyy"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <th>Credit Limit:</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${customer.creditLimit != null && customer.creditLimit > 0}">
                                            IDR <fmt:formatNumber value="${customer.creditLimit}" pattern="#,##0"/>
                                        </c:when>
                                        <c:otherwise>No Limit</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <th>Payment Terms:</th>
                                <td>${customer.paymentTerms != null ? customer.paymentTerms : 30} Days</td>
                            </tr>
                            <tr>
                                <th>Tax ID:</th>
                                <td>${customer.taxId != null ? customer.taxId : '-'}</td>
                            </tr>
                            <tr>
                                <th>Created:</th>
                                <td>
                                    <c:if test="${customer.createdAt != null}">
                                        <fmt:formatDate value="${customer.createdAt}" pattern="dd MMM yyyy HH:mm"/>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                
                <c:if test="${customer.address != null || customer.city != null || customer.state != null}">
                    <hr>
                    <h6 class="text-primary">Address Information</h6>
                    <p class="text-gray-800">
                        <c:if test="${customer.address != null}">
                            ${customer.address}<br>
                        </c:if>
                        <c:if test="${customer.city != null}">
                            ${customer.city}
                        </c:if>
                        <c:if test="${customer.state != null}">
                            , ${customer.state}
                        </c:if>
                        <c:if test="${customer.postalCode != null}">
                            ${customer.postalCode}
                        </c:if>
                        <c:if test="${customer.country != null && customer.country != 'Indonesia'}">
                            <br>${customer.country}
                        </c:if>
                    </p>
                </c:if>
                
                <c:if test="${customer.notes != null}">
                    <hr>
                    <h6 class="text-primary">Notes</h6>
                    <p class="text-gray-800">${customer.notes}</p>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Statistics and Quick Actions -->
    <div class="col-lg-4">
        <!-- Financial Summary -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Financial Summary</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-12">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-sm">Total Outstanding:</span>
                            <span class="font-weight-bold ${customer.totalOutstanding != null && customer.totalOutstanding > 0 ? 'text-danger' : 'text-success'}">
                                IDR <fmt:formatNumber value="${customer.totalOutstanding != null ? customer.totalOutstanding : 0}" pattern="#,##0"/>
                            </span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-sm">Overdue Invoices:</span>
                            <span class="font-weight-bold ${customer.overdueInvoicesCount != null && customer.overdueInvoicesCount > 0 ? 'text-danger' : 'text-success'}">
                                ${customer.overdueInvoicesCount != null ? customer.overdueInvoicesCount : 0}
                            </span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-sm">Total Payments:</span>
                            <span class="text-success font-weight-bold">
                                IDR <fmt:formatNumber value="${customer.totalPayments != null ? customer.totalPayments : 0}" pattern="#,##0"/>
                            </span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="text-sm">Total Invoiced:</span>
                            <span class="font-weight-bold">
                                IDR <fmt:formatNumber value="${customer.totalInvoiced != null ? customer.totalInvoiced : 0}" pattern="#,##0"/>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Quick Actions</h6>
            </div>
            <div class="card-body">
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/invoices/create?customerId=${customer.id}" class="btn btn-primary btn-sm">
                        <i class="fas fa-file-invoice"></i> Create Invoice
                    </a>
                    <a href="${pageContext.request.contextPath}/invoices?customerId=${customer.id}" class="btn btn-info btn-sm">
                        <i class="fas fa-list"></i> View Invoices
                    </a>
                    <a href="${pageContext.request.contextPath}/payments?customerId=${customer.id}" class="btn btn-success btn-sm">
                        <i class="fas fa-money-bill-wave"></i> View Payments
                    </a>
                    <a href="${pageContext.request.contextPath}/customers/billing-groups/${customer.id}" class="btn btn-warning btn-sm">
                        <i class="fas fa-tags"></i> Manage Billing Groups
                    </a>
                </div>
            </div>
        </div>

        <!-- Recent Activity -->
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Recent Activity</h6>
            </div>
            <div class="card-body">
                <div class="small text-gray-500">
                    <p class="mb-2">
                        <i class="fas fa-user-edit text-primary"></i>
                        Customer record updated
                        <c:if test="${customer.updatedAt != null}">
                            <br><small class="text-muted">
                                <fmt:formatDate value="${customer.updatedAt}" pattern="dd MMM yyyy HH:mm"/>
                            </small>
                        </c:if>
                    </p>
                    <p class="mb-2">
                        <i class="fas fa-user-plus text-success"></i>
                        Customer created
                        <c:if test="${customer.createdAt != null}">
                            <br><small class="text-muted">
                                <fmt:formatDate value="${customer.createdAt}" pattern="dd MMM yyyy HH:mm"/>
                            </small>
                        </c:if>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
