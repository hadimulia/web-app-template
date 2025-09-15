package app.spring.web.mapper;

import app.spring.web.model.CustomerBillingGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@org.apache.ibatis.annotations.Mapper
public interface CustomerBillingGroupMapper extends tk.mybatis.mapper.common.Mapper<CustomerBillingGroup> {
    
    @Select("SELECT cbg.*, c.customer_code, c.company_name, c.contact_person, " +
            "bg.group_code, bg.group_name, bg.base_price, bg.currency, bg.billing_cycle " +
            "FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "WHERE cbg.customer_id = #{customerId} AND cbg.status = 1 " +
            "ORDER BY cbg.created_at DESC")
    List<Map<String, Object>> findByCustomerId(@Param("customerId") Long customerId);
    
    @Select("SELECT cbg.*, c.customer_code, c.company_name, c.contact_person, " +
            "bg.group_code, bg.group_name, bg.base_price, bg.currency, bg.billing_cycle " +
            "FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "WHERE cbg.billing_group_id = #{billingGroupId} AND cbg.status = 1 " +
            "ORDER BY cbg.created_at DESC")
    List<Map<String, Object>> findByBillingGroupId(@Param("billingGroupId") Long billingGroupId);
    
    @Select("SELECT * FROM crm_customer_billing_group " +
            "WHERE customer_id = #{customerId} AND billing_group_id = #{billingGroupId}")
    CustomerBillingGroup findByCustomerAndBillingGroup(@Param("customerId") Long customerId, 
                                                       @Param("billingGroupId") Long billingGroupId);
    
    @Select("SELECT cbg.*, c.customer_code, c.company_name, c.contact_person, " +
            "bg.group_code, bg.group_name, bg.base_price, bg.currency, bg.billing_cycle, " +
            "(SELECT COUNT(*) FROM crm_invoice i WHERE i.customer_id = cbg.customer_id " +
            " AND i.billing_group_id = cbg.billing_group_id AND i.payment_status != 'PAID') as active_invoices_count " +
            "FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "WHERE cbg.id = #{id}")
    Map<String, Object> findByIdWithDetails(@Param("id") Long id);
    
    // Search and pagination methods
    @Select("<script>" +
            "SELECT cbg.*, c.customer_code, c.company_name, c.contact_person, " +
            "bg.group_code, bg.group_name, bg.base_price, bg.currency, bg.billing_cycle " +
            "FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (c.customer_code LIKE CONCAT('%', #{search}, '%') " +
            "OR c.company_name LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.group_code LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.group_name LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='customerId != null'>" +
            "AND cbg.customer_id = #{customerId}" +
            "</if>" +
            "<if test='billingGroupId != null'>" +
            "AND cbg.billing_group_id = #{billingGroupId}" +
            "</if>" +
            "<if test='status != null'>" +
            "AND cbg.status = #{status}" +
            "</if>" +
            "</where>" +
            "ORDER BY ${sortBy} ${sortDir} " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Map<String, Object>> findWithPagination(@Param("search") String search,
                                                 @Param("customerId") Long customerId,
                                                 @Param("billingGroupId") Long billingGroupId,
                                                 @Param("status") Integer status,
                                                 @Param("sortBy") String sortBy,
                                                 @Param("sortDir") String sortDir,
                                                 @Param("offset") int offset,
                                                 @Param("size") int size);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (c.customer_code LIKE CONCAT('%', #{search}, '%') " +
            "OR c.company_name LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.group_code LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.group_name LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='customerId != null'>" +
            "AND cbg.customer_id = #{customerId}" +
            "</if>" +
            "<if test='billingGroupId != null'>" +
            "AND cbg.billing_group_id = #{billingGroupId}" +
            "</if>" +
            "<if test='status != null'>" +
            "AND cbg.status = #{status}" +
            "</if>" +
            "</where>" +
            "</script>")
    long countWithFilter(@Param("search") String search,
                        @Param("customerId") Long customerId,
                        @Param("billingGroupId") Long billingGroupId,
                        @Param("status") Integer status);
    
    // Statistics queries
    @Select("SELECT COUNT(*) FROM crm_customer_billing_group WHERE status = 1")
    int countActiveAssignments();
    
    @Select("SELECT COUNT(DISTINCT customer_id) FROM crm_customer_billing_group WHERE status = 1")
    int countCustomersWithAssignments();
    
    @Select("SELECT COUNT(DISTINCT billing_group_id) FROM crm_customer_billing_group WHERE status = 1")
    int countBillingGroupsWithAssignments();
    
    // Assignments expiring soon
    @Select("SELECT cbg.*, c.customer_code, c.company_name, bg.group_name " +
            "FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "WHERE cbg.status = 1 AND cbg.end_date IS NOT NULL " +
            "AND cbg.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL #{days} DAY) " +
            "ORDER BY cbg.end_date")
    List<Map<String, Object>> findExpiringSoon(@Param("days") int days);
    
    // Revenue calculation methods
    @Select("SELECT cbg.*, c.customer_code, c.company_name, bg.group_name, bg.base_price, " +
            "(CASE WHEN cbg.custom_price IS NOT NULL THEN cbg.custom_price ELSE bg.base_price END) as effective_base_price, " +
            "(CASE WHEN cbg.custom_price IS NOT NULL THEN cbg.custom_price ELSE bg.base_price END) * " +
            "(1 - COALESCE(cbg.discount_percent, 0) / 100) as effective_price " +
            "FROM crm_customer_billing_group cbg " +
            "INNER JOIN crm_customer c ON cbg.customer_id = c.id " +
            "INNER JOIN crm_billing_group bg ON cbg.billing_group_id = bg.id " +
            "WHERE cbg.status = 1 " +
            "ORDER BY effective_price DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findHighestValueAssignments(@Param("limit") Integer limit);
    
    // Check for conflicts
    @Select("SELECT COUNT(*) FROM crm_customer_billing_group " +
            "WHERE customer_id = #{customerId} AND billing_group_id = #{billingGroupId} " +
            "AND status = 1 AND id != #{excludeId}")
    int countActiveAssignmentConflicts(@Param("customerId") Long customerId, 
                                      @Param("billingGroupId") Long billingGroupId,
                                      @Param("excludeId") Long excludeId);
}
