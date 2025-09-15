package app.spring.web.mapper;

import app.spring.web.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CustomerMapper extends tk.mybatis.mapper.common.Mapper<Customer> {
    
    @Select("SELECT * FROM crm_customer WHERE customer_code = #{customerCode}")
    Customer findByCustomerCode(@Param("customerCode") String customerCode);
    
    @Select("SELECT * FROM crm_customer WHERE email = #{email}")
    Customer findByEmail(@Param("email") String email);
    
    @Select("SELECT * FROM crm_customer WHERE status = 1 ORDER BY created_at DESC")
    List<Customer> findActiveCustomers();
    
    @Select("SELECT COUNT(*) FROM crm_customer WHERE customer_code = #{customerCode} AND id != #{id}")
    int countByCustomerCodeExcludeId(@Param("customerCode") String customerCode, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM crm_customer WHERE email = #{email} AND id != #{id}")
    int countByEmailExcludeId(@Param("email") String email, @Param("id") Long id);
    
    // Search and pagination methods
    @Select("<script>" +
            "SELECT c.*, " +
            "(SELECT COALESCE(SUM(i.total_amount - i.paid_amount), 0) " +
            " FROM crm_invoice i WHERE i.customer_id = c.id AND i.payment_status != 'PAID') as total_outstanding, " +
            "(SELECT COUNT(*) FROM crm_invoice i " +
            " WHERE i.customer_id = c.id AND i.due_date &lt; CURDATE() AND i.payment_status != 'PAID') as overdue_invoices_count " +
            "FROM crm_customer c " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (c.customer_code LIKE CONCAT('%', #{search}, '%') " +
            "OR c.company_name LIKE CONCAT('%', #{search}, '%') " +
            "OR c.contact_person LIKE CONCAT('%', #{search}, '%') " +
            "OR c.email LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            "AND c.status = #{status}" +
            "</if>" +
            "<if test='customerType != null and customerType != \"\"'>" +
            "AND c.customer_type = #{customerType}" +
            "</if>" +
            "</where>" +
            "ORDER BY ${sortBy} ${sortDir} " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Customer> findWithPagination(@Param("search") String search,
                                     @Param("status") Integer status,
                                     @Param("customerType") String customerType,
                                     @Param("sortBy") String sortBy,
                                     @Param("sortDir") String sortDir,
                                     @Param("offset") int offset,
                                     @Param("size") int size);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM crm_customer c " +
            "<where>" +
            "<if test='search != null and search != \"\"'>" +
            "AND (c.customer_code LIKE CONCAT('%', #{search}, '%') " +
            "OR c.company_name LIKE CONCAT('%', #{search}, '%') " +
            "OR c.contact_person LIKE CONCAT('%', #{search}, '%') " +
            "OR c.email LIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            "AND c.status = #{status}" +
            "</if>" +
            "<if test='customerType != null and customerType != \"\"'>" +
            "AND c.customer_type = #{customerType}" +
            "</if>" +
            "</where>" +
            "</script>")
    long countWithFilter(@Param("search") String search,
                        @Param("status") Integer status,
                        @Param("customerType") String customerType);
    
    // Statistics queries
    @Select("SELECT COUNT(*) FROM crm_customer WHERE status = 1")
    int countActiveCustomers();
    
    @Select("SELECT COUNT(*) FROM crm_customer WHERE status = 2")
    int countSuspendedCustomers();
    
    @Select("SELECT COUNT(*) FROM crm_customer WHERE DATE(created_at) = CURDATE()")
    int countNewCustomersToday();
    
    @Select("SELECT COUNT(*) FROM crm_customer WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())")
    int countNewCustomersThisMonth();
    
    @Select("<script>" +
            "SELECT c.*, " +
            "(SELECT COALESCE(SUM(i.total_amount - i.paid_amount), 0) " +
            " FROM crm_invoice i WHERE i.customer_id = c.id AND i.payment_status != 'PAID') as total_outstanding " +
            "FROM crm_customer c " +
            "WHERE c.status = 1 " +
            "AND EXISTS (SELECT 1 FROM crm_invoice i " +
            "            WHERE i.customer_id = c.id " +
            "            AND i.due_date &lt; CURDATE() " +
            "            AND i.payment_status != 'PAID') " +
            "ORDER BY total_outstanding DESC " +
            "<if test='limit != null and limit > 0'>" +
            "LIMIT #{limit}" +
            "</if>" +
            "</script>")
    List<Customer> findCustomersWithOverdueInvoices(@Param("limit") Integer limit);
    
    @Select("SELECT c.* FROM crm_customer c " +
            "INNER JOIN crm_customer_billing_group cbg ON c.id = cbg.customer_id " +
            "WHERE cbg.billing_group_id = #{billingGroupId} AND cbg.status = 1")
    List<Customer> findByBillingGroupId(@Param("billingGroupId") Long billingGroupId);
    
    // Customer performance metrics
    @Select("SELECT c.*, " +
            "(SELECT COALESCE(SUM(p.amount), 0) FROM crm_payment p " +
            " INNER JOIN crm_invoice i ON p.invoice_id = i.id " +
            " WHERE i.customer_id = c.id AND p.status = 'SUCCESS') as total_payments, " +
            "(SELECT COALESCE(SUM(i.total_amount), 0) FROM crm_invoice i " +
            " WHERE i.customer_id = c.id) as total_invoiced " +
            "FROM crm_customer c " +
            "WHERE c.id = #{customerId}")
    Customer findWithPaymentSummary(@Param("customerId") Long customerId);
    
    // Update customer statistics (called by triggers or scheduled jobs)
    @Update("UPDATE crm_customer SET " +
            "total_outstanding = (SELECT COALESCE(SUM(total_amount - paid_amount), 0) " +
            "                     FROM crm_invoice WHERE customer_id = #{customerId} AND payment_status != 'PAID'), " +
            "overdue_invoices_count = (SELECT COUNT(*) FROM crm_invoice " +
            "                          WHERE customer_id = #{customerId} AND due_date < CURDATE() AND payment_status != 'PAID'), " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{customerId}")
    void updateCustomerStatistics(@Param("customerId") Long customerId);
}
