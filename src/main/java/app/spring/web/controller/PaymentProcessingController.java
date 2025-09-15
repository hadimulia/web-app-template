package app.spring.web.controller;

import app.spring.web.model.PaymentGateway;
import app.spring.web.model.PaymentTransaction;
import app.spring.web.model.Payment;
import app.spring.web.model.Invoice;
import app.spring.web.model.Customer;
import app.spring.web.service.PaymentGatewayService;
import app.spring.web.service.InvoiceService;
import app.spring.web.service.CustomerService;
import app.spring.web.service.PaymentGatewayService.PaymentProcessingResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Payment Processing Controller for handling payment gateway operations
 * Manages payment initiation, webhook callbacks, and transaction status updates
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentProcessingController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessingController.class);
    
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * Get available payment gateways
     */
    @GetMapping("/gateways")
    public ResponseEntity<Map<String, Object>> getAvailableGateways() {
        try {
            List<PaymentGateway> gateways = paymentGatewayService.getActiveGateways();
            
            List<Map<String, Object>> gatewayList = gateways.stream().map(gateway -> {
                Map<String, Object> gatewayInfo = new HashMap<>();
                gatewayInfo.put("id", gateway.getId());
                gatewayInfo.put("name", gateway.getGatewayName());
                gatewayInfo.put("displayName", gateway.getDisplayName());
                gatewayInfo.put("type", gateway.getGatewayType());
                gatewayInfo.put("supportedCurrencies", gateway.getSupportedCurrencyArray());
                gatewayInfo.put("feePercent", gateway.getFeePercent());
                gatewayInfo.put("feeFixed", gateway.getFeeFixed());
                return gatewayInfo;
            }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", gatewayList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving payment gateways", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve payment gateways");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get payment gateways by type
     */
    @GetMapping("/gateways/type/{gatewayType}")
    public ResponseEntity<Map<String, Object>> getGatewaysByType(@PathVariable String gatewayType) {
        try {
            List<PaymentGateway> gateways = paymentGatewayService.getActiveGateways()
                .stream()
                .filter(gateway -> gatewayType.equalsIgnoreCase(gateway.getGatewayType().toString()))
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", gateways);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving gateways by type: {}", gatewayType, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve gateways by type");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Initialize payment for an invoice
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializePayment(@RequestBody Map<String, Object> request) {
        try {
            Long invoiceId = Long.valueOf(request.get("invoiceId").toString());
            String gatewayName = request.get("gatewayName").toString();
            String redirectUrl = request.getOrDefault("redirectUrl", "").toString();
            
            // Get invoice and validate
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invoice not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if invoice is already paid
            if ("PAID".equals(invoice.getStatus())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invoice is already paid");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Get customer
            Customer customer = customerService.findById(invoice.getCustomerId());
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Customer not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Get payment gateway
            PaymentGateway gateway = paymentGatewayService.getGatewayByName(gatewayName);
            if (gateway == null || !gateway.getIsActive()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Payment gateway not available");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create payment record
            Payment payment = new Payment();
            payment.setInvoiceId(invoiceId);
            payment.setCustomerId(customer.getId());
            payment.setAmount(invoice.getTotalAmount());
            // Set payment method based on gateway type - will be implemented when Payment model is enhanced
            // payment.setPaymentMethod(Payment.PaymentMethod.valueOf(gateway.getGatewayType().toString()));
            // Set other payment fields as needed
            
            // Create payment options
            Map<String, Object> options = new HashMap<>();
            options.put("redirect_url", redirectUrl);
            options.putAll(request); // Include any additional options
            
            // Create payment transaction
            PaymentTransaction transaction = paymentGatewayService.createPaymentTransaction(payment, gateway, options);
            
            // Process payment through gateway
            PaymentProcessingResult result = paymentGatewayService.processPayment(transaction, invoice, customer);
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("transactionId", result.getTransactionId());
            response.put("gatewayReference", result.getGatewayReference());
            
            if (result.getRedirectUrl() != null) {
                response.put("redirectUrl", result.getRedirectUrl());
            }
            if (result.getClientSecret() != null) {
                response.put("clientSecret", result.getClientSecret());
            }
            if (result.getQrCode() != null) {
                response.put("qrCode", result.getQrCode());
            }
            if (result.getDeepLink() != null) {
                response.put("deepLink", result.getDeepLink());
            }
            if (result.getBankDetails() != null) {
                response.put("bankDetails", result.getBankDetails());
            }
            
            response.put("status", result.getStatus().toString());
            response.put("message", result.getMessage());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error initializing payment", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to initialize payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Handle webhook callbacks from payment gateways
     */
    @PostMapping("/callback/{gatewayName}")
    public ResponseEntity<Map<String, Object>> handleWebhookCallback(
            @PathVariable String gatewayName,
            @RequestBody String payload,
            HttpServletRequest request) {
        
        try {
            logger.info("Received webhook callback from gateway: {}", gatewayName);
            
            // Extract headers
            Map<String, String> headers = new HashMap<>();
            java.util.Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
            
            // Process webhook
            paymentGatewayService.handleWebhookCallback(gatewayName, payload, headers);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Webhook processed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing webhook from gateway: {}", gatewayName, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Webhook processing failed");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Calculate payment fee for a gateway and amount
     */
    @PostMapping("/calculate-fee")
    public ResponseEntity<Map<String, Object>> calculatePaymentFee(@RequestBody Map<String, Object> request) {
        try {
            String gatewayName = request.get("gatewayName").toString();
            Double amount = Double.valueOf(request.get("amount").toString());
            
            PaymentGateway gateway = paymentGatewayService.getGatewayByName(gatewayName);
            if (gateway == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Payment gateway not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Double fee = gateway.calculateFee(amount);
            Double netAmount = gateway.getNetAmount(amount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("amount", amount);
            response.put("fee", fee);
            response.put("netAmount", netAmount);
            response.put("feePercent", gateway.getFeePercent());
            response.put("feeFixed", gateway.getFeeFixed());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error calculating payment fee", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to calculate payment fee");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get transaction status
     */
    @GetMapping("/transaction/{transactionId}/status")
    public ResponseEntity<Map<String, Object>> getTransactionStatus(@PathVariable String transactionId) {
        try {
            // This would typically query the payment gateway or database for transaction status
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transactionId);
            response.put("status", "PENDING"); // This would come from actual lookup
            response.put("message", "Transaction status retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving transaction status for: {}", transactionId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve transaction status");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Cancel a pending transaction
     */
    @PostMapping("/transaction/{transactionId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelTransaction(@PathVariable String transactionId) {
        try {
            // Implementation to cancel transaction would go here
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transactionId);
            response.put("message", "Transaction cancelled successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelling transaction: {}", transactionId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to cancel transaction");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get payment gateway configuration (admin only)
     */
    @GetMapping("/admin/gateways/{gatewayId}/config")
    public ResponseEntity<Map<String, Object>> getGatewayConfig(@PathVariable Long gatewayId) {
        try {
            // This would be protected by admin authentication
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gateway configuration retrieved");
            // Add gateway configuration details here
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving gateway configuration for ID: {}", gatewayId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve gateway configuration");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update payment gateway configuration (admin only)
     */
    @PutMapping("/admin/gateways/{gatewayId}/config")
    public ResponseEntity<Map<String, Object>> updateGatewayConfig(
            @PathVariable Long gatewayId, 
            @RequestBody Map<String, Object> config) {
        try {
            // This would be protected by admin authentication
            // Implementation to update gateway configuration
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gateway configuration updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating gateway configuration for ID: {}", gatewayId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update gateway configuration");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
