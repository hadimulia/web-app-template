<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="${user.id == null ? 'Add User' : 'Edit User'}" scope="request"/>
<c:set var="content" value="/WEB-INF/views/users/form-content.jsp" scope="request"/>

<jsp:include page="/WEB-INF/views/layout/main.jsp"/>