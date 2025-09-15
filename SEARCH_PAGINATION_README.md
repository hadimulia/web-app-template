# Search and Pagination Feature Documentation

## Overview
This feature adds asynchronous search and pagination functionality to the Role management system and provides a generic taglib that can be reused across all menu items in the application.

## Features Implemented

### 1. Role Management Enhancement
- ✅ Asynchronous search functionality
- ✅ Server-side pagination
- ✅ Sortable columns
- ✅ Real-time search with debouncing
- ✅ Responsive design
- ✅ Loading states and error handling

### 2. Generic Taglib Components
- ✅ `<pageUtil:searchTable>` - Generic search and pagination table
- ✅ Reusable across all menu items
- ✅ Customizable rendering
- ✅ Built-in search, sort, and pagination controls

### 3. Backend Infrastructure
- ✅ `PageRequest` and `PageResponse` DTOs
- ✅ Generic pagination in mappers
- ✅ Async support with CompletableFuture
- ✅ SQL injection prevention with column validation

## Files Added/Modified

### New Files Created:
1. **Models**
   - `PageRequest.java` - Pagination request DTO
   - `PageResponse.java` - Pagination response DTO

2. **Taglib Components**
   - `pageUtil.tld` - Tag library descriptor
   - `SearchTableTag.java` - Main search table tag
   - `PaginationTag.java` - Pagination controls tag

3. **Frontend Resources**
   - `search-table.js` - JavaScript component for async operations
   - `search-table.css` - Styling for the search table

4. **Configuration**
   - `AsyncConfig.java` - Spring async configuration

### Modified Files:
1. **Backend**
   - `RoleMapper.java` - Added pagination queries
   - `RoleService.java` - Added pagination methods
   - `RoleController.java` - Added async API endpoints
   - `UserMapper.java` - Added pagination queries (for reuse example)

2. **Frontend**
   - `roles/list-content.jsp` - Updated to use generic taglib

## Usage Guide

### 1. Using the Generic Search Table

```jsp
<%@ taglib prefix="pageUtil" uri="http://app.spring.web/pageUtil" %>

<pageUtil:searchTable 
    id="roles"
    apiUrl="${pageContext.request.contextPath}/roles/api/list"
    searchUrl="${pageContext.request.contextPath}/roles/api/search"
    title="Role Management"
    createUrl="${pageContext.request.contextPath}/roles/create"
    createLabel="Add New Role"
    pageSize="10"
    sortBy="id"
    sortDir="asc">
    
    <!-- Table Headers -->
    <tr>
        <th data-sortable="true" data-column="id">ID</th>
        <th data-sortable="true" data-column="role_name">Role Name</th>
        <th>Actions</th>
    </tr>
</pageUtil:searchTable>

<script>
// Customize row rendering
window.rolesTable.options.renderRow = function(item) {
    return `
        <tr>
            <td>${item.id}</td>
            <td>${item.roleName}</td>
            <td>
                <a href="/edit/${item.id}" class="btn btn-sm btn-warning">Edit</a>
            </td>
        </tr>
    `;
};
</script>
```

### 2. Controller Pattern for API Endpoints

```java
@GetMapping("/api/list")
@ResponseBody
public CompletableFuture<ResponseEntity<PageResponse<YourModel>>> listAsync(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(defaultValue = "") String search) {
    
    return CompletableFuture.supplyAsync(() -> {
        try {
            PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
            pageRequest.setSearch(search);
            
            PageResponse<YourModel> result = yourService.findWithPagination(pageRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    });
}
```

### 3. Service Pattern for Pagination

```java
public PageResponse<YourModel> findWithPagination(PageRequest pageRequest) {
    String sortBy = validateSortColumn(pageRequest.getSortBy());
    
    List<YourModel> items = yourMapper.findWithPagination(
        pageRequest.hasSearch() ? pageRequest.getSearch() : null,
        sortBy,
        pageRequest.getSortDir(),
        pageRequest.getOffset(),
        pageRequest.getSize()
    );
    
    long totalElements = yourMapper.countWithSearch(
        pageRequest.hasSearch() ? pageRequest.getSearch() : null
    );
    
    return new PageResponse<>(items, pageRequest, totalElements);
}
```

### 4. Mapper Pattern for Database Queries

```java
@Select("<script>" +
        "SELECT * FROM your_table " +
        "<where>" +
        "<if test='search != null and search != \"\"'>" +
        "AND (column1 LIKE CONCAT('%', #{search}, '%') " +
        "OR column2 LIKE CONCAT('%', #{search}, '%'))" +
        "</if>" +
        "</where>" +
        "ORDER BY ${sortBy} ${sortDir} " +
        "LIMIT #{offset}, #{size}" +
        "</script>")
List<YourModel> findWithPagination(@Param("search") String search,
                                  @Param("sortBy") String sortBy,
                                  @Param("sortDir") String sortDir,
                                  @Param("offset") int offset,
                                  @Param("size") int size);
```

## Security Features

### 1. SQL Injection Prevention
- Column names are validated against a whitelist
- Search parameters are properly escaped using MyBatis

### 2. Input Validation
- Page size limits (max 100 items)
- Sort direction validation (asc/desc only)
- Search term length limits

## Performance Optimizations

### 1. Frontend
- Debounced search (300ms delay)
- Loading states to prevent multiple requests
- Efficient DOM manipulation

### 2. Backend
- Asynchronous processing with CompletableFuture
- Database query optimization with LIMIT/OFFSET
- Connection pooling via Spring Boot

### 3. Database
- Proper indexing on searchable columns recommended
- Pagination using LIMIT/OFFSET for MySQL

## Customization Options

### 1. Table Appearance
- Modify `search-table.css` for styling
- Override CSS classes in your custom stylesheets

### 2. Search Behavior
- Adjust debounce delay in JavaScript
- Customize search column selection in mappers

### 3. Pagination Controls
- Change page size options in the taglib
- Modify pagination button appearance

## Example Implementation for Other Menus

To implement search and pagination for Users menu:

1. **Add API endpoint to UserController:**
```java
@GetMapping("/api/list")
@ResponseBody
public CompletableFuture<ResponseEntity<PageResponse<User>>> listAsync(/* parameters */) {
    // Implementation similar to RoleController
}
```

2. **Update users/list-content.jsp:**
```jsp
<pageUtil:searchTable 
    id="users"
    apiUrl="${pageContext.request.contextPath}/users/api/list"
    title="User Management">
    <!-- headers -->
</pageUtil:searchTable>
```

3. **Implement pagination in UserService:**
```java
public PageResponse<User> findWithPagination(PageRequest pageRequest) {
    // Implementation similar to RoleService
}
```

## Browser Compatibility
- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## Dependencies
- jQuery (already included)
- Bootstrap 4/5 (already included)
- FontAwesome (already included)

## Future Enhancements
1. Export functionality (CSV/Excel)
2. Advanced filtering options
3. Column show/hide toggles
4. Bulk operations
5. Real-time updates via WebSocket

## Troubleshooting

### Common Issues:
1. **JavaScript errors**: Ensure jQuery is loaded before search-table.js
2. **No data loading**: Check API endpoint URLs and CORS settings
3. **Sorting not working**: Verify column names match database columns
4. **Search not triggering**: Check for JavaScript console errors

### Debug Mode:
Enable debug logging by adding to application.properties:
```properties
logging.level.app.spring.web.mapper=DEBUG
logging.level.app.spring.web.service=DEBUG
```
