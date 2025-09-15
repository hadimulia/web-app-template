package app.spring.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.spring.web.mapper.InvoiceMapper;
import app.spring.web.mapper.CustomerMapper;
import app.spring.web.mapper.BillingGroupMapper;
import app.spring.web.model.Invoice;
import app.spring.web.model.InvoiceItem;
import app.spring.web.model.Customer;
import app.spring.web.model.BillingGroup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InvoiceService {
    
    @Autowired
    private InvoiceMapper invoiceMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private BillingGroupMapper billingGroupMapper;
    
    // Basic CRUD Operations
    public void createInvoice(Invoice invoice) {
        // Generate invoice number if not provided
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        }
        
        // Set timestamps
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        
        // Recalculate amounts
        invoice.recalculateAmounts();
        
        invoiceMapper.insert(invoice);
        
        // Insert invoice items
        if (invoice.getInvoiceItems() != null) {
            for (int i = 0; i < invoice.getInvoiceItems().size(); i++) {
                InvoiceItem item = invoice.getInvoiceItems().get(i);
                item.setInvoiceId(invoice.getId());
                item.setSortOrder(i + 1);
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());
                invoiceMapper.insertItem(item);
            }
        }
    }
    
    public void updateInvoice(Invoice invoice) {
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice.recalculateAmounts();
        invoiceMapper.update(invoice);
        
        // Update invoice items
        if (invoice.getInvoiceItems() != null) {
            // Delete existing items
            invoiceMapper.deleteItemsByInvoiceId(invoice.getId());
            
            // Insert updated items
            for (int i = 0; i < invoice.getInvoiceItems().size(); i++) {
                InvoiceItem item = invoice.getInvoiceItems().get(i);
                item.setId(null); // Reset ID for new insert
                item.setInvoiceId(invoice.getId());
                item.setSortOrder(i + 1);
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());
                invoiceMapper.insertItem(item);
            }
        }
    }
    
    public void deleteInvoice(Long id) {
        // Delete invoice items first
        invoiceMapper.deleteItemsByInvoiceId(id);
        // Delete invoice
        invoiceMapper.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        Invoice invoice = invoiceMapper.findById(id);
        if (invoice != null) {
            // Load invoice items
            List<InvoiceItem> items = invoiceMapper.findItemsByInvoiceId(id);
            invoice.setInvoiceItems(items);
            
            // Load customer details
            if (invoice.getCustomerId() != null) {
                Customer customer = customerMapper.selectByPrimaryKey(invoice.getCustomerId());
                invoice.setCustomer(customer);
            }
            
            // Load billing group details
            if (invoice.getBillingGroupId() != null) {
                BillingGroup billingGroup = billingGroupMapper.selectByPrimaryKey(invoice.getBillingGroupId());
                invoice.setBillingGroup(billingGroup);
            }
            
            // Calculate transient fields
            invoice.setRemainingAmount(invoice.calculateRemainingAmount());
            invoice.setIsOverdue(invoice.isOverdue());
            invoice.setDaysPastDue(invoice.calculateDaysPastDue());
        }
        return invoice;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getInvoices(int page, int size, String sortField, String sortDir,
                                           String searchTerm, Long customerId, String status, 
                                           String paymentStatus, LocalDate startDate, LocalDate endDate,
                                           Boolean overdue) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", page * size);
        params.put("limit", size);
        params.put("sortField", sortField);
        params.put("sortDir", sortDir);
        params.put("searchTerm", searchTerm);
        params.put("customerId", customerId);
        params.put("status", status);
        params.put("paymentStatus", paymentStatus);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("overdue", overdue);
        
        List<Invoice> invoices = invoiceMapper.findAll(params);
        long totalElements = invoiceMapper.count(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", invoices);
        result.put("totalElements", totalElements);
        result.put("totalPages", (int) Math.ceil((double) totalElements / size));
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }
    
    // Invoice Generation
    @Transactional
    public Invoice generateInvoiceForCustomer(Long customerId, Long billingGroupId, 
                                            LocalDate periodStart, LocalDate periodEnd) {
        Customer customer = customerMapper.selectByPrimaryKey(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        
        BillingGroup billingGroup = billingGroupMapper.selectByPrimaryKey(billingGroupId);
        if (billingGroup == null) {
            throw new RuntimeException("Billing group not found");
        }
        
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setCustomerId(customerId);
        invoice.setBillingGroupId(billingGroupId);
        invoice.setInvoiceDate(LocalDate.now());
        
        // Calculate due date based on billing group payment terms
        int paymentTermDays = billingGroup.getDueDays() != null ? billingGroup.getDueDays() : 30;
        invoice.setDueDate(LocalDate.now().plusDays(paymentTermDays));
        
        invoice.setPeriodStart(periodStart);
        invoice.setPeriodEnd(periodEnd);
        
        // Create invoice item from billing group
        InvoiceItem item = new InvoiceItem();
        item.setDescription(billingGroup.getGroupName() + " - " + periodStart + " to " + periodEnd);
        item.setQuantity(BigDecimal.ONE);
        
        // Use custom price if available, otherwise use billing group price
        BigDecimal unitPrice = billingGroup.getBasePrice();
        if (customer.getCreditLimit() != null && customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0) {
            // For now, use base price - we'll implement custom pricing later
            unitPrice = billingGroup.getBasePrice();
        }
        
        item.setUnitPrice(unitPrice);
        item.calculateLineTotal();
        
        invoice.getInvoiceItems().add(item);
        
        // Calculate totals
        invoice.setSubtotal(item.getLineTotal());
        
        // Apply tax based on billing group tax rate
        if (billingGroup.getTaxRate() != null && billingGroup.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxRate = billingGroup.getTaxRate().divide(BigDecimal.valueOf(100));
            invoice.setTaxAmount(invoice.getSubtotal().multiply(taxRate));
        } else {
            invoice.setTaxAmount(BigDecimal.ZERO);
        }
        
        // For now, no discount - we'll implement custom pricing later
        invoice.setDiscountAmount(BigDecimal.ZERO);
        
        invoice.recalculateAmounts();
        
        // Set initial status
        invoice.setStatus("PENDING");
        invoice.setPaymentStatus("UNPAID");
        
        createInvoice(invoice);
        
        return getInvoiceById(invoice.getId());
    }
    
    // Status Management
    public void updateInvoiceStatus(Long id, String status, Long updatedBy) {
        invoiceMapper.updateStatus(id, status, updatedBy);
    }
    
    public void updatePaymentStatus(Long id, String paymentStatus, BigDecimal paidAmount, Long updatedBy) {
        invoiceMapper.updatePaymentStatus(id, paymentStatus, paidAmount, updatedBy);
    }
    
    public void markInvoiceAsSent(Long id, Long updatedBy) {
        updateInvoiceStatus(id, "SENT", updatedBy);
    }
    
    public void markInvoiceAsPaid(Long id, BigDecimal paidAmount, Long updatedBy) {
        Invoice invoice = getInvoiceById(id);
        if (invoice != null) {
            BigDecimal totalPaid = invoice.getPaidAmount().add(paidAmount);
            String paymentStatus;
            
            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                paymentStatus = "PAID";
                updateInvoiceStatus(id, "PAID", updatedBy);
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                paymentStatus = "PARTIAL";
            } else {
                paymentStatus = "UNPAID";
            }
            
            updatePaymentStatus(id, paymentStatus, totalPaid, updatedBy);
        }
    }
    
    // Statistics and Reports
    @Transactional(readOnly = true)
    public Map<String, Object> getInvoiceStatistics(Long customerId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        return invoiceMapper.getStatistics(params);
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyStatistics(Long customerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        
        return invoiceMapper.getMonthlyStatistics(params);
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopOutstandingCustomers(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        return invoiceMapper.getTopOutstandingCustomers(params);
    }
    
    @Transactional(readOnly = true)
    public List<Invoice> getOverdueInvoices() {
        return invoiceMapper.findOverdueInvoices();
    }
    
    @Transactional(readOnly = true)
    public List<Invoice> getCustomerInvoices(Long customerId, int limit) {
        return invoiceMapper.findByCustomerId(customerId, limit);
    }
    
    // Utility Methods
    private String generateInvoiceNumber() {
        String prefix = "INV";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        // Find the next sequence number for this month
        // This would ideally use a sequence or counter table
        // For now, use timestamp-based approach
        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        
        return prefix + datePart + String.format("%04d", Integer.parseInt(timestamp));
    }
    
    public boolean isInvoiceNumberUnique(String invoiceNumber) {
        return invoiceMapper.findByInvoiceNumber(invoiceNumber) == null;
    }
    
    // Invoice Items Management
    public void addInvoiceItem(Long invoiceId, InvoiceItem item) {
        item.setInvoiceId(invoiceId);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        invoiceMapper.insertItem(item);
        
        // Recalculate invoice totals
        recalculateInvoiceTotals(invoiceId);
    }
    
    public void updateInvoiceItem(InvoiceItem item) {
        item.setUpdatedAt(LocalDateTime.now());
        invoiceMapper.updateItem(item);
        
        // Recalculate invoice totals
        recalculateInvoiceTotals(item.getInvoiceId());
    }
    
    public void deleteInvoiceItem(Long itemId) {
        InvoiceItem item = invoiceMapper.findItemsByInvoiceId(null).stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
        
        if (item != null) {
            invoiceMapper.deleteItemById(itemId);
            recalculateInvoiceTotals(item.getInvoiceId());
        }
    }
    
    private void recalculateInvoiceTotals(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        if (invoice != null) {
            invoice.recalculateAmounts();
            invoiceMapper.update(invoice);
        }
    }
    
    // Batch Operations
    @Transactional
    public void generateMonthlyInvoicesForAllCustomers(LocalDate periodStart, LocalDate periodEnd) {
        // Get all active customers
        List<Customer> customers = customerMapper.findActiveCustomers();
        
        for (Customer customer : customers) {
            // Get customer's billing groups
            List<Map<String, Object>> billingGroupMaps = billingGroupMapper.findByCustomerId(customer.getId());
            
            for (Map<String, Object> billingGroupMap : billingGroupMaps) {
                try {
                    Long billingGroupId = (Long) billingGroupMap.get("id");
                    generateInvoiceForCustomer(customer.getId(), billingGroupId, periodStart, periodEnd);
                } catch (Exception e) {
                    // Log error and continue with next customer/billing group
                    System.err.println("Error generating invoice for customer " + customer.getId() + 
                                     ", billing group " + billingGroupMap.get("id") + ": " + e.getMessage());
                }
            }
        }
    }
    
    @Transactional
    public int markOverdueInvoices() {
        List<Invoice> overdueInvoices = getOverdueInvoices();
        int count = 0;
        
        for (Invoice invoice : overdueInvoices) {
            if (!"OVERDUE".equals(invoice.getStatus())) {
                updateInvoiceStatus(invoice.getId(), "OVERDUE", null);
                count++;
            }
        }
        
        return count;
    }
}
