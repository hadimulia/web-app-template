package app.spring.web.controller;

import app.spring.web.model.Role;
import app.spring.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }
    
    @GetMapping("/roles/count")
    public String getRoleCount() {
        List<Role> roles = roleService.findAll();
        return "Total roles: " + roles.size();
    }
}
