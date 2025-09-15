package app.spring.web.security;

import app.spring.web.model.Menu;
import app.spring.web.model.User;
import app.spring.web.service.MenuService;
import app.spring.web.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private final UserService userService;
    private final MenuService menuService;
    
    public LoginSuccessHandler(UserService userService, MenuService menuService) {
        this.userService = userService;
        this.menuService = menuService;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        
        // Update last login time
        userService.updateLastLogin(user.getId());
        
        // Cache user details and menus in session
        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("fullName", user.getFullName());
        
        // Cache user menus in session for dynamic menu rendering
        List<Menu> userMenuTree = menuService.getUserMenuTree(user.getId());
        session.setAttribute("userMenus", userMenuTree);
        
        // Set session timeout (30 minutes)
        session.setMaxInactiveInterval(30 * 60);
        
        // Redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}