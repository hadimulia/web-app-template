<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Manage Customers - ${billingGroup.groupName}" scope="request"/>
<c:set var="content" value="/WEB-INF/views/billing-groups/manage-customers-content.jsp" scope="request"/>

<jsp:include page="/WEB-INF/views/layout/main.jsp"/>
