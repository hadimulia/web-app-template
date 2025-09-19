package app.spring.web.controller;

import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.model.Role;
import app.spring.web.model.User;
import app.spring.web.model.UserRole;
import app.spring.web.service.role.RoleServiceImpl;
import app.spring.web.service.user.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserServiceImpl userService;
    
    @Autowired
    private RoleServiceImpl roleService;
    
    @GetMapping
    public String list(Model model) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        return "users/list";
    }
    
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findActiveRoles());
        return "users/form";
    }
    
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.get(id);
        if (user == null) {
            return "redirect:/users?error=User not found";
        }
        
        List<UserRole> userRoles = userService.getUserRoles(id);
        List<Long> selectedRoleIds = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            selectedRoleIds.add(userRole.getRoleId());
        }
        
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findActiveRoles());
        model.addAttribute("selectedRoleIds", selectedRoleIds);
        return "users/form";
    }
    
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute User user, BindingResult result,
                      @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                      Model model, RedirectAttributes redirectAttributes) {
        
        // Validation
        if (userService.existsByUsername(user.getUsername(), user.getId())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }
        
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && 
            userService.existsByEmail(user.getEmail(), user.getId())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findActiveRoles());
            model.addAttribute("selectedRoleIds", roleIds);
            return "users/form";
        }
        
        try {
            userService.save(user);
            if (roleIds != null && !roleIds.isEmpty()) {
                userService.assignRolesToUser(user.getId(), roleIds);
            }
            
            String message = user.getId() == null ? "User created successfully!" : "User updated successfully!";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving user: " + e.getMessage());
            model.addAttribute("roles", roleService.findActiveRoles());
            model.addAttribute("selectedRoleIds", roleIds);
            return "users/form";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.get(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/users";
            }
            
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/users";
    }
    
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        User user = userService.get(id);
        if (user == null) {
            return "redirect:/users?error=User not found";
        }
        
        List<Role> userRoles = roleService.findRolesByUserId(id);
        user.setRoles(userRoles);
        
        model.addAttribute("user", user);
        return "users/view";
    }

    // API endpoints for async search and pagination
    @GetMapping("/api/list")
    @ResponseBody
    public CompletableFuture<ResponseEntity<PageResponse<User>>> listAsync(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String search) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
                pageRequest.setSearch(search);
                
                PageResponse<User> result = userService.findWithPagination(pageRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    @GetMapping("/api/search")
    @ResponseBody
    public CompletableFuture<ResponseEntity<PageResponse<User>>> searchAsync(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam String search) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
                pageRequest.setSearch(search);
                
                PageResponse<User> result = userService.findWithPagination(pageRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        });
    }
}