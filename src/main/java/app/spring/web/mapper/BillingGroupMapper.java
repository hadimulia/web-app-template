package app.spring.web.mapper;

import app.spring.web.model.BillingGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BillingGroupMapper extends tk.mybatis.mapper.common.Mapper<BillingGroup> {
    
    @Select("SELECT * FROM crm_billing_group WHERE group_code = #{groupCode}")
    BillingGroup findByGroupCode(@Param("groupCode") String groupCode);
    
    @Select("SELECT * FROM crm_billing_group WHERE status = 1 ORDER BY group_name")
    List<BillingGroup> findActiveBillingGroups();
    
    @Select("SELECT COUNT(*) FROM crm_billing_group WHERE group_code = #{groupCode} AND id != #{id}")
    int countByGroupCodeExcludeId(@Param("groupCode") String groupCode, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM crm_billing_group WHERE group_name = #{groupName} AND id != #{id}")
    int countByGroupNameExcludeId(@Param("groupName") String groupName, @Param("id") Long id);
    
    // Search and pagination methods
    @Select("<script>" +
            "SELECT bg.*, " +
            "(SELECT COUNT(*) FROM crm_customer_billing_group cbg " +
            " WHERE cbg.billing_group_id = bg.id AND cbg.status = 1) as active_customers_count, " +
            "(SELECT COALESCE(SUM(i.total_amount), 0) FROM crm_invoice i " +
            " INNER JOIN crm_customer_billing_group cbg ON i.billing_group_id = bg.id " +
            " WHERE MONTH(i.invoice_date) = MONTH(CURDATE()) AND YEAR(i.invoice_date) = YEAR(CURDATE())) as monthly_revenue, " +
            "(SELECT COUNT(*) FROM crm_invoice i " +
            " WHERE i.billing_group_id = bg.id AND i.status = 'PENDING') as pending_invoices_count " +
            "FROM crm_billing_group bg " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (bg.group_code LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.group_name LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.description LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            "AND bg.status = #{status}" +
            "</if>" +
            "<if test='billingCycle != null and billingCycle != \"\"'>" +
            "AND bg.billing_cycle = #{billingCycle}" +
            "</if>" +
            "</where>" +
            "ORDER BY ${sortBy} ${sortDir} " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<BillingGroup> findWithPagination(@Param("search") String search,
                                          @Param("status") Integer status,
                                          @Param("billingCycle") String billingCycle,
                                          @Param("sortBy") String sortBy,
                                          @Param("sortDir") String sortDir,
                                          @Param("offset") int offset,
                                          @Param("size") int size);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM crm_billing_group bg " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (bg.group_code LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.group_name LIKE CONCAT('%', #{search}, '%') " +
            "OR bg.description LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            "AND bg.status = #{status}" +
            "</if>" +
            "<if test='billingCycle != null and billingCycle != \"\"'>" +
            "AND bg.billing_cycle = #{billingCycle}" +
            "</if>" +
            "</where>" +
            "</script>")
    long countWithFilter(@Param("search") String search,
                        @Param("status") Integer status,
                        @Param("billingCycle") String billingCycle);
    
    // Statistics queries
    @Select("SELECT COUNT(*) FROM crm_billing_group WHERE status = 1")
    int countActiveBillingGroups();
    
    @Select("SELECT COUNT(*) FROM crm_billing_group WHERE auto_generate = true AND status = 1")
    int countAutoGenerateBillingGroups();
    
    @Select("SELECT COALESCE(SUM(base_price), 0) FROM crm_billing_group WHERE status = 1")
    java.math.BigDecimal getTotalBasePrice();
    
    @Select("SELECT billing_cycle, COUNT(*) as count FROM crm_billing_group " +
            "WHERE status = 1 GROUP BY billing_cycle ORDER BY count DESC")
    List<java.util.Map<String, Object>> getBillingCycleStats();
    
    // Monthly revenue by billing group
    @Select("SELECT bg.group_name, bg.group_code, " +
            "COALESCE(SUM(i.total_amount), 0) as monthly_revenue, " +
            "COUNT(DISTINCT i.customer_id) as customer_count " +
            "FROM crm_billing_group bg " +
            "LEFT JOIN crm_invoice i ON bg.id = i.billing_group_id " +
            "AND MONTH(i.invoice_date) = MONTH(CURDATE()) " +
            "AND YEAR(i.invoice_date) = YEAR(CURDATE()) " +
            "WHERE bg.status = 1 " +
            "GROUP BY bg.id, bg.group_name, bg.group_code " +
            "ORDER BY monthly_revenue DESC " +
            "LIMIT #{limit}")
    List<java.util.Map<String, Object>> getTopRevenueGroups(@Param("limit") Integer limit);
    
    // Customer assignments
    @Select("SELECT cbg.id,cbg.customer_id, c.customer_code, c.company_name, c.contact_person, c.email, " +
            "cbg.custom_price, cbg.discount_percent, cbg.start_date, cbg.end_date, cbg.status as assignment_status " +
            "FROM crm_customer c " +
            "INNER JOIN crm_customer_billing_group cbg ON c.id = cbg.customer_id " +
            "WHERE cbg.billing_group_id = #{billingGroupId} " +
            "ORDER BY cbg.created_at DESC")
    List<java.util.Map<String, Object>> findCustomerAssignments(@Param("billingGroupId") Long billingGroupId);
    
    // Find billing groups assigned to a customer
    @Select("SELECT bg.*, cbg.custom_price, cbg.discount_percent, cbg.start_date, cbg.end_date, " +
            "cbg.status as assignment_status, cbg.notes " +
            "FROM crm_billing_group bg " +
            "INNER JOIN crm_customer_billing_group cbg ON bg.id = cbg.billing_group_id " +
            "WHERE cbg.customer_id = #{customerId} " +
            "ORDER BY cbg.created_at DESC")
    List<java.util.Map<String, Object>> findByCustomerId(@Param("customerId") Long customerId);
}
