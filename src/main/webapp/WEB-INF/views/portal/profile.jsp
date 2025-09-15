<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Customer Portal - My Profile</title>

    <!-- Custom fonts for this template-->
    <link href="/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="/css/sb-admin-2.min.css" rel="stylesheet">
    
    <style>
        .portal-navbar {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
        }
        
        .sidebar-dark {
            background-color: #2c3e50;
        }
        
        .sidebar-dark .sidebar-brand {
            background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        }
        
        .card-header-primary {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            color: white;
        }
        
        .profile-card {
            border-left: 4px solid #4e73df;
        }
        
        .password-card {
            border-left: 4px solid #e74a3b;
        }
        
        .form-control:focus {
            border-color: #4e73df;
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
        }
        
        .password-strength {
            height: 4px;
            border-radius: 2px;
            margin-top: 5px;
            transition: all 0.3s ease;
        }
        
        .strength-weak { background-color: #dc3545; }
        .strength-fair { background-color: #fd7e14; }
        .strength-good { background-color: #ffc107; }
        .strength-strong { background-color: #28a745; }
    </style>
</head>

<body id="page-top">

    <!-- Page Wrapper -->
    <div id="wrapper">

        <!-- Sidebar -->
        <ul class="navbar-nav sidebar sidebar-dark accordion" id="accordionSidebar">

            <!-- Sidebar - Brand -->
            <a class="sidebar-brand d-flex align-items-center justify-content-center" href="/portal/dashboard">
                <div class="sidebar-brand-icon rotate-n-15">
                    <i class="fas fa-user-circle"></i>
                </div>
                <div class="sidebar-brand-text mx-3">Portal</div>
            </a>

            <!-- Divider -->
            <hr class="sidebar-divider my-0">

            <!-- Nav Item - Dashboard -->
            <li class="nav-item">
                <a class="nav-link" href="/portal/dashboard">
                    <i class="fas fa-fw fa-tachometer-alt"></i>
                    <span>Dashboard</span>
                </a>
            </li>

            <!-- Divider -->
            <hr class="sidebar-divider">

            <!-- Nav Item - Invoices -->
            <li class="nav-item">
                <a class="nav-link" href="/portal/invoices">
                    <i class="fas fa-fw fa-file-invoice-dollar"></i>
                    <span>My Invoices</span>
                </a>
            </li>

            <!-- Nav Item - Payments -->
            <li class="nav-item">
                <a class="nav-link" href="/portal/payments">
                    <i class="fas fa-fw fa-credit-card"></i>
                    <span>Payment History</span>
                </a>
            </li>

            <!-- Nav Item - Profile -->
            <li class="nav-item active">
                <a class="nav-link" href="/portal/profile">
                    <i class="fas fa-fw fa-user"></i>
                    <span>My Profile</span>
                </a>
            </li>

            <!-- Divider -->
            <hr class="sidebar-divider d-none d-md-block">

            <!-- Sidebar Toggler (Sidebar) -->
            <div class="text-center d-none d-md-inline">
                <button class="rounded-circle border-0" id="sidebarToggle"></button>
            </div>

        </ul>
        <!-- End of Sidebar -->

        <!-- Content Wrapper -->
        <div id="content-wrapper" class="d-flex flex-column">

            <!-- Main Content -->
            <div id="content">

                <!-- Topbar -->
                <nav class="navbar navbar-expand navbar-light portal-navbar topbar mb-4 static-top shadow">

                    <!-- Sidebar Toggle (Topbar) -->
                    <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                        <i class="fa fa-bars"></i>
                    </button>

                    <!-- Topbar Navbar -->
                    <ul class="navbar-nav ml-auto">

                        <!-- Nav Item - User Information -->
                        <li class="nav-item dropdown no-arrow">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="mr-2 d-none d-lg-inline text-white small" id="customerNameDisplay">${customerName}</span>
                                <i class="fas fa-user-circle fa-fw text-white"></i>
                            </a>
                            <!-- Dropdown - User Information -->
                            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                                aria-labelledby="userDropdown">
                                <a class="dropdown-item" href="/portal/profile">
                                    <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Profile
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="#" onclick="logout()">
                                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Logout
                                </a>
                            </div>
                        </li>

                    </ul>

                </nav>
                <!-- End of Topbar -->

                <!-- Begin Page Content -->
                <div class="container-fluid">

                    <!-- Page Heading -->
                    <div class="d-sm-flex align-items-center justify-content-between mb-4">
                        <h1 class="h3 mb-0 text-gray-800">My Profile</h1>
                    </div>

                    <div class="row">
                        <!-- Profile Information -->
                        <div class="col-lg-8">
                            <div class="card shadow mb-4 profile-card">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-primary">
                                        <i class="fas fa-user"></i> Profile Information
                                    </h6>
                                </div>
                                <div class="card-body">
                                    <form id="profileForm">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="customerName">Customer Name *</label>
                                                    <input type="text" class="form-control" id="customerName" name="customerName" required>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="email">Email Address *</label>
                                                    <input type="email" class="form-control" id="email" name="email" required>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="phone">Phone Number</label>
                                                    <input type="text" class="form-control" id="phone" name="phone">
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="customerCode">Customer Code</label>
                                                    <input type="text" class="form-control" id="customerCode" name="customerCode" readonly>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="form-group">
                                            <label for="address">Address</label>
                                            <textarea class="form-control" id="address" name="address" rows="3"></textarea>
                                        </div>
                                        
                                        <div class="row">
                                            <div class="col-md-4">
                                                <div class="form-group">
                                                    <label for="city">City</label>
                                                    <input type="text" class="form-control" id="city" name="city">
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="form-group">
                                                    <label for="state">State/Province</label>
                                                    <input type="text" class="form-control" id="state" name="state">
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="form-group">
                                                    <label for="postalCode">Postal Code</label>
                                                    <input type="text" class="form-control" id="postalCode" name="postalCode">
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="form-group">
                                            <label for="country">Country</label>
                                            <input type="text" class="form-control" id="country" name="country">
                                        </div>
                                        
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save"></i> Update Profile
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Account Settings -->
                        <div class="col-lg-4">
                            <!-- Change Password -->
                            <div class="card shadow mb-4 password-card">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-danger">
                                        <i class="fas fa-key"></i> Change Password
                                    </h6>
                                </div>
                                <div class="card-body">
                                    <form id="passwordForm">
                                        <div class="form-group">
                                            <label for="currentPassword">Current Password *</label>
                                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                                        </div>
                                        
                                        <div class="form-group">
                                            <label for="newPassword">New Password *</label>
                                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                                            <div class="password-strength" id="passwordStrength"></div>
                                            <small class="form-text text-muted" id="passwordHelp">
                                                Password must be at least 8 characters long
                                            </small>
                                        </div>
                                        
                                        <div class="form-group">
                                            <label for="confirmPassword">Confirm New Password *</label>
                                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                            <div class="invalid-feedback" id="confirmPasswordFeedback"></div>
                                        </div>
                                        
                                        <button type="submit" class="btn btn-danger btn-block">
                                            <i class="fas fa-key"></i> Change Password
                                        </button>
                                    </form>
                                </div>
                            </div>
                            
                            <!-- Account Summary -->
                            <div class="card shadow mb-4">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-primary">
                                        <i class="fas fa-info-circle"></i> Account Summary
                                    </h6>
                                </div>
                                <div class="card-body">
                                    <div class="mb-3">
                                        <strong>Member Since:</strong>
                                        <p id="memberSince" class="text-muted">Loading...</p>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <strong>Billing Group:</strong>
                                        <p id="billingGroup" class="text-muted">Loading...</p>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <strong>Total Invoices:</strong>
                                        <p id="totalInvoicesInfo" class="text-muted">Loading...</p>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <strong>Account Status:</strong>
                                        <p id="accountStatus" class="text-muted">Loading...</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <!-- /.container-fluid -->

            </div>
            <!-- End of Main Content -->

            <!-- Footer -->
            <footer class="sticky-footer bg-white">
                <div class="container my-auto">
                    <div class="copyright text-center my-auto">
                        <span>Copyright &copy; Customer Portal 2024</span>
                    </div>
                </div>
            </footer>
            <!-- End of Footer -->

        </div>
        <!-- End of Content Wrapper -->

    </div>
    <!-- End of Page Wrapper -->

    <!-- Scroll to Top Button-->
    <a class="scroll-to-top rounded" href="#page-top">
        <i class="fas fa-angle-up"></i>
    </a>

    <!-- Bootstrap core JavaScript-->
    <script src="/vendor/jquery/jquery.min.js"></script>
    <script src="/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

    <!-- Core plugin JavaScript-->
    <script src="/vendor/jquery-easing/jquery.easing.min.js"></script>

    <!-- Custom scripts for all pages-->
    <script src="/js/sb-admin-2.min.js"></script>

    <script>
        $(document).ready(function() {
            // Check authentication status
            checkAuthStatus();
            
            // Load profile data
            loadProfileData();
            
            // Bind form events
            bindFormEvents();
            
            // Password strength checker
            $('#newPassword').on('input', checkPasswordStrength);
            $('#confirmPassword').on('input', validatePasswordConfirmation);
        });
        
        function checkAuthStatus() {
            $.get('/api/portal/auth-status')
                .done(function(response) {
                    if (!response.authenticated) {
                        window.location.href = '/portal/login';
                    } else {
                        $('#customerNameDisplay').text(response.customerName || 'Customer');
                    }
                })
                .fail(function() {
                    window.location.href = '/portal/login';
                });
        }
        
        function loadProfileData() {
            $.get('/api/portal/profile')
                .done(function(response) {
                    if (response.success && response.data) {
                        populateProfileForm(response.data);
                        updateAccountSummary(response.data);
                    }
                })
                .fail(function(xhr) {
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    } else {
                        alert('Failed to load profile data');
                    }
                });
        }
        
        function populateProfileForm(data) {
            $('#customerName').val(data.customerName || '');
            $('#email').val(data.email || '');
            $('#phone').val(data.phone || '');
            $('#customerCode').val(data.customerCode || '');
            $('#address').val(data.address || '');
            $('#city').val(data.city || '');
            $('#state').val(data.state || '');
            $('#postalCode').val(data.postalCode || '');
            $('#country').val(data.country || '');
        }
        
        function updateAccountSummary(data) {
            $('#memberSince').text(data.createdAt ? formatDate(data.createdAt) : 'N/A');
            $('#billingGroup').text(data.billingGroupName || 'Default');
            $('#totalInvoicesInfo').text(data.invoiceCount || 0);
            $('#accountStatus').html(getStatusBadge(data.status || 'ACTIVE'));
        }
        
        function bindFormEvents() {
            $('#profileForm').on('submit', function(e) {
                e.preventDefault();
                updateProfile();
            });
            
            $('#passwordForm').on('submit', function(e) {
                e.preventDefault();
                changePassword();
            });
        }
        
        function updateProfile() {
            const formData = {
                customerName: $('#customerName').val(),
                email: $('#email').val(),
                phone: $('#phone').val(),
                address: $('#address').val(),
                city: $('#city').val(),
                state: $('#state').val(),
                postalCode: $('#postalCode').val(),
                country: $('#country').val()
            };
            
            $.ajax({
                url: '/api/portal/profile',
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function(response) {
                    if (response.success) {
                        showAlert('Profile updated successfully!', 'success');
                        $('#customerNameDisplay').text(formData.customerName);
                    } else {
                        showAlert(response.message || 'Failed to update profile', 'danger');
                    }
                },
                error: function(xhr) {
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    } else {
                        const response = xhr.responseJSON;
                        showAlert(response ? response.message : 'Failed to update profile', 'danger');
                    }
                }
            });
        }
        
        function changePassword() {
            const currentPassword = $('#currentPassword').val();
            const newPassword = $('#newPassword').val();
            const confirmPassword = $('#confirmPassword').val();
            
            // Validate passwords match
            if (newPassword !== confirmPassword) {
                showAlert('Passwords do not match', 'danger');
                return;
            }
            
            // Validate password strength
            if (newPassword.length < 8) {
                showAlert('Password must be at least 8 characters long', 'danger');
                return;
            }
            
            const formData = {
                currentPassword: currentPassword,
                newPassword: newPassword
            };
            
            $.ajax({
                url: '/api/portal/change-password',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function(response) {
                    if (response.success) {
                        showAlert('Password changed successfully!', 'success');
                        $('#passwordForm')[0].reset();
                        $('#passwordStrength').removeClass().addClass('password-strength');
                    } else {
                        showAlert(response.message || 'Failed to change password', 'danger');
                    }
                },
                error: function(xhr) {
                    if (xhr.status === 401) {
                        window.location.href = '/portal/login';
                    } else {
                        const response = xhr.responseJSON;
                        showAlert(response ? response.message : 'Failed to change password', 'danger');
                    }
                }
            });
        }
        
        function checkPasswordStrength() {
            const password = $('#newPassword').val();
            const strengthBar = $('#passwordStrength');
            const helpText = $('#passwordHelp');
            
            if (password.length === 0) {
                strengthBar.removeClass().addClass('password-strength');
                helpText.text('Password must be at least 8 characters long');
                return;
            }
            
            let strength = 0;
            let feedback = [];
            
            // Length check
            if (password.length >= 8) strength++;
            else feedback.push('at least 8 characters');
            
            // Lowercase check
            if (/[a-z]/.test(password)) strength++;
            else feedback.push('lowercase letter');
            
            // Uppercase check
            if (/[A-Z]/.test(password)) strength++;
            else feedback.push('uppercase letter');
            
            // Number check
            if (/[0-9]/.test(password)) strength++;
            else feedback.push('number');
            
            // Special character check
            if (/[^A-Za-z0-9]/.test(password)) strength++;
            else feedback.push('special character');
            
            // Update strength bar
            strengthBar.removeClass();
            if (strength <= 1) {
                strengthBar.addClass('password-strength strength-weak');
                helpText.html('<span class="text-danger">Weak: needs ' + feedback.join(', ') + '</span>');
            } else if (strength <= 2) {
                strengthBar.addClass('password-strength strength-fair');
                helpText.html('<span class="text-warning">Fair: needs ' + feedback.join(', ') + '</span>');
            } else if (strength <= 3) {
                strengthBar.addClass('password-strength strength-good');
                helpText.html('<span class="text-info">Good: could use ' + feedback.join(', ') + '</span>');
            } else {
                strengthBar.addClass('password-strength strength-strong');
                helpText.html('<span class="text-success">Strong password!</span>');
            }
        }
        
        function validatePasswordConfirmation() {
            const newPassword = $('#newPassword').val();
            const confirmPassword = $('#confirmPassword').val();
            const feedback = $('#confirmPasswordFeedback');
            const confirmField = $('#confirmPassword');
            
            if (confirmPassword.length === 0) {
                confirmField.removeClass('is-valid is-invalid');
                feedback.text('');
                return;
            }
            
            if (newPassword === confirmPassword) {
                confirmField.removeClass('is-invalid').addClass('is-valid');
                feedback.text('');
            } else {
                confirmField.removeClass('is-valid').addClass('is-invalid');
                feedback.text('Passwords do not match');
            }
        }
        
        function formatDate(dateString) {
            if (!dateString) return 'N/A';
            const date = new Date(dateString);
            return date.toLocaleDateString('id-ID');
        }
        
        function getStatusBadge(status) {
            const statusColors = {
                'ACTIVE': 'success',
                'INACTIVE': 'secondary',
                'SUSPENDED': 'warning',
                'CLOSED': 'danger'
            };
            const statusTexts = {
                'ACTIVE': 'Active',
                'INACTIVE': 'Inactive',
                'SUSPENDED': 'Suspended',
                'CLOSED': 'Closed'
            };
            return '<span class="badge badge-' + (statusColors[status] || 'secondary') + '">' + 
                   (statusTexts[status] || status) + '</span>';
        }
        
        function showAlert(message, type) {
            const alertHtml = `
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            `;
            
            // Insert alert at the top of the page content
            $('.container-fluid').prepend(alertHtml);
            
            // Auto-dismiss after 5 seconds
            setTimeout(function() {
                $('.alert').alert('close');
            }, 5000);
        }
        
        function logout() {
            if (confirm('Are you sure you want to logout?')) {
                $.post('/api/portal/logout')
                    .always(function() {
                        window.location.href = '/portal/login';
                    });
            }
        }
    </script>

</body>

</html>
