package app.spring.web.mapper;

import app.spring.web.model.CustomerAuth;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CustomerAuthMapper extends tk.mybatis.mapper.common.Mapper<CustomerAuth> {
    
    @Select("SELECT * FROM crm_customer_auth WHERE username = #{username}")
    CustomerAuth findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM crm_customer_auth WHERE customer_id = #{customerId}")
    CustomerAuth findByCustomerId(@Param("customerId") Long customerId);
    
    @Select("SELECT * FROM crm_customer_auth WHERE password_reset_token = #{token}")
    CustomerAuth findByPasswordResetToken(@Param("token") String token);
    
    @Select("SELECT * FROM crm_customer_auth WHERE email_verification_token = #{token}")
    CustomerAuth findByEmailVerificationToken(@Param("token") String token);
    
    @Select("SELECT COUNT(*) FROM crm_customer_auth WHERE username = #{username} AND id != #{id}")
    int countByUsernameExcludeId(@Param("username") String username, @Param("id") Long id);
    
    @Update("UPDATE crm_customer_auth SET last_login = #{lastLogin}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateLastLogin(@Param("id") Long id, @Param("lastLogin") java.time.LocalDateTime lastLogin, 
                        @Param("updatedAt") java.time.LocalDateTime updatedAt);
    
    @Update("UPDATE crm_customer_auth SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Update("UPDATE crm_customer_auth SET email_verified = true, email_verification_token = null, " +
            "updated_at = NOW() WHERE email_verification_token = #{token}")
    void verifyEmailByToken(@Param("token") String token);
    
    @Select("SELECT ca.*, c.customer_code, c.company_name, c.contact_person, c.email as customer_email " +
            "FROM crm_customer_auth ca " +
            "JOIN crm_customer c ON ca.customer_id = c.id " +
            "WHERE ca.username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "lastLogin", column = "last_login"),
        @Result(property = "passwordResetToken", column = "password_reset_token"),
        @Result(property = "passwordResetExpires", column = "password_reset_expires"),
        @Result(property = "emailVerified", column = "email_verified"),
        @Result(property = "emailVerificationToken", column = "email_verification_token"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "customer.id", column = "customer_id"),
        @Result(property = "customer.customerCode", column = "customer_code"),
        @Result(property = "customer.companyName", column = "company_name"),
        @Result(property = "customer.contactPerson", column = "contact_person"),
        @Result(property = "customer.email", column = "customer_email")
    })
    CustomerAuth findByUsernameWithCustomer(@Param("username") String username);
}
