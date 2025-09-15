package app.spring.web.mapper;

import org.apache.ibatis.annotations.*;
import app.spring.web.model.Invoice;
import app.spring.web.model.InvoiceItem;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceMapper {
    
    // Basic CRUD Operations
    @Insert("INSERT INTO crm_invoice (invoice_number, customer_id, billing_group_id, invoice_date, " +
            "due_date, period_start, period_end, subtotal, tax_amount, discount_amount, " +
            "total_amount, currency, status, payment_status, paid_amount, notes, " +
            "created_at, updated_at, created_by, updated_by) " +
            "VALUES (#{invoiceNumber}, #{customerId}, #{billingGroupId}, #{invoiceDate}, " +
            "#{dueDate}, #{periodStart}, #{periodEnd}, #{subtotal}, #{taxAmount}, #{discountAmount}, " +
            "#{totalAmount}, #{currency}, #{status}, #{paymentStatus}, #{paidAmount}, #{notes}, " +
            "#{createdAt}, #{updatedAt}, #{createdBy}, #{updatedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Invoice invoice);
    
    @Update("UPDATE crm_invoice SET invoice_number = #{invoiceNumber}, customer_id = #{customerId}, " +
            "billing_group_id = #{billingGroupId}, invoice_date = #{invoiceDate}, due_date = #{dueDate}, " +
            "period_start = #{periodStart}, period_end = #{periodEnd}, subtotal = #{subtotal}, " +
            "tax_amount = #{taxAmount}, discount_amount = #{discountAmount}, total_amount = #{totalAmount}, " +
            "currency = #{currency}, status = #{status}, payment_status = #{paymentStatus}, " +
            "paid_amount = #{paidAmount}, notes = #{notes}, updated_at = #{updatedAt}, " +
            "updated_by = #{updatedBy} WHERE id = #{id}")
    void update(Invoice invoice);
    
    @Delete("DELETE FROM crm_invoice WHERE id = #{id}")
    void deleteById(Long id);
    
    @Select("SELECT i.*, c.name as customer_name, c.email as customer_email, " +
            "bg.name as billing_group_name, bg.price as billing_group_price " +
            "FROM crm_invoice i " +
            "LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "LEFT JOIN crm_billing_group bg ON i.billing_group_id = bg.id " +
            "WHERE i.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "invoiceNumber", column = "invoice_number"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "billingGroupId", column = "billing_group_id"),
        @Result(property = "invoiceDate", column = "invoice_date"),
        @Result(property = "dueDate", column = "due_date"),
        @Result(property = "periodStart", column = "period_start"),
        @Result(property = "periodEnd", column = "period_end"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "taxAmount", column = "tax_amount"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "currency", column = "currency"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentStatus", column = "payment_status"),
        @Result(property = "paidAmount", column = "paid_amount"),
        @Result(property = "notes", column = "notes"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by"),
        @Result(property = "customer.name", column = "customer_name"),
        @Result(property = "customer.email", column = "customer_email"),
        @Result(property = "billingGroup.name", column = "billing_group_name"),
        @Result(property = "billingGroup.price", column = "billing_group_price")
    })
    Invoice findById(Long id);
    
    @Select("SELECT i.*, c.name as customer_name, c.email as customer_email, " +
            "bg.name as billing_group_name, bg.price as billing_group_price, " +
            "CASE WHEN i.due_date < CURRENT_DATE AND i.payment_status != 'PAID' THEN true ELSE false END as is_overdue, " +
            "(i.total_amount - i.paid_amount) as remaining_amount, " +
            "CASE WHEN i.due_date < CURRENT_DATE AND i.payment_status != 'PAID' " +
            "THEN EXTRACT(DAY FROM CURRENT_DATE - i.due_date) ELSE 0 END as days_past_due " +
            "FROM crm_invoice i " +
            "LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "LEFT JOIN crm_billing_group bg ON i.billing_group_id = bg.id " +
            "WHERE 1=1 " +
            "<if test='customerId != null'>AND i.customer_id = #{customerId}</if> " +
            "<if test='status != null and status != \"\"'>AND i.status = #{status}</if> " +
            "<if test='paymentStatus != null and paymentStatus != \"\"'>AND i.payment_status = #{paymentStatus}</if> " +
            "<if test='startDate != null'>AND i.invoice_date &gt;= #{startDate}</if> " +
            "<if test='endDate != null'>AND i.invoice_date &lt;= #{endDate}</if> " +
            "<if test='overdue != null and overdue == true'>AND i.due_date &lt; CURRENT_DATE AND i.payment_status != 'PAID'</if> " +
            "<if test='searchTerm != null and searchTerm != \"\"'> " +
            "AND (UPPER(i.invoice_number) LIKE UPPER(CONCAT('%', #{searchTerm}, '%')) " +
            "OR UPPER(c.name) LIKE UPPER(CONCAT('%', #{searchTerm}, '%')) " +
            "OR UPPER(c.email) LIKE UPPER(CONCAT('%', #{searchTerm}, '%'))) " +
            "</if> " +
            "ORDER BY " +
            "<choose> " +
            "<when test='sortField == \"invoiceNumber\"'>i.invoice_number</when> " +
            "<when test='sortField == \"customerName\"'>c.name</when> " +
            "<when test='sortField == \"invoiceDate\"'>i.invoice_date</when> " +
            "<when test='sortField == \"dueDate\"'>i.due_date</when> " +
            "<when test='sortField == \"totalAmount\"'>i.total_amount</when> " +
            "<when test='sortField == \"status\"'>i.status</when> " +
            "<when test='sortField == \"paymentStatus\"'>i.payment_status</when> " +
            "<otherwise>i.created_at</otherwise> " +
            "</choose> " +
            "<choose> " +
            "<when test='sortDir == \"asc\"'>ASC</when> " +
            "<otherwise>DESC</otherwise> " +
            "</choose> " +
            "LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "invoiceNumber", column = "invoice_number"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "billingGroupId", column = "billing_group_id"),
        @Result(property = "invoiceDate", column = "invoice_date"),
        @Result(property = "dueDate", column = "due_date"),
        @Result(property = "periodStart", column = "period_start"),
        @Result(property = "periodEnd", column = "period_end"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "taxAmount", column = "tax_amount"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "currency", column = "currency"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentStatus", column = "payment_status"),
        @Result(property = "paidAmount", column = "paid_amount"),
        @Result(property = "notes", column = "notes"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by"),
        @Result(property = "customer.name", column = "customer_name"),
        @Result(property = "customer.email", column = "customer_email"),
        @Result(property = "billingGroup.name", column = "billing_group_name"),
        @Result(property = "billingGroup.price", column = "billing_group_price"),
        @Result(property = "remainingAmount", column = "remaining_amount"),
        @Result(property = "daysPastDue", column = "days_past_due"),
        @Result(property = "isOverdue", column = "is_overdue")
    })
    List<Invoice> findAll(Map<String, Object> params);
    
    @Select("SELECT COUNT(*) FROM crm_invoice i " +
            "LEFT JOIN crm_customer c ON i.customer_id = c.id " +
            "WHERE 1=1 " +
            "<if test='customerId != null'>AND i.customer_id = #{customerId}</if> " +
            "<if test='status != null and status != \"\"'>AND i.status = #{status}</if> " +
            "<if test='paymentStatus != null and paymentStatus != \"\"'>AND i.payment_status = #{paymentStatus}</if> " +
            "<if test='startDate != null'>AND i.invoice_date &gt;= #{startDate}</if> " +
            "<if test='endDate != null'>AND i.invoice_date &lt;= #{endDate}</if> " +
            "<if test='overdue != null and overdue == true'>AND i.due_date &lt; CURRENT_DATE AND i.payment_status != 'PAID'</if> " +
            "<if test='searchTerm != null and searchTerm != \"\"'> " +
            "AND (UPPER(i.invoice_number) LIKE UPPER(CONCAT('%', #{searchTerm}, '%')) " +
            "OR UPPER(c.name) LIKE UPPER(CONCAT('%', #{searchTerm}, '%')) " +
            "OR UPPER(c.email) LIKE UPPER(CONCAT('%', #{searchTerm}, '%'))) " +
            "</if>")
    long count(Map<String, Object> params);
    
    // Invoice Items
    @Insert("INSERT INTO crm_invoice_item (invoice_id, service_id, description, quantity, " +
            "unit_price, line_total, discount_amount, tax_amount, notes, sort_order, " +
            "created_at, updated_at) " +
            "VALUES (#{invoiceId}, #{serviceId}, #{description}, #{quantity}, #{unitPrice}, " +
            "#{lineTotal}, #{discountAmount}, #{taxAmount}, #{notes}, #{sortOrder}, " +
            "#{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertItem(InvoiceItem item);
    
    @Update("UPDATE crm_invoice_item SET service_id = #{serviceId}, description = #{description}, " +
            "quantity = #{quantity}, unit_price = #{unitPrice}, line_total = #{lineTotal}, " +
            "discount_amount = #{discountAmount}, tax_amount = #{taxAmount}, notes = #{notes}, " +
            "sort_order = #{sortOrder}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateItem(InvoiceItem item);
    
    @Delete("DELETE FROM crm_invoice_item WHERE id = #{id}")
    void deleteItemById(Long id);
    
    @Delete("DELETE FROM crm_invoice_item WHERE invoice_id = #{invoiceId}")
    void deleteItemsByInvoiceId(Long invoiceId);
    
    @Select("SELECT ii.*, s.name as service_name " +
            "FROM crm_invoice_item ii " +
            "LEFT JOIN crm_service s ON ii.service_id = s.id " +
            "WHERE ii.invoice_id = #{invoiceId} " +
            "ORDER BY ii.sort_order, ii.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "invoiceId", column = "invoice_id"),
        @Result(property = "serviceId", column = "service_id"),
        @Result(property = "description", column = "description"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "unitPrice", column = "unit_price"),
        @Result(property = "lineTotal", column = "line_total"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "taxAmount", column = "tax_amount"),
        @Result(property = "notes", column = "notes"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "service.name", column = "service_name")
    })
    List<InvoiceItem> findItemsByInvoiceId(Long invoiceId);
    
    // Statistics and Reports
    @Select("SELECT " +
            "COUNT(*) as total_invoices, " +
            "COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_invoices, " +
            "COUNT(CASE WHEN status = 'SENT' THEN 1 END) as sent_invoices, " +
            "COUNT(CASE WHEN status = 'PAID' THEN 1 END) as paid_invoices, " +
            "COUNT(CASE WHEN due_date < CURRENT_DATE AND payment_status != 'PAID' THEN 1 END) as overdue_invoices, " +
            "COALESCE(SUM(total_amount), 0) as total_amount, " +
            "COALESCE(SUM(paid_amount), 0) as total_paid, " +
            "COALESCE(SUM(total_amount - paid_amount), 0) as total_outstanding " +
            "FROM crm_invoice " +
            "WHERE 1=1 " +
            "<if test='customerId != null'>AND customer_id = #{customerId}</if> " +
            "<if test='startDate != null'>AND invoice_date &gt;= #{startDate}</if> " +
            "<if test='endDate != null'>AND invoice_date &lt;= #{endDate}</if>")
    Map<String, Object> getStatistics(Map<String, Object> params);
    
    @Select("SELECT " +
            "DATE_FORMAT(invoice_date, '%Y-%m') as month, " +
            "COUNT(*) as count, " +
            "SUM(total_amount) as total_amount, " +
            "SUM(paid_amount) as paid_amount " +
            "FROM crm_invoice " +
            "WHERE invoice_date >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH) " +
            "<if test='customerId != null'>AND customer_id = #{customerId}</if> " +
            "GROUP BY DATE_FORMAT(invoice_date, '%Y-%m') " +
            "ORDER BY month DESC")
    List<Map<String, Object>> getMonthlyStatistics(Map<String, Object> params);
    
    @Select("SELECT " +
            "c.name as customer_name, " +
            "COUNT(*) as invoice_count, " +
            "SUM(i.total_amount) as total_amount, " +
            "SUM(i.paid_amount) as paid_amount, " +
            "SUM(i.total_amount - i.paid_amount) as outstanding_amount " +
            "FROM crm_invoice i " +
            "JOIN crm_customer c ON i.customer_id = c.id " +
            "WHERE 1=1 " +
            "<if test='startDate != null'>AND i.invoice_date &gt;= #{startDate}</if> " +
            "<if test='endDate != null'>AND i.invoice_date &lt;= #{endDate}</if> " +
            "GROUP BY c.id, c.name " +
            "ORDER BY outstanding_amount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> getTopOutstandingCustomers(Map<String, Object> params);
    
    // Utility Methods
    @Select("SELECT invoice_number FROM crm_invoice WHERE invoice_number = #{invoiceNumber}")
    String findByInvoiceNumber(String invoiceNumber);
    
    @Update("UPDATE crm_invoice SET status = #{status}, updated_at = NOW(), updated_by = #{updatedBy} " +
            "WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") Long updatedBy);
    
    @Update("UPDATE crm_invoice SET payment_status = #{paymentStatus}, paid_amount = #{paidAmount}, " +
            "updated_at = NOW(), updated_by = #{updatedBy} WHERE id = #{id}")
    void updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") String paymentStatus, 
                            @Param("paidAmount") BigDecimal paidAmount, @Param("updatedBy") Long updatedBy);
    
    @Select("SELECT * FROM crm_invoice WHERE due_date < CURRENT_DATE AND payment_status != 'PAID' " +
            "AND status != 'CANCELLED' ORDER BY due_date ASC")
    List<Invoice> findOverdueInvoices();
    
    @Select("SELECT * FROM crm_invoice WHERE customer_id = #{customerId} " +
            "ORDER BY invoice_date DESC LIMIT #{limit}")
    List<Invoice> findByCustomerId(@Param("customerId") Long customerId, @Param("limit") int limit);
}
