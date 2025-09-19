package app.spring.web.service.role;

import app.spring.web.model.Role;
import app.spring.web.model.RoleMenu;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericService;

import java.util.List;

public interface RoleService extends GenericService<Role, Long> {
    
    List<Role> findActiveRoles();
    
    Role findByRoleCode(String roleCode);
    
    List<Role> findRolesByUserId(Long userId);
    
    PageResponse<Role> findWithPagination(PageRequest pageRequest);
    
    boolean existsByRoleCode(String roleCode, Long excludeId);
    
    boolean existsByRoleName(String roleName, Long excludeId);
    
    void assignMenusToRole(Long roleId, List<Long> menuIds);
    
    List<RoleMenu> getRoleMenus(Long roleId);
}
