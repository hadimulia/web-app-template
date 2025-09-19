<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${billingGroup.id == null ? 'Add New Billing Group' : 'Edit Billing Group'}</h1>
    <a href="${pageContext.request.contextPath}/billing-groups" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Billing Groups
    </a>
</div>

<!-- Billing Group Form -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">${billingGroup.id == null ? 'Billing Group Information' : 'Edit Billing Group Information'}</h6>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/billing-groups/save">
            <c:if test="${billingGroup.id != null}">
                <input type="hidden" name="id" value="${billingGroup.id}"/>
            </c:if>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="groupCode">Group Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="groupCode" name="groupCode" 
                               value="${billingGroup.groupCode}" required maxlength="50">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="groupName">Group Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="groupName" name="groupName" 
                               value="${billingGroup.groupName}" required maxlength="100">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="basePrice">Base Price <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="basePrice" name="basePrice" 
                               value="${billingGroup.basePrice}" required step="0.01" min="0">
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="currency">Currency</label>
                        <select class="form-control" id="currency" name="currency">
                            <option value="IDR" ${billingGroup.currency == 'IDR' ? 'selected' : ''}>IDR</option>
                            <option value="USD" ${billingGroup.currency == 'USD' ? 'selected' : ''}>USD</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="billingCycle">Billing Cycle</label>
                        <select class="form-control" id="billingCycle" name="billingCycle">
                            <option value="MONTHLY" ${billingGroup.billingCycle == 'MONTHLY' ? 'selected' : ''}>Monthly</option>
                            <option value="QUARTERLY" ${billingGroup.billingCycle == 'QUARTERLY' ? 'selected' : ''}>Quarterly</option>
                            <option value="YEARLY" ${billingGroup.billingCycle == 'YEARLY' ? 'selected' : ''}>Yearly</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-8">
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3">${billingGroup.description}</textarea>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="status">Status</label>
                        <select class="form-control" id="status" name="status">
                            <option value="1" ${billingGroup.status == 1 ? 'selected' : ''}>Active</option>
                            <option value="0" ${billingGroup.status == 0 ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> ${billingGroup.id == null ? 'Create Billing Group' : 'Update Billing Group'}
                </button>
                <a href="${pageContext.request.contextPath}/billing-groups" class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>

