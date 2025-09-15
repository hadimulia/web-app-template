package app.spring.web.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_payment")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Payment number is required")
    @Size(max = 50, message = "Payment number must not exceed 50 characters")
    private String paymentNumber;
    
    @Column(name = "invoice_id", nullable = false)
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @Column(name = "customer_id", nullable = false)
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be positive")
    private BigDecimal amount;
    
    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency = "IDR";
    
    @Column(name = "payment_date", nullable = false)
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
    
    @Column(name = "payment_method", length = 50)
    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;
    
    @Column(name = "status", length = 20)
    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "PENDING|PROCESSING|COMPLETED|FAILED|CANCELLED|REFUNDED", message = "Invalid payment status")
    private String status = "PENDING";
    
    @Column(name = "gateway_reference", length = 255)
    @Size(max = 255, message = "Gateway reference must not exceed 255 characters")
    private String gatewayReference;
    
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "fee_amount", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Fee amount must be positive")
    private BigDecimal feeAmount = BigDecimal.ZERO;
    
    @Column(name = "net_amount", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Net amount must be positive")
    private BigDecimal netAmount;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Transient fields
    @Transient
    private Invoice invoice;
    
    @Transient
    private Customer customer;
    
    // Constructors
    public Payment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paymentDate = LocalDate.now();
    }
    
    public Payment(Long invoiceId, Long customerId, BigDecimal amount) {
        this();
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.amount = amount;
        calculateNetAmount();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPaymentNumber() {
        return paymentNumber;
    }
    
    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }
    
    public Long getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        calculateNetAmount();
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        if ("COMPLETED".equals(status) && processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
    
    public String getGatewayReference() {
        return gatewayReference;
    }
    
    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }
    
    public String getGatewayResponse() {
        return gatewayResponse;
    }
    
    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public BigDecimal getFeeAmount() {
        return feeAmount;
    }
    
    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
        calculateNetAmount();
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
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
    
    public Invoice getInvoice() {
        return invoice;
    }
    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    // Utility methods
    public String getStatusText() {
        switch (status) {
            case "PENDING": return "Pending";
            case "PROCESSING": return "Processing";
            case "COMPLETED": return "Completed";
            case "FAILED": return "Failed";
            case "CANCELLED": return "Cancelled";
            case "REFUNDED": return "Refunded";
            default: return "Unknown";
        }
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    public String getFormattedAmount() {
        return String.format("%s %,.2f", currency, amount);
    }
    
    public String getFormattedNetAmount() {
        if (netAmount != null) {
            return String.format("%s %,.2f", currency, netAmount);
        }
        return getFormattedAmount();
    }
    
    public void calculateNetAmount() {
        if (amount != null) {
            if (feeAmount != null) {
                netAmount = amount.subtract(feeAmount);
            } else {
                netAmount = amount;
            }
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateNetAmount();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateNetAmount();
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentNumber='" + paymentNumber + '\'' +
                ", invoiceId=" + invoiceId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
