package app.spring.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.spring.web.model.Customer;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.customer.CustomerService;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Customer Management");
        return "customers/list";
    }

    // Async API endpoint for pagination and search
    @GetMapping("/api/list")
    @ResponseBody
    public CompletableFuture<ResponseEntity<PageResponse<Customer>>> listAsync(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String search) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
                pageRequest.setSearch(search);
                
                PageResponse<Customer> result = customerService.findWithPagination(pageRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    // API endpoint for quick search suggestions
    @GetMapping("/api/search")
    @ResponseBody
    public CompletableFuture<ResponseEntity<Map<String, Object>>> searchAsync(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageRequest pageRequest = new PageRequest(1, limit, "company_name", "asc");
                pageRequest.setSearch(query);
                
                PageResponse<Customer> result = customerService.findWithPagination(pageRequest);
                
                Map<String, Object> response = new HashMap<>();
                response.put("suggestions", result.getContent());
                response.put("totalCount", result.getTotalElements());
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "Create Customer");
        return "customers/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Customer customer = customerService.get(id);
        if (customer == null) {
            return "redirect:/customers?error=Customer not found";
        }

        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Edit Customer");
        return "customers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Customer customer, BindingResult result,
                      Model model, RedirectAttributes redirectAttributes) {

        // Validation
        if (customerService.existsByCustomerCode(customer.getCustomerCode(), customer.getId())) {
            result.rejectValue("customerCode", "error.customer", "Customer code already exists");
        }

        if (customerService.existsByEmail(customer.getEmail(), customer.getId())) {
            result.rejectValue("email", "error.customer", "Email address already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", customer.getId() == null ? "Create Customer" : "Edit Customer");
            return "customers/form";
        }

        try {
            customerService.save(customer);

            String message = customer.getId() == null ? "Customer created successfully!" : "Customer updated successfully!";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/customers";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving customer: " + e.getMessage());
            model.addAttribute("pageTitle", customer.getId() == null ? "Create Customer" : "Edit Customer");
            return "customers/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.get(id);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Customer not found");
                return "redirect:/customers";
            }

            customerService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Customer deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Customer customer = customerService.get(id);
        if (customer == null) {
            return "redirect:/customers?error=Customer not found";
        }

        // Get customer with payment summary
        Customer customerWithSummary = customerService.findWithPaymentSummary(id);
        if (customerWithSummary != null) {
            customer = customerWithSummary;
        }

        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Customer Details");
        return "customers/view";
    }

    // Status management endpoints
    @PostMapping("/activate/{id}")
    public String activate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.get(id);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Customer not found");
                return "redirect:/customers";
            }

            customerService.activateCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error activating customer: " + e.getMessage());
        }
        return "redirect:/customers/view/" + id;
    }

    @PostMapping("/suspend/{id}")
    public String suspend(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.get(id);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Customer not found");
                return "redirect:/customers";
            }

            customerService.suspendCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer suspended successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error suspending customer: " + e.getMessage());
        }
        return "redirect:/customers/view/" + id;
    }

    // API endpoint for customer statistics
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCustomerStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeCustomers", customerService.countActiveCustomers());
            stats.put("suspendedCustomers", customerService.countSuspendedCustomers());
            stats.put("newCustomersToday", customerService.countNewCustomersToday());
            stats.put("newCustomersThisMonth", customerService.countNewCustomersThisMonth());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // API endpoint for customers with overdue invoices
    @GetMapping("/api/overdue")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCustomersWithOverdueInvoices(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("customers", customerService.findCustomersWithOverdueInvoices(limit));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
