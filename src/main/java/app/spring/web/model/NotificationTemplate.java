package app.spring.web.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Notification Template entity for managing email/SMS templates
 * Used for invoice notifications, payment reminders, and confirmations
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "crm_notification_template")
public class NotificationTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "template_code", nullable = false, unique = true, length = 50)
    private String templateCode;
    
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;
    
    @Column(name = "template_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TemplateType templateType;
    
    @Column(name = "event_trigger", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EventTrigger eventTrigger;
    
    @Column(name = "subject", length = 255)
    private String subject;
    
    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Enums
    public enum TemplateType {
        EMAIL, SMS, PUSH, WEBHOOK
    }
    
    public enum EventTrigger {
        INVOICE_CREATED,
        INVOICE_SENT, 
        PAYMENT_DUE,
        PAYMENT_OVERDUE,
        PAYMENT_RECEIVED,
        PAYMENT_FAILED,
        INVOICE_CANCELLED,
        CUSTOMER_REGISTERED,
        ACCOUNT_SUSPENDED,
        ACCOUNT_ACTIVATED
    }
    
    // Constructors
    public NotificationTemplate() {}
    
    public NotificationTemplate(String templateCode, String templateName, TemplateType templateType, EventTrigger eventTrigger) {
        this.templateCode = templateCode;
        this.templateName = templateName;
        this.templateType = templateType;
        this.eventTrigger = eventTrigger;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isEmailTemplate() {
        return TemplateType.EMAIL.equals(this.templateType);
    }
    
    public boolean isSmsTemplate() {
        return TemplateType.SMS.equals(this.templateType);
    }
    
    public boolean isPushTemplate() {
        return TemplateType.PUSH.equals(this.templateType);
    }
    
    public boolean isWebhookTemplate() {
        return TemplateType.WEBHOOK.equals(this.templateType);
    }
    
    public String processTemplate(java.util.Map<String, Object> variables) {
        String processedBody = this.bodyTemplate;
        
        if (variables != null) {
            for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                processedBody = processedBody.replace(placeholder, value);
            }
        }
        
        return processedBody;
    }
    
    public String processSubject(java.util.Map<String, Object> variables) {
        if (this.subject == null) return null;
        
        String processedSubject = this.subject;
        
        if (variables != null) {
            for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                processedSubject = processedSubject.replace(placeholder, value);
            }
        }
        
        return processedSubject;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTemplateCode() {
        return templateCode;
    }
    
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public TemplateType getTemplateType() {
        return templateType;
    }
    
    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }
    
    public EventTrigger getEventTrigger() {
        return eventTrigger;
    }
    
    public void setEventTrigger(EventTrigger eventTrigger) {
        this.eventTrigger = eventTrigger;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBodyTemplate() {
        return bodyTemplate;
    }
    
    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public Long getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTemplate that = (NotificationTemplate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(templateCode, that.templateCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, templateCode);
    }
    
    @Override
    public String toString() {
        return "NotificationTemplate{" +
                "id=" + id +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateType=" + templateType +
                ", eventTrigger=" + eventTrigger +
                ", isActive=" + isActive +
                '}';
    }
}
