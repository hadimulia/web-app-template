package app.spring.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.spring.web.service.InvoiceService;

import app.spring.web.model.Invoice;
import app.spring.web.model.InvoiceItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    

    
    // Get all invoices with pagination and filtering
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean overdue) {
        
        try {
            Map<String, Object> result = invoiceService.getInvoices(page, size, sortField, sortDir,
                    searchTerm, customerId, status, paymentStatus, startDate, endDate, overdue);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInvoiceById(@PathVariable Long id) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            Map<String, Object> response = new HashMap<>();
            
            if (invoice != null) {
                response.put("success", true);
                response.put("data", invoice);
            } else {
                response.put("success", false);
                response.put("message", "Invoice not found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Create new invoice
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInvoice(@RequestBody Invoice invoice) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            if (invoice.getCustomerId() == null) {
                response.put("success", false);
                response.put("message", "Customer ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (invoice.getBillingGroupId() == null) {
                response.put("success", false);
                response.put("message", "Billing Group ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            invoiceService.createInvoice(invoice);
            
            // Retrieve the created invoice with full details
            Invoice createdInvoice = invoiceService.getInvoiceById(invoice.getId());
            
            response.put("success", true);
            response.put("message", "Invoice created successfully");
            response.put("data", createdInvoice);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Update invoice
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoice.setId(id);
            invoiceService.updateInvoice(invoice);
            
            // Retrieve the updated invoice with full details
            Invoice updatedInvoice = invoiceService.getInvoiceById(id);
            
            response.put("success", true);
            response.put("message", "Invoice updated successfully");
            response.put("data", updatedInvoice);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Delete invoice
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteInvoice(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.deleteInvoice(id);
            
            response.put("success", true);
            response.put("message", "Invoice deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Generate invoice for customer
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateInvoice(
            @RequestParam Long customerId,
            @RequestParam Long billingGroupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Invoice invoice = invoiceService.generateInvoiceForCustomer(customerId, billingGroupId, periodStart, periodEnd);
            
            response.put("success", true);
            response.put("message", "Invoice generated successfully");
            response.put("data", invoice);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to generate invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Update invoice status
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.updateInvoiceStatus(id, status, null); // TODO: Add user context
            
            response.put("success", true);
            response.put("message", "Invoice status updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update invoice status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Mark invoice as sent
    @PutMapping("/{id}/send")
    public ResponseEntity<Map<String, Object>> sendInvoice(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.markInvoiceAsSent(id, null); // TODO: Add user context
            
            response.put("success", true);
            response.put("message", "Invoice marked as sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Record payment
    @PutMapping("/{id}/payment")
    public ResponseEntity<Map<String, Object>> recordPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.markInvoiceAsPaid(id, amount, null); // TODO: Add user context
            
            response.put("success", true);
            response.put("message", "Payment recorded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to record payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Add invoice item
    @PostMapping("/{id}/items")
    public ResponseEntity<Map<String, Object>> addInvoiceItem(
            @PathVariable Long id,
            @RequestBody InvoiceItem item) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.addInvoiceItem(id, item);
            
            response.put("success", true);
            response.put("message", "Invoice item added successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add invoice item: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Update invoice item
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> updateInvoiceItem(
            @PathVariable Long itemId,
            @RequestBody InvoiceItem item) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            item.setId(itemId);
            invoiceService.updateInvoiceItem(item);
            
            response.put("success", true);
            response.put("message", "Invoice item updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update invoice item: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Delete invoice item
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteInvoiceItem(@PathVariable Long itemId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.deleteInvoiceItem(itemId);
            
            response.put("success", true);
            response.put("message", "Invoice item deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete invoice item: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get invoice statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getInvoiceStatistics(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> statistics = invoiceService.getInvoiceStatistics(customerId, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get monthly statistics
    @GetMapping("/statistics/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(
            @RequestParam(required = false) Long customerId) {
        
        try {
            List<Map<String, Object>> monthlyStats = invoiceService.getMonthlyStatistics(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", monthlyStats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve monthly statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get overdue invoices
    @GetMapping("/overdue")
    public ResponseEntity<Map<String, Object>> getOverdueInvoices() {
        try {
            List<Invoice> overdueInvoices = invoiceService.getOverdueInvoices();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", overdueInvoices);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve overdue invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get customer invoices
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerInvoices(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Invoice> invoices = invoiceService.getCustomerInvoices(customerId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", invoices);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve customer invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Batch generate invoices
    @PostMapping("/generate-batch")
    public ResponseEntity<Map<String, Object>> generateBatchInvoices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            invoiceService.generateMonthlyInvoicesForAllCustomers(periodStart, periodEnd);
            
            response.put("success", true);
            response.put("message", "Batch invoice generation completed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to generate batch invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Mark overdue invoices
    @PutMapping("/mark-overdue")
    public ResponseEntity<Map<String, Object>> markOverdueInvoices() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int count = invoiceService.markOverdueInvoices();
            
            response.put("success", true);
            response.put("message", count + " invoices marked as overdue");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to mark overdue invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
