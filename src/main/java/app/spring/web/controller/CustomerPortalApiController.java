package app.spring.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.spring.web.service.CustomerAuthService;
import app.spring.web.service.InvoiceService;
import app.spring.web.model.CustomerAuth;
import app.spring.web.model.Invoice;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/portal")

public class CustomerPortalApiController {
    
    @Autowired
    private CustomerAuthService customerAuthService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    // Authentication endpoints
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        System.out.println("Attempting login for user: " + username);
        try {
            CustomerAuth auth = customerAuthService.authenticateCustomer(username, password);
            
            if (auth == null) {
                response.put("success", false);
                response.put("message", "Invalid username or password");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Store customer info in session
            session.setAttribute("customerId", auth.getCustomerId());
            session.setAttribute("customerAuth", auth);
            session.setAttribute("customerName", auth.getCustomer().getCompanyName() != null ? 
                    auth.getCustomer().getCompanyName() : auth.getCustomer().getContactPerson());
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("customer", auth.getCustomer());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            session.invalidate();
            response.put("success", true);
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = customerAuthService.generatePasswordResetToken(username);
            
            // TODO: Send email with reset link
            response.put("success", true);
            response.put("message", "Password reset instructions have been sent to your email");
            response.put("resetToken", token); // Remove this in production
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send reset instructions: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            customerAuthService.resetPasswordWithToken(token, newPassword);
            
            response.put("success", true);
            response.put("message", "Password reset successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Password reset failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Customer dashboard data
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            // Get invoice statistics for customer
            Map<String, Object> statistics = invoiceService.getInvoiceStatistics(customerId, null, null);
            
            // Get recent invoices
            List<Invoice> recentInvoices = invoiceService.getCustomerInvoices(customerId, 5);
            
            // Get overdue invoices for this customer
            Map<String, Object> invoiceParams = new HashMap<>();
            invoiceParams.put("customerId", customerId);
            invoiceParams.put("overdue", true);
            
            Map<String, Object> overdueResult = invoiceService.getInvoices(0, 10, "dueDate", "asc", 
                    null, customerId, null, null, null, null, true);
            
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("statistics", statistics);
            dashboardData.put("recentInvoices", recentInvoices);
            dashboardData.put("overdueInvoices", overdueResult.get("content"));
            
            response.put("success", true);
            response.put("data", dashboardData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load dashboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Customer invoices
    @GetMapping("/invoices")
    public ResponseEntity<Map<String, Object>> getCustomerInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Map<String, Object> result = invoiceService.getInvoices(page, size, sortField, sortDir,
                    searchTerm, customerId, status, paymentStatus, startDate, endDate, null);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get specific invoice details
    @GetMapping("/invoices/{id}")
    public ResponseEntity<Map<String, Object>> getInvoiceDetails(
            @PathVariable Long id, HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Invoice invoice = invoiceService.getInvoiceById(id);
            
            if (invoice == null) {
                response.put("success", false);
                response.put("message", "Invoice not found");
                return ResponseEntity.notFound().build();
            }
            
            // Verify that the invoice belongs to the authenticated customer
            if (!invoice.getCustomerId().equals(customerId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }
            
            response.put("success", true);
            response.put("data", invoice);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load invoice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get customer profile
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            CustomerAuth auth = (CustomerAuth) session.getAttribute("customerAuth");
            
            if (auth != null && auth.getCustomer() != null) {
                response.put("success", true);
                response.put("data", auth.getCustomer());
            } else {
                response.put("success", false);
                response.put("message", "Profile not found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Update password
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            CustomerAuth auth = (CustomerAuth) session.getAttribute("customerAuth");
            
            // Verify current password
            CustomerAuth currentAuth = customerAuthService.authenticateCustomer(auth.getUsername(), currentPassword);
            if (currentAuth == null) {
                response.put("success", false);
                response.put("message", "Current password is incorrect");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update password
            customerAuthService.updatePassword(auth.getId(), newPassword);
            
            response.put("success", true);
            response.put("message", "Password updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update password: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Check authentication status
    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long customerId = (Long) session.getAttribute("customerId");
        String customerName = (String) session.getAttribute("customerName");
        
        if (customerId != null) {
            response.put("authenticated", true);
            response.put("customerId", customerId);
            response.put("customerName", customerName);
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
}
