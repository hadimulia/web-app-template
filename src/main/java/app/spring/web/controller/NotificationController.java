package app.spring.web.controller;

import app.spring.web.model.NotificationTemplate;
import app.spring.web.model.NotificationLog;
import app.spring.web.model.Customer;
import app.spring.web.service.NotificationService;
import app.spring.web.service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Notification Controller
 * REST API endpoints for notification management and template operations
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private CustomerService customerService;
    
    // Template Management Endpoints
    
    /**
     * Get all active notification templates
     */
    @GetMapping("/templates")
    public ResponseEntity<?> getAllActiveTemplates() {
        try {
            List<NotificationTemplate> templates = notificationService.getAllActiveTemplates();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Templates retrieved successfully");
            response.put("data", templates);
            response.put("count", templates.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving notification templates", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve templates: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get template by code
     */
    @GetMapping("/templates/{templateCode}")
    public ResponseEntity<?> getTemplateByCode(@PathVariable String templateCode) {
        try {
            NotificationTemplate template = notificationService.getTemplateByCode(templateCode);
            
            if (template == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Template not found: " + templateCode);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Template retrieved successfully");
            response.put("data", template);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving template: {}", templateCode, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve template: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get templates by event trigger
     */
    @GetMapping("/templates/event/{eventTrigger}")
    public ResponseEntity<?> getTemplatesByEvent(@PathVariable String eventTrigger) {
        try {
            NotificationTemplate.EventTrigger trigger = NotificationTemplate.EventTrigger.valueOf(eventTrigger.toUpperCase());
            List<NotificationTemplate> templates = notificationService.getTemplatesByEvent(trigger);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Templates retrieved successfully");
            response.put("data", templates);
            response.put("count", templates.size());
            response.put("eventTrigger", eventTrigger);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid event trigger: " + eventTrigger);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving templates for event: {}", eventTrigger, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve templates: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Notification Sending Endpoints
    
    /**
     * Send custom notification
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendCustomNotification(@RequestBody Map<String, Object> request) {
        try {
            String templateCode = (String) request.get("templateCode");
            Long customerId = Long.valueOf(request.get("customerId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) request.getOrDefault("variables", new HashMap<>());
            
            if (templateCode == null || customerId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Template code and customer ID are required");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            NotificationTemplate template = notificationService.getTemplateByCode(templateCode);
            if (template == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Template not found: " + templateCode);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Customer not found: " + customerId);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            NotificationLog log = notificationService.sendNotification(template, customer, variables);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification sent successfully");
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("notificationId", log.getId());
            notificationData.put("status", log.getStatus());
            notificationData.put("recipient", log.getRecipient());
            notificationData.put("type", log.getNotificationType());
            response.put("data", notificationData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error sending custom notification", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send notification: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Send test notification to customer
     */
    @PostMapping("/test/{customerId}")
    public ResponseEntity<?> sendTestNotification(@PathVariable Long customerId) {
        try {
            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Customer not found: " + customerId);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Send registration notification as test
            notificationService.sendCustomerRegistrationNotification(customer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test notification sent successfully");
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("id", customer.getId());
            customerData.put("name", customer.getContactPerson() != null ? customer.getContactPerson() : customer.getCompanyName());
            customerData.put("email", customer.getEmail());
            response.put("customer", customerData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error sending test notification to customer: {}", customerId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send test notification: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Notification Log Management Endpoints
    
    /**
     * Get notification logs for customer
     */
    @GetMapping("/logs/customer/{customerId}")
    public ResponseEntity<?> getCustomerNotificationLogs(@PathVariable Long customerId,
                                                       @RequestParam(defaultValue = "50") int limit) {
        try {
            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Customer not found: " + customerId);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            List<NotificationLog> logs = notificationService.getNotificationLogsForCustomer(customerId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification logs retrieved successfully");
            response.put("data", logs);
            response.put("count", logs.size());
            Map<String, Object> customerInfo = new HashMap<>();
            customerInfo.put("id", customer.getId());
            customerInfo.put("name", customer.getContactPerson() != null ? customer.getContactPerson() : customer.getCompanyName());
            response.put("customer", customerInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving notification logs for customer: {}", customerId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve notification logs: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get failed notifications for retry
     */
    @GetMapping("/logs/failed")
    public ResponseEntity<?> getFailedNotifications() {
        try {
            List<NotificationLog> failedLogs = notificationService.getFailedNotificationsForRetry();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Failed notifications retrieved successfully");
            response.put("data", failedLogs);
            response.put("count", failedLogs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving failed notifications", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve failed notifications: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Retry failed notification
     */
    @PostMapping("/logs/{notificationId}/retry")
    public ResponseEntity<?> retryFailedNotification(@PathVariable Long notificationId) {
        try {
            notificationService.retryFailedNotification(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification retry initiated successfully");
            response.put("notificationId", notificationId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrying notification: {}", notificationId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retry notification: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Analytics and Statistics Endpoints
    
    /**
     * Get notification statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getNotificationStatistics() {
        try {
            Map<String, Object> statistics = notificationService.getNotificationStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification statistics retrieved successfully");
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving notification statistics", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve statistics: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Administrative Endpoints
    
    /**
     * Get system health for notifications
     */
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("notificationService", "UP");
            health.put("templatesActive", notificationService.getAllActiveTemplates().size());
            health.put("timestamp", java.time.LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification system health check completed");
            response.put("data", health);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking notification system health", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Health check failed: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get available event triggers
     */
    @GetMapping("/events")
    public ResponseEntity<?> getAvailableEventTriggers() {
        try {
            NotificationTemplate.EventTrigger[] triggers = NotificationTemplate.EventTrigger.values();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event triggers retrieved successfully");
            response.put("data", triggers);
            response.put("count", triggers.length);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving event triggers", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve event triggers: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get available notification types
     */
    @GetMapping("/types")
    public ResponseEntity<?> getAvailableNotificationTypes() {
        try {
            NotificationTemplate.TemplateType[] types = NotificationTemplate.TemplateType.values();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification types retrieved successfully");
            response.put("data", types);
            response.put("count", types.length);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving notification types", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve notification types: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Bulk Operations
    
    /**
     * Send bulk notifications to customers
     */
    @PostMapping("/bulk/send")
    public ResponseEntity<?> sendBulkNotifications(@RequestBody Map<String, Object> request) {
        try {
            String templateCode = (String) request.get("templateCode");
            @SuppressWarnings("unchecked")
            List<Long> customerIds = (List<Long>) request.get("customerIds");
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) request.getOrDefault("variables", new HashMap<>());
            
            if (templateCode == null || customerIds == null || customerIds.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Template code and customer IDs are required");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            NotificationTemplate template = notificationService.getTemplateByCode(templateCode);
            if (template == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Template not found: " + templateCode);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            int successCount = 0;
            int failureCount = 0;
            
            for (Long customerId : customerIds) {
                try {
                    Customer customer = customerService.findById(customerId);
                    if (customer != null) {
                        notificationService.sendNotification(template, customer, variables);
                        successCount++;
                    } else {
                        failureCount++;
                        logger.warn("Customer not found for bulk notification: {}", customerId);
                    }
                } catch (Exception e) {
                    failureCount++;
                    logger.error("Error sending bulk notification to customer: {}", customerId, e);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Bulk notifications completed. Success: %d, Failed: %d", successCount, failureCount));
            Map<String, Object> bulkData = new HashMap<>();
            bulkData.put("totalRequested", customerIds.size());
            bulkData.put("successCount", successCount);
            bulkData.put("failureCount", failureCount);
            bulkData.put("templateCode", templateCode);
            response.put("data", bulkData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error sending bulk notifications", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send bulk notifications: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
