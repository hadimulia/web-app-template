package app.spring.web.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_billing_group")
public class BillingGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "group_code", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Group code is required")
    @Size(max = 50, message = "Group code must not exceed 50 characters")
    private String groupCode;
    
    @Column(name = "group_name", nullable = false, length = 100)
    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String groupName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.00", message = "Base price must be positive")
    private BigDecimal basePrice = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency = "IDR";
    
    @Column(name = "billing_cycle", length = 20)
    @NotBlank(message = "Billing cycle is required")
    @Pattern(regexp = "MONTHLY|QUARTERLY|YEARLY|ONE_TIME", message = "Billing cycle must be MONTHLY, QUARTERLY, YEARLY, or ONE_TIME")
    private String billingCycle = "MONTHLY";
    
    @Column(name = "due_days")
    @Min(value = 1, message = "Due days must be at least 1")
    @Max(value = 365, message = "Due days must not exceed 365")
    private Integer dueDays = 30;
    
    @Column(name = "auto_generate")
    private Boolean autoGenerate = true;
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Tax rate must be positive")
    @DecimalMax(value = "100.00", message = "Tax rate must not exceed 100%")
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private Integer status = 1; // 1=Active, 0=Inactive
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Transient fields for statistics
    @Transient
    private Integer activeCustomersCount;
    
    @Transient
    private BigDecimal monthlyRevenue;
    
    @Transient
    private Integer pendingInvoicesCount;
    
    // Constructors
    public BillingGroup() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BillingGroup(String groupCode, String groupName, BigDecimal basePrice) {
        this();
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.basePrice = basePrice;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGroupCode() {
        return groupCode;
    }
    
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    public Integer getDueDays() {
        return dueDays;
    }
    
    public void setDueDays(Integer dueDays) {
        this.dueDays = dueDays;
    }
    
    public Boolean getAutoGenerate() {
        return autoGenerate;
    }
    
    public void setAutoGenerate(Boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }
    
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
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
    
    public Integer getActiveCustomersCount() {
        return activeCustomersCount;
    }
    
    public void setActiveCustomersCount(Integer activeCustomersCount) {
        this.activeCustomersCount = activeCustomersCount;
    }
    
    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }
    
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
    
    public Integer getPendingInvoicesCount() {
        return pendingInvoicesCount;
    }
    
    public void setPendingInvoicesCount(Integer pendingInvoicesCount) {
        this.pendingInvoicesCount = pendingInvoicesCount;
    }
    
    // Utility methods
    public String getStatusText() {
        return status == 1 ? "Active" : "Inactive";
    }
    
    public boolean isActive() {
        return status == 1;
    }
    
    public String getBillingCycleText() {
        switch (billingCycle) {
            case "MONTHLY": return "Monthly";
            case "QUARTERLY": return "Quarterly";
            case "YEARLY": return "Yearly";
            case "ONE_TIME": return "One Time";
            default: return "Unknown";
        }
    }
    
    public String getFormattedPrice() {
        return String.format("%s %,.2f", currency, basePrice);
    }
    
    public String getFormattedTaxRate() {
        return String.format("%.2f%%", taxRate);
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
        return "BillingGroup{" +
                "id=" + id +
                ", groupCode='" + groupCode + '\'' +
                ", groupName='" + groupName + '\'' +
                ", basePrice=" + basePrice +
                ", billingCycle='" + billingCycle + '\'' +
                '}';
    }
}
