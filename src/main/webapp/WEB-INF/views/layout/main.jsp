<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title><c:out value="${title}" default="Admin Dashboard"/> - Spring Boot Admin</title>

    <!-- SB Admin CSS -->
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <!-- FontAwesome -->
    <script src="${pageContext.request.contextPath}/js/all.js" crossorigin="anonymous"></script>

        <!-- jQuery (required for search table functionality) -->
    <script src="${pageContext.request.contextPath}/vendor/jquery/jquery.min.js"></script>
    <!-- Bootstrap core JavaScript-->
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
    <!-- Custom scripts for all pages-->
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
    <script>
        const ctxPath = '${pageContext.request.contextPath}';
    </script>

</head>

<body>
    <!-- Top Navigation -->
    <nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
        <!-- Navbar Brand-->
        <a class="navbar-brand ps-3" href="${pageContext.request.contextPath}/dashboard">Admin Panel</a>
        <!-- Sidebar Toggle-->
        <button class="btn btn-link btn-sm order-1 order-lg-0 me-4 me-lg-0" id="sidebarToggle" href="#!"><i class="fas fa-bars"></i></button>
        <!-- Navbar Search-->
        <form class="d-none d-md-inline-block form-inline ms-auto me-0 me-md-3 my-2 my-md-0">
            
        </form>
        <!-- Navbar-->
        <ul class="navbar-nav ms-auto ms-md-0 me-3 me-lg-4">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="navbarDropdown" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false"><i class="fas fa-user fa-fw"></i></a>
                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                    <li><a class="dropdown-item" href="#!">
                        <c:out value="${fullName}" default="${username}"/>
                    </a></li>
                    <li><a class="dropdown-item" href="#!">Settings</a></li>
                    <li><hr class="dropdown-divider" /></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </ul>
            </li>
        </ul>
    </nav>

    <div id="layoutSidenav">
        <div id="layoutSidenav_nav">
            <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
                <div class="sb-sidenav-menu">
                    <div class="nav">
                        <!-- Core Section -->
                        <div class="sb-sidenav-menu-heading">Core</div>
                        
                        <!-- Dynamic Menu from Database -->
                        <c:if test="${not empty userMenus}">
                            <c:set var="currentPath" value="${pageContext.request.requestURI}" />
                            <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                            
                            <c:forEach var="menu" items="${userMenus}">
                                <c:choose>
                                    <c:when test="${not empty menu.children}">
                                        <!-- Check if any child is active using improved logic -->
                                        <c:set var="hasActiveChild" value="false" />
                                        <c:forEach var="child" items="${menu.children}">
                                            <!-- More robust URL matching -->
                                            <c:set var="childPath" value="${child.menuUrl}" />
                                            <c:choose>
                                                <c:when test="${fn:startsWith(childPath, '/')}">
                                                    <c:set var="fullChildUrl" value="${contextPath}${childPath}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="fullChildUrl" value="${contextPath}/${childPath}" />
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <!-- Check multiple conditions for active state -->
                                            <c:if test="${currentPath eq fullChildUrl or 
                                                         fn:endsWith(currentPath, childPath) or 
                                                         fn:startsWith(currentPath, fullChildUrl) or
                                                         (fn:contains(currentPath, childPath) and fn:length(childPath) > 3)}">
                                                <c:set var="hasActiveChild" value="true" />
                                            </c:if>
                                        </c:forEach>
                                        
                                        <!-- Menu with submenu -->
                                        <a class="nav-link <c:if test='${hasActiveChild}'>active</c:if>" href="#" data-bs-toggle="collapse" data-bs-target="#collapse${menu.id}" aria-expanded="${hasActiveChild}" aria-controls="collapse${menu.id}">
                                            <div class="sb-nav-link-icon"><i class="${menu.menuIcon}"></i></div>
                                            ${menu.menuName}
                                            <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                                        </a>
                                        <div class="collapse <c:if test='${hasActiveChild}'>show</c:if>" id="collapse${menu.id}" aria-labelledby="heading${menu.id}" data-bs-parent="#sidenavAccordion">
                                            <nav class="sb-sidenav-menu-nested nav">
                                                <c:forEach var="child" items="${menu.children}">
                                                    <!-- Apply same logic for child active state -->
                                                    <c:set var="childPath" value="${child.menuUrl}" />
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(childPath, '/')}">
                                                            <c:set var="fullChildUrl" value="${contextPath}${childPath}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="fullChildUrl" value="${contextPath}/${childPath}" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    
                                                    <c:set var="isChildActive" value="false" />
                                                    <c:if test="${currentPath eq fullChildUrl or 
                                                                 fn:endsWith(currentPath, childPath) or 
                                                                 fn:startsWith(currentPath, fullChildUrl) or
                                                                 (fn:contains(currentPath, childPath) and fn:length(childPath) > 3)}">
                                                        <c:set var="isChildActive" value="true" />
                                                    </c:if>
                                                    
                                                    <a class="nav-link <c:if test='${isChildActive}'>active</c:if>" 
                                                       href="${pageContext.request.contextPath}${child.menuUrl}"
                                                       <c:if test='${isChildActive}'>aria-current="page"</c:if>>
                                                        ${child.menuName}
                                                    </a>
                                                </c:forEach>
                                            </nav>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Single menu item with improved matching -->
                                        <c:set var="menuPath" value="${menu.menuUrl}" />
                                        <c:choose>
                                            <c:when test="${fn:startsWith(menuPath, '/')}">
                                                <c:set var="fullMenuUrl" value="${contextPath}${menuPath}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="fullMenuUrl" value="${contextPath}/${menuPath}" />
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        <c:set var="isMenuActive" value="false" />
                                        <c:if test="${currentPath eq fullMenuUrl or 
                                                     fn:endsWith(currentPath, menuPath) or 
                                                     fn:startsWith(currentPath, fullMenuUrl) or
                                                     (fn:contains(currentPath, menuPath) and fn:length(menuPath) > 3)}">
                                            <c:set var="isMenuActive" value="true" />
                                        </c:if>
                                        
                                        <!-- Special case for dashboard -->
                                        <c:if test="${(menuPath eq '/dashboard' or menuPath eq 'dashboard') and (currentPath eq contextPath or currentPath eq '/' or fn:endsWith(currentPath, '/dashboard'))}">
                                            <c:set var="isMenuActive" value="true" />
                                        </c:if>
                                        
                                        <a class="nav-link <c:if test='${isMenuActive}'>active</c:if>" 
                                           href="${pageContext.request.contextPath}${menu.menuUrl}"
                                           <c:if test='${isMenuActive}'>aria-current="page"</c:if>>
                                            <div class="sb-nav-link-icon"><i class="${menu.menuIcon}"></i></div>
                                            ${menu.menuName}
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
                <div class="sb-sidenav-footer">
                    <div class="small">Logged in as:</div>
                    <c:out value="${fullName}" default="${username}"/>
                </div>
            </nav>
        </div>
        <div id="layoutSidenav_content">
            <main>
                <div class="container-fluid px-4">
                    <!-- Page Content -->
                    <jsp:include page="${content}"/>
                </div>
            </main>
            <footer class="py-4 bg-light mt-auto">
                <div class="container-fluid px-4">
                    <div class="d-flex align-items-center justify-content-between small">
                        <div class="text-muted">Copyright &copy; Spring Boot Admin Template 2025</div>
                        <div>
                            <a href="#">Privacy Policy</a>
                            &middot;
                            <a href="#">Terms &amp; Conditions</a>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    </div>

    

</body>

</html>
