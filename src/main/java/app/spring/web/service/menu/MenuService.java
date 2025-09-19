package app.spring.web.service.menu;

import app.spring.web.model.Menu;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericService;

import java.util.List;

public interface MenuService extends GenericService<Menu, Long> {
    
    List<Menu> findActiveMenus();
    
    Menu findByMenuCode(String menuCode);
    
    List<Menu> findMenusByUserId(Long userId);
    
    List<Menu> findMenusByRoleId(Long roleId);
    
    List<Menu> findRootMenus();
    
    List<Menu> findChildMenus(Long parentId);
    
    boolean existsByMenuCode(String menuCode, Long excludeId);
    
    boolean hasChildMenus(Long parentId);
    
    PageResponse<Menu> findWithPagination(PageRequest pageRequest);
    
    List<Menu> buildMenuTree(List<Menu> menus);
    
    List<Menu> getUserMenuTree(Long userId);
}
