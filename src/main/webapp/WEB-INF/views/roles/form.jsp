<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="${role.id == null ? 'Add Role' : 'Edit Role'}" scope="request"/>
<c:set var="content" value="/WEB-INF/views/roles/form-content.jsp" scope="request"/>

<jsp:include page="/WEB-INF/views/layout/main.jsp"/>