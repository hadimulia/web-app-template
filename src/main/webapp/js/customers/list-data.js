$(document).ready(function() {
    
    try {
        const config = JSON.parse($('#customers-config').text());

        // Define the render function before creating the SearchTable
        config.renderRow = function(customer) {

            const statusBadge = getStatusBadge(customer.status);
            const customerSince = customer.customerSince
                ? new Date(customer.customerSince).toLocaleDateString()
                : '-';
            const companyName = customer.companyName || '-';
            const contactPerson = customer.contactPerson || '-';
            const customerType = customer.customerType === 'CORPORATE' ? 'Corporate' : 'Individual';
            const outstanding = formatCurrency(customer.totalOutstanding || 0);
            
            console.log('Rendering customer:', customer);

            return '' +
                '<tr>' +
                '  <td>' + (customer.id || 'N/A') + '</td>' +
                '  <td><code>' + (customer.customerCode || 'N/A') + '</code></td>' +
                '  <td>' + companyName + '</td>' +
                '  <td>' + contactPerson + '</td>' +
                '  <td>' + (customer.email || 'N/A') + '</td>' +
                '  <td><span class="badge badge-secondary">' + customerType + '</span></td>' +
                '  <td>' + statusBadge + '</td>' +
                '  <td>' + customerSince + '</td>' +
                '  <td class="text-right">' + outstanding + '</td>' +
                '  <td>' +
                '    <a href="' + ctxPath + '/customers/view/' + customer.id + '" class="btn btn-info btn-sm" title="View">' +
                '        <i class="fas fa-eye"></i>' +
                '    </a>' +
                '    <a href="' + ctxPath + '/customers/edit/' + customer.id + '" class="btn btn-warning btn-sm" title="Edit">' +
                '        <i class="fas fa-edit"></i>' +
                '    </a>' +
                getStatusActionButton(customer) +
                '    <button type="button" class="btn btn-danger btn-sm delete-customer-btn" title="Deactivate"' +
                '            data-customer-id="' + customer.id + '"' + 
                '            data-customer-code="' + (customer.customerCode || 'N/A') + '"' +
                '            data-customer-name="' + (customer.companyName || customer.contactPerson || 'N/A') + '"' +
                '            data-customer-email="' + (customer.email || 'N/A') + '">' +
                '        <i class="fas fa-user-times"></i>' +
                '    </button>' +
                '  </td>' +
                '</tr>';
        };

        window.customersTable = new SearchTable(config);

    } catch (error) {
        console.error('Error initializing customer search table:', error);
        $('#customers-container').html('<div class="alert alert-warning">Error loading customer table. Please refresh the page.</div>');
    }
    
    // Handle delete button clicks to show modal
    $(document).on('click', '.delete-customer-btn', function(e) {
        e.preventDefault();
        
        const customerId = $(this).data('customer-id');
        const customerCode = $(this).data('customer-code');
        const customerName = $(this).data('customer-name');
        const customerEmail = $(this).data('customer-email');
        
        // Populate modal with customer information
        $('#customerCodeToDelete').text(customerCode);
        $('#customerNameToDelete').text(customerName);
        $('#customerEmailToDelete').text(customerEmail);
        
        // Store customer data for deletion
        $('#confirmDeleteBtn').data('customer-id', customerId);
        $('#confirmDeleteBtn').data('customer-code', customerCode);
        $('#confirmDeleteBtn').data('customer-name', customerName);
        
        // Show the modal
        try {
            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        } catch (e) {
            $('#deleteModal').modal('show');
        }
    });
    
    // Handle confirm delete button in modal
    $('#confirmDeleteBtn').on('click', function() {
        const customerId = $(this).data('customer-id');
        
        // Show loading state
        $(this).html('<i class="fas fa-spinner fa-spin me-1"></i>Deactivating...').prop('disabled', true);
        
        // Perform delete - redirect to delete URL
        window.location.href = ctxPath + '/customers/delete/' + customerId;
    });
    
    // Handle status action buttons
    $(document).on('click', '.activate-customer-btn', function(e) {
        e.preventDefault();
        const customerId = $(this).data('customer-id');
        
        if (confirm('Are you sure you want to activate this customer?')) {
            // Create form and submit
            const form = $('<form method="POST" action="' + ctxPath + '/customers/activate/' + customerId + '"></form>');
            $('body').append(form);
            form.submit();
        }
    });
    
    $(document).on('click', '.suspend-customer-btn', function(e) {
        e.preventDefault();
        const customerId = $(this).data('customer-id');
        
        if (confirm('Are you sure you want to suspend this customer? This will restrict their access.')) {
            // Create form and submit
            const form = $('<form method="POST" action="' + ctxPath + '/customers/suspend/' + customerId + '"></form>');
            $('body').append(form);
            form.submit();
        }
    });
    
    // Manual close handlers for better compatibility
    $('#cancelDeleteBtn, .btn-close').on('click', function() {
        closeDeleteModal();
    });
    
    // Close on backdrop click
    $('#deleteModal').on('click', function(e) {
        if (e.target === this) {
            closeDeleteModal();
        }
    });
    
    // Close on ESC key
    $(document).on('keyup', function(e) {
        if (e.key === 'Escape' && $('#deleteModal').hasClass('show')) {
            closeDeleteModal();
        }
    });
    
    // Function to close modal with proper cleanup
    function closeDeleteModal() {
        try {
            const modal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
            if (modal) {
                modal.hide();
            } else {
                $('#deleteModal').removeClass('show').hide();
                $('.modal-backdrop').remove();
                $('body').removeClass('modal-open').css('padding-right', '');
            }
        } catch (e) {
            $('#deleteModal').removeClass('show').hide();
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open').css('padding-right', '');
        }
        
        // Reset button state
        resetDeleteButton();
    }
    
    function resetDeleteButton() {
        $('#confirmDeleteBtn')
            .html('<i class="fas fa-user-times me-1"></i>Deactivate Customer')
            .prop('disabled', false)
            .removeData('customer-id')
            .removeData('customer-code')
            .removeData('customer-name');
    }
    
    // Reset modal when closed (for both Bootstrap versions)
    $('#deleteModal').on('hidden.bs.modal hide.bs.modal', function() {
        resetDeleteButton();
    });
});

// Helper functions
function getStatusBadge(status) {
    switch (status) {
        case 0:
            return '<span class="badge badge-secondary">Inactive</span>';
        case 1:
            return '<span class="badge badge-success">Active</span>';
        case 2:
            return '<span class="badge badge-warning">Suspended</span>';
        default:
            return '<span class="badge badge-dark">Unknown</span>';
    }
}

function getStatusActionButton(customer) {
    if (customer.status === 1) { // Active
        return '    <button type="button" class="btn btn-warning btn-sm suspend-customer-btn" title="Suspend"' +
               '            data-customer-id="' + customer.id + '">' +
               '        <i class="fas fa-pause"></i>' +
               '    </button>';
    } else if (customer.status === 0 || customer.status === 2) { // Inactive or Suspended
        return '    <button type="button" class="btn btn-success btn-sm activate-customer-btn" title="Activate"' +
               '            data-customer-id="' + customer.id + '">' +
               '        <i class="fas fa-play"></i>' +
               '    </button>';
    }
    return '';
}

function formatCurrency(amount) {
    if (!amount || amount === 0) {
        return 'IDR 0';
    }
    return 'IDR ' + parseFloat(amount).toLocaleString('id-ID', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    });
}
