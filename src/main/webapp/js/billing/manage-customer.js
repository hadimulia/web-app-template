$(document).ready(function() {
    // Initialize DataTable
    if ($('#assignmentsTable').length > 0) {
        $('#assignmentsTable').DataTable({
            "pageLength": 10,
            "ordering": true,
            "searching": true,
            "info": true,
            "lengthChange": true,
            "autoWidth": false,
            "responsive": true
        });
    }
    
    // Set today as default start date
    const today = new Date().toISOString().split('T')[0];
    $('#startDate').val(today);
    
    // Form validation
    $('#assignForm').on('submit', function(e) {
        const customerId = $('#customerId').val();
        const startDate = $('#startDate').val();
        const endDate = $('#endDate').val();
        
        if (!customerId) {
            e.preventDefault();
            showAlert('danger', 'Please select a customer.');
            return;
        }
        
        if (!startDate) {
            e.preventDefault();
            showAlert('danger', 'Please enter a start date.');
            return;
        }
        
        if (endDate && endDate <= startDate) {
            e.preventDefault();
            showAlert('danger', 'End date must be after start date.');
            return;
        }
    });
    
    // Price calculation
    $('#customPrice, #discountPercent').on('input', function() {
        calculateEffectivePrice();
    });
    
    calculateEffectivePrice();
    
    // Handle remove button clicks
    $(document).on('click', '.remove-btn', function() {
        const assignmentId = $(this).data('assignment-id');
        const customerName = $(this).data('customer-name');
        confirmRemove(assignmentId, customerName);
    });
});

function calculateEffectivePrice() {
    const basePrice = parseFloat('${billingGroup.basePrice}') || 0;
    const customPrice = parseFloat($('#customPrice').val()) || basePrice;
    const discount = parseFloat($('#discountPercent').val()) || 0;
    
    const effectivePrice = customPrice * (1 - discount / 100);
    
    // Show calculation preview
    let preview = `<small class="text-muted">`;
    if ($('#customPrice').val()) {
        preview += `Custom: ${billingGroup.currency} ${customPrice.toFixed(2)}`;
    } else {
        preview += `Base: ${billingGroup.currency} ${basePrice.toFixed(2)}`;
    }
    if (discount > 0) {
        preview += ` - ${discount}% = <strong>${billingGroup.currency} ${effectivePrice.toFixed(2)}</strong>`;
    }
    preview += `</small>`;
    
    // Add or update preview
    if ($('#pricePreview').length === 0) {
        $('#discountPercent').after('<div id="pricePreview" class="form-text"></div>');
    }
    $('#pricePreview').html(preview);
}

function confirmRemove(assignmentId, customerName) {
    $('#customerToRemove').text(customerName);
    $('#removeForm').attr('action', '${pageContext.request.contextPath}/billing-groups/remove-customer/' + assignmentId);
    $('#removeModal').modal('show');
}

function showAlert(type, message) {
    const alertClass = type === 'danger' ? 'alert-danger' : 'alert-success';
    const alertHTML = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    $('.container-fluid').prepend(alertHTML);
    $('html, body').animate({scrollTop: 0}, 500);
    
    setTimeout(function() {
        $('.alert').fadeOut();
    }, 5000);
}