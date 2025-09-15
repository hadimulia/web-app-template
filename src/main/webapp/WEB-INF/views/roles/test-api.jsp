<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Simple test page to verify API endpoints are working -->
<div class="container-fluid">
    <h1>Role Management - API Test</h1>
    
    <div class="card">
        <div class="card-header">
            <h5>Test API Endpoints</h5>
        </div>
        <div class="card-body">
            <div class="row mb-3">
                <div class="col-md-12">
                    <button class="btn btn-primary" onclick="testApiList()">Test /api/list</button>
                    <button class="btn btn-secondary" onclick="testApiSearch()">Test /api/search</button>
                    <button class="btn btn-info" onclick="testPagination()">Test Pagination</button>
                </div>
            </div>
            
            <div id="test-results" class="mt-3">
                <p>Click the buttons above to test the API endpoints.</p>
            </div>
        </div>
    </div>
    
    <div class="card mt-4">
        <div class="card-header">
            <h5>Manual Search & Pagination Test</h5>
        </div>
        <div class="card-body">
            <div class="row mb-3">
                <div class="col-md-4">
                    <input type="text" id="search-input" class="form-control" placeholder="Search roles...">
                </div>
                <div class="col-md-2">
                    <select id="page-size" class="form-control">
                        <option value="5">5</option>
                        <option value="10" selected>10</option>
                        <option value="25">25</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <button class="btn btn-primary" onclick="loadRoles()">Load Roles</button>
                </div>
            </div>
            
            <div id="roles-container">
                <!-- Roles will be loaded here -->
            </div>
        </div>
    </div>
</div>

<script>
let currentPage = 1;

function testApiList() {
    fetch('${pageContext.request.contextPath}/roles/api/list')
        .then(response => response.json())
        .then(data => {
            document.getElementById('test-results').innerHTML = 
                '<h6>API List Test Result:</h6><pre>' + JSON.stringify(data, null, 2) + '</pre>';
        })
        .catch(error => {
            document.getElementById('test-results').innerHTML = 
                '<h6>API List Test Error:</h6><div class="alert alert-danger">' + error.message + '</div>';
        });
}

function testApiSearch() {
    fetch('${pageContext.request.contextPath}/roles/api/search?query=admin')
        .then(response => response.json())
        .then(data => {
            document.getElementById('test-results').innerHTML = 
                '<h6>API Search Test Result:</h6><pre>' + JSON.stringify(data, null, 2) + '</pre>';
        })
        .catch(error => {
            document.getElementById('test-results').innerHTML = 
                '<h6>API Search Test Error:</h6><div class="alert alert-danger">' + error.message + '</div>';
        });
}

function testPagination() {
    fetch('${pageContext.request.contextPath}/roles/api/list?page=1&size=5&sortBy=role_name&sortDir=asc')
        .then(response => response.json())
        .then(data => {
            document.getElementById('test-results').innerHTML = 
                '<h6>Pagination Test Result:</h6><pre>' + JSON.stringify(data, null, 2) + '</pre>';
        })
        .catch(error => {
            document.getElementById('test-results').innerHTML = 
                '<h6>Pagination Test Error:</h6><div class="alert alert-danger">' + error.message + '</div>';
        });
}

function loadRoles() {
    const search = document.getElementById('search-input').value;
    const pageSize = document.getElementById('page-size').value;
    
    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize,
        search: search,
        sortBy: 'id',
        sortDir: 'asc'
    });
    
    fetch('${pageContext.request.contextPath}/roles/api/list?' + params)
        .then(response => response.json())
        .then(data => {
            renderRoles(data);
        })
        .catch(error => {
            document.getElementById('roles-container').innerHTML = 
                '<div class="alert alert-danger">Error loading roles: ' + error.message + '</div>';
        });
}

function renderRoles(pageResponse) {
    const container = document.getElementById('roles-container');
    
    if (pageResponse.content.length === 0) {
        container.innerHTML = '<div class="alert alert-info">No roles found.</div>';
        return;
    }
    
    let html = '<div class="table-responsive">';
    html += '<table class="table table-bordered table-striped">';
    html += '<thead><tr><th>ID</th><th>Role Name</th><th>Role Code</th><th>Status</th><th>Actions</th></tr></thead>';
    html += '<tbody>';
    
    pageResponse.content.forEach(role => {
        const statusBadge = role.status === 1 
            ? '<span class="badge badge-success">Active</span>'
            : '<span class="badge badge-danger">Inactive</span>';
            
        html += '<tr>';
        html += '<td>' + role.id + '</td>';
        html += '<td>' + role.roleName + '</td>';
        html += '<td><code>' + role.roleCode + '</code></td>';
        html += '<td>' + statusBadge + '</td>';
        html += '<td>';
        html += '<a href="${pageContext.request.contextPath}/roles/view/' + role.id + '" class="btn btn-sm btn-info">View</a> ';
        html += '<a href="${pageContext.request.contextPath}/roles/edit/' + role.id + '" class="btn btn-sm btn-warning">Edit</a>';
        html += '</td>';
        html += '</tr>';
    });
    
    html += '</tbody></table></div>';
    
    // Add pagination info
    html += '<div class="d-flex justify-content-between align-items-center mt-3">';
    html += '<div>Showing ' + ((pageResponse.page - 1) * pageResponse.size + 1) + 
            ' to ' + Math.min(pageResponse.page * pageResponse.size, pageResponse.totalElements) + 
            ' of ' + pageResponse.totalElements + ' entries</div>';
    html += '<div>';
    
    // Previous button
    if (!pageResponse.first) {
        html += '<button class="btn btn-sm btn-outline-primary mr-2" onclick="goToPage(' + (pageResponse.page - 1) + ')">Previous</button>';
    }
    
    // Page info
    html += '<span class="mx-2">Page ' + pageResponse.page + ' of ' + pageResponse.totalPages + '</span>';
    
    // Next button
    if (!pageResponse.last) {
        html += '<button class="btn btn-sm btn-outline-primary ml-2" onclick="goToPage(' + (pageResponse.page + 1) + ')">Next</button>';
    }
    
    html += '</div></div>';
    
    container.innerHTML = html;
}

function goToPage(page) {
    currentPage = page;
    loadRoles();
}

// Load initial data
document.addEventListener('DOMContentLoaded', function() {
    loadRoles();
});

// Search on enter key
document.getElementById('search-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        currentPage = 1;
        loadRoles();
    }
});
</script>
