$(document).ready(function() {
    
    // Define context path - for billing groups specifically
    const ctxPath = '/billing-groups';
    
    try {
        const config = JSON.parse($('#billing-groups-config').text());

        // Define the render function before creating the SearchTable
        config.renderRow = function(billingGroup) {

            const statusBadge = billingGroup.status == 1
                ? '<span class="badge badge-success">Active</span>'
                : '<span class="badge badge-secondary">Inactive</span>';

            const createdAt = billingGroup.createdAt
                ? new Date(billingGroup.createdAt).toLocaleDateString()
                : '-';

            const description = billingGroup.description || '-';

            const basePrice = billingGroup.basePrice 
                ? new Intl.NumberFormat('id-ID', { style: 'currency', currency: billingGroup.currency || 'IDR' }).format(billingGroup.basePrice)
                : 'N/A';

            return '' +
                '<tr>' +
                '  <td>' + (billingGroup.id || 'N/A') + '</td>' +
                '  <td><code>' + (billingGroup.groupCode || 'N/A') + '</code></td>' +
                '  <td>' + (billingGroup.groupName || 'N/A') + '</td>' +
                '  <td>' + basePrice + '</td>' +
                '  <td>' + (billingGroup.currency || 'N/A') + '</td>' +
                '  <td>' + (billingGroup.billingCycle || 'N/A') + '</td>' +
                '  <td>' + (billingGroup.autoGenerate ? 'Yes' : 'No') + '</td>' +
                '  <td>' + statusBadge + '</td>' +
                '  <td>' + createdAt + '</td>' +
                '  <td>' +
                '    <a href="' + ctxPath + '/view/' + billingGroup.id + '" class="btn btn-info btn-sm" title="View">' +
                '        <i class="fas fa-eye"></i>' +
                '    </a>' +
                '    <a href="' + ctxPath + '/edit/' + billingGroup.id + '" class="btn btn-warning btn-sm" title="Edit">' +
                '        <i class="fas fa-edit"></i>' +
                '    </a>' +
                '    <button type="button" class="btn btn-danger btn-sm delete-btn" title="Delete"' +
                '            data-billing-group-id="' + billingGroup.id + '" data-billing-group-name="' + (billingGroup.groupName || 'N/A') + '" data-billing-group-code="' + (billingGroup.groupCode || 'N/A') + '">' +
                '        <i class="fas fa-trash"></i>' +
                '    </button>' +
                '  </td>' +
                '</tr>';
        };

        window.billingGroupsTable = new SearchTable(config);

    } catch (error) {
        console.error('Error initializing search table:', error);
        $('#billing-groups-container').html('<div class="alert alert-warning">Error loading search table. Please refresh the page.</div>');
    }
    
    // Handle delete button clicks to show modal
    $(document).on('click', '.delete-btn', function(e) {
        e.preventDefault();

        const billingGroupId = $(this).data('billing-group-id');
        const billingGroupName = $(this).data('billing-group-name');
        const billingGroupCode = $(this).data('billing-group-code');

        // Populate modal with user information
        $('#groupNameToDelete').text(billingGroupName);
        $('#groupIdToDelete').text(billingGroupId);
        $('#groupCodeToDelete').text(billingGroupCode);

        // Store user data for deletion
        $('#confirmDeleteBtn').data('billing-group-id', billingGroupId);
        $('#confirmDeleteBtn').data('billing-group-name', billingGroupName);

        // Show the modal (Bootstrap 5 syntax, with fallback)
        try {
            // Try Bootstrap 5 first
            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        } catch (e) {
            // Fallback to Bootstrap 4 or jQuery
            $('#deleteModal').modal('show');
        }
    });
    
    // Handle confirm delete button in modal
    $('#confirmDeleteBtn').on('click', function() {
        const billingGroupId = $(this).data('billing-group-id');
        const billingGroupName = $(this).data('billing-group-name');

        // Show loading state
        $(this).html('<i class="fas fa-spinner fa-spin me-1"></i>Deleting...').prop('disabled', true);
        
        // Perform delete - redirect to delete URL
        window.location.href = ctxPath + '/billing-groups/delete/' + billingGroupId;
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
            // Try Bootstrap 5 first
            const modal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
            if (modal) {
                modal.hide();
            } else {
                $('#deleteModal').removeClass('show').hide();
                $('.modal-backdrop').remove();
                $('body').removeClass('modal-open').css('padding-right', '');
            }
        } catch (e) {
            // Fallback to manual hide
            $('#deleteModal').removeClass('show').hide();
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open').css('padding-right', '');
        }
        
        // Reset button state
        $('#confirmDeleteBtn')
            .html('<i class="fas fa-trash me-1"></i>Delete User')
            .prop('disabled', false)
            .removeData('user-id')
            .removeData('username');
    }
    
    // Reset modal when closed (for both Bootstrap versions)
    $('#deleteModal').on('hidden.bs.modal hide.bs.modal', function() {
        $('#confirmDeleteBtn')
            .html('<i class="fas fa-trash me-1"></i>Delete User')
            .prop('disabled', false)
            .removeData('user-id')
            .removeData('username');
    });
});