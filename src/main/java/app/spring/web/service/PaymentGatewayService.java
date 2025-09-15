package app.spring.web.service;

import app.spring.web.model.PaymentGateway;
import app.spring.web.model.PaymentTransaction;
import app.spring.web.model.Payment;
import app.spring.web.model.Invoice;
import app.spring.web.model.Customer;
import app.spring.web.mapper.PaymentGatewayMapper;
import app.spring.web.mapper.PaymentTransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Optional;

/**
 * Payment Gateway Service for processing payments through multiple gateways
 * Supports Midtrans, Stripe, PayPal, and other payment processors
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Transactional
public class PaymentGatewayService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);
    
    @Autowired
    private PaymentGatewayMapper paymentGatewayMapper;
    
    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${app.payment.callback.base-url:http://localhost:8080}")
    private String callbackBaseUrl;
    
    @Value("${app.payment.default-expiry-minutes:30}")
    private int defaultExpiryMinutes;
    
    // Payment Gateway Integration Methods
    
    /**
     * Get all active payment gateways
     */
    public List<PaymentGateway> getActiveGateways() {
        try {
            return paymentGatewayMapper.findByIsActive(true);
        } catch (Exception e) {
            logger.error("Error retrieving active gateways", e);
            throw new RuntimeException("Failed to retrieve active gateways", e);
        }
    }
    
    /**
     * Get gateway by name
     */
    public PaymentGateway getGatewayByName(String gatewayName) {
        try {
            return paymentGatewayMapper.findByGatewayName(gatewayName);
        } catch (Exception e) {
            logger.error("Error retrieving gateway: {}", gatewayName, e);
            throw new RuntimeException("Failed to retrieve gateway: " + gatewayName, e);
        }
    }
    
    /**
     * Create payment transaction for specific gateway
     */
    public PaymentTransaction createPaymentTransaction(Payment payment, PaymentGateway gateway, Map<String, Object> options) {
        try {
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setPaymentId(payment.getId());
            transaction.setGatewayId(gateway.getId());
            transaction.setTransactionType(PaymentTransaction.TransactionType.PAYMENT);
            // This line is already handled above
            transaction.setCurrency("IDR");
            transaction.setExpirationTime(defaultExpiryMinutes);
            
            // Calculate gateway fee
            Double amount = payment.getAmount() != null ? payment.getAmount().doubleValue() : 0.0;
            transaction.setAmount(amount);
            Double gatewayFee = gateway.calculateFee(amount);
            transaction.setGatewayFee(gatewayFee);
            
            // Set callback URLs
            transaction.setCallbackUrl(callbackBaseUrl + "/api/payments/callback/" + gateway.getGatewayName());
            transaction.setRedirectUrl(options.getOrDefault("redirect_url", "").toString());
            
            // Generate external transaction ID
            String externalId = generateTransactionId(gateway.getGatewayName());
            transaction.setExternalTransactionId(externalId);
            
            paymentTransactionMapper.insert(transaction);
            
            logger.info("Created payment transaction: {} for gateway: {}", transaction.getId(), gateway.getGatewayName());
            return transaction;
            
        } catch (Exception e) {
            logger.error("Error creating payment transaction for gateway: {}", gateway.getGatewayName(), e);
            throw new RuntimeException("Failed to create payment transaction", e);
        }
    }
    
    /**
     * Process payment through gateway
     */
    public PaymentProcessingResult processPayment(PaymentTransaction transaction, Invoice invoice, Customer customer) {
        PaymentGateway gateway = paymentGatewayMapper.selectByPrimaryKey(transaction.getGatewayId());
        
        if (gateway == null || !gateway.getIsActive()) {
            throw new RuntimeException("Payment gateway not available");
        }
        
        try {
            PaymentProcessingResult result;
            
            switch (gateway.getGatewayName().toUpperCase()) {
                case "MIDTRANS_SNAP":
                    result = processMidtransPayment(transaction, invoice, customer, gateway);
                    break;
                case "STRIPE":
                    result = processStripePayment(transaction, invoice, customer, gateway);
                    break;
                case "BANK_TRANSFER":
                    result = processBankTransferPayment(transaction, invoice, customer, gateway);
                    break;
                case "GOPAY":
                case "OVO":
                case "DANA":
                    result = processEwalletPayment(transaction, invoice, customer, gateway);
                    break;
                default:
                    result = processGenericGatewayPayment(transaction, invoice, customer, gateway);
            }
            
            // Update transaction with result
            updateTransactionWithResult(transaction, result);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error processing payment through gateway: {}", gateway.getGatewayName(), e);
            transaction.markAsFailed("PROCESSING_ERROR", e.getMessage());
            paymentTransactionMapper.updateByPrimaryKey(transaction);
            throw new RuntimeException("Payment processing failed", e);
        }
    }
    
    /**
     * Process Midtrans Snap payment
     */
    private PaymentProcessingResult processMidtransPayment(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        logger.info("Processing Midtrans payment for transaction: {}", transaction.getId());
        
        // Build Midtrans request
        Map<String, Object> request = buildMidtransRequest(transaction, invoice, customer, gateway);
        
        // For demo purposes, we'll simulate the API call
        // In production, you would call Midtrans API here
        String snapToken = "snap-token-" + transaction.getExternalTransactionId();
        String redirectUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + snapToken;
        
        PaymentProcessingResult result = new PaymentProcessingResult();
        result.setSuccess(true);
        result.setTransactionId(transaction.getExternalTransactionId());
        result.setGatewayReference(snapToken);
        result.setRedirectUrl(redirectUrl);
        result.setStatus(PaymentTransaction.TransactionStatus.PROCESSING);
        result.setGatewayResponse(convertToJson(request));
        
        return result;
    }
    
    /**
     * Process Stripe payment
     */
    private PaymentProcessingResult processStripePayment(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        logger.info("Processing Stripe payment for transaction: {}", transaction.getId());
        
        // Build Stripe request
        Map<String, Object> request = buildStripeRequest(transaction, invoice, customer, gateway);
        
        // Simulate Stripe Payment Intent creation
        String paymentIntentId = "pi_" + transaction.getExternalTransactionId();
        String clientSecret = paymentIntentId + "_secret";
        
        PaymentProcessingResult result = new PaymentProcessingResult();
        result.setSuccess(true);
        result.setTransactionId(transaction.getExternalTransactionId());
        result.setGatewayReference(paymentIntentId);
        result.setClientSecret(clientSecret);
        result.setStatus(PaymentTransaction.TransactionStatus.PROCESSING);
        result.setGatewayResponse(convertToJson(request));
        
        return result;
    }
    
    /**
     * Process bank transfer payment
     */
    private PaymentProcessingResult processBankTransferPayment(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        logger.info("Processing bank transfer for transaction: {}", transaction.getId());
        
        // Generate virtual account or bank details
        Map<String, Object> bankDetails = new HashMap<>();
        bankDetails.put("bank_name", "Bank Mandiri");
        bankDetails.put("account_number", "1234567890" + transaction.getId());
        bankDetails.put("account_name", "CRM Payment System");
        bankDetails.put("amount", transaction.getAmount());
        bankDetails.put("expires_at", transaction.getExpiresAt());
        
        PaymentProcessingResult result = new PaymentProcessingResult();
        result.setSuccess(true);
        result.setTransactionId(transaction.getExternalTransactionId());
        result.setGatewayReference(bankDetails.get("account_number").toString());
        result.setBankDetails(bankDetails);
        result.setStatus(PaymentTransaction.TransactionStatus.PENDING);
        result.setGatewayResponse(convertToJson(bankDetails));
        
        return result;
    }
    
    /**
     * Process e-wallet payment (GoPay, OVO, DANA)
     */
    private PaymentProcessingResult processEwalletPayment(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        logger.info("Processing e-wallet payment for gateway: {} and transaction: {}", gateway.getGatewayName(), transaction.getId());
        
        // Generate QR code or deeplink
        String qrCode = generateQRCode(transaction, gateway);
        String deepLink = generateDeepLink(transaction, gateway);
        
        Map<String, Object> ewalletDetails = new HashMap<>();
        ewalletDetails.put("gateway", gateway.getGatewayName());
        ewalletDetails.put("qr_code", qrCode);
        ewalletDetails.put("deeplink", deepLink);
        ewalletDetails.put("amount", transaction.getAmount());
        ewalletDetails.put("expires_at", transaction.getExpiresAt());
        
        PaymentProcessingResult result = new PaymentProcessingResult();
        result.setSuccess(true);
        result.setTransactionId(transaction.getExternalTransactionId());
        result.setGatewayReference(qrCode);
        result.setQrCode(qrCode);
        result.setDeepLink(deepLink);
        result.setStatus(PaymentTransaction.TransactionStatus.PENDING);
        result.setGatewayResponse(convertToJson(ewalletDetails));
        
        return result;
    }
    
    /**
     * Process generic gateway payment
     */
    private PaymentProcessingResult processGenericGatewayPayment(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        logger.info("Processing generic gateway payment for: {}", gateway.getGatewayName());
        
        PaymentProcessingResult result = new PaymentProcessingResult();
        result.setSuccess(true);
        result.setTransactionId(transaction.getExternalTransactionId());
        result.setStatus(PaymentTransaction.TransactionStatus.PENDING);
        result.setMessage("Payment initiated through " + gateway.getDisplayName());
        
        return result;
    }
    
    /**
     * Handle webhook callback from payment gateway
     */
    @Transactional
    public void handleWebhookCallback(String gatewayName, String payload, Map<String, String> headers) {
        try {
            PaymentGateway gateway = getGatewayByName(gatewayName);
            if (gateway == null) {
                logger.error("Unknown gateway for webhook: {}", gatewayName);
                return;
            }
            
            // Parse webhook payload
            JsonNode webhookData = objectMapper.readTree(payload);
            String externalTransactionId = extractTransactionId(webhookData, gatewayName);
            
            if (externalTransactionId == null) {
                logger.error("Could not extract transaction ID from webhook for gateway: {}", gatewayName);
                return;
            }
            
            // Find transaction
            PaymentTransaction transaction = paymentTransactionMapper.findByExternalTransactionId(externalTransactionId);
            if (transaction == null) {
                logger.error("Transaction not found for ID: {} from gateway: {}", externalTransactionId, gatewayName);
                return;
            }
            
            // Update transaction based on webhook
            updateTransactionFromWebhook(transaction, webhookData, gatewayName);
            
            // If payment is successful, update the payment record
            if (transaction.isSuccessful()) {
                updatePaymentStatus(transaction);
            }
            
            logger.info("Webhook processed successfully for transaction: {} from gateway: {}", transaction.getId(), gatewayName);
            
        } catch (Exception e) {
            logger.error("Error processing webhook from gateway: {}", gatewayName, e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }
    
    // Helper Methods
    
    private Map<String, Object> buildMidtransRequest(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        Map<String, Object> request = new HashMap<>();
        
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", transaction.getExternalTransactionId());
        transactionDetails.put("gross_amount", transaction.getAmount().intValue());
        request.put("transaction_details", transactionDetails);
        
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("first_name", customer.getContactPerson());
        customerDetails.put("email", customer.getEmail());
        customerDetails.put("phone", customer.getPhone());
        request.put("customer_details", customerDetails);
        
        Map<String, Object> callbacks = new HashMap<>();
        callbacks.put("finish", transaction.getRedirectUrl());
        request.put("callbacks", callbacks);
        
        return request;
    }
    
    private Map<String, Object> buildStripeRequest(PaymentTransaction transaction, Invoice invoice, Customer customer, PaymentGateway gateway) {
        Map<String, Object> request = new HashMap<>();
        request.put("amount", Double.valueOf(transaction.getAmount() * 100).intValue()); // Stripe uses cents
        request.put("currency", transaction.getCurrency().toLowerCase());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("transaction_id", transaction.getExternalTransactionId());
        metadata.put("invoice_number", invoice.getInvoiceNumber());
        metadata.put("customer_id", customer.getId().toString());
        request.put("metadata", metadata);
        
        return request;
    }
    
    private String generateTransactionId(String gatewayName) {
        String prefix = gatewayName.substring(0, Math.min(3, gatewayName.length())).toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }
    
    private String generateQRCode(PaymentTransaction transaction, PaymentGateway gateway) {
        return "QR-" + gateway.getGatewayName() + "-" + transaction.getExternalTransactionId();
    }
    
    private String generateDeepLink(PaymentTransaction transaction, PaymentGateway gateway) {
        String baseUrl;
        switch (gateway.getGatewayName().toLowerCase()) {
            case "gopay":
                baseUrl = "gojek://pay?";
                break;
            case "ovo":
                baseUrl = "ovo://pay?";
                break;
            case "dana":
                baseUrl = "dana://pay?";
                break;
            default:
                baseUrl = "payment://pay?";
        }
        return baseUrl + "amount=" + transaction.getAmount() + "&ref=" + transaction.getExternalTransactionId();
    }
    
    private void updateTransactionWithResult(PaymentTransaction transaction, PaymentProcessingResult result) {
        transaction.setGatewayReference(result.getGatewayReference());
        transaction.setStatus(result.getStatus());
        transaction.setGatewayResponse(result.getGatewayResponse());
        
        if (!result.isSuccess()) {
            transaction.setErrorMessage(result.getMessage());
        }
        
        paymentTransactionMapper.updateByPrimaryKey(transaction);
    }
    
    private String extractTransactionId(JsonNode webhookData, String gatewayName) {
        // Extract transaction ID based on gateway format
        switch (gatewayName.toUpperCase()) {
            case "MIDTRANS_SNAP":
                return webhookData.has("order_id") ? webhookData.get("order_id").asText() : null;
            case "STRIPE":
                return webhookData.has("metadata") && webhookData.get("metadata").has("transaction_id") ?
                       webhookData.get("metadata").get("transaction_id").asText() : null;
            default:
                return webhookData.has("transaction_id") ? webhookData.get("transaction_id").asText() : null;
        }
    }
    
    private void updateTransactionFromWebhook(PaymentTransaction transaction, JsonNode webhookData, String gatewayName) {
        transaction.setWebhookData(webhookData.toString());
        
        // Update status based on webhook data
        String status = extractStatusFromWebhook(webhookData, gatewayName);
        switch (status) {
            case "success":
            case "capture":
            case "settlement":
                transaction.markAsSuccessful();
                break;
            case "failed":
            case "failure":
                String errorMsg = webhookData.has("status_message") ? webhookData.get("status_message").asText() : "Payment failed";
                transaction.markAsFailed("WEBHOOK_FAILURE", errorMsg);
                break;
            case "cancelled":
            case "cancel":
                transaction.markAsCancelled();
                break;
            case "expired":
            case "expire":
                transaction.markAsExpired();
                break;
        }
        
        paymentTransactionMapper.updateByPrimaryKey(transaction);
    }
    
    private String extractStatusFromWebhook(JsonNode webhookData, String gatewayName) {
        switch (gatewayName.toUpperCase()) {
            case "MIDTRANS_SNAP":
                return webhookData.has("transaction_status") ? webhookData.get("transaction_status").asText() : "unknown";
            case "STRIPE":
                return webhookData.has("status") ? webhookData.get("status").asText() : "unknown";
            default:
                return webhookData.has("status") ? webhookData.get("status").asText() : "unknown";
        }
    }
    
    private void updatePaymentStatus(PaymentTransaction transaction) {
        try {
            // TODO: Implement payment status update when PaymentMapper is available
            logger.info("Payment completed for transaction: {}, gateway reference: {}", 
                       transaction.getId(), transaction.getGatewayReference());
            
            // This will be implemented once we have the PaymentMapper
            /*
            Payment payment = paymentMapper.selectByPrimaryKey(transaction.getPaymentId());
            if (payment != null) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setReference(transaction.getGatewayReference());
                payment.setPaymentDate(LocalDateTime.now());
                paymentMapper.updateByPrimaryKey(payment);
                
                logger.info("Updated payment status to COMPLETED for payment ID: {}", payment.getId());
            }
            */
        } catch (Exception e) {
            logger.error("Error updating payment status for transaction: {}", transaction.getId(), e);
        }
    }
    
    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Error converting object to JSON", e);
            return "{}";
        }
    }
    
    // Payment Processing Result Class
    public static class PaymentProcessingResult {
        private boolean success;
        private String transactionId;
        private String gatewayReference;
        private String redirectUrl;
        private String clientSecret;
        private String qrCode;
        private String deepLink;
        private Map<String, Object> bankDetails;
        private PaymentTransaction.TransactionStatus status;
        private String message;
        private String gatewayResponse;
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getGatewayReference() { return gatewayReference; }
        public void setGatewayReference(String gatewayReference) { this.gatewayReference = gatewayReference; }
        
        public String getRedirectUrl() { return redirectUrl; }
        public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
        
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        
        public String getQrCode() { return qrCode; }
        public void setQrCode(String qrCode) { this.qrCode = qrCode; }
        
        public String getDeepLink() { return deepLink; }
        public void setDeepLink(String deepLink) { this.deepLink = deepLink; }
        
        public Map<String, Object> getBankDetails() { return bankDetails; }
        public void setBankDetails(Map<String, Object> bankDetails) { this.bankDetails = bankDetails; }
        
        public PaymentTransaction.TransactionStatus getStatus() { return status; }
        public void setStatus(PaymentTransaction.TransactionStatus status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getGatewayResponse() { return gatewayResponse; }
        public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
    }
}
