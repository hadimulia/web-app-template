package app.spring.web.security;

import app.spring.web.model.Role;
import app.spring.web.model.User;
import app.spring.web.service.RoleService;
import app.spring.web.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserService userService;
    private final RoleService roleService;
    
    public CustomUserDetailsService(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }
        
        List<Role> roles = roleService.findRolesByUserId(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
        }
        
        return new CustomUserPrincipal(user, authorities);
    }
}