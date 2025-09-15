package app.spring.web.mapper;

import app.spring.web.model.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserRoleMapper extends Mapper<UserRole> {
    
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId}")
    List<UserRole> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM sys_user_role WHERE role_id = #{roleId}")
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);
    
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
    
    @Delete("DELETE FROM sys_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
    
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    @Select("SELECT COUNT(*) FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}