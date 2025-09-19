package app.spring.web.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
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
