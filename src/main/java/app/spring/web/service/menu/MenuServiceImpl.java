package app.spring.web.service.menu;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.spring.web.mapper.MenuMapper;
import app.spring.web.model.Menu;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericServiceImpl;

@Service
@Transactional
public class MenuServiceImpl extends GenericServiceImpl<Menu, Long> implements MenuService {
    
    private MenuMapper menuMapper;
    
    public MenuServiceImpl(MenuMapper mapper) {
        super(mapper);
        this.menuMapper = mapper;
    }
    
    public List<Menu> findActiveMenus() {
        return menuMapper.findActiveMenus();
    }
    
    public Menu findByMenuCode(String menuCode) {
        return menuMapper.findByMenuCode(menuCode);
    }
    
    public List<Menu> findMenusByUserId(Long userId) {
        return menuMapper.findMenusByUserId(userId);
    }
    
    public List<Menu> findMenusByRoleId(Long roleId) {
        return menuMapper.findMenusByRoleId(roleId);
    }
    
    public List<Menu> findRootMenus() {
        return menuMapper.findRootMenus();
    }
    
    public List<Menu> findChildMenus(Long parentId) {
        return menuMapper.findChildMenus(parentId);
    }
    
    public boolean existsByMenuCode(String menuCode, Long excludeId) {
        return menuMapper.countByMenuCodeExcludeId(menuCode, excludeId != null ? excludeId : -1L) > 0;
    }
    
    public boolean hasChildMenus(Long parentId) {
        return menuMapper.countChildMenus(parentId) > 0;
    }
    
    @Override
    public Menu save(Menu menu) {
        try {
            if (menu.getId() == null) {
                // Create new menu
                menu.setCreatedAt(LocalDateTime.now());
                menu.setStatus(1);
                if (menu.getSortOrder() == null) {
                    menu.setSortOrder(0);
                }
                menuMapper.insertSelective(menu);
            } else {
                // Update existing menu
                menu.setUpdatedAt(LocalDateTime.now());
                menuMapper.updateByPrimaryKeySelective(menu);
            }
            
            // Set parent name for display
            if (menu.getParentId() != null) {
                menu.setParentName(menuMapper.getMenuNameById(menu.getParentId()));
            }
            
            return menu;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving menu", e);
        }
    }
    
    public void deleteMenu(Long id) {
        try {
            menuMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting menu", e);
        }
    }

    // Add pagination support
    public PageResponse<Menu> findWithPagination(PageRequest pageRequest) {
        // Validate sort column to prevent SQL injection
        String sortBy = validateSortColumn(pageRequest.getSortBy());
        
        List<Menu> menus = menuMapper.findWithPagination(
            pageRequest.hasSearch() ? pageRequest.getSearch() : null,
            sortBy,
            pageRequest.getSortDir(),
            pageRequest.getOffset(),
            pageRequest.getSize()
        );
        
        long totalElements = menuMapper.countWithSearch(
            pageRequest.hasSearch() ? pageRequest.getSearch() : null
        );
        
        return new PageResponse<>(menus, pageRequest, totalElements);
    }

    private String validateSortColumn(String sortBy) {
        // Whitelist of allowed sort columns to prevent SQL injection
        switch (sortBy) {
            case "id":
            case "menu_name":
            case "menu_code":
            case "parent_id":
            case "menu_url":
            case "sort_order":
            case "menu_type":
            case "status":
            case "created_at":
            case "updated_at":
                return sortBy;
            default:
                return "id"; // default safe column
        }
    }

    public List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> rootMenus = new ArrayList<>();
        Map<Long, List<Menu>> childMenuMap = new HashMap<>();

        // Group menus by parent ID
        for (Menu menu : menus) {
            if (menu.getParentId() == null) {
                rootMenus.add(menu);
            } else {
                childMenuMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
            }
        }

        // Build tree structure
        for (Menu rootMenu : rootMenus) {
            setChildMenus(rootMenu, childMenuMap);
        }

        return rootMenus;
    }

    private void setChildMenus(Menu parentMenu, Map<Long, List<Menu>> childMenuMap) {
        List<Menu> children = childMenuMap.get(parentMenu.getId());
        if (children != null) {
            parentMenu.setChildren(children);
            for (Menu child : children) {
                setChildMenus(child, childMenuMap);
            }
        }
    }

    
    public List<Menu> getUserMenuTree(Long userId) {
        List<Menu> userMenus = findMenusByUserId(userId);
        return buildMenuTree(userMenus);
    }
}