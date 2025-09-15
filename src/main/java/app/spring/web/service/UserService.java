package app.spring.web.service;

import app.spring.web.mapper.UserMapper;
import app.spring.web.mapper.UserRoleMapper;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.model.User;
import app.spring.web.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserService {
    
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserMapper userMapper, UserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    public List<User> findAll() {
        return userMapper.selectAll();
    }
    
    public List<User> findActiveUsers() {
        return userMapper.findActiveUsers();
    }
    
    public User findById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }
    
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    public User findUserWithRoles(Long userId) {
        return userMapper.findUserWithRoles(userId);
    }
    
    public boolean existsByUsername(String username, Long excludeId) {
        return userMapper.countByUsernameExcludeId(username, excludeId != null ? excludeId : -1L) > 0;
    }
    
    public boolean existsByEmail(String email, Long excludeId) {
        return userMapper.countByEmailExcludeId(email, excludeId != null ? excludeId : -1L) > 0;
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            // Create new user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setStatus(1);
            userMapper.insertSelective(user);
        } else {
            // Update existing user
            user.setUpdatedAt(LocalDateTime.now());
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // Don't update password if it's empty
                User existingUser = userMapper.selectByPrimaryKey(user.getId());
                user.setPassword(existingUser.getPassword());
            }
            userMapper.updateByPrimaryKeySelective(user);
        }
        return user;
    }
    
    public void delete(Long id) {
        // Delete user roles first
        userRoleMapper.deleteByUserId(id);
        // Delete user
        userMapper.deleteByPrimaryKey(id);
    }
    
    public void updateLastLogin(Long userId) {
        userMapper.updateLastLogin(userId, LocalDateTime.now());
    }
    
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // Delete existing roles
        userRoleMapper.deleteByUserId(userId);
        
        // Assign new roles
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole(userId, roleId);
                userRole.setCreatedAt(LocalDateTime.now());
                userRoleMapper.insertSelective(userRole);
            }
        }
    }
    
    public List<UserRole> getUserRoles(Long userId) {
        return userRoleMapper.findByUserId(userId);
    }

    // Add pagination support
    public PageResponse<User> findWithPagination(PageRequest pageRequest) {
        // Validate sort column to prevent SQL injection
        String sortBy = validateSortColumn(pageRequest.getSortBy());
        
        List<User> users = userMapper.findWithPagination(
            pageRequest.hasSearch() ? pageRequest.getSearch() : null,
            sortBy,
            pageRequest.getSortDir(),
            pageRequest.getOffset(),
            pageRequest.getSize()
        );
        
        long totalElements = userMapper.countWithSearch(
            pageRequest.hasSearch() ? pageRequest.getSearch() : null
        );
        
        return new PageResponse<>(users, pageRequest, totalElements);
    }

    private String validateSortColumn(String sortBy) {
        // Whitelist of allowed sort columns to prevent SQL injection
        switch (sortBy) {
            case "id":
            case "username":
            case "email":
            case "full_name":
            case "phone":
            case "status":
            case "last_login":
            case "created_at":
            case "updated_at":
                return sortBy;
            default:
                return "id"; // default safe column
        }
    }
}