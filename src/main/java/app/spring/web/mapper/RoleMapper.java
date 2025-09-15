package app.spring.web.mapper;

import app.spring.web.model.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface RoleMapper extends tk.mybatis.mapper.common.Mapper<Role> {
    
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    Role findByRoleCode(@Param("roleCode") String roleCode);
    
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM sys_role WHERE status = 1 ORDER BY created_at DESC")
    List<Role> findActiveRoles();
    
    @Select("SELECT COUNT(*) FROM sys_role WHERE role_code = #{roleCode} AND id != #{id}")
    int countByRoleCodeExcludeId(@Param("roleCode") String roleCode, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM sys_role WHERE role_name = #{roleName} AND id != #{id}")
    int countByRoleNameExcludeId(@Param("roleName") String roleName, @Param("id") Long id);
    
    // Search and pagination methods
    @Select("<script>" +
            "SELECT * FROM sys_role " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (role_name LIKE CONCAT('%', #{search}, '%') " +
            "OR role_code LIKE CONCAT('%', #{search}, '%') " +
            "OR description LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY ${sortBy} ${sortDir} " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Role> findWithPagination(@Param("search") String search,
                                 @Param("sortBy") String sortBy,
                                 @Param("sortDir") String sortDir,
                                 @Param("offset") int offset,
                                 @Param("size") int size);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_role " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (role_name LIKE CONCAT('%', #{search}, '%') " +
            "OR role_code LIKE CONCAT('%', #{search}, '%') " +
            "OR description LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    long countWithSearch(@Param("search") String search);
}