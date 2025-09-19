<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dateUtil" uri="http://app.spring.web/dateUtil" %>

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

<!-- Billing Group Details Card -->
<div class="card shadow mb-4">
    <div class="card-header py-3 d-flex justify-content-between align-items-center">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-layer-group me-2"></i>Billing Group Details
        </h6>
        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/billing-groups/edit/${billingGroup.id}" class="btn btn-primary btn-sm">
                <i class="fas fa-edit me-1"></i>Edit
            </a>
            <a href="${pageContext.request.contextPath}/billing-groups/customers/${billingGroup.id}" class="btn btn-success btn-sm">
                <i class="fas fa-users me-1"></i>Manage Customers
            </a>
            <c:if test="${billingGroup.status == 1}">
                <form action="${pageContext.request.contextPath}/billing-groups/deactivate/${billingGroup.id}" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-warning btn-sm" onclick="return confirm('Are you sure you want to deactivate this billing group?')">
                        <i class="fas fa-ban me-1"></i>Deactivate
                    </button>
                </form>
            </c:if>
            <c:if test="${billingGroup.status == 0}">
                <form action="${pageContext.request.contextPath}/billing-groups/activate/${billingGroup.id}" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Are you sure you want to activate this billing group?')">
                        <i class="fas fa-check me-1"></i>Activate
                    </button>
                </form>
            </c:if>
        </div>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-8">
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Group Code:</strong></div>
                    <div class="col-sm-9"><code class="bg-light p-1 rounded">${billingGroup.groupCode}</code></div>
                </div>
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Group Name:</strong></div>
                    <div class="col-sm-9">${billingGroup.groupName}</div>
                </div>
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Base Price:</strong></div>
                    <div class="col-sm-9">
                        <span class="fs-5 text-success">
                            <fmt:formatNumber value="${billingGroup.basePrice}" type="currency" currencyCode="${billingGroup.currency}"/>
                        </span>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Currency:</strong></div>
                    <div class="col-sm-9"><span class="badge bg-info">${billingGroup.currency}</span></div>
                </div>
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Billing Cycle:</strong></div>
                    <div class="col-sm-9"><span class="badge bg-primary">${billingGroup.billingCycle}</span></div>
                </div>
                <c:if test="${not empty billingGroup.description}">
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Description:</strong></div>
                    <div class="col-sm-9">${billingGroup.description}</div>
                </div>
                </c:if>
                <c:if test="${not empty billingGroup.notes}">
                <div class="row mb-3">
                    <div class="col-sm-3"><strong>Notes:</strong></div>
                    <div class="col-sm-9">
                        <div class="alert alert-light border-start border-4 border-info">
                            ${billingGroup.notes}
                        </div>
                    </div>
                </div>
                </c:if>
            </div>
            <div class="col-md-4">
                <div class="card bg-light">
                    <div class="card-header">
                        <h6 class="mb-0">Status & Settings</h6>
                    </div>
                    <div class="card-body">
                        <div class="mb-2">
                            <strong>Status:</strong>
                            <c:choose>
                                <c:when test="${billingGroup.status == 1}">
                                    <span class="badge bg-success ms-1">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger ms-1">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="mb-2">
                            <strong>Auto Generate:</strong>
                            <c:choose>
                                <c:when test="${billingGroup.autoGenerate == true}">
                                    <span class="badge bg-success ms-1"><i class="fas fa-magic me-1"></i>Enabled</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary ms-1">Disabled</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="mb-2">
                            <strong>Created:</strong><br>
                            <small class="text-muted">
                                ${dateUtil:formatLocalDateTime(billingGroup.createdAt, 'dd MMM yyyy HH:mm')}
                            </small>
                        </div>
                        <div class="mb-2">
                            <strong>Last Updated:</strong><br>
                            <small class="text-muted">
                                ${dateUtil:formatLocalDateTime(billingGroup.updatedAt, 'dd MMM yyyy HH:mm')}
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Customer Assignments Card -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-users me-2"></i>Customer Assignments 
            <span class="badge bg-info ms-2">${customerAssignments.size()}</span>
        </h6>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty customerAssignments}">
                <div class="table-responsive">
                    <table class="table table-bordered" id="customersTable">
                        <thead>
                            <tr>
                                <th>Customer Code</th>
                                <th>Company Name</th>
                                <th>Contact Person</th>
                                <th>Custom Price</th>
                                <th>Discount %</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            
                            <c:forEach var="assignment" items="${customerAssignments}">
                                <tr>
                                    <td><strong>${assignment.customer_code}</strong></td>
                                    <td>${assignment.company_name}</td>
                                    <td>${assignment.contact_person}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty assignment.custom_price}">
                                                <fmt:formatNumber value="${assignment.custom_price}" type="currency" currencyCode="${billingGroup.currency}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Default</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${assignment.discount_percent > 0}">
                                                <span class="text-success">${assignment.discount_percent}%</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">0%</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        ${dateUtil:formatLocalDateTime(assignment.startDate, 'dd MMM yyyy')}
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty assignment.endDate}">
                                                ${dateUtil:formatLocalDateTime(assignment.endDate, 'dd MMM yyyy')}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Ongoing</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${assignment.assignment_status == 1}">
                                                <span class="badge bg-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/customers/view/${assignment.customer_id}" 
                                               class="btn btn-outline-info btn-sm" title="View Customer">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/billing-groups/remove-customer/${assignment.id}" 
                                                  method="post" style="display: inline;">
                                                <input type="hidden" name="billingGroupId" value="${billingGroup.id}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm" 
                                                        onclick="return confirm('Are you sure you want to remove this customer assignment?')"
                                                        title="Remove Assignment">
                                                    <i class="fas fa-times"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="fas fa-users text-muted" style="font-size: 3rem;"></i>
                    <h5 class="mt-3 text-muted">No Customer Assignments</h5>
                    <p class="text-muted">This billing group has no customers assigned yet.</p>
                    <a href="${pageContext.request.contextPath}/billing-groups/customers/${billingGroup.id}" 
                       class="btn btn-primary">
                        <i class="fas fa-plus me-1"></i>Assign Customers
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Navigation -->
<div class="row">
    <div class="col-12">
        <a href="${pageContext.request.contextPath}/billing-groups" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i>Back to List
        </a>
    </div>
</div>

<script>
$(document).ready(function() {
    // Initialize DataTable if customers exist
    if ($('#customersTable').length > 0) {
        $('#customersTable').DataTable({
            "pageLength": 10,
            "ordering": true,
            "searching": true,
            "info": true,
            "lengthChange": true,
            "autoWidth": false,
            "responsive": true,
            "language": {
                "emptyTable": "No customer assignments found",
                "zeroRecords": "No assignments match your search"
            }
        });
    }
});
</script>
