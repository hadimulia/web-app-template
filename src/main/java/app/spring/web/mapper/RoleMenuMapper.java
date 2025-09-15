package app.spring.web.mapper;

import app.spring.web.model.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMenuMapper extends Mapper<RoleMenu> {
    
    @Select("SELECT * FROM sys_role_menu WHERE role_id = #{roleId}")
    List<RoleMenu> findByRoleId(@Param("roleId") Long roleId);
    
    @Select("SELECT * FROM sys_role_menu WHERE menu_id = #{menuId}")
    List<RoleMenu> findByMenuId(@Param("menuId") Long menuId);
    
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
    
    @Delete("DELETE FROM sys_role_menu WHERE menu_id = #{menuId}")
    int deleteByMenuId(@Param("menuId") Long menuId);
    
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId} AND menu_id = #{menuId}")
    int deleteByRoleIdAndMenuId(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
    
    @Select("SELECT COUNT(*) FROM sys_role_menu WHERE role_id = #{roleId} AND menu_id = #{menuId}")
    int countByRoleIdAndMenuId(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}