package app.spring.web.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
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

import app.spring.web.model.BillingGroup;
import app.spring.web.model.CustomerBillingGroup;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.billinggroup.BillingGroupService;
import app.spring.web.service.customer.CustomerServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/billing-groups")
@Slf4j
public class BillingGroupController {

	private BillingGroupService billingGroupService;

	private CustomerServiceImpl customerService;

	public BillingGroupController(BillingGroupService billingGroupService, CustomerServiceImpl customerService) {
		this.billingGroupService = billingGroupService;
		this.customerService = customerService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("pageTitle", "Billing Group Management");
		model.addAttribute("currentSection", "billing-groups");
		return "billing-groups/list";
	}

	// Async API endpoint for pagination and search
	@GetMapping("/api/list")
	@ResponseBody
	public CompletableFuture<ResponseEntity<PageResponse<BillingGroup>>> listAsync(
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir,
			@RequestParam(defaultValue = "") String search) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
				pageRequest.setSearch(search);

				PageResponse<BillingGroup> result = billingGroupService.findWithPagination(pageRequest);
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
	public CompletableFuture<ResponseEntity<Map<String, Object>>> searchAsync(@RequestParam String query,
			@RequestParam(defaultValue = "5") int limit) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				PageRequest pageRequest = new PageRequest(1, limit, "group_name", "asc");
				pageRequest.setSearch(query);

				PageResponse<BillingGroup> result = billingGroupService.findWithPagination(pageRequest);

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
		model.addAttribute("billingGroup", new BillingGroup());
		model.addAttribute("pageTitle", "Create Billing Group");
		return "billing-groups/form";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		BillingGroup billingGroup = billingGroupService.get(id);
		if (billingGroup == null) {
			return "redirect:/billing-groups?error=Billing group not found";
		}

		model.addAttribute("billingGroup", billingGroup);
		model.addAttribute("pageTitle", "Edit Billing Group");
		return "billing-groups/form";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute BillingGroup billingGroup, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {

		// Validation
		if (billingGroupService.existsByGroupCode(billingGroup.getGroupCode(), billingGroup.getId())) {
			result.rejectValue("groupCode", "error.billingGroup", "Group code already exists");
		}

		if (billingGroupService.existsByGroupName(billingGroup.getGroupName(), billingGroup.getId())) {
			result.rejectValue("groupName", "error.billingGroup", "Group name already exists");
		}

		if (result.hasErrors()) {
			model.addAttribute("pageTitle",
					billingGroup.getId() == null ? "Create Billing Group" : "Edit Billing Group");
			return "billing-groups/form";
		}

		try {
			billingGroupService.save(billingGroup);

			String message = billingGroup.getId() == null ? "Billing group created successfully!"
					: "Billing group updated successfully!";
			redirectAttributes.addFlashAttribute("success", message);
			return "redirect:/billing-groups";
		} catch (Exception e) {
			model.addAttribute("error", "Error saving billing group: " + e.getMessage());
			model.addAttribute("pageTitle",
					billingGroup.getId() == null ? "Create Billing Group" : "Edit Billing Group");
			return "billing-groups/form";
		}
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			BillingGroup billingGroup = billingGroupService.get(id);
			if (billingGroup == null) {
				redirectAttributes.addFlashAttribute("error", "Billing group not found");
				return "redirect:/billing-groups";
			}

			billingGroupService.delete(id);
			redirectAttributes.addFlashAttribute("success", "Billing group deactivated successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error deactivating billing group: " + e.getMessage());
		}
		return "redirect:/billing-groups";
	}

		@GetMapping("/view/{id}")
	public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		try {
			BillingGroup billingGroup = billingGroupService.get(id);
			List<Map<String, Object>> customerAssignments = billingGroupService.findCustomerAssignments(id);
			
			model.addAttribute("billingGroup", billingGroup);
			model.addAttribute("customerAssignments", customerAssignments);
			model.addAttribute("pageTitle", "Billing Group Details");
			model.addAttribute("currentSection", "billing-groups");
			return "billing-groups/view";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Billing group not found or error occurred: " + e.getMessage());
			return "redirect:/billing-groups";
		}
	}

	// Customer assignment management
	@GetMapping("/customers/{billingGroupId}")
	public String manageCustomers(@PathVariable Long billingGroupId, Model model) {
		BillingGroup billingGroup = billingGroupService.get(billingGroupId);
		if (billingGroup == null) {
			return "redirect:/billing-groups?error=Billing group not found";
		}

		model.addAttribute("billingGroup", billingGroup);
		model.addAttribute("customers", customerService.findActiveCustomers());
		model.addAttribute("customerAssignments", billingGroupService.findCustomerAssignments(billingGroupId));
		model.addAttribute("pageTitle", "Manage Customers - " + billingGroup.getGroupName());
		return "billing-groups/manage-customers";
	}

	@GetMapping("/manage-customers")
	public String manageCustomersGeneral(Model model) {
		// Get all active billing groups for selection
		List<BillingGroup> billingGroups = billingGroupService.findActiveBillingGroups();
		model.addAttribute("billingGroups", billingGroups);
		model.addAttribute("customers", customerService.findActiveCustomers());
		model.addAttribute("pageTitle", "Manage Billing Group Customers");
		return "billing-groups/manage-customers-general";
	}

	@PostMapping("/assign-customer")
	public String assignCustomer(@RequestParam Long billingGroupId, @RequestParam Long customerId,
			@RequestParam(required = false) BigDecimal customPrice,
			@RequestParam(defaultValue = "0") BigDecimal discountPercent,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(required = false) String notes, RedirectAttributes redirectAttributes) {
		try {
			CustomerBillingGroup assignment = new CustomerBillingGroup();
			assignment.setBillingGroupId(billingGroupId);
			assignment.setCustomerId(customerId);
			assignment.setCustomPrice(customPrice);
			assignment.setDiscountPercent(discountPercent);
			assignment.setStartDate(startDate);
			assignment.setEndDate(endDate);
			assignment.setNotes(notes);
			assignment.setStatus(1);

			billingGroupService.assignCustomerToBillingGroup(assignment);
			redirectAttributes.addFlashAttribute("success", "Customer assigned to billing group successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error assigning customer: " + e.getMessage());
		}
		return "redirect:/billing-groups/customers/" + billingGroupId;
	}

	@PostMapping("/remove-customer/{assignmentId}")
	public String removeCustomer(@PathVariable Long assignmentId, @RequestParam Long billingGroupId,
			RedirectAttributes redirectAttributes) {
		try {
			billingGroupService.removeCustomerFromBillingGroup(assignmentId);
			redirectAttributes.addFlashAttribute("success", "Customer removed from billing group successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error removing customer: " + e.getMessage());
		}
		return "redirect:/billing-groups/customers/" + billingGroupId;
	}

	// Status management endpoints
	@PostMapping("/activate/{id}")
	public String activate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			BillingGroup billingGroup = billingGroupService.get(id);
			if (billingGroup == null) {
				redirectAttributes.addFlashAttribute("error", "Billing group not found");
				return "redirect:/billing-groups";
			}

			billingGroupService.activateBillingGroup(id);
			redirectAttributes.addFlashAttribute("success", "Billing group activated successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error activating billing group: " + e.getMessage());
		}
		return "redirect:/billing-groups/view/" + id;
	}

	@PostMapping("/deactivate/{id}")
	public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			BillingGroup billingGroup = billingGroupService.get(id);
			if (billingGroup == null) {
				redirectAttributes.addFlashAttribute("error", "Billing group not found");
				return "redirect:/billing-groups";
			}

			billingGroupService.deactivateBillingGroup(id);
			redirectAttributes.addFlashAttribute("success", "Billing group deactivated successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error deactivating billing group: " + e.getMessage());
		}
		return "redirect:/billing-groups/view/" + id;
	}

	// API endpoints for statistics and data
	@GetMapping("/api/stats")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getBillingGroupStats() {
		try {
			Map<String, Object> stats = new HashMap<>();
			stats.put("activeBillingGroups", billingGroupService.countActiveBillingGroups());
			stats.put("autoGenerateGroups", billingGroupService.countAutoGenerateBillingGroups());
			stats.put("activeAssignments", billingGroupService.countActiveAssignments());
			stats.put("customersWithAssignments", billingGroupService.countCustomersWithAssignments());
			stats.put("billingCycleStats", billingGroupService.getBillingCycleStats());

			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/api/top-revenue")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getTopRevenueGroups(@RequestParam(defaultValue = "10") int limit) {
		try {
			Map<String, Object> response = new HashMap<>();
			response.put("topRevenueGroups", billingGroupService.getTopRevenueGroups(limit));

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/api/expiring-assignments")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getExpiringAssignments(@RequestParam(defaultValue = "30") int days) {
		try {
			Map<String, Object> response = new HashMap<>();
			response.put("expiringAssignments", billingGroupService.findExpiringSoon(days));

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/api/highest-value")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getHighestValueAssignments(
			@RequestParam(defaultValue = "10") int limit) {
		try {
			Map<String, Object> response = new HashMap<>();
			response.put("highestValueAssignments", billingGroupService.findHighestValueAssignments(limit));

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}
}
