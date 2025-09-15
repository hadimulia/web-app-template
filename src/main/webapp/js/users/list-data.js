$(document).ready(function() {
    
    try {
        const config = JSON.parse($('#users-config').text());

        // Define the render function before creating the SearchTable
        config.renderRow = function(user) {

            const statusBadge = user.status == 1
                ? 'Active'
                : 'Inactive';

            const createdAt = user.createdAt
                ? new Date(user.createdAt).toLocaleDateString()
                : '-';

            const description = user.description || '-';
            

            return '' +
                '<tr>' +
                '  <td>' + (user.id || 'N/A') + '</td>' +
                '  <td>' + (user.username || 'N/A') + '</td>' +
                '  <td><code>' + (user.fullName || 'N/A') + '</code></td>' +
                '  <td>' + (user.email || 'N/A') + '</td>' +
                '  <td>' + (user.phone || 'N/A') + '</td>' +
                '  <td>' + statusBadge + '</td>' +
                '  <td>' + createdAt + '</td>' +
                '  <td>' +
                '    <a href="' + ctxPath + '/users/view/' + user.id + '" class="btn btn-info btn-sm" title="View">' +
                '        <i class="fas fa-eye"></i>' +
                '    </a>' +
                '    <a href="' + ctxPath + '/users/edit/' + user.id + '" class="btn btn-warning btn-sm" title="Edit">' +
                '        <i class="fas fa-edit"></i>' +
                '    </a>' +
                '    <button type="button" class="btn btn-danger btn-sm delete-btn" title="Delete"' +
                '            data-user-id="' + user.id + '" data-username="' + (user.username || 'N/A') + '">' +
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
    $(document).on('click', '.delete-btn', function(e) {
        e.preventDefault();

        const userId = $(this).data('user-id');
        const username = $(this).data('username');

        // Populate modal with user information
        $('#usernameToDelete').text(username);
        $('#userIdToDelete').text(userId);

        // Store user data for deletion
        $('#confirmDeleteBtn').data('user-id', userId);
        $('#confirmDeleteBtn').data('username', username);

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
        const userId = $(this).data('user-id');
        const username = $(this).data('username');

        // Show loading state
        $(this).html('<i class="fas fa-spinner fa-spin me-1"></i>Deleting...').prop('disabled', true);
        
        // Perform delete - redirect to delete URL
        window.location.href = ctxPath + '/users/delete/' + userId;
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