package app.spring.web.mapper;

import app.spring.web.model.PaymentTransaction;
import tk.mybatis.mapper.common.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Payment Transaction Mapper for database operations
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@org.apache.ibatis.annotations.Mapper
public interface PaymentTransactionMapper extends Mapper<PaymentTransaction> {
    
    /**
     * Find transaction by external transaction ID
     */
    @Select("SELECT * FROM crm_payment_transaction WHERE external_transaction_id = #{externalTransactionId}")
    PaymentTransaction findByExternalTransactionId(@Param("externalTransactionId") String externalTransactionId);
    
    /**
     * Find transactions by payment ID
     */
    @Select("SELECT * FROM crm_payment_transaction WHERE payment_id = #{paymentId} ORDER BY created_at DESC")
    List<PaymentTransaction> findByPaymentId(@Param("paymentId") Long paymentId);
    
    /**
     * Find transactions by gateway ID
     */
    @Select("SELECT * FROM crm_payment_transaction WHERE gateway_id = #{gatewayId} ORDER BY created_at DESC")
    List<PaymentTransaction> findByGatewayId(@Param("gatewayId") Long gatewayId);
    
    /**
     * Find transactions by status
     */
    @Select("SELECT * FROM crm_payment_transaction WHERE status = #{status} ORDER BY created_at DESC")
    List<PaymentTransaction> findByStatus(@Param("status") String status);
    
    /**
     * Find expired transactions
     */
    @Select("SELECT * FROM crm_payment_transaction WHERE status = 'PENDING' AND expires_at < #{currentTime}")
    List<PaymentTransaction> findExpiredTransactions(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Update transaction status
     */
    @Update("UPDATE crm_payment_transaction SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * Update transaction with gateway response
     */
    @Update("UPDATE crm_payment_transaction SET gateway_response = #{gatewayResponse}, gateway_reference = #{gatewayReference}, " +
            "status = #{status}, processed_at = #{processedAt}, updated_at = NOW() WHERE id = #{id}")
    int updateWithGatewayResponse(@Param("id") Long id, 
                                  @Param("gatewayResponse") String gatewayResponse,
                                  @Param("gatewayReference") String gatewayReference,
                                  @Param("status") String status,
                                  @Param("processedAt") LocalDateTime processedAt);
    
    /**
     * Find transactions for statistics
     */
    @Select("SELECT COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_count, " +
            "SUM(CASE WHEN status = 'SUCCESS' THEN amount ELSE 0 END) as total_amount " +
            "FROM crm_payment_transaction WHERE gateway_id = #{gatewayId}")
    PaymentTransactionStats getStatsByGateway(@Param("gatewayId") Long gatewayId);
    
    /**
     * Statistics result class
     */
    class PaymentTransactionStats {
        private Long totalCount;
        private Long successCount;
        private Long failedCount;
        private Double totalAmount;
        
        // Getters and setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        
        public Long getSuccessCount() { return successCount; }
        public void setSuccessCount(Long successCount) { this.successCount = successCount; }
        
        public Long getFailedCount() { return failedCount; }
        public void setFailedCount(Long failedCount) { this.failedCount = failedCount; }
        
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    }
}
