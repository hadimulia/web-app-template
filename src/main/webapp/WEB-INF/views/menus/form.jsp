<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${menu.id == null ? 'Add New Menu' : 'Edit Menu'}</h1>
    <a href="${pageContext.request.contextPath}/menus" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Menus
    </a>
</div>

<!-- Menu Form -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">${menu.id == null ? 'Menu Information' : 'Edit Menu Information'}</h6>
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/menus/save">
            <c:if test="${menu.id != null}">
                <input type="hidden" name="id" value="${menu.id}"/>
            </c:if>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuName">Menu Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="menuName" name="menuName" 
                               value="${menu.menuName}" required maxlength="100">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuCode">Menu Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="menuCode" name="menuCode" 
                               value="${menu.menuCode}" required maxlength="50">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuUrl">Menu URL</label>
                        <input type="text" class="form-control" id="menuUrl" name="menuUrl" 
                               value="${menu.menuUrl}" maxlength="255">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuIcon">Menu Icon</label>
                        <input type="text" class="form-control" id="menuIcon" name="menuIcon" 
                               value="${menu.menuIcon}" maxlength="50">
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="parentId">Parent Menu</label>
                        <select class="form-control" id="parentId" name="parentId">
                            <option value="">-- Root Menu --</option>
                            <c:forEach var="parentMenu" items="${parentMenus}">
                                <option value="${parentMenu.id}" ${menu.parentId == parentMenu.id ? 'selected' : ''}>
                                    ${parentMenu.menuName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="menuType">Menu Type</label>
                        <select class="form-control" id="menuType" name="menuType">
                            <option value="MENU" ${menu.menuType == 'MENU' ? 'selected' : ''}>Menu</option>
                            <option value="ACTION" ${menu.menuType == 'ACTION' ? 'selected' : ''}>Action</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="sortOrder">Sort Order</label>
                        <input type="number" class="form-control" id="sortOrder" name="sortOrder" 
                               value="${menu.sortOrder}" min="0">
                    </div>
                </div>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> ${menu.id == null ? 'Create Menu' : 'Update Menu'}
                </button>
                <a href="${pageContext.request.contextPath}/menus" class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>