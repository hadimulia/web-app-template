package app.spring.web.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Notification Log entity for tracking sent notifications
 * Records delivery status, timestamps, and error details for all notifications
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "crm_notification_log")
public class NotificationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
    
    @Column(name = "template_id", nullable = false)
    private Long templateId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private NotificationTemplate template;
    
    @Column(name = "notification_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    
    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;
    
    @Column(name = "subject", length = 255)
    private String subject;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Enums
    public enum NotificationType {
        EMAIL, SMS, PUSH, WEBHOOK
    }
    
    public enum NotificationStatus {
        PENDING, QUEUED, SENT, DELIVERED, READ, FAILED, CANCELLED, BOUNCED
    }
    
    // Constructors
    public NotificationLog() {}
    
    public NotificationLog(Long customerId, Long templateId, NotificationType notificationType, String recipient) {
        this.customerId = customerId;
        this.templateId = templateId;
        this.notificationType = notificationType;
        this.recipient = recipient;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isPending() {
        return NotificationStatus.PENDING.equals(this.status);
    }
    
    public boolean isQueued() {
        return NotificationStatus.QUEUED.equals(this.status);
    }
    
    public boolean isSent() {
        return NotificationStatus.SENT.equals(this.status) || 
               NotificationStatus.DELIVERED.equals(this.status) || 
               NotificationStatus.READ.equals(this.status);
    }
    
    public boolean isDelivered() {
        return NotificationStatus.DELIVERED.equals(this.status) || 
               NotificationStatus.READ.equals(this.status);
    }
    
    public boolean isRead() {
        return NotificationStatus.READ.equals(this.status);
    }
    
    public boolean isFailed() {
        return NotificationStatus.FAILED.equals(this.status) || 
               NotificationStatus.BOUNCED.equals(this.status);
    }
    
    public boolean canRetry() {
        return isFailed() && (retryCount == null || retryCount < 3);
    }
    
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }
    
    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }
    
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount = (this.retryCount != null) ? this.retryCount + 1 : 1;
    }
    
    public void markAsBounced(String errorMessage) {
        this.status = NotificationStatus.BOUNCED;
        this.errorMessage = errorMessage;
    }
    
    public void markAsQueued() {
        this.status = NotificationStatus.QUEUED;
    }
    
    public long getDeliveryTimeInMillis() {
        if (sentAt != null && deliveredAt != null) {
            return java.time.Duration.between(sentAt, deliveredAt).toMillis();
        }
        return 0;
    }
    
    public long getReadTimeInMillis() {
        if (deliveredAt != null && readAt != null) {
            return java.time.Duration.between(deliveredAt, readAt).toMillis();
        }
        return 0;
    }
    
    public boolean isEmailNotification() {
        return NotificationType.EMAIL.equals(this.notificationType);
    }
    
    public boolean isSmsNotification() {
        return NotificationType.SMS.equals(this.notificationType);
    }
    
    public boolean isPushNotification() {
        return NotificationType.PUSH.equals(this.notificationType);
    }
    
    public boolean isWebhookNotification() {
        return NotificationType.WEBHOOK.equals(this.notificationType);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Long getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    
    public NotificationTemplate getTemplate() {
        return template;
    }
    
    public void setTemplate(NotificationTemplate template) {
        this.template = template;
    }
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public String getMetadataJson() {
        return metadataJson;
    }
    
    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationLog that = (NotificationLog) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "NotificationLog{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", templateId=" + templateId +
                ", notificationType=" + notificationType +
                ", recipient='" + recipient + '\'' +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
