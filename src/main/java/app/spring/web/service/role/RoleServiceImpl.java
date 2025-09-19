package app.spring.web.service.role;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.spring.web.mapper.RoleMapper;
import app.spring.web.mapper.RoleMenuMapper;
import app.spring.web.mapper.UserRoleMapper;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.model.Role;
import app.spring.web.model.RoleMenu;
import app.spring.web.service.generic.GenericServiceImpl;

@Service
@Transactional
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements RoleService {

    private RoleMapper roleMapper;

    private RoleMenuMapper roleMenuMapper;

    private UserRoleMapper userRoleMapper;

    public RoleServiceImpl(RoleMapper mapper,UserRoleMapper userRoleMapper, RoleMenuMapper roleMenuMapper) {
        super(mapper);
        this.roleMapper = mapper;
        this.roleMenuMapper = roleMenuMapper;
        this.userRoleMapper = userRoleMapper;
    }

    public List<Role> findActiveRoles() {
        return roleMapper.findActiveRoles();
    }

    public Role findByRoleCode(String roleCode) {
        return roleMapper.findByRoleCode(roleCode);
    }

    public List<Role> findRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }

    // Add pagination support
    public PageResponse<Role> findWithPagination(PageRequest pageRequest) {
        // Validate sort column to prevent SQL injection
        String sortBy = validateSortColumn(pageRequest.getSortBy());
        
        List<Role> roles = roleMapper.findWithPagination(
            pageRequest.hasSearch() ? pageRequest.getSearch() : null,
            sortBy,
            pageRequest.getSortDir(),
            pageRequest.getOffset(),
            pageRequest.getSize()
        );
        
        long totalElements = roleMapper.countWithSearch(
            pageRequest.hasSearch() ? pageRequest.getSearch() : null
        );
        
        return new PageResponse<>(roles, pageRequest, totalElements);
    }

    private String validateSortColumn(String sortBy) {
        // Whitelist of allowed sort columns to prevent SQL injection
        switch (sortBy.toLowerCase()) {
            case "id":
                return "id";
            case "rolename":
            case "role_name":
                return "role_name";
            case "rolecode":
            case "role_code":
                return "role_code";
            case "description":
                return "description";
            case "status":
                return "status";
            case "createdat":
            case "created_at":
                return "created_at";
            case "updatedat":
            case "updated_at":
                return "updated_at";
            default:
                return "id"; // Default safe sort column
        }
    }

    public boolean existsByRoleCode(String roleCode, Long excludeId) {
        return roleMapper.countByRoleCodeExcludeId(roleCode, excludeId != null ? excludeId : -1L) > 0;
    }

    public boolean existsByRoleName(String roleName, Long excludeId) {
        return roleMapper.countByRoleNameExcludeId(roleName, excludeId != null ? excludeId : -1L) > 0;
    }

    @Override
    public Role save(Role role) {
        try {
            if (role.getId() == null) {
                // Create new role
                role.setCreatedAt(LocalDateTime.now());
                role.setStatus(1);
                roleMapper.insertSelective(role);
            } else {
                // Update existing role
                role.setUpdatedAt(LocalDateTime.now());
                roleMapper.updateByPrimaryKeySelective(role);
            }
            return role;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving role", e);
        }
    }

    public void deleteRole(Long id) {
        try {
            // Delete role menus first
            roleMenuMapper.deleteByRoleId(id);
            // Delete user roles
            userRoleMapper.deleteByRoleId(id);
            // Delete role
            roleMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting role", e);
        }
    }

    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        // Delete existing menus
        roleMenuMapper.deleteByRoleId(roleId);

        // Assign new menus
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu(roleId, menuId);
                roleMenu.setCreatedAt(LocalDateTime.now());
                roleMenuMapper.insertSelective(roleMenu);
            }
        }
    }

    public List<RoleMenu> getRoleMenus(Long roleId) {
        return roleMenuMapper.findByRoleId(roleId);
    }
}