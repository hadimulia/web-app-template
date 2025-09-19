package app.spring.web.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "crm_customer")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_code", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    private String customerCode;
    
    @Column(name = "company_name", length = 255)
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;
    
    @Column(name = "contact_person", length = 100)
    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Column(name = "phone", length = 20)
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "city", length = 100)
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Column(name = "state", length = 100)
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Column(name = "postal_code", length = 20)
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
    
    @Column(name = "country", length = 100)
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country = "Indonesia";
    
    @Column(name = "tax_id", length = 50)
    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;
    
    @Column(name = "customer_type", length = 20)
    @NotBlank(message = "Customer type is required")
    @Pattern(regexp = "CORPORATE|INDIVIDUAL", message = "Customer type must be CORPORATE or INDIVIDUAL")
    private String customerType = "CORPORATE";
    
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private Integer status = 1; // 1=Active, 0=Inactive, 2=Suspended
    
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;
    
    @Column(name = "payment_terms")
    private Integer paymentTerms = 30; // Days
    
    @Column(name = "customer_since")
    private Date customerSince;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Transient fields for relationships
    @Transient
    private List<CustomerBillingGroup> billingGroups;
    
    @Transient
    private CustomerAuth customerAuth;
    
    @Transient
    private BigDecimal totalOutstanding;
    
    @Transient
    private Integer overdueInvoicesCount;
    
    // Constructors
    public Customer() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    public Customer(String customerCode, String email) {
        this();
        this.customerCode = customerCode;
        this.email = email;
    }
    
    // Utility methods
    public String getStatusText() {
        switch (status) {
            case 0: return "Inactive";
            case 1: return "Active";
            case 2: return "Suspended";
            default: return "Unknown";
        }
    }
    
    public String getDisplayName() {
        if (companyName != null && !companyName.trim().isEmpty()) {
            return companyName;
        }
        return contactPerson != null ? contactPerson : email;
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.trim().isEmpty()) {
            sb.append(address);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode);
        }
        if (country != null && !country.trim().isEmpty() && !"Indonesia".equals(country)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", customerCode='" + customerCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
