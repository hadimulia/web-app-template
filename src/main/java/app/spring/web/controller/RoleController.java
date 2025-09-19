package app.spring.web.controller;

import app.spring.web.model.Menu;
import app.spring.web.model.Role;
import app.spring.web.model.RoleMenu;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.menu.MenuServiceImpl;
import app.spring.web.service.role.RoleServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private MenuServiceImpl menuService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Role Management");
        return "roles/list";
    }

    @GetMapping("/test-api")
    public String testApi(Model model) {
        model.addAttribute("title", "Role API Test");
        model.addAttribute("content", "/WEB-INF/views/roles/test-api.jsp");
        return "layout/main";
    }

    // Async API endpoint for pagination and search
    @GetMapping("/api/list")
    @ResponseBody
    public CompletableFuture<ResponseEntity<PageResponse<Role>>> listAsync(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String search) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
                pageRequest.setSearch(search);
                
                PageResponse<Role> result = roleService.findWithPagination(pageRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                // Log error
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    // API endpoint for quick search suggestions
    @GetMapping("/api/search")
    @ResponseBody
    public CompletableFuture<ResponseEntity<Map<String, Object>>> searchAsync(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(1, limit, "role_name", "asc");
                pageRequest.setSearch(query);
                
                PageResponse<Role> result = roleService.findWithPagination(pageRequest);
                
                Map<String, Object> response = new HashMap<>();
                response.put("suggestions", result.getContent());
                response.put("totalCount", result.getTotalElements());
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("menus", menuService.findActiveMenus());
        return "roles/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Role role = roleService.get(id);
        if (role == null) {
            return "redirect:/roles?error=Role not found";
        }

        List<RoleMenu> roleMenus = roleService.getRoleMenus(id);
        List<Long> selectedMenuIds = new ArrayList<>();
        for (RoleMenu roleMenu : roleMenus) {
            selectedMenuIds.add(roleMenu.getMenuId());
        }

        model.addAttribute("role", role);
        model.addAttribute("menus", menuService.findActiveMenus());
        model.addAttribute("selectedMenuIds", selectedMenuIds);
        return "roles/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Role role, BindingResult result,
                      @RequestParam(value = "menuIds", required = false) List<Long> menuIds,
                      Model model, RedirectAttributes redirectAttributes) {

        // Validation
        if (roleService.existsByRoleCode(role.getRoleCode(), role.getId())) {
            result.rejectValue("roleCode", "error.role", "Role code already exists");
        }

        if (roleService.existsByRoleName(role.getRoleName(), role.getId())) {
            result.rejectValue("roleName", "error.role", "Role name already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("menus", menuService.findActiveMenus());
            model.addAttribute("selectedMenuIds", menuIds);
            return "roles/form";
        }

        try {
            roleService.save(role);
            if (menuIds != null && !menuIds.isEmpty()) {
                roleService.assignMenusToRole(role.getId(), menuIds);
            }

            String message = role.getId() == null ? "Role created successfully!" : "Role updated successfully!";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/roles";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving role: " + e.getMessage());
            model.addAttribute("menus", menuService.findActiveMenus());
            model.addAttribute("selectedMenuIds", menuIds);
            return "roles/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.get(id);
            if (role == null) {
                redirectAttributes.addFlashAttribute("error", "Role not found");
                return "redirect:/roles";
            }

            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting role: " + e.getMessage());
        }
        return "redirect:/roles";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Role role = roleService.get(id);
        if (role == null) {
            return "redirect:/roles?error=Role not found";
        }

        List<Menu> roleMenus = menuService.findMenusByRoleId(id);
        role.setMenus(roleMenus);

        model.addAttribute("role", role);
        return "roles/view";
    }
}