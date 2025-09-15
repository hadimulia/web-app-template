package app.spring.web.mapper;

import app.spring.web.model.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MenuMapper extends tk.mybatis.mapper.common.Mapper<Menu> {
    
    @Select("SELECT * FROM sys_menu WHERE menu_code = #{menuCode}")
    Menu findByMenuCode(@Param("menuCode") String menuCode);
    
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 " +
            "ORDER BY m.sort_order ASC")
    List<Menu> findMenusByUserId(@Param("userId") Long userId);
    
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} AND m.status = 1 " +
            "ORDER BY m.sort_order ASC")
    List<Menu> findMenusByRoleId(@Param("roleId") Long roleId);
    
    @Select("SELECT * FROM sys_menu WHERE parent_id IS NULL AND status = 1 ORDER BY sort_order ASC")
    List<Menu> findRootMenus();
    
    @Select("SELECT * FROM sys_menu WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order ASC")
    List<Menu> findChildMenus(@Param("parentId") Long parentId);
    
    @Select("SELECT * FROM sys_menu WHERE status = 1 ORDER BY sort_order ASC")
    List<Menu> findActiveMenus();
    
    @Select("SELECT COUNT(*) FROM sys_menu WHERE menu_code = #{menuCode} AND id != #{id}")
    int countByMenuCodeExcludeId(@Param("menuCode") String menuCode, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM sys_menu WHERE parent_id = #{parentId}")
    int countChildMenus(@Param("parentId") Long parentId);
    
    @Select("SELECT menu_name FROM sys_menu WHERE id = #{id}")
    String getMenuNameById(@Param("id") Long id);

    // Search and pagination methods
    @Select("<script>" +
            "SELECT * FROM sys_menu " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (menu_name LIKE CONCAT('%', #{search}, '%') " +
            "OR menu_code LIKE CONCAT('%', #{search}, '%') " +
            "OR description LIKE CONCAT('%', #{search}, '%') " +
            "OR menu_url LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY ${sortBy} ${sortDir} " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Menu> findWithPagination(@Param("search") String search,
                                 @Param("sortBy") String sortBy,
                                 @Param("sortDir") String sortDir,
                                 @Param("offset") int offset,
                                 @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_menu " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (menu_name LIKE CONCAT('%', #{search}, '%') " +
            "OR menu_code LIKE CONCAT('%', #{search}, '%') " +
            "OR description LIKE CONCAT('%', #{search}, '%') " +
            "OR menu_url LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    long countWithSearch(@Param("search") String search);
}