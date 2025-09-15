package app.spring.web.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_invoice_item")
public class InvoiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_id", nullable = false)
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @Column(name = "service_id")
    private Long serviceId;
    
    @Column(name = "description", nullable = false, length = 500)
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be positive")
    private BigDecimal quantity = BigDecimal.ONE;
    
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price must be positive")
    private BigDecimal unitPrice = BigDecimal.ZERO;
    
    @Column(name = "line_total", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Line total is required")
    @DecimalMin(value = "0.00", message = "Line total must be positive")
    private BigDecimal lineTotal = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Discount amount must be positive")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Tax amount must be positive")
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "notes", length = 255)
    @Size(max = 255, message = "Notes must not exceed 255 characters")
    private String notes;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Transient fields
    @Transient
    private Invoice invoice;
    
    @Transient
    private Service service;
    
    // Constructors
    public InvoiceItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public InvoiceItem(Long invoiceId, String description, BigDecimal quantity, BigDecimal unitPrice) {
        this();
        this.invoiceId = invoiceId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public Long getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }
    
    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    
    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        calculateLineTotal();
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
    
    public Invoice getInvoice() {
        return invoice;
    }
    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    // Utility methods
    public void calculateLineTotal() {
        if (quantity != null && unitPrice != null) {
            BigDecimal subtotal = quantity.multiply(unitPrice);
            if (discountAmount != null) {
                subtotal = subtotal.subtract(discountAmount);
            }
            lineTotal = subtotal;
        }
    }
    
    public String getFormattedLineTotal() {
        if (lineTotal != null) {
            return String.format("%,.2f", lineTotal);
        }
        return "0.00";
    }
    
    public String getFormattedUnitPrice() {
        if (unitPrice != null) {
            return String.format("%,.2f", unitPrice);
        }
        return "0.00";
    }
    
    public String getFormattedQuantity() {
        if (quantity != null) {
            // Remove trailing zeros
            return quantity.stripTrailingZeros().toPlainString();
        }
        return "1";
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateLineTotal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateLineTotal();
    }
    
    @Override
    public String toString() {
        return "InvoiceItem{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}
