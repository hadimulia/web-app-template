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

    <title>Customer Portal - Login</title>

    <!-- Custom fonts for this template-->
    <link href="/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="/css/sb-admin-2.min.css" rel="stylesheet">
    
    <style>
        .portal-bg {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .login-card {
            border: 0;
            border-radius: 1rem;
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
        }
        
        .login-header {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            color: white;
            border-radius: 1rem 1rem 0 0;
            padding: 2rem;
            text-align: center;
        }
        
        .login-body {
            padding: 2rem;
        }
        
        .form-control {
            border-radius: 0.5rem;
            border: 1px solid #d1d3e2;
            padding: 0.75rem 1rem;
        }
        
        .form-control:focus {
            border-color: #4e73df;
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
        }
        
        .btn-login {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            border: none;
            border-radius: 0.5rem;
            padding: 0.75rem 1.5rem;
            font-weight: 600;
        }
        
        .btn-login:hover {
            background: linear-gradient(135deg, #224abe 0%, #1e3a8a 100%);
            transform: translateY(-1px);
        }
        
        .footer-link {
            color: #5a5c69;
            text-decoration: none;
        }
        
        .footer-link:hover {
            color: #4e73df;
            text-decoration: underline;
        }
    </style>
</head>

<body class="portal-bg">

    <div class="container">
        <!-- Outer Row -->
        <div class="row justify-content-center">
            <div class="col-xl-6 col-lg-8 col-md-9">
                <div class="card login-card my-5">
                    <div class="login-header">
                        <div class="text-center">
                            <i class="fas fa-user-circle fa-3x mb-3"></i>
                            <h1 class="h4 mb-2">Customer Portal</h1>
                            <p class="mb-0">Welcome back! Please login to your account</p>
                        </div>
                    </div>
                    <div class="login-body">
                        <!-- Alert for messages -->
                        <div id="alertContainer" style="display: none;">
                            <div id="alertMessage" class="alert" role="alert"></div>
                        </div>
                        
                        <form id="loginForm" class="user">
                            <div class="form-group">
                                <label for="username" class="form-label">Username</label>
                                <input type="text" class="form-control" id="username" name="username" 
                                       placeholder="Enter your username..." required>
                            </div>
                            <div class="form-group">
                                <label for="password" class="form-label">Password</label>
                                <input type="password" class="form-control" id="password" name="password"
                                       placeholder="Password" required>
                            </div>
                            <div class="form-group">
                                <div class="custom-control custom-checkbox small">
                                    <input type="checkbox" class="custom-control-input" id="rememberMe">
                                    <label class="custom-control-label" for="rememberMe">Remember Me</label>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-primary btn-login btn-block">
                                <i class="fas fa-sign-in-alt"></i> Login
                            </button>
                        </form>
                        
                        <hr>
                        <div class="text-center">
                            <a class="footer-link" href="/portal/forgot-password">
                                <i class="fas fa-key"></i> Forgot Password?
                            </a>
                        </div>
                        
                        <hr>
                        <div class="text-center">
                            <small class="text-muted">
                                Need help? Contact support at <a href="mailto:support@company.com" class="footer-link">support@company.com</a>
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap core JavaScript-->
    <script src="/vendor/jquery/jquery.min.js"></script>
    <script src="/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

    <!-- Core plugin JavaScript-->
    <script src="/vendor/jquery-easing/jquery.easing.min.js"></script>

    <!-- Custom scripts for all pages-->
    <script src="/js/sb-admin-2.min.js"></script>

    <script>
        $(document).ready(function() {
            $('#loginForm').on('submit', function(e) {
                e.preventDefault();
                
                const username = $('#username').val();
                const password = $('#password').val();
                
                if (!username || !password) {
                    showAlert('Please enter both username and password.', 'danger');
                    return;
                }
                
                // Disable submit button
                const submitBtn = $('button[type="submit"]');
                const originalText = submitBtn.html();
                submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Logging in...');
                
                $.post('/api/portal/login', {
                    username: username,
                    password: password
                })
                .done(function(response) {
                    if (response.success) {
                        showAlert('Login successful! Redirecting...', 'success');
                        setTimeout(function() {
                            window.location.href = '/portal/dashboard';
                        }, 1000);
                    } else {
                        showAlert(response.message || 'Login failed', 'danger');
                        submitBtn.prop('disabled', false).html(originalText);
                    }
                })
                .fail(function(xhr) {
                    const response = xhr.responseJSON;
                    showAlert(response?.message || 'Login failed. Please try again.', 'danger');
                    submitBtn.prop('disabled', false).html(originalText);
                });
            });
            
            // Show alert function
            function showAlert(message, type) {
                const alertContainer = $('#alertContainer');
                const alertMessage = $('#alertMessage');
                
                alertMessage.removeClass('alert-success alert-danger alert-warning alert-info');
                alertMessage.addClass('alert-' + type);
                alertMessage.html(message);
                alertContainer.show();
                
                // Auto-hide success messages
                if (type === 'success') {
                    setTimeout(function() {
                        alertContainer.fadeOut();
                    }, 3000);
                }
            }
            
            // Focus on username field
            $('#username').focus();
            
            // Handle Enter key in form
            $('#loginForm input').on('keypress', function(e) {
                if (e.which === 13) {
                    $('#loginForm').submit();
                }
            });
        });
    </script>

</body>

</html>
