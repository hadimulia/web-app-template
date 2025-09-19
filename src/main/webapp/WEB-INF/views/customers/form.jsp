<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${customer.id == null ? 'Add New Customer' : 'Edit Customer'}</h1>
    <a href="${pageContext.request.contextPath}/customers" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Customers
    </a>
</div>

<!-- Messages -->
<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${error}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

<!-- Customer Form -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">${customer.id == null ? 'Customer Information' : 'Edit Customer Information'}</h6>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/customers/save" id="customerForm">
            <c:if test="${customer.id != null}">
                <input type="hidden" name="id" value="${customer.id}"/>
            </c:if>
            
            <!-- Basic Information -->
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="customerCode">Customer Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="customerCode" name="customerCode" 
                               value="${customer.customerCode}" required maxlength="50"
                               ${customer.id != null ? 'readonly' : ''}>
                        <small class="form-text text-muted">
                            <c:choose>
                                <c:when test="${customer.id == null}">Leave empty to auto-generate</c:when>
                                <c:otherwise>Customer code cannot be changed</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="customerType">Customer Type <span class="text-danger">*</span></label>
                        <select class="form-control" id="customerType" name="customerType" required>
                            <option value="CORPORATE" ${customer.customerType == 'CORPORATE' ? 'selected' : ''}>Corporate</option>
                            <option value="INDIVIDUAL" ${customer.customerType == 'INDIVIDUAL' ? 'selected' : ''}>Individual</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="companyName">Company Name</label>
                        <input type="text" class="form-control" id="companyName" name="companyName" 
                               value="${customer.companyName}" maxlength="255">
                        <small class="form-text text-muted">Required for Corporate customers</small>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="contactPerson">Contact Person</label>
                        <input type="text" class="form-control" id="contactPerson" name="contactPerson" 
                               value="${customer.contactPerson}" maxlength="100">
                    </div>
                </div>
            </div>
            
            <!-- Contact Information -->
            <h6 class="mt-4 mb-3 text-primary">Contact Information</h6>
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="email">Email Address <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${customer.email}" required maxlength="100">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="phone">Phone Number</label>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               value="${customer.phone}" maxlength="20">
                    </div>
                </div>
            </div>
            
            <!-- Address Information -->
            <h6 class="mt-4 mb-3 text-primary">Address Information</h6>
            <div class="form-group">
                <label for="address">Address</label>
                <textarea class="form-control" id="address" name="address" rows="3">${customer.address}</textarea>
            </div>
            
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="city">City</label>
                        <input type="text" class="form-control" id="city" name="city" 
                               value="${customer.city}" maxlength="100">
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="state">State/Province</label>
                        <input type="text" class="form-control" id="state" name="state" 
                               value="${customer.state}" maxlength="100">
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="postalCode">Postal Code</label>
                        <input type="text" class="form-control" id="postalCode" name="postalCode" 
                               value="${customer.postalCode}" maxlength="20">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="country">Country</label>
                        <input type="text" class="form-control" id="country" name="country" 
                               value="${customer.country != null ? customer.country : 'Indonesia'}" maxlength="100">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="taxId">Tax ID/NPWP</label>
                        <input type="text" class="form-control" id="taxId" name="taxId" 
                               value="${customer.taxId}" maxlength="50">
                    </div>
                </div>
            </div>
            
            <!-- Business Settings -->
            <h6 class="mt-4 mb-3 text-primary">Business Settings</h6>
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="creditLimit">Credit Limit (IDR)</label>
                        <input type="number" class="form-control" id="creditLimit" name="creditLimit" 
                               value="${customer.creditLimit}" min="0" step="1000">
                        <small class="form-text text-muted">Maximum outstanding amount allowed</small>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="paymentTerms">Payment Terms (Days)</label>
                        <select class="form-control" id="paymentTerms" name="paymentTerms">
                            <option value="7" ${customer.paymentTerms == 7 ? 'selected' : ''}>7 Days</option>
                            <option value="14" ${customer.paymentTerms == 14 ? 'selected' : ''}>14 Days</option>
                            <option value="30" ${customer.paymentTerms == 30 || customer.paymentTerms == null ? 'selected' : ''}>30 Days</option>
                            <option value="45" ${customer.paymentTerms == 45 ? 'selected' : ''}>45 Days</option>
                            <option value="60" ${customer.paymentTerms == 60 ? 'selected' : ''}>60 Days</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="customerSince">Customer Since</label>
                        <input type="date" class="form-control" id="customerSince" name="customerSince" 
                               value="<fmt:formatDate value='${customer.customerSince}' pattern='yyyy-MM-dd'/>">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-8">
                    <div class="form-group">
                        <label for="notes">Notes</label>
                        <textarea class="form-control" id="notes" name="notes" rows="3">${customer.notes}</textarea>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="status">Status</label>
                        <select class="form-control" id="status" name="status">
                            <option value="1" ${customer.status == 1 || customer.status == null ? 'selected' : ''}>Active</option>
                            <option value="2" ${customer.status == 2 ? 'selected' : ''}>Suspended</option>
                            <option value="0" ${customer.status == 0 ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <div class="form-group mt-4">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> ${customer.id == null ? 'Create Customer' : 'Update Customer'}
                </button>
                <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<script>
$(document).ready(function() {
    // Customer type change handler
    $('#customerType').on('change', function() {
        const isCorporate = $(this).val() === 'CORPORATE';
        
        if (isCorporate) {
            $('#companyName').prop('required', true);
            $('#companyName').closest('.form-group').find('label').html('Company Name <span class="text-danger">*</span>');
        } else {
            $('#companyName').prop('required', false);
            $('#companyName').closest('.form-group').find('label').html('Company Name');
        }
    });
    
    // Trigger change event on page load
    $('#customerType').trigger('change');
    
    // Form validation
    $('#customerForm').on('submit', function(e) {
        const customerType = $('#customerType').val();
        const companyName = $('#companyName').val().trim();
        
        if (customerType === 'CORPORATE' && !companyName) {
            e.preventDefault();
            alert('Company name is required for corporate customers.');
            $('#companyName').focus();
            return false;
        }
    });
    
    // Auto-format currency input
    $('#creditLimit').on('input', function() {
        let value = $(this).val().replace(/\D/g, ''); // Remove non-digits
        if (value) {
            $(this).val(value);
        }
    });
    
    // Customer code formatting (uppercase, no spaces)
    $('#customerCode').on('input', function() {
        let value = $(this).val().toUpperCase().replace(/[^A-Z0-9]/g, '');
        $(this).val(value);
    });
});
</script>
