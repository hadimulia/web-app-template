package app.spring.web.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payment Transaction entity representing individual payment gateway transactions
 * Tracks all payment processing through external gateways with status updates
 * 
 * @author CRM System
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "crm_payment_transaction")
public class PaymentTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id")
    private Long paymentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", insertable = false, updatable = false)
    private Payment payment;
    
    @Column(name = "gateway_id", nullable = false)
    private Long gatewayId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gateway_id", insertable = false, updatable = false)
    private PaymentGateway gateway;
    
    @Column(name = "external_transaction_id", length = 255)
    private String externalTransactionId;
    
    @Column(name = "gateway_reference", length = 255)
    private String gatewayReference;
    
    @Column(name = "transaction_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private Double amount;
    
    @Column(name = "gateway_fee", precision = 10, scale = 2)
    private Double gatewayFee = 0.0;
    
    @Column(name = "currency", length = 3)
    private String currency = "IDR";
    
    @Column(name = "redirect_url", length = 500)
    private String redirectUrl;
    
    @Column(name = "callback_url", length = 500)
    private String callbackUrl;
    
    @Column(name = "webhook_data", columnDefinition = "TEXT")
    private String webhookData;
    
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;
    
    @Column(name = "error_code", length = 50)
    private String errorCode;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum TransactionType {
        PAYMENT, REFUND, CHARGEBACK, VOID
    }
    
    public enum TransactionStatus {
        PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, EXPIRED, REFUNDED
    }
    
    // Constructors
    public PaymentTransaction() {}
    
    public PaymentTransaction(Long paymentId, Long gatewayId, TransactionType transactionType, Double amount) {
        this.paymentId = paymentId;
        this.gatewayId = gatewayId;
        this.transactionType = transactionType;
        this.amount = amount;
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
    public boolean isPending() {
        return TransactionStatus.PENDING.equals(this.status);
    }
    
    public boolean isProcessing() {
        return TransactionStatus.PROCESSING.equals(this.status);
    }
    
    public boolean isSuccessful() {
        return TransactionStatus.SUCCESS.equals(this.status);
    }
    
    public boolean isFailed() {
        return TransactionStatus.FAILED.equals(this.status);
    }
    
    public boolean isCancelled() {
        return TransactionStatus.CANCELLED.equals(this.status);
    }
    
    public boolean isExpired() {
        return TransactionStatus.EXPIRED.equals(this.status);
    }
    
    public boolean isRefunded() {
        return TransactionStatus.REFUNDED.equals(this.status);
    }
    
    public boolean isCompleted() {
        return isSuccessful() || isRefunded();
    }
    
    public boolean canBeProcessed() {
        return isPending() && !isExpired();
    }
    
    public boolean isExpiredByTime() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public Double getNetAmount() {
        if (amount == null) return 0.0;
        Double fee = gatewayFee != null ? gatewayFee : 0.0;
        return amount - fee;
    }
    
    public void markAsProcessing() {
        this.status = TransactionStatus.PROCESSING;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsSuccessful() {
        this.status = TransactionStatus.SUCCESS;
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
    
    public void markAsFailed(String errorCode, String errorMessage) {
        this.status = TransactionStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
    
    public void markAsCancelled() {
        this.status = TransactionStatus.CANCELLED;
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
    
    public void markAsExpired() {
        this.status = TransactionStatus.EXPIRED;
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
    
    public void setExpirationTime(int minutes) {
        this.expiresAt = LocalDateTime.now().plusMinutes(minutes);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    public Long getGatewayId() {
        return gatewayId;
    }
    
    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }
    
    public PaymentGateway getGateway() {
        return gateway;
    }
    
    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }
    
    public String getExternalTransactionId() {
        return externalTransactionId;
    }
    
    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }
    
    public String getGatewayReference() {
        return gatewayReference;
    }
    
    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public Double getGatewayFee() {
        return gatewayFee;
    }
    
    public void setGatewayFee(Double gatewayFee) {
        this.gatewayFee = gatewayFee;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }
    
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public String getWebhookData() {
        return webhookData;
    }
    
    public void setWebhookData(String webhookData) {
        this.webhookData = webhookData;
    }
    
    public String getGatewayResponse() {
        return gatewayResponse;
    }
    
    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(externalTransactionId, that.externalTransactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, externalTransactionId);
    }
    
    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "id=" + id +
                ", paymentId=" + paymentId +
                ", gatewayId=" + gatewayId +
                ", externalTransactionId='" + externalTransactionId + '\'' +
                ", transactionType=" + transactionType +
                ", status=" + status +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
