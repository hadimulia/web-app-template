<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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

<!-- Billing Group Info -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-layer-group me-2"></i>Billing Group: ${billingGroup.groupName}
        </h6>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-3">
                <strong>Group Code:</strong><br>
                <code class="bg-light p-1 rounded">${billingGroup.groupCode}</code>
            </div>
            <div class="col-md-3">
                <strong>Base Price:</strong><br>
                <span class="text-success fs-6">
                    <fmt:formatNumber value="${billingGroup.basePrice}" type="currency" currencyCode="${billingGroup.currency}"/>
                </span>
            </div>
            <div class="col-md-3">
                <strong>Billing Cycle:</strong><br>
                <span class="badge bg-primary">${billingGroup.billingCycle}</span>
            </div>
            <div class="col-md-3">
                <strong>Status:</strong><br>
                <c:choose>
                    <c:when test="${billingGroup.status == 1}">
                        <span class="badge bg-success">Active</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-danger">Inactive</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Assign New Customer -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-success">
            <i class="fas fa-plus me-2"></i>Assign New Customer
        </h6>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/billing-groups/assign-customer" method="post" id="assignForm">
            <input type="hidden" name="billingGroupId" value="${billingGroup.id}">
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="customerId" class="form-label">
                        <i class="fas fa-building me-1"></i>Customer <span class="text-danger">*</span>
                    </label>
                    <select name="customerId" id="customerId" class="form-select" required>
                        <option value="">Select Customer</option>
                        <c:forEach var="customer" items="${customers}">
                            <option value="${customer.id}">${customer.customerCode} - ${customer.companyName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="customPrice" class="form-label">
                        <i class="fas fa-dollar-sign me-1"></i>Custom Price (Optional)
                    </label>
                    <div class="input-group">
                        <span class="input-group-text">${billingGroup.currency}</span>
                        <input type="number" name="customPrice" id="customPrice" class="form-control" 
                               step="0.01" min="0" placeholder="Leave empty for base price">
                    </div>
                    <div class="form-text">Leave empty to use base price: 
                        <fmt:formatNumber value="${billingGroup.basePrice}" type="currency" currencyCode="${billingGroup.currency}"/>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label for="discountPercent" class="form-label">
                        <i class="fas fa-percent me-1"></i>Discount Percentage
                    </label>
                    <input type="number" name="discountPercent" id="discountPercent" class="form-control" 
                           value="0" step="0.01" min="0" max="100">
                    <div class="form-text">Discount percentage (0-100)</div>
                </div>
                <div class="col-md-4 mb-3">
                    <label for="startDate" class="form-label">
                        <i class="fas fa-calendar-alt me-1"></i>Start Date <span class="text-danger">*</span>
                    </label>
                    <input type="date" name="startDate" id="startDate" class="form-control" required>
                </div>
                <div class="col-md-4 mb-3">
                    <label for="endDate" class="form-label">
                        <i class="fas fa-calendar-alt me-1"></i>End Date (Optional)
                    </label>
                    <input type="date" name="endDate" id="endDate" class="form-control">
                    <div class="form-text">Leave empty for ongoing assignment</div>
                </div>
            </div>
            
            <div class="mb-3">
                <label for="notes" class="form-label">
                    <i class="fas fa-sticky-note me-1"></i>Notes
                </label>
                <textarea name="notes" id="notes" class="form-control" rows="3" 
                          placeholder="Add any notes for this assignment..."></textarea>
            </div>
            
            <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-success">
                    <i class="fas fa-plus me-1"></i>Assign Customer
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Current Assignments -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-users me-2"></i>Current Assignments 
            <span class="badge bg-info ms-2">${customerAssignments.size()}</span>
        </h6>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty customerAssignments}">
                <div class="table-responsive">
                    <table class="table table-bordered" id="assignmentsTable">
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
                                                <span class="text-info">
                                                    <fmt:formatNumber value="${assignment.custom_price}" type="currency" currencyCode="${billingGroup.currency}"/>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">
                                                    <fmt:formatNumber value="${billingGroup.basePrice}" type="currency" currencyCode="${billingGroup.currency}"/>
                                                    <small>(Default)</small>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${assignment.discount_percent > 0}">
                                                <span class="badge bg-success">${assignment.discount_percent}%</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">0%</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${assignment.start_date}" pattern="dd MMM yyyy"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty assignment.end_date}">
                                                <fmt:formatDate value="${assignment.end_date}" pattern="dd MMM yyyy"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-info">Ongoing</span>
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
                                               class="btn btn-outline-info" title="View Customer">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <button type="button" class="btn btn-outline-danger remove-btn" 
                                                    data-assignment-id="${assignment.id}"
                                                    data-customer-name="${assignment.company_name}"
                                                    title="Remove Assignment">
                                                <i class="fas fa-times"></i>
                                            </button>
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
                    <h5 class="mt-3 text-muted">No Customers Assigned</h5>
                    <p class="text-muted">Use the form above to assign customers to this billing group.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Navigation -->
<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/billing-groups" class="btn btn-secondary">
                <i class="fas fa-arrow-left me-1"></i>Back to List
            </a>
            <a href="${pageContext.request.contextPath}/billing-groups/view/${billingGroup.id}" class="btn btn-info">
                <i class="fas fa-eye me-1"></i>View Details
            </a>
        </div>
    </div>
</div>

<!-- Remove Assignment Modal -->
<div class="modal fade" id="removeModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">
                    <i class="fas fa-exclamation-triangle me-2"></i>Confirm Removal
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="text-center mb-3">
                    <i class="fas fa-user-times text-danger" style="font-size: 3rem;"></i>
                </div>
                <h6 class="text-center mb-3">Remove customer assignment?</h6>
                <div class="alert alert-info">
                    <strong>Customer:</strong> <span id="customerToRemove"></span><br>
                    <strong>Billing Group:</strong> ${billingGroup.groupName}
                </div>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <strong>Warning:</strong> This will remove the customer from this billing group.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="removeForm" method="post" style="display: inline;">
                    <input type="hidden" name="billingGroupId" value="${billingGroup.id}">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-times me-1"></i>Remove
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/billing/manage-customer.js"></script>

