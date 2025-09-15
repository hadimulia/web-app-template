package app.spring.web.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_customer_billing_group")
public class CustomerBillingGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Column(name = "billing_group_id", nullable = false)
    @NotNull(message = "Billing group ID is required")
    private Long billingGroupId;
    
    @Column(name = "custom_price", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Custom price must be positive")
    private BigDecimal customPrice;
    
    @Column(name = "discount_percent", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Discount percent must be positive")
    @DecimalMax(value = "100.00", message = "Discount percent must not exceed 100%")
    private BigDecimal discountPercent = BigDecimal.ZERO;
    
    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private Integer status = 1; // 1=Active, 0=Inactive
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Transient fields for relationships
    @Transient
    private Customer customer;
    
    @Transient
    private BillingGroup billingGroup;
    
    @Transient
    private BigDecimal effectivePrice; // Final price after custom price and discount
    
    @Transient
    private Integer activeInvoicesCount;
    
    // Constructors
    public CustomerBillingGroup() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.startDate = LocalDate.now();
    }
    
    public CustomerBillingGroup(Long customerId, Long billingGroupId) {
        this();
        this.customerId = customerId;
        this.billingGroupId = billingGroupId;
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
    
    public Long getBillingGroupId() {
        return billingGroupId;
    }
    
    public void setBillingGroupId(Long billingGroupId) {
        this.billingGroupId = billingGroupId;
    }
    
    public BigDecimal getCustomPrice() {
        return customPrice;
    }
    
    public void setCustomPrice(BigDecimal customPrice) {
        this.customPrice = customPrice;
    }
    
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }
    
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public BillingGroup getBillingGroup() {
        return billingGroup;
    }
    
    public void setBillingGroup(BillingGroup billingGroup) {
        this.billingGroup = billingGroup;
    }
    
    public BigDecimal getEffectivePrice() {
        return effectivePrice;
    }
    
    public void setEffectivePrice(BigDecimal effectivePrice) {
        this.effectivePrice = effectivePrice;
    }
    
    public Integer getActiveInvoicesCount() {
        return activeInvoicesCount;
    }
    
    public void setActiveInvoicesCount(Integer activeInvoicesCount) {
        this.activeInvoicesCount = activeInvoicesCount;
    }
    
    // Utility methods
    public String getStatusText() {
        return status == 1 ? "Active" : "Inactive";
    }
    
    public boolean isActive() {
        return status == 1 && (endDate == null || endDate.isAfter(LocalDate.now()));
    }
    
    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }
    
    public BigDecimal calculateEffectivePrice() {
        if (billingGroup == null) {
            return customPrice != null ? customPrice : BigDecimal.ZERO;
        }
        
        BigDecimal basePrice = customPrice != null ? customPrice : billingGroup.getBasePrice();
        
        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = basePrice.multiply(discountPercent).divide(new BigDecimal("100"));
            return basePrice.subtract(discountAmount);
        }
        
        return basePrice;
    }
    
    public String getFormattedEffectivePrice() {
        BigDecimal price = effectivePrice != null ? effectivePrice : calculateEffectivePrice();
        String currency = billingGroup != null ? billingGroup.getCurrency() : "IDR";
        return String.format("%s %,.2f", currency, price);
    }
    
    public String getFormattedDiscount() {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return "No Discount";
        }
        return String.format("%.2f%% Off", discountPercent);
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "CustomerBillingGroup{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", billingGroupId=" + billingGroupId +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
