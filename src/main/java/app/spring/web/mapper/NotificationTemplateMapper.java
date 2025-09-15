package app.spring.web.mapper;

import app.spring.web.model.NotificationTemplate;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import java.util.List;
import java.util.Map;

/**
 * NotificationTemplate Mapper Interface
 * Handles database operations for notification templates
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface NotificationTemplateMapper {
    
    // Basic CRUD Operations
    
    @Insert("INSERT INTO crm_notification_template (" +
            "template_code, template_name, template_type, event_trigger, " +
            "subject, body_template, " +
            "is_active, created_at, updated_at) " +
            "VALUES (" +
            "#{templateCode,jdbcType=VARCHAR}, " +
            "#{templateName,jdbcType=VARCHAR}, " +
            "#{templateType,jdbcType=VARCHAR}, " +
            "#{eventTrigger,jdbcType=VARCHAR}, " +
            "#{subject,jdbcType=VARCHAR}, " +
            "#{bodyTemplate,jdbcType=LONGVARCHAR}, " +
            "#{isActive,jdbcType=BOOLEAN}, " +
            "NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NotificationTemplate record);
    
    @Select("SELECT * FROM crm_notification_template WHERE id = #{id}")
    @Results({
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
        @Result(column = "template_code", property = "templateCode", jdbcType = JdbcType.VARCHAR),
        @Result(column = "template_name", property = "templateName", jdbcType = JdbcType.VARCHAR),
        @Result(column = "template_type", property = "templateType", jdbcType = JdbcType.VARCHAR),
        @Result(column = "event_trigger", property = "eventTrigger", jdbcType = JdbcType.VARCHAR),
        @Result(column = "subject", property = "subject", jdbcType = JdbcType.VARCHAR),
        @Result(column = "body_template", property = "bodyTemplate", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "is_active", property = "isActive", jdbcType = JdbcType.BOOLEAN),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    NotificationTemplate selectByPrimaryKey(Long id);
    
    @Update("UPDATE crm_notification_template SET " +
            "template_code = #{templateCode,jdbcType=VARCHAR}, " +
            "template_name = #{templateName,jdbcType=VARCHAR}, " +
            "template_type = #{templateType,jdbcType=VARCHAR}, " +
            "event_trigger = #{eventTrigger,jdbcType=VARCHAR}, " +
            "subject = #{subject,jdbcType=VARCHAR}, " +
            "body_template = #{bodyTemplate,jdbcType=LONGVARCHAR}, " +
            "is_active = #{isActive,jdbcType=BOOLEAN}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateByPrimaryKey(NotificationTemplate record);
    
    @Delete("DELETE FROM crm_notification_template WHERE id = #{id}")
    int deleteByPrimaryKey(Long id);
    
    // Custom Query Methods
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE is_active = #{isActive} " +
            "ORDER BY template_name")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> findByIsActive(@Param("isActive") boolean isActive);
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE template_code = #{templateCode} " +
            "AND is_active = true")
    @ResultMap("notificationTemplateResultMap")
    NotificationTemplate findByTemplateCode(@Param("templateCode") String templateCode);
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE event_trigger = #{eventTrigger} " +
            "AND is_active = true " +
            "ORDER BY template_name")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> findByEventTrigger(@Param("eventTrigger") String eventTrigger);
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE template_type = #{templateType} " +
            "AND is_active = true " +
            "ORDER BY template_name")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> findByTemplateType(@Param("templateType") String templateType);
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE language = #{language} " +
            "AND is_active = true " +
            "ORDER BY template_name")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> findByLanguage(@Param("language") String language);
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE event_trigger = #{eventTrigger} " +
            "AND template_type = #{templateType} " +
            "AND is_active = true " +
            "ORDER BY template_name")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> findByEventTriggerAndType(
        @Param("eventTrigger") String eventTrigger, 
        @Param("templateType") String templateType);
    
    // Search and Filter Methods
    
    @Select("SELECT * FROM crm_notification_template " +
            "WHERE (template_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR template_code LIKE CONCAT('%', #{keyword}, '%') " +
            "OR message_template LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND is_active = #{isActive} " +
            "ORDER BY template_name " +
            "LIMIT #{limit}")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> searchTemplates(
        @Param("keyword") String keyword,
        @Param("isActive") boolean isActive,
        @Param("limit") int limit);
    
    @Select("SELECT * FROM crm_notification_template " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    @ResultMap("notificationTemplateResultMap")
    List<NotificationTemplate> findRecentTemplates(@Param("limit") int limit);
    
    // Statistics and Analytics
    
    @Select("SELECT COUNT(*) FROM crm_notification_template WHERE is_active = true")
    int countActiveTemplates();
    
    @Select("SELECT COUNT(*) FROM crm_notification_template WHERE is_active = false")
    int countInactiveTemplates();
    
    @Select("SELECT template_type, COUNT(*) as count " +
            "FROM crm_notification_template " +
            "WHERE is_active = true " +
            "GROUP BY template_type " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getTemplateTypeStatistics();
    
    @Select("SELECT event_trigger, COUNT(*) as count " +
            "FROM crm_notification_template " +
            "WHERE is_active = true " +
            "GROUP BY event_trigger " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getEventTriggerStatistics();
    
    // Bulk Operations
    
    @Update("UPDATE crm_notification_template SET is_active = #{isActive}, updated_at = NOW() " +
            "WHERE event_trigger = #{eventTrigger}")
    int updateActiveStatusByEventTrigger(
        @Param("eventTrigger") String eventTrigger,
        @Param("isActive") boolean isActive);
    
    @Update("UPDATE crm_notification_template SET is_active = #{isActive}, updated_at = NOW() " +
            "WHERE template_type = #{templateType}")
    int updateActiveStatusByTemplateType(
        @Param("templateType") String templateType,
        @Param("isActive") boolean isActive);
    
    @Delete("DELETE FROM crm_notification_template WHERE is_active = false")
    int deleteInactiveTemplates();
    
    // Template Management
    
    @Update("UPDATE crm_notification_template SET is_active = false, updated_at = NOW() " +
            "WHERE id = #{id}")
    int deactivateTemplate(@Param("id") Long id);
    
    @Update("UPDATE crm_notification_template SET is_active = true, updated_at = NOW() " +
            "WHERE id = #{id}")
    int activateTemplate(@Param("id") Long id);
    
    @Select("SELECT EXISTS(SELECT 1 FROM crm_notification_template " +
            "WHERE template_code = #{templateCode} AND id != #{excludeId})")
    boolean existsByTemplateCodeExcludingId(
        @Param("templateCode") String templateCode, 
        @Param("excludeId") Long excludeId);
    
    @Select("SELECT EXISTS(SELECT 1 FROM crm_notification_template " +
            "WHERE template_code = #{templateCode})")
    boolean existsByTemplateCode(@Param("templateCode") String templateCode);
    
    // Result Map Definition
    @Results(id = "notificationTemplateResultMap", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
        @Result(column = "template_code", property = "templateCode", jdbcType = JdbcType.VARCHAR),
        @Result(column = "template_name", property = "templateName", jdbcType = JdbcType.VARCHAR),
        @Result(column = "template_type", property = "templateType", jdbcType = JdbcType.VARCHAR),
        @Result(column = "event_trigger", property = "eventTrigger", jdbcType = JdbcType.VARCHAR),
        @Result(column = "subject", property = "subject", jdbcType = JdbcType.VARCHAR),
        @Result(column = "body_template", property = "bodyTemplate", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "is_active", property = "isActive", jdbcType = JdbcType.BOOLEAN),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    @Select("SELECT * FROM crm_notification_template WHERE 1=0") // Dummy query for result map
    void dummyQueryForResultMap();
}
