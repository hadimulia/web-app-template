package app.spring.web.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for menu-related operations
 */
public class MenuUtil {
    
    /**
     * Determines if a menu URL matches the current request path
     * This method provides more intelligent matching than simple string comparison
     * 
     * @param request The HTTP request
     * @param menuUrl The menu URL to check
     * @return true if the menu should be considered active
     */
    public static boolean isMenuActive(HttpServletRequest request, String menuUrl) {
        if (request == null || menuUrl == null || menuUrl.trim().isEmpty()) {
            return false;
        }
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Remove context path from request URI for comparison
        String requestPath = requestURI;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            requestPath = requestURI.substring(contextPath.length());
        }
        
        // Normalize paths (remove trailing slashes)
        requestPath = normalizePath(requestPath);
        menuUrl = normalizePath(menuUrl);
        
        // Direct match
        if (requestPath.equals(menuUrl)) {
            return true;
        }
        
        // Check if request path starts with menu URL (for nested pages)
        if (requestPath.startsWith(menuUrl + "/")) {
            return true;
        }
        
        // Special cases for common patterns
        if (menuUrl.equals("/dashboard") && (requestPath.equals("/") || requestPath.equals(""))) {
            return true;
        }
        
        // For CRUD operations, check base path
        String basePath = getBasePath(requestPath);
        if (basePath.equals(menuUrl)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if any child menu is active for parent menu highlighting
     * 
     * @param request The HTTP request
     * @param childUrls Array of child menu URLs
     * @return true if any child menu is active
     */
    public static boolean hasActiveChild(HttpServletRequest request, String[] childUrls) {
        if (childUrls == null || childUrls.length == 0) {
            return false;
        }
        
        for (String childUrl : childUrls) {
            if (isMenuActive(request, childUrl)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Normalize path by removing trailing slash and ensuring it starts with /
     */
    private static String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "/";
        }
        
        path = path.trim();
        
        // Ensure path starts with /
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        // Remove trailing slash (except for root)
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        return path;
    }
    
    /**
     * Extract base path from request path (useful for CRUD operations)
     * e.g., /users/edit/123 -> /users
     */
    private static String getBasePath(String requestPath) {
        if (requestPath == null || requestPath.trim().isEmpty()) {
            return "/";
        }
        
        String[] segments = requestPath.split("/");
        if (segments.length >= 2) {
            return "/" + segments[1];
        }
        
        return requestPath;
    }
    
    /**
     * Generate a CSS class string for menu item based on active state
     */
    public static String getMenuClass(HttpServletRequest request, String menuUrl, String baseClass) {
        StringBuilder cssClass = new StringBuilder(baseClass != null ? baseClass : "nav-link");
        
        if (isMenuActive(request, menuUrl)) {
            cssClass.append(" active");
        }
        
        return cssClass.toString();
    }
    
    /**
     * Get aria-current attribute value for active menu items
     */
    public static String getAriaCurrent(HttpServletRequest request, String menuUrl) {
        return isMenuActive(request, menuUrl) ? "page" : null;
    }
}
