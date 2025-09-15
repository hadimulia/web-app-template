package app.spring.web.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "crm_invoice")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;
    
    @Column(name = "customer_id", nullable = false)
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Column(name = "billing_group_id", nullable = false)
    @NotNull(message = "Billing group ID is required")
    private Long billingGroupId;
    
    @Column(name = "invoice_date", nullable = false)
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @Column(name = "due_date", nullable = false)
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @Column(name = "period_start", nullable = false)
    @NotNull(message = "Period start date is required")
    private LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    @NotNull(message = "Period end date is required")
    private LocalDate periodEnd;
    
    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.00", message = "Subtotal must be positive")
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount must be positive")
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.00", message = "Discount amount must be positive")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount must be positive")
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency = "IDR";
    
    @Column(name = "status", length = 20)
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PENDING|SENT|PAID|OVERDUE|CANCELLED", message = "Invalid status")
    private String status = "PENDING";
    
    @Column(name = "payment_status", length = 20)
    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "UNPAID|PARTIAL|PAID", message = "Invalid payment status")
    private String paymentStatus = "UNPAID";
    
    @Column(name = "paid_amount", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Paid amount must be positive")
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
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
    
    // Transient fields for relationships and calculations
    @Transient
    private Customer customer;
    
    @Transient
    private BillingGroup billingGroup;
    
    @Transient
    private List<InvoiceItem> invoiceItems;
    
    @Transient
    private List<Payment> payments;
    
    @Transient
    private BigDecimal remainingAmount;
    
    @Transient
    private Integer daysPastDue;
    
    @Transient
    private Boolean isOverdue;
    
    // Constructors
    public Invoice() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.invoiceDate = LocalDate.now();
    }
    
    public Invoice(Long customerId, Long billingGroupId) {
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
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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
    
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
    
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }
    
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
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
    
    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }
    
    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
    
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    
    public Integer getDaysPastDue() {
        return daysPastDue;
    }
    
    public void setDaysPastDue(Integer daysPastDue) {
        this.daysPastDue = daysPastDue;
    }
    
    public Boolean getIsOverdue() {
        return isOverdue;
    }
    
    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
    
    // Utility methods
    public String getStatusText() {
        switch (status) {
            case "PENDING": return "Pending";
            case "SENT": return "Sent";
            case "PAID": return "Paid";
            case "OVERDUE": return "Overdue";
            case "CANCELLED": return "Cancelled";
            default: return "Unknown";
        }
    }
    
    public String getPaymentStatusText() {
        switch (paymentStatus) {
            case "UNPAID": return "Unpaid";
            case "PARTIAL": return "Partially Paid";
            case "PAID": return "Fully Paid";
            default: return "Unknown";
        }
    }
    
    public BigDecimal calculateRemainingAmount() {
        if (totalAmount == null) return BigDecimal.ZERO;
        if (paidAmount == null) return totalAmount;
        return totalAmount.subtract(paidAmount);
    }
    
    public boolean isPaid() {
        return "PAID".equals(paymentStatus);
    }
    
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && !"PAID".equals(paymentStatus);
    }
    
    public int calculateDaysPastDue() {
        if (!isOverdue()) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    public String getFormattedTotal() {
        return String.format("%s %,.2f", currency, totalAmount);
    }
    
    public String getFormattedRemaining() {
        BigDecimal remaining = calculateRemainingAmount();
        return String.format("%s %,.2f", currency, remaining);
    }
    
    public String getPeriodText() {
        if (periodStart != null && periodEnd != null) {
            return String.format("%s - %s", periodStart.toString(), periodEnd.toString());
        }
        return "-";
    }
    
    public void recalculateAmounts() {
        // Recalculate totals based on items
        if (invoiceItems != null && !invoiceItems.isEmpty()) {
            subtotal = invoiceItems.stream()
                    .map(InvoiceItem::getLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        // Calculate total: subtotal + tax - discount
        totalAmount = subtotal.add(taxAmount).subtract(discountAmount);
        
        // Update remaining amount
        remainingAmount = calculateRemainingAmount();
        
        // Update overdue status
        isOverdue = isOverdue();
        daysPastDue = calculateDaysPastDue();
        
        // Update payment status based on paid amount
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = "UNPAID";
        } else if (paidAmount.compareTo(totalAmount) >= 0) {
            paymentStatus = "PAID";
        } else {
            paymentStatus = "PARTIAL";
        }
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
        return "Invoice{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
