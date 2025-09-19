<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${user.id == null ? 'Add New User' : 'Edit User'}</h1>
    <a href="${pageContext.request.contextPath}/users" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Users
    </a>
</div>

<!-- Messages -->
<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${error}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

<!-- User Form -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">${user.id == null ? 'User Information' : 'Edit User Information'}</h6>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/users/save">
            <c:if test="${user.id != null}">
                <input type="hidden" name="id" value="${user.id}"/>
            </c:if>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="username">Username <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="username" name="username" 
                               value="${user.username}" required maxlength="50">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${user.email}" maxlength="100">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="fullName">Full Name</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" 
                               value="${user.fullName}" maxlength="100">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="phone">Phone</label>
                        <input type="text" class="form-control" id="phone" name="phone" 
                               value="${user.phone}" maxlength="20">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="password">Password ${user.id == null ? '*' : '(Leave blank to keep current)'}</label>
                        <input type="password" class="form-control" id="password" name="password" 
                               ${user.id == null ? 'required' : ''}>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="status">Status</label>
                        <select class="form-control" id="status" name="status">
                            <option value="1" ${user.status == 1 ? 'selected' : ''}>Active</option>
                            <option value="0" ${user.status == 0 ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <!-- Role Assignment -->
            <div class="form-group">
                <label>Assign Roles</label>
                <div class="row">
                    <c:forEach var="role" items="${roles}">
                        <div class="col-md-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="roleIds" 
                                       value="${role.id}" id="role_${role.id}"
                                       <c:if test="${selectedRoleIds.contains(role.id)}">checked</c:if>>
                                <label class="form-check-label" for="role_${role.id}">
                                    ${role.roleName}
                                </label>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> ${user.id == null ? 'Create User' : 'Update User'}
                </button>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>