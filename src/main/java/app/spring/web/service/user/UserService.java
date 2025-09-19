package app.spring.web.service.user;

import app.spring.web.model.User;
import app.spring.web.model.UserRole;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericService;

import java.util.List;

public interface UserService extends GenericService<User, Long> {
    
    List<User> findActiveUsers();
    
    User findByUsername(String username);
    
    User findUserWithRoles(Long userId);
    
    boolean existsByUsername(String username, Long excludeId);
    
    boolean existsByEmail(String email, Long excludeId);
    
    PageResponse<User> findWithPagination(PageRequest pageRequest);
    
    void updateLastLogin(Long userId);
    
    void assignRolesToUser(Long userId, List<Long> roleIds);
    
    List<UserRole> getUserRoles(Long userId);
}
