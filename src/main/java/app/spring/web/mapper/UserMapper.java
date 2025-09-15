package app.spring.web.mapper;

import app.spring.web.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMapper extends Mapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT u.*, GROUP_CONCAT(r.role_name) as roleNames " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.id = #{userId} " +
            "GROUP BY u.id")
    User findUserWithRoles(@Param("userId") Long userId);
    
    @Select("SELECT u.* FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "WHERE ur.role_id = #{roleId}")
    List<User> findUsersByRoleId(@Param("roleId") Long roleId);
    
    @Update("UPDATE sys_user SET last_login = #{lastLogin} WHERE id = #{userId}")
    int updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);
    
    @Select("SELECT u.* FROM sys_user u WHERE u.status = 1 ORDER BY u.created_at DESC")
    List<User> findActiveUsers();
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND id != #{id}")
    int countByUsernameExcludeId(@Param("username") String username, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE email = #{email} AND id != #{id}")
    int countByEmailExcludeId(@Param("email") String email, @Param("id") Long id);
    
    // Search and pagination methods
    @Select("<script>" +
            "SELECT * FROM sys_user " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{search}, '%') " +
            "OR full_name LIKE CONCAT('%', #{search}, '%') " +
            "OR email LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY ${sortBy} ${sortDir} " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<User> findWithPagination(@Param("search") String search,
                                 @Param("sortBy") String sortBy,
                                 @Param("sortDir") String sortDir,
                                 @Param("offset") int offset,
                                 @Param("size") int size);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_user " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{search}, '%') " +
            "OR full_name LIKE CONCAT('%', #{search}, '%') " +
            "OR email LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    long countWithSearch(@Param("search") String search);
}