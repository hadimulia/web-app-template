<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${role.id == null ? 'Add New Role' : 'Edit Role'}</h1>
    <a href="${pageContext.request.contextPath}/roles" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Roles
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

<!-- Role Form -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">${role.id == null ? 'Role Information' : 'Edit Role Information'}</h6>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/roles/save">
            <c:if test="${role.id != null}">
                <input type="hidden" name="id" value="${role.id}"/>
            </c:if>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="roleName">Role Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="roleName" name="roleName" 
                               value="${role.roleName}" required maxlength="50">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="roleCode">Role Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="roleCode" name="roleCode" 
                               value="${role.roleCode}" required maxlength="50">
                        <small class="form-text text-muted">Uppercase letters and underscores only (e.g., ADMIN, MANAGER)</small>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-8">
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="3" maxlength="255">${role.description}</textarea>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="status">Status</label>
                        <select class="form-control" id="status" name="status">
                            <option value="1" ${role.status == 1 ? 'selected' : ''}>Active</option>
                            <option value="0" ${role.status == 0 ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <!-- Menu Assignment -->
            <div class="form-group">
                <label>Assign Menus</label>
                <div class="row">
                    <c:forEach var="menu" items="${menus}">
                        <div class="col-md-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="menuIds" 
                                       value="${menu.id}" id="menu_${menu.id}"
                                       <c:if test="${selectedMenuIds.contains(menu.id)}">checked</c:if>>
                                <label class="form-check-label" for="menu_${menu.id}">
                                    <c:if test="${menu.parentId != null}">
                                        &nbsp;&nbsp;&nbsp;&nbsp;
                                    </c:if>
                                    <i class="fas ${menu.menuIcon}"></i> ${menu.menuName}
                                </label>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> ${role.id == null ? 'Create Role' : 'Update Role'}
                </button>
                <a href="${pageContext.request.contextPath}/roles" class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>