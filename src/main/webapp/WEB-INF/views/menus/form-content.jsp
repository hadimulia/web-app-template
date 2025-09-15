<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Heading -->
<div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">${menu.id == null ? 'Add New Menu' : 'Edit Menu'}</h1>
    <a href="${pageContext.request.contextPath}/menus" class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Back to Menus
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
                               value="${menu.menuName}" required maxlength="50">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuCode">Menu Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="menuCode" name="menuCode" 
                               value="${menu.menuCode}" required maxlength="50">
                        <small class="form-text text-muted">Unique identifier for the menu</small>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="parentId">Parent Menu</label>
                        <select class="form-control" id="parentId" name="parentId">
                            <option value="">-- Root Menu --</option>
                            <c:forEach var="parentMenu" items="${parentMenus}">
                                <option value="${parentMenu.id}" 
                                        ${menu.parentId == parentMenu.id ? 'selected' : ''}>
                                    ${parentMenu.menuName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuUrl">Menu URL</label>
                        <input type="text" class="form-control" id="menuUrl" name="menuUrl" 
                               value="${menu.menuUrl}" maxlength="255" placeholder="/example/path">
                        <small class="form-text text-muted">Leave empty for parent menus</small>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="menuIcon">Menu Icon</label>
                        <input type="text" class="form-control" id="menuIcon" name="menuIcon" 
                               value="${menu.menuIcon}" maxlength="50" placeholder="fa-dashboard">
                        <small class="form-text text-muted">FontAwesome icon class (without 'fas')</small>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="sortOrder">Sort Order</label>
                        <input type="number" class="form-control" id="sortOrder" name="sortOrder" 
                               value="${menu.sortOrder}" min="0">
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="status">Status</label>
                        <select class="form-control" id="status" name="status">
                            <option value="1" ${menu.status == 1 ? 'selected' : ''}>Active</option>
                            <option value="0" ${menu.status == 0 ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="menuType">Menu Type</label>
                        <select class="form-control" id="menuType" name="menuType">
                            <option value="1" ${menu.menuType == 1 ? 'selected' : ''}>Menu</option>
                            <option value="2" ${menu.menuType == 2 ? 'selected' : ''}>Button</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="3" maxlength="255">${menu.description}</textarea>
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

<!-- Icon Preview -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Icon Preview</h6>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-12">
                <p><strong>Common FontAwesome Icons:</strong></p>
                <div class="icon-grid">
                    <span class="icon-item" onclick="setIcon('fa-dashboard')"><i class="fas fa-dashboard"></i> fa-dashboard</span>
                    <span class="icon-item" onclick="setIcon('fa-users')"><i class="fas fa-users"></i> fa-users</span>
                    <span class="icon-item" onclick="setIcon('fa-user-shield')"><i class="fas fa-user-shield"></i> fa-user-shield</span>
                    <span class="icon-item" onclick="setIcon('fa-list')"><i class="fas fa-list"></i> fa-list</span>
                    <span class="icon-item" onclick="setIcon('fa-cogs')"><i class="fas fa-cogs"></i> fa-cogs</span>
                    <span class="icon-item" onclick="setIcon('fa-chart-bar')"><i class="fas fa-chart-bar"></i> fa-chart-bar</span>
                    <span class="icon-item" onclick="setIcon('fa-file-alt')"><i class="fas fa-file-alt"></i> fa-file-alt</span>
                    <span class="icon-item" onclick="setIcon('fa-history')"><i class="fas fa-history"></i> fa-history</span>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.icon-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 10px;
    margin-top: 10px;
}

.icon-item {
    padding: 8px;
    border: 1px solid #e3e6f0;
    border-radius: 4px;
    cursor: pointer;
    text-align: center;
    transition: background-color 0.2s;
}

.icon-item:hover {
    background-color: #f8f9fc;
}

.icon-item i {
    margin-right: 5px;
}
</style>

<script>
function setIcon(iconClass) {
    document.getElementById('menuIcon').value = iconClass;
}
</script>