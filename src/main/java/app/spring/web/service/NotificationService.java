package app.spring.web.service;

import app.spring.web.model.NotificationTemplate;
import app.spring.web.model.NotificationLog;
import app.spring.web.model.Customer;
import app.spring.web.model.Invoice;
import app.spring.web.model.Payment;
import app.spring.web.mapper.NotificationTemplateMapper;
import app.spring.web.mapper.NotificationLogMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Notification Service for managing email, SMS, and push notifications
 * Handles template processing, notification queuing, and delivery tracking
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationTemplateMapper notificationTemplateMapper;
    
    @Autowired
    private NotificationLogMapper notificationLogMapper;
    
    // Mail sender would be configured in production
    // @Autowired(required = false)
    // private MailSender mailSender;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${app.notification.from-email:noreply@crm-system.com}")
    private String defaultFromEmail;
    
    @Value("${app.notification.from-name:CRM System}")
    private String defaultFromName;
    
    @Value("${app.notification.max-retries:3}")
    private int maxRetries;
    
    @Value("${app.notification.enabled:true}")
    private boolean notificationsEnabled;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Template Management
    
    /**
     * Get all active notification templates
     */
    public List<NotificationTemplate> getAllActiveTemplates() {
        try {
            return notificationTemplateMapper.findByIsActive(true);
        } catch (Exception e) {
            logger.error("Error retrieving active templates", e);
            throw new RuntimeException("Failed to retrieve notification templates", e);
        }
    }
    
    /**
     * Get template by code
     */
    public NotificationTemplate getTemplateByCode(String templateCode) {
        try {
            return notificationTemplateMapper.findByTemplateCode(templateCode);
        } catch (Exception e) {
            logger.error("Error retrieving template: {}", templateCode, e);
            return null;
        }
    }
    
    /**
     * Get templates by event trigger
     */
    public List<NotificationTemplate> getTemplatesByEvent(NotificationTemplate.EventTrigger eventTrigger) {
        try {
            return notificationTemplateMapper.findByEventTrigger(eventTrigger.toString());
        } catch (Exception e) {
            logger.error("Error retrieving templates for event: {}", eventTrigger, e);
            return new ArrayList<>();
        }
    }
    
    // Notification Sending Methods
    
    /**
     * Send invoice created notification
     */
    @Async
    public CompletableFuture<Void> sendInvoiceCreatedNotification(Invoice invoice, Customer customer) {
        try {
            if (!notificationsEnabled) {
                logger.info("Notifications disabled, skipping invoice created notification");
                return CompletableFuture.completedFuture(null);
            }
            
            Map<String, Object> variables = buildInvoiceVariables(invoice, customer);
            sendNotificationsByEvent(NotificationTemplate.EventTrigger.INVOICE_CREATED, customer, variables);
            
            logger.info("Invoice created notification sent for invoice: {}", invoice.getInvoiceNumber());
        } catch (Exception e) {
            logger.error("Error sending invoice created notification for invoice: {}", invoice.getInvoiceNumber(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Send payment reminder notification
     */
    @Async
    public CompletableFuture<Void> sendPaymentReminderNotification(Invoice invoice, Customer customer) {
        try {
            if (!notificationsEnabled) {
                logger.info("Notifications disabled, skipping payment reminder notification");
                return CompletableFuture.completedFuture(null);
            }
            
            Map<String, Object> variables = buildInvoiceVariables(invoice, customer);
            sendNotificationsByEvent(NotificationTemplate.EventTrigger.PAYMENT_DUE, customer, variables);
            
            logger.info("Payment reminder notification sent for invoice: {}", invoice.getInvoiceNumber());
        } catch (Exception e) {
            logger.error("Error sending payment reminder notification for invoice: {}", invoice.getInvoiceNumber(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Send overdue invoice notification
     */
    @Async
    public CompletableFuture<Void> sendOverdueNotification(Invoice invoice, Customer customer) {
        try {
            if (!notificationsEnabled) {
                logger.info("Notifications disabled, skipping overdue notification");
                return CompletableFuture.completedFuture(null);
            }
            
            Map<String, Object> variables = buildInvoiceVariables(invoice, customer);
            sendNotificationsByEvent(NotificationTemplate.EventTrigger.PAYMENT_OVERDUE, customer, variables);
            
            logger.info("Overdue notification sent for invoice: {}", invoice.getInvoiceNumber());
        } catch (Exception e) {
            logger.error("Error sending overdue notification for invoice: {}", invoice.getInvoiceNumber(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Send payment received notification
     */
    @Async
    public CompletableFuture<Void> sendPaymentReceivedNotification(Payment payment, Invoice invoice, Customer customer) {
        try {
            if (!notificationsEnabled) {
                logger.info("Notifications disabled, skipping payment received notification");
                return CompletableFuture.completedFuture(null);
            }
            
            Map<String, Object> variables = buildPaymentVariables(payment, invoice, customer);
            sendNotificationsByEvent(NotificationTemplate.EventTrigger.PAYMENT_RECEIVED, customer, variables);
            
            logger.info("Payment received notification sent for payment: {}", payment.getId());
        } catch (Exception e) {
            logger.error("Error sending payment received notification for payment: {}", payment.getId(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Send customer registration notification
     */
    @Async
    public CompletableFuture<Void> sendCustomerRegistrationNotification(Customer customer) {
        try {
            if (!notificationsEnabled) {
                logger.info("Notifications disabled, skipping customer registration notification");
                return CompletableFuture.completedFuture(null);
            }
            
            Map<String, Object> variables = buildCustomerVariables(customer);
            sendNotificationsByEvent(NotificationTemplate.EventTrigger.CUSTOMER_REGISTERED, customer, variables);
            
            logger.info("Customer registration notification sent for customer: {}", customer.getCustomerCode());
        } catch (Exception e) {
            logger.error("Error sending customer registration notification for customer: {}", customer.getCustomerCode(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    // Core Notification Methods
    
    /**
     * Send notifications for a specific event
     */
    private void sendNotificationsByEvent(NotificationTemplate.EventTrigger eventTrigger, Customer customer, Map<String, Object> variables) {
        List<NotificationTemplate> templates = getTemplatesByEvent(eventTrigger);
        
        for (NotificationTemplate template : templates) {
            if (template.getIsActive()) {
                sendNotification(template, customer, variables);
            }
        }
    }
    
    /**
     * Send a single notification using template
     */
    public NotificationLog sendNotification(NotificationTemplate template, Customer customer, Map<String, Object> variables) {
        try {
            NotificationLog log = createNotificationLog(template, customer, variables);
            
            switch (template.getTemplateType()) {
                case EMAIL:
                    sendEmailNotification(log, template, variables);
                    break;
                case SMS:
                    sendSmsNotification(log, template, variables);
                    break;
                case PUSH:
                    sendPushNotification(log, template, variables);
                    break;
                case WEBHOOK:
                    sendWebhookNotification(log, template, variables);
                    break;
            }
            
            return log;
            
        } catch (Exception e) {
            logger.error("Error sending notification with template: {}", template.getTemplateCode(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
    
    /**
     * Send email notification
     */
    private void sendEmailNotification(NotificationLog log, NotificationTemplate template, Map<String, Object> variables) {
        try {
            // For demo purposes, we'll simulate email sending
            String processedSubject = template.processSubject(variables);
            String processedMessage = template.processTemplate(variables);
            
            log.setSubject(processedSubject);
            log.setMessage(processedMessage);
            
            // Simulate email sending delay
            Thread.sleep(100);
            
            // Mark as sent (in production, integrate with email service)
            log.markAsSent();
            notificationLogMapper.updateByPrimaryKey(log);
            
            logger.info("Email notification sent to: {}", log.getRecipient());
            
        } catch (Exception e) {
            logger.error("Error sending email notification", e);
            log.markAsFailed(e.getMessage());
            notificationLogMapper.updateByPrimaryKey(log);
        }
    }
    
    /**
     * Send SMS notification
     */
    private void sendSmsNotification(NotificationLog log, NotificationTemplate template, Map<String, Object> variables) {
        try {
            String processedMessage = template.processTemplate(variables);
            log.setMessage(processedMessage);
            
            // For demo purposes, we'll mark as sent
            // In production, you would integrate with SMS gateway
            log.markAsSent();
            notificationLogMapper.updateByPrimaryKey(log);
            
            logger.info("SMS notification sent to: {}", log.getRecipient());
            
        } catch (Exception e) {
            logger.error("Error sending SMS notification", e);
            log.markAsFailed(e.getMessage());
            notificationLogMapper.updateByPrimaryKey(log);
        }
    }
    
    /**
     * Send push notification
     */
    private void sendPushNotification(NotificationLog log, NotificationTemplate template, Map<String, Object> variables) {
        try {
            String processedSubject = template.processSubject(variables);
            String processedMessage = template.processTemplate(variables);
            
            log.setSubject(processedSubject);
            log.setMessage(processedMessage);
            
            // For demo purposes, we'll mark as sent
            // In production, you would integrate with push notification service
            log.markAsSent();
            notificationLogMapper.updateByPrimaryKey(log);
            
            logger.info("Push notification sent to: {}", log.getRecipient());
            
        } catch (Exception e) {
            logger.error("Error sending push notification", e);
            log.markAsFailed(e.getMessage());
            notificationLogMapper.updateByPrimaryKey(log);
        }
    }
    
    /**
     * Send webhook notification
     */
    private void sendWebhookNotification(NotificationLog log, NotificationTemplate template, Map<String, Object> variables) {
        try {
            String processedMessage = template.processTemplate(variables);
            log.setMessage(processedMessage);
            
            // For demo purposes, we'll mark as sent
            // In production, you would make HTTP request to webhook URL
            log.markAsSent();
            notificationLogMapper.updateByPrimaryKey(log);
            
            logger.info("Webhook notification sent to: {}", log.getRecipient());
            
        } catch (Exception e) {
            logger.error("Error sending webhook notification", e);
            log.markAsFailed(e.getMessage());
            notificationLogMapper.updateByPrimaryKey(log);
        }
    }
    
    // Helper Methods
    
    /**
     * Create notification log entry
     */
    private NotificationLog createNotificationLog(NotificationTemplate template, Customer customer, Map<String, Object> variables) {
        NotificationLog log = new NotificationLog();
        log.setCustomerId(customer.getId());
        log.setTemplateId(template.getId());
        log.setNotificationType(NotificationLog.NotificationType.valueOf(template.getTemplateType().toString()));
        
        // Determine recipient based on notification type
        String recipient = getRecipient(template.getTemplateType(), customer);
        log.setRecipient(recipient);
        
        // Add metadata
        if (variables != null && !variables.isEmpty()) {
            try {
                log.setMetadataJson(objectMapper.writeValueAsString(variables));
            } catch (Exception e) {
                logger.warn("Failed to serialize notification metadata", e);
            }
        }
        
        notificationLogMapper.insert(log);
        return log;
    }
    
    /**
     * Get recipient address based on notification type
     */
    private String getRecipient(NotificationTemplate.TemplateType templateType, Customer customer) {
        switch (templateType) {
            case EMAIL:
                return customer.getEmail();
            case SMS:
                return customer.getPhone();
            case PUSH:
                return customer.getCustomerCode(); // Could be device token
            case WEBHOOK:
                return "webhook_endpoint"; // Could be configured per customer
            default:
                return customer.getEmail();
        }
    }
    
    /**
     * Build variables for invoice-related notifications
     */
    private Map<String, Object> buildInvoiceVariables(Invoice invoice, Customer customer) {
        Map<String, Object> variables = new HashMap<>();
        
        // Customer variables
        variables.putAll(buildCustomerVariables(customer));
        
        // Invoice variables
        variables.put("invoice_number", invoice.getInvoiceNumber());
        variables.put("invoice_date", invoice.getInvoiceDate().format(DATE_FORMATTER));
        variables.put("due_date", invoice.getDueDate().format(DATE_FORMATTER));
        variables.put("total_amount", formatCurrency(invoice.getTotalAmount()));
        variables.put("currency", "IDR");
        variables.put("status", invoice.getStatus());
        
        // Add description if available (method may need to be added to Invoice model)
        // if (invoice.getDescription() != null) {
        //     variables.put("description", invoice.getDescription());
        // }
        
        // Calculate days until due or overdue
        long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), invoice.getDueDate());
        variables.put("days_until_due", String.valueOf(daysUntilDue));
        variables.put("days_overdue", String.valueOf(Math.max(0, -daysUntilDue)));
        
        return variables;
    }
    
    /**
     * Build variables for payment-related notifications
     */
    private Map<String, Object> buildPaymentVariables(Payment payment, Invoice invoice, Customer customer) {
        Map<String, Object> variables = buildInvoiceVariables(invoice, customer);
        
        // Payment variables
        variables.put("payment_id", payment.getId());
        variables.put("payment_date", payment.getPaymentDate() != null ? payment.getPaymentDate().format(DATETIME_FORMATTER) : "");
        variables.put("payment_amount", formatCurrency(payment.getAmount()));
        variables.put("payment_method", payment.getPaymentMethod() != null ? payment.getPaymentMethod().toString() : "");
        
        // Add payment reference if available (method may need to be added to Payment model)
        // if (payment.getReference() != null) {
        //     variables.put("payment_reference", payment.getReference());
        // }
        
        return variables;
    }
    
    /**
     * Build variables for customer-related notifications
     */
    private Map<String, Object> buildCustomerVariables(Customer customer) {
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("customer_name", customer.getContactPerson() != null ? customer.getContactPerson() : customer.getCompanyName());
        variables.put("customer_code", customer.getCustomerCode());
        variables.put("company_name", customer.getCompanyName());
        variables.put("contact_person", customer.getContactPerson());
        variables.put("email", customer.getEmail());
        variables.put("phone", customer.getPhone());
        variables.put("customer_since", customer.getCustomerSince() != null ? customer.getCustomerSince().format(DATE_FORMATTER) : "");
        
        return variables;
    }
    
    /**
     * Format currency amount
     */
    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "IDR 0";
        return "IDR " + String.format("%,.0f", amount.doubleValue());
    }
    
    // Notification Log Management
    
    /**
     * Get notification logs for customer
     */
    public List<NotificationLog> getNotificationLogsForCustomer(Long customerId, int limit) {
        try {
            return notificationLogMapper.findByCustomerId(customerId, limit);
        } catch (Exception e) {
            logger.error("Error retrieving notification logs for customer: {}", customerId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get failed notifications for retry
     */
    public List<NotificationLog> getFailedNotificationsForRetry() {
        try {
            return notificationLogMapper.findFailedNotificationsForRetry(maxRetries);
        } catch (Exception e) {
            logger.error("Error retrieving failed notifications for retry", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Retry failed notification
     */
    @Async
    public CompletableFuture<Void> retryFailedNotification(Long notificationLogId) {
        try {
            NotificationLog log = notificationLogMapper.selectByPrimaryKey(notificationLogId);
            if (log != null && log.canRetry()) {
                NotificationTemplate template = notificationTemplateMapper.selectByPrimaryKey(log.getTemplateId());
                if (template != null) {
                    // Parse metadata back to variables
                    Map<String, Object> variables = new HashMap<>();
                    if (log.getMetadataJson() != null) {
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> parsedVariables = objectMapper.readValue(log.getMetadataJson(), Map.class);
                            variables = parsedVariables;
                        } catch (Exception e) {
                            logger.warn("Failed to parse notification metadata for retry", e);
                        }
                    }
                    
                    // Reset status and retry
                    log.setStatus(NotificationLog.NotificationStatus.PENDING);
                    notificationLogMapper.updateByPrimaryKey(log);
                    
                    // Send again based on type
                    switch (template.getTemplateType()) {
                        case EMAIL:
                            sendEmailNotification(log, template, variables);
                            break;
                        case SMS:
                            sendSmsNotification(log, template, variables);
                            break;
                        case PUSH:
                            sendPushNotification(log, template, variables);
                            break;
                        case WEBHOOK:
                            sendWebhookNotification(log, template, variables);
                            break;
                    }
                    
                    logger.info("Retried failed notification: {}", notificationLogId);
                }
            }
        } catch (Exception e) {
            logger.error("Error retrying failed notification: {}", notificationLogId, e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Get notification statistics
     */
    public Map<String, Object> getNotificationStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            // Implementation would query database for statistics
            stats.put("total_sent", 0);
            stats.put("total_delivered", 0);
            stats.put("total_failed", 0);
            stats.put("delivery_rate", 0.0);
            
            return stats;
        } catch (Exception e) {
            logger.error("Error retrieving notification statistics", e);
            return new HashMap<>();
        }
    }
}
