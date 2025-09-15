<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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

<!-- Form Card -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-layer-group me-2"></i>
            ${billingGroup.id == null ? 'Create New Billing Group' : 'Edit Billing Group'}
        </h6>
    </div>
    <div class="card-body">
        <form:form action="${pageContext.request.contextPath}/billing-groups/save" method="post" modelAttribute="billingGroup">
            <form:hidden path="id"/>
            
            <div class="row">
                <!-- Group Code -->
                <div class="col-md-6 mb-3">
                    <label for="groupCode" class="form-label">
                        <i class="fas fa-code me-1"></i>Group Code <span class="text-danger">*</span>
                    </label>
                    <form:input path="groupCode" class="form-control" id="groupCode" placeholder="Enter group code" maxlength="50" required="true"/>
                    <form:errors path="groupCode" cssClass="text-danger small"/>
                    <div class="form-text">Unique identifier for the billing group</div>
                </div>
                
                <!-- Group Name -->
                <div class="col-md-6 mb-3">
                    <label for="groupName" class="form-label">
                        <i class="fas fa-tag me-1"></i>Group Name <span class="text-danger">*</span>
                    </label>
                    <form:input path="groupName" class="form-control" id="groupName" placeholder="Enter group name" maxlength="100" required="true"/>
                    <form:errors path="groupName" cssClass="text-danger small"/>
                    <div class="form-text">Display name for the billing group</div>
                </div>
            </div>
            
            <div class="row">
                <!-- Base Price -->
                <div class="col-md-4 mb-3">
                    <label for="basePrice" class="form-label">
                        <i class="fas fa-dollar-sign me-1"></i>Base Price <span class="text-danger">*</span>
                    </label>
                    <div class="input-group">
                        <form:input path="basePrice" type="number" step="0.01" min="0" class="form-control" id="basePrice" placeholder="0.00" required="true"/>
                    </div>
                    <form:errors path="basePrice" cssClass="text-danger small"/>
                    <div class="form-text">Base price for this billing group</div>
                </div>
                
                <!-- Currency -->
                <div class="col-md-4 mb-3">
                    <label for="currency" class="form-label">
                        <i class="fas fa-coins me-1"></i>Currency <span class="text-danger">*</span>
                    </label>
                    <form:select path="currency" class="form-select" id="currency" required="true">
                        <form:option value="" label="Select Currency"/>
                        <form:option value="USD" label="USD - US Dollar"/>
                        <form:option value="EUR" label="EUR - Euro"/>
                        <form:option value="GBP" label="GBP - British Pound"/>
                        <form:option value="JPY" label="JPY - Japanese Yen"/>
                        <form:option value="IDR" label="IDR - Indonesian Rupiah"/>
                        <form:option value="SGD" label="SGD - Singapore Dollar"/>
                        <form:option value="MYR" label="MYR - Malaysian Ringgit"/>
                        <form:option value="THB" label="THB - Thai Baht"/>
                    </form:select>
                    <form:errors path="currency" cssClass="text-danger small"/>
                </div>
                
                <!-- Billing Cycle -->
                <div class="col-md-4 mb-3">
                    <label for="billingCycle" class="form-label">
                        <i class="fas fa-calendar-alt me-1"></i>Billing Cycle <span class="text-danger">*</span>
                    </label>
                    <form:select path="billingCycle" class="form-select" id="billingCycle" required="true">
                        <form:option value="" label="Select Cycle"/>
                        <form:option value="MONTHLY" label="Monthly"/>
                        <form:option value="QUARTERLY" label="Quarterly"/>
                        <form:option value="SEMI_ANNUALLY" label="Semi-Annually"/>
                        <form:option value="ANNUALLY" label="Annually"/>
                        <form:option value="ONE_TIME" label="One Time"/>
                    </form:select>
                    <form:errors path="billingCycle" cssClass="text-danger small"/>
                </div>
            </div>
            
            <div class="row">
                <!-- Auto Generate -->
                <div class="col-md-6 mb-3">
                    <label class="form-label">
                        <i class="fas fa-magic me-1"></i>Auto Generate Invoice
                    </label>
                    <div class="form-check">
                        <form:checkbox path="autoGenerate" class="form-check-input" id="autoGenerate" value="1"/>
                        <label class="form-check-label" for="autoGenerate">
                            Automatically generate invoices for this billing group
                        </label>
                    </div>
                    <form:errors path="autoGenerate" cssClass="text-danger small"/>
                    <div class="form-text">If enabled, invoices will be generated automatically based on billing cycle</div>
                </div>
                
                <!-- Status -->
                <div class="col-md-6 mb-3">
                    <label for="status" class="form-label">
                        <i class="fas fa-toggle-on me-1"></i>Status <span class="text-danger">*</span>
                    </label>
                    <form:select path="status" class="form-select" id="status" required="true">
                        <form:option value="1" label="Active"/>
                        <form:option value="0" label="Inactive"/>
                    </form:select>
                    <form:errors path="status" cssClass="text-danger small"/>
                    <div class="form-text">Only active billing groups can be assigned to customers</div>
                </div>
            </div>
            
            <!-- Description -->
            <div class="mb-3">
                <label for="description" class="form-label">
                    <i class="fas fa-align-left me-1"></i>Description
                </label>
                <form:textarea path="description" class="form-control" id="description" rows="3" 
                              placeholder="Enter detailed description of this billing group..." maxlength="500"/>
                <form:errors path="description" cssClass="text-danger small"/>
                <div class="form-text">Optional description for internal reference</div>
            </div>
            
            <!-- Notes -->
            <div class="mb-3">
                <label for="notes" class="form-label">
                    <i class="fas fa-sticky-note me-1"></i>Notes
                </label>
                <form:textarea path="notes" class="form-control" id="notes" rows="2" 
                              placeholder="Add any additional notes..." maxlength="1000"/>
                <form:errors path="notes" cssClass="text-danger small"/>
                <div class="form-text">Internal notes for this billing group</div>
            </div>
            
            <!-- Form Actions -->
            <div class="row">
                <div class="col-12">
                    <div class="d-flex justify-content-between">
                        <div>
                            <a href="${pageContext.request.contextPath}/billing-groups" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-1"></i>Back to List
                            </a>
                        </div>
                        <div>
                            <button type="reset" class="btn btn-outline-secondary me-2">
                                <i class="fas fa-undo me-1"></i>Reset
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-1"></i>
                                ${billingGroup.id == null ? 'Create' : 'Update'} Billing Group
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </form:form>
    </div>
</div>



<script>
$(document).ready(function() {
    // Form validation
    $('form').on('submit', function(e) {
        let valid = true;
        
        // Check required fields
        $('[required]').each(function() {
            if (!$(this).val()) {
                $(this).addClass('is-invalid');
                valid = false;
            } else {
                $(this).removeClass('is-invalid');
            }
        });
        
        // Validate base price
        const basePrice = parseFloat($('#basePrice').val());
        if (basePrice < 0) {
            $('#basePrice').addClass('is-invalid');
            valid = false;
        }
        
        if (!valid) {
            e.preventDefault();
            showAlert('danger', 'Please fill in all required fields correctly.');
        }
    });
    
    // Real-time form preview
    $('input, select, textarea').on('input change', function() {
        updatePreview();
    });
    
    
    
    // Format price input
    $('#basePrice').on('blur', function() {
        const value = parseFloat($(this).val());
        if (!isNaN(value)) {
            $(this).val(value.toFixed(2));
        }
    });
});


function showAlert(type, message) {
    const alertClass = type === 'danger' ? 'alert-danger' : 'alert-success';
    const alertHTML = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    $('form').prepend(alertHTML);
    $('html, body').animate({scrollTop: 0}, 500);
    
    setTimeout(function() {
        $('.alert').fadeOut();
    }, 5000);
}
</script>
