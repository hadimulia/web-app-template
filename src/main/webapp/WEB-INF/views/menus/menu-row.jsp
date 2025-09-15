<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Get menu object and level from request --%>
<c:set var="menu" value="${param.menu}" />
<c:set var="level" value="${param.level}" />

<%-- Render the current menu row --%>
<tr>
    <td>${menu.id}</td>
    <td>
        <%-- Add indentation based on level --%>
        <c:forEach begin="0" end="${level - 1}">
            &nbsp;&nbsp;&nbsp;&nbsp;
        </c:forEach>
        <c:if test="${level > 0}">
            <i class="fas fa-level-up-alt text-muted me-1" style="transform: rotate(90deg);"></i>
        </c:if>
        <i class="fas ${menu.menuIcon != null ? menu.menuIcon : 'fa-circle'} me-2"></i>
        <strong>${menu.menuName}</strong>
    </td>
    <td>
        <span class="badge badge-secondary">${menu.menuCode}</span>
    </td>
    <td>
        <c:choose>
            <c:when test="${menu.parentId != null}">
                <small class="text-muted">Parent ID: ${menu.parentId}</small>
            </c:when>
            <c:otherwise>
                <small class="text-muted">Root Menu</small>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:choose>
            <c:when test="${not empty menu.menuUrl}">
                <code>${menu.menuUrl}</code>
            </c:when>
            <c:otherwise>-</c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:choose>
            <c:when test="${menu.menuType == 1}">
                <span class="badge badge-primary">Menu</span>
            </c:when>
            <c:otherwise>
                <span class="badge badge-info">Button</span>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:choose>
            <c:when test="${menu.status == 1}">
                <span class="badge badge-success">Active</span>
            </c:when>
            <c:otherwise>
                <span class="badge badge-danger">Inactive</span>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <span class="badge badge-light">${menu.sortOrder != null ? menu.sortOrder : 0}</span>
    </td>
    <td>
        <div class="btn-group" role="group">
            <a href="${pageContext.request.contextPath}/menus/view/${menu.id}" 
               class="btn btn-info btn-sm" title="View">
                <i class="fas fa-eye"></i>
            </a>
            <a href="${pageContext.request.contextPath}/menus/edit/${menu.id}" 
               class="btn btn-warning btn-sm" title="Edit">
                <i class="fas fa-edit"></i>
            </a>
            <button class="btn btn-danger btn-sm" 
                    onclick="deleteMenu(${menu.id})" 
                    title="Delete">
                <i class="fas fa-trash"></i>
            </button>
        </div>
    </td>
</tr>

<%-- Recursively render children if they exist --%>
<c:if test="${not empty menu.children}">
    <c:forEach var="childMenu" items="${menu.children}">
        <jsp:include page="menu-row.jsp">
            <jsp:param name="menu" value="${childMenu}" />
            <jsp:param name="level" value="${level + 1}" />
        </jsp:include>
    </c:forEach>
</c:if>
