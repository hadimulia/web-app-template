$(document).ready(function() {
    
    try {
        const config = JSON.parse($('#roles-config').text());

        // Define the render function before creating the SearchTable
        config.renderRow = function(role) {

            const statusBadge = role.status == 1
                ? 'Active'
                : 'Inactive';

            const createdAt = role.createdAt
                ? new Date(role.createdAt).toLocaleDateString()
                : '-';

            const description = role.description || '-';
            console.log('Rendering role:', role);

            return '' +
                '<tr>' +
                '  <td>' + (role.id || 'N/A') + '</td>' +
                '  <td>' + (role.roleName || 'N/A') + '</td>' +
                '  <td><code>' + (role.roleCode || 'N/A') + '</code></td>' +
                '  <td>' + description + '</td>' +
                '  <td>' + statusBadge + '</td>' +
                '  <td>' + createdAt + '</td>' +
                '  <td>' +
                '    <a href="' + ctxPath + '/roles/view/' + role.id + '" class="btn btn-info btn-sm" title="View">' +
                '        <i class="fas fa-eye"></i>' +
                '    </a>' +
                '    <a href="' + ctxPath + '/roles/edit/' + role.id + '" class="btn btn-warning btn-sm" title="Edit">' +
                '        <i class="fas fa-edit"></i>' +
                '    </a>' +
                '    <button type="button" class="btn btn-danger btn-sm delete-role-btn" title="Delete"' +
                '            data-role-id="' + role.id + '" data-role-name="' + (role.roleName || 'N/A') + '">' +
                '        <i class="fas fa-trash"></i>' +
                '    </button>' +
                '  </td>' +
                '</tr>';
        };

        window.rolesTable = new SearchTable(config);

    } catch (error) {
        console.error('Error initializing search table:', error);
        $('#roles-container').html('<div class="alert alert-warning">Error loading search table. Please refresh the page.</div>');
    }
    
    // Handle delete button clicks to show modal
    $(document).on('click', '.delete-role-btn', function(e) {
        e.preventDefault();
        
        const roleId = $(this).data('role-id');
        const roleName = $(this).data('role-name');
        
        // Populate modal with role information
        $('#roleIdToDelete').text(roleId);
        $('#roleNameToDelete').text(roleName);
        
        // Store role data for deletion
        $('#confirmDeleteBtn').data('role-id', roleId);
        $('#confirmDeleteBtn').data('role-name', roleName);
        
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
        const roleId = $(this).data('role-id');
        const roleName = $(this).data('role-name');
        
        // Show loading state
        $(this).html('<i class="fas fa-spinner fa-spin me-1"></i>Deleting...').prop('disabled', true);
        
        // Perform delete - redirect to delete URL
        window.location.href = ctxPath + '/roles/delete/' + roleId;
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
            .html('<i class="fas fa-trash me-1"></i>Delete Role')
            .prop('disabled', false)
            .removeData('role-id')
            .removeData('role-name');
    }
    
    // Reset modal when closed (for both Bootstrap versions)
    $('#deleteModal').on('hidden.bs.modal hide.bs.modal', function() {
        $('#confirmDeleteBtn')
            .html('<i class="fas fa-trash me-1"></i>Delete Role')
            .prop('disabled', false)
            .removeData('role-id')
            .removeData('role-name');
    });
});