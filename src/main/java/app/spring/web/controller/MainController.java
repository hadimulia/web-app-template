package app.spring.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class MainController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "expired", required = false) String expired,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        
        if (expired != null) {
            model.addAttribute("error", "Your session has expired!");
        }
        
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("userMenus", session.getAttribute("userMenus"));
        return "dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }
    
    @GetMapping("/force-logout")
    public String forceLogout(@RequestParam("username") String username, 
                             HttpServletRequest request, HttpServletResponse response) {
        // This would be used by admin to force logout specific users
        // Implementation would involve session registry to track active sessions
        return "redirect:/users?message=User " + username + " has been logged out";
    }
}