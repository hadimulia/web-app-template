<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Admin Login - Spring Boot Admin Template</title>

    <!-- SB Admin CSS -->
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <!-- FontAwesome -->
    <script src="${pageContext.request.contextPath}/js/all.js" crossorigin="anonymous"></script>
    
    <style>
        .login-container {
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border: none;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            max-width: 900px;
            width: 100%;
        }
        
        .login-left {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            color: white;
            padding: 60px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            position: relative;
        }
        
        .login-left::before {
            content: '';
            position: absolute;
            width: 200px;
            height: 200px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            top: -100px;
            right: -100px;
        }
        
        .login-left::after {
            content: '';
            position: absolute;
            width: 150px;
            height: 150px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 50%;
            bottom: -75px;
            left: -75px;
        }
        
        .login-logo {
            font-size: 4rem;
            margin-bottom: 20px;
            color: #fff;
        }
        
        .login-title {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 15px;
        }
        
        .login-subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
            line-height: 1.6;
        }
        
        .login-right {
            padding: 60px 40px;
        }
        
        .form-title {
            font-size: 2rem;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 10px;
            text-align: center;
        }
        
        .form-subtitle {
            color: #6c757d;
            text-align: center;
            margin-bottom: 40px;
        }
        
        .form-group {
            position: relative;
            margin-bottom: 30px;
        }
        
        .form-control-modern {
            border: 2px solid #e1e8ed;
            border-radius: 12px;
            padding: 15px 20px 15px 50px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #f8f9fa;
        }
        
        .form-control-modern:focus {
            border-color: #4e73df;
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
            background: #fff;
        }
        
        .form-icon {
            position: absolute;
            left: 18px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
            font-size: 18px;
        }
        
        .btn-login {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            border: none;
            border-radius: 12px;
            padding: 15px 30px;
            font-size: 16px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(78, 115, 223, 0.3);
        }
        
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(78, 115, 223, 0.4);
        }
        
        .remember-check {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 30px;
        }
        
        .custom-checkbox {
            width: 20px;
            height: 20px;
            border: 2px solid #ddd;
            border-radius: 4px;
            position: relative;
        }
        
        .custom-checkbox:checked {
            background: #4e73df;
            border-color: #4e73df;
        }
        
        .forgot-link {
            color: #4e73df;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.3s ease;
        }
        
        .forgot-link:hover {
            color: #224abe;
            text-decoration: none;
        }
        
        @media (max-width: 768px) {
            .login-left {
                padding: 40px 20px;
            }
            
            .login-right {
                padding: 40px 20px;
            }
            
            .login-logo {
                font-size: 3rem;
            }
            
            .login-title {
                font-size: 1.5rem;
            }
        }
    </style>
</head>

<body>
    <div class="login-container">
        <div class="login-card">
            <div class="row no-gutters h-100">
                <div class="col-lg-6 d-none d-lg-flex">
                    <div class="login-left">
                        <div>
                            <i class="fas fa-shield-alt login-logo"></i>
                            <h2 class="login-title">Admin Panel</h2>
                            <p class="login-subtitle">
                                Secure access to your Spring Boot administration dashboard. 
                                Manage your application with confidence and style.
                            </p>
                        </div>
                    </div>
                </div>
                
                <div class="col-lg-6">
                    <div class="login-right">
                        <h1 class="form-title">Welcome Back!</h1>
                        <p class="form-subtitle">Sign in to your account to continue</p>
                        
                        <!-- Display error messages -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>
                                ${error}
                            </div>
                        </c:if>
                        
                        <!-- Display success messages -->
                        <c:if test="${not empty message}">
                            <div class="alert alert-success" role="alert">
                                <i class="fas fa-check-circle"></i>
                                ${message}
                            </div>
                        </c:if>
                        
                        <form method="post" action="${pageContext.request.contextPath}/login">
                            <div class="form-group">
                                <i class="fas fa-user form-icon"></i>
                                <input type="text" class="form-control form-control-modern"
                                    id="username" name="username" 
                                    placeholder="Enter your username" required>
                            </div>
                            
                            <div class="form-group">
                                <i class="fas fa-lock form-icon"></i>
                                <input type="password" class="form-control form-control-modern"
                                    id="password" name="password" 
                                    placeholder="Enter your password" required>
                            </div>
                            
                            <div class="remember-check">
                                <input type="checkbox" class="custom-control-input" id="customCheck">
                                <label class="custom-control-label" for="customCheck">
                                    Remember me for 30 days
                                </label>
                            </div>
                            
                            <button type="submit" class="btn btn-primary btn-login w-100">
                                <i class="fas fa-sign-in-alt mr-2"></i>
                                Sign In
                            </button>
                        </form>
                        
                        <hr class="my-4">
                        
                        <div class="text-center">
                            <a class="forgot-link" href="#forgot-password">
                                <i class="fas fa-question-circle mr-1"></i>
                                Forgot your password?
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap core JavaScript-->
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
    <!-- Custom scripts for all pages-->
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
    
    <script>
        // Add some interactive effects
        document.addEventListener('DOMContentLoaded', function() {
            // Add focus effects to form inputs
            const formControls = document.querySelectorAll('.form-control-modern');
            formControls.forEach(function(input) {
                input.addEventListener('focus', function() {
                    const icon = this.parentElement.querySelector('.form-icon');
                    if (icon) icon.style.color = '#4e73df';
                });
                
                input.addEventListener('blur', function() {
                    const icon = this.parentElement.querySelector('.form-icon');
                    if (icon) icon.style.color = '#6c757d';
                });
            });
            
            // Add loading state to login button
            const form = document.querySelector('form');
            if (form) {
                form.addEventListener('submit', function() {
                    const btn = document.querySelector('.btn-login');
                    if (btn) {
                        btn.disabled = true;
                        btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Signing in...';
                    }
                });
            }
        });
    </script>

</body>

</html>