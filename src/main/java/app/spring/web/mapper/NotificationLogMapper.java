package app.spring.web.mapper;

import app.spring.web.model.NotificationLog;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import java.util.List;
import java.util.Map;

/**
 * NotificationLog Mapper Interface
 * Handles database operations for notification logs
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface NotificationLogMapper {
    
    // Basic CRUD Operations
    
    @Insert("INSERT INTO crm_notification_log (" +
            "customer_id, template_id, notification_type, recipient, " +
            "subject, message, status, metadata_json, " +
            "sent_at, delivered_at, read_at, failed_at, " +
            "retry_count, max_retries, error_message, " +
            "created_at, updated_at) " +
            "VALUES (" +
            "#{customerId,jdbcType=BIGINT}, " +
            "#{templateId,jdbcType=BIGINT}, " +
            "#{notificationType,jdbcType=VARCHAR}, " +
            "#{recipient,jdbcType=VARCHAR}, " +
            "#{subject,jdbcType=VARCHAR}, " +
            "#{message,jdbcType=LONGVARCHAR}, " +
            "#{status,jdbcType=VARCHAR}, " +
            "#{metadataJson,jdbcType=LONGVARCHAR}, " +
            "#{sentAt,jdbcType=TIMESTAMP}, " +
            "#{deliveredAt,jdbcType=TIMESTAMP}, " +
            "#{readAt,jdbcType=TIMESTAMP}, " +
            "#{failedAt,jdbcType=TIMESTAMP}, " +
            "#{retryCount,jdbcType=INTEGER}, " +
            "#{maxRetries,jdbcType=INTEGER}, " +
            "#{errorMessage,jdbcType=LONGVARCHAR}, " +
            "NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NotificationLog record);
    
    @Select("SELECT * FROM crm_notification_log WHERE id = #{id}")
    @Results({
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
        @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.BIGINT),
        @Result(column = "template_id", property = "templateId", jdbcType = JdbcType.BIGINT),
        @Result(column = "notification_type", property = "notificationType", jdbcType = JdbcType.VARCHAR),
        @Result(column = "recipient", property = "recipient", jdbcType = JdbcType.VARCHAR),
        @Result(column = "subject", property = "subject", jdbcType = JdbcType.VARCHAR),
        @Result(column = "message", property = "message", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
        @Result(column = "metadata_json", property = "metadataJson", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "sent_at", property = "sentAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "delivered_at", property = "deliveredAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "read_at", property = "readAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "failed_at", property = "failedAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "retry_count", property = "retryCount", jdbcType = JdbcType.INTEGER),
        @Result(column = "max_retries", property = "maxRetries", jdbcType = JdbcType.INTEGER),
        @Result(column = "error_message", property = "errorMessage", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    NotificationLog selectByPrimaryKey(Long id);
    
    @Update("UPDATE crm_notification_log SET " +
            "customer_id = #{customerId,jdbcType=BIGINT}, " +
            "template_id = #{templateId,jdbcType=BIGINT}, " +
            "notification_type = #{notificationType,jdbcType=VARCHAR}, " +
            "recipient = #{recipient,jdbcType=VARCHAR}, " +
            "subject = #{subject,jdbcType=VARCHAR}, " +
            "message = #{message,jdbcType=LONGVARCHAR}, " +
            "status = #{status,jdbcType=VARCHAR}, " +
            "metadata_json = #{metadataJson,jdbcType=LONGVARCHAR}, " +
            "sent_at = #{sentAt,jdbcType=TIMESTAMP}, " +
            "delivered_at = #{deliveredAt,jdbcType=TIMESTAMP}, " +
            "read_at = #{readAt,jdbcType=TIMESTAMP}, " +
            "failed_at = #{failedAt,jdbcType=TIMESTAMP}, " +
            "retry_count = #{retryCount,jdbcType=INTEGER}, " +
            "max_retries = #{maxRetries,jdbcType=INTEGER}, " +
            "error_message = #{errorMessage,jdbcType=LONGVARCHAR}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateByPrimaryKey(NotificationLog record);
    
    @Delete("DELETE FROM crm_notification_log WHERE id = #{id}")
    int deleteByPrimaryKey(Long id);
    
    // Customer-specific queries
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE customer_id = #{customerId} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByCustomerId(@Param("customerId") Long customerId, @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE customer_id = #{customerId} " +
            "AND status = #{status} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByCustomerIdAndStatus(
        @Param("customerId") Long customerId,
        @Param("status") String status,
        @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE customer_id = #{customerId} " +
            "AND notification_type = #{notificationType} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByCustomerIdAndType(
        @Param("customerId") Long customerId,
        @Param("notificationType") String notificationType,
        @Param("limit") int limit);
    
    // Status-based queries
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE status = #{status} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByStatus(@Param("status") String status, @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE status = 'FAILED' " +
            "AND retry_count < #{maxRetries} " +
            "AND (failed_at IS NULL OR failed_at < NOW() - INTERVAL 1 HOUR) " +
            "ORDER BY created_at ASC " +
            "LIMIT 100")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findFailedNotificationsForRetry(@Param("maxRetries") int maxRetries);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE status IN ('PENDING', 'SENDING') " +
            "AND created_at < NOW() - INTERVAL #{timeoutMinutes} MINUTE " +
            "ORDER BY created_at ASC")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findPendingNotificationsOlderThan(@Param("timeoutMinutes") int timeoutMinutes);
    
    // Template-based queries
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE template_id = #{templateId} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByTemplateId(@Param("templateId") Long templateId, @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE template_id = #{templateId} " +
            "AND status = #{status} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByTemplateIdAndStatus(
        @Param("templateId") Long templateId,
        @Param("status") String status,
        @Param("limit") int limit);
    
    // Date-based queries
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE created_at >= #{startDate} " +
            "AND created_at <= #{endDate} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByDateRange(
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate,
        @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE sent_at >= #{startDate} " +
            "AND sent_at <= #{endDate} " +
            "ORDER BY sent_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findBySentDateRange(
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate,
        @Param("limit") int limit);
    
    // Statistics and Analytics
    
    @Select("SELECT COUNT(*) FROM crm_notification_log WHERE status = #{status}")
    int countByStatus(@Param("status") String status);
    
    @Select("SELECT COUNT(*) FROM crm_notification_log " +
            "WHERE customer_id = #{customerId} AND status = #{status}")
    int countByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") String status);
    
    @Select("SELECT notification_type, COUNT(*) as count " +
            "FROM crm_notification_log " +
            "WHERE created_at >= NOW() - INTERVAL #{days} DAY " +
            "GROUP BY notification_type " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getNotificationTypeStatistics(@Param("days") int days);
    
    @Select("SELECT status, COUNT(*) as count " +
            "FROM crm_notification_log " +
            "WHERE created_at >= NOW() - INTERVAL #{days} DAY " +
            "GROUP BY status " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getStatusStatistics(@Param("days") int days);
    
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COUNT(*) as total_sent, " +
            "SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END) as delivered, " +
            "SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed " +
            "FROM crm_notification_log " +
            "WHERE created_at >= NOW() - INTERVAL #{days} DAY " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date DESC")
    List<Map<String, Object>> getDailyStatistics(@Param("days") int days);
    
    @Select("SELECT " +
            "COUNT(*) as total_notifications, " +
            "SUM(CASE WHEN status = 'SENT' THEN 1 ELSE 0 END) as total_sent, " +
            "SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END) as total_delivered, " +
            "SUM(CASE WHEN status = 'READ' THEN 1 ELSE 0 END) as total_read, " +
            "SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as total_failed, " +
            "ROUND(SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as delivery_rate " +
            "FROM crm_notification_log " +
            "WHERE created_at >= NOW() - INTERVAL #{days} DAY")
    Map<String, Object> getOverallStatistics(@Param("days") int days);
    
    // Customer Analytics
    
    @Select("SELECT customer_id, COUNT(*) as notification_count " +
            "FROM crm_notification_log " +
            "WHERE created_at >= NOW() - INTERVAL #{days} DAY " +
            "GROUP BY customer_id " +
            "ORDER BY notification_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getTopNotifiedCustomers(@Param("days") int days, @Param("limit") int limit);
    
    @Select("SELECT " +
            "recipient, " +
            "COUNT(*) as total_notifications, " +
            "SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END) as delivered_count, " +
            "ROUND(SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as delivery_rate " +
            "FROM crm_notification_log " +
            "WHERE notification_type = #{notificationType} " +
            "AND created_at >= NOW() - INTERVAL #{days} DAY " +
            "GROUP BY recipient " +
            "HAVING COUNT(*) >= #{minNotifications} " +
            "ORDER BY delivery_rate DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getDeliveryRateByRecipient(
        @Param("notificationType") String notificationType,
        @Param("days") int days,
        @Param("minNotifications") int minNotifications,
        @Param("limit") int limit);
    
    // Performance Metrics
    
    @Select("SELECT " +
            "AVG(TIMESTAMPDIFF(SECOND, created_at, sent_at)) as avg_queue_time_seconds, " +
            "AVG(TIMESTAMPDIFF(SECOND, sent_at, delivered_at)) as avg_delivery_time_seconds, " +
            "MAX(TIMESTAMPDIFF(SECOND, created_at, sent_at)) as max_queue_time_seconds, " +
            "MAX(TIMESTAMPDIFF(SECOND, sent_at, delivered_at)) as max_delivery_time_seconds " +
            "FROM crm_notification_log " +
            "WHERE sent_at IS NOT NULL " +
            "AND created_at >= NOW() - INTERVAL #{days} DAY")
    Map<String, Object> getPerformanceMetrics(@Param("days") int days);
    
    // Cleanup Operations
    
    @Delete("DELETE FROM crm_notification_log " +
            "WHERE status IN ('DELIVERED', 'READ') " +
            "AND created_at < NOW() - INTERVAL #{retentionDays} DAY")
    int deleteOldSuccessfulNotifications(@Param("retentionDays") int retentionDays);
    
    @Delete("DELETE FROM crm_notification_log " +
            "WHERE status = 'FAILED' " +
            "AND retry_count >= max_retries " +
            "AND created_at < NOW() - INTERVAL #{retentionDays} DAY")
    int deleteOldFailedNotifications(@Param("retentionDays") int retentionDays);
    
    // Bulk Status Updates
    
    @Update("UPDATE crm_notification_log " +
            "SET status = 'FAILED', " +
            "failed_at = NOW(), " +
            "error_message = 'Timeout after maximum retries', " +
            "updated_at = NOW() " +
            "WHERE status IN ('PENDING', 'SENDING') " +
            "AND retry_count >= max_retries")
    int markTimeoutNotificationsAsFailed();
    
    @Update("UPDATE crm_notification_log " +
            "SET retry_count = retry_count + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int incrementRetryCount(@Param("id") Long id);
    
    // Search Operations
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE (recipient LIKE CONCAT('%', #{keyword}, '%') " +
            "OR subject LIKE CONCAT('%', #{keyword}, '%') " +
            "OR message LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> searchNotifications(@Param("keyword") String keyword, @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_log " +
            "WHERE notification_type = #{notificationType} " +
            "AND recipient = #{recipient} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationLogResultMap")
    List<NotificationLog> findByTypeAndRecipient(
        @Param("notificationType") String notificationType,
        @Param("recipient") String recipient,
        @Param("limit") int limit);
    
    // Result Map Definition
    @Results(id = "notificationLogResultMap", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
        @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.BIGINT),
        @Result(column = "template_id", property = "templateId", jdbcType = JdbcType.BIGINT),
        @Result(column = "notification_type", property = "notificationType", jdbcType = JdbcType.VARCHAR),
        @Result(column = "recipient", property = "recipient", jdbcType = JdbcType.VARCHAR),
        @Result(column = "subject", property = "subject", jdbcType = JdbcType.VARCHAR),
        @Result(column = "message", property = "message", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
        @Result(column = "metadata_json", property = "metadataJson", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "sent_at", property = "sentAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "delivered_at", property = "deliveredAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "read_at", property = "readAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "failed_at", property = "failedAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "retry_count", property = "retryCount", jdbcType = JdbcType.INTEGER),
        @Result(column = "max_retries", property = "maxRetries", jdbcType = JdbcType.INTEGER),
        @Result(column = "error_message", property = "errorMessage", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    @Select("SELECT * FROM crm_notification_log WHERE 1=0") // Dummy query for result map
    void dummyQueryForResultMap();
}
