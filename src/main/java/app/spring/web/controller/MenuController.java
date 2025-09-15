package app.spring.web.controller;

import app.spring.web.model.Menu;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.MenuService;
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
@RequestMapping("/menus")
public class MenuController {
    
    @Autowired
    private MenuService menuService;
    
    @GetMapping
    public String list(Model model) {
        List<Menu> menus = menuService.findAll();
        // Build tree structure for display
        List<Menu> menuTree = menuService.buildMenuTree(menus);
        // Flatten the tree for easier JSP rendering
        List<MenuDisplay> flatMenus = flattenMenuTree(menuTree, 0);
        model.addAttribute("menus", flatMenus);
        return "menus/list";
    }
    
    private List<MenuDisplay> flattenMenuTree(List<Menu> menus, int level) {
        List<MenuDisplay> result = new ArrayList<>();
        for (Menu menu : menus) {
            result.add(new MenuDisplay(menu, level));
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                result.addAll(flattenMenuTree(menu.getChildren(), level + 1));
            }
        }
        return result;
    }
    
    // Inner class to hold menu with level information
    public static class MenuDisplay {
        private Menu menu;
        private int level;
        
        public MenuDisplay(Menu menu, int level) {
            this.menu = menu;
            this.level = level;
        }
        
        public Menu getMenu() { return menu; }
        public int getLevel() { return level; }
        public String getIndent() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            return sb.toString();
        }
    }
    
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("menu", new Menu());
        model.addAttribute("parentMenus", menuService.findRootMenus());
        return "menus/form";
    }
    
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Menu menu = menuService.findById(id);
        if (menu == null) {
            return "redirect:/menus?error=Menu not found";
        }
        
        // Set parent name for display
        if (menu.getParentId() != null) {
            Menu parentMenu = menuService.findById(menu.getParentId());
            if (parentMenu != null) {
                menu.setParentName(parentMenu.getMenuName());
            }
        }
        
        model.addAttribute("menu", menu);
        model.addAttribute("parentMenus", menuService.findRootMenus());
        return "menus/form";
    }
    
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Menu menu, BindingResult result,
                      Model model, RedirectAttributes redirectAttributes) {
        
        // Validation
        if (menuService.existsByMenuCode(menu.getMenuCode(), menu.getId())) {
            result.rejectValue("menuCode", "error.menu", "Menu code already exists");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("parentMenus", menuService.findRootMenus());
            return "menus/form";
        }
        
        try {
            menuService.save(menu);
            String message = menu.getId() == null ? "Menu created successfully!" : "Menu updated successfully!";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/menus";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving menu: " + e.getMessage());
            model.addAttribute("parentMenus", menuService.findRootMenus());
            return "menus/form";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Menu menu = menuService.findById(id);
            if (menu == null) {
                redirectAttributes.addFlashAttribute("error", "Menu not found");
                return "redirect:/menus";
            }
            
            if (menuService.hasChildMenus(id)) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete menu with child menus");
                return "redirect:/menus";
            }
            
            menuService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Menu deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting menu: " + e.getMessage());
        }
        return "redirect:/menus";
    }
    
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Menu menu = menuService.findById(id);
        if (menu == null) {
            return "redirect:/menus?error=Menu not found";
        }
        
        // Set parent name for display
        if (menu.getParentId() != null) {
            Menu parentMenu = menuService.findById(menu.getParentId());
            if (parentMenu != null) {
                menu.setParentName(parentMenu.getMenuName());
            }
        }
        
        // Get child menus
        List<Menu> childMenus = menuService.findChildMenus(id);
        menu.setChildren(childMenus);
        
        model.addAttribute("menu", menu);
        return "menus/view";
    }

    // API endpoints for async search and pagination
    @GetMapping("/api/list")
    @ResponseBody
    public CompletableFuture<ResponseEntity<PageResponse<Menu>>> listAsync(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String search) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
                pageRequest.setSearch(search);
                
                PageResponse<Menu> result = menuService.findWithPagination(pageRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    @GetMapping("/api/search")
    @ResponseBody
    public CompletableFuture<ResponseEntity<PageResponse<Menu>>> searchAsync(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "menu_name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam String search) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
                pageRequest.setSearch(search);
                
                PageResponse<Menu> result = menuService.findWithPagination(pageRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        });
    }
}