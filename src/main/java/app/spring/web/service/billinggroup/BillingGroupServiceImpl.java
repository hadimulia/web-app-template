package app.spring.web.service.billinggroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.spring.web.mapper.BillingGroupMapper;
import app.spring.web.mapper.CustomerBillingGroupMapper;
import app.spring.web.model.BillingGroup;
import app.spring.web.model.CustomerBillingGroup;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericServiceImpl;

@Service
@Transactional
public class BillingGroupServiceImpl extends GenericServiceImpl<BillingGroup, Long> implements BillingGroupService {

	
    private BillingGroupMapper billingGroupMapper;
    
    private CustomerBillingGroupMapper customerBillingGroupMapper;

    public BillingGroupServiceImpl(BillingGroupMapper mapper,CustomerBillingGroupMapper customerBillingGroupMapper) {
		super(mapper);
		this.billingGroupMapper = mapper;
		this.customerBillingGroupMapper = customerBillingGroupMapper;
	}
    
    
    public List<BillingGroup> findAll() {
        return billingGroupMapper.selectAll();
    }

    public List<BillingGroup> findActiveBillingGroups() {
        return billingGroupMapper.findActiveBillingGroups();
    }

    public BillingGroup findById(Long id) {
        return billingGroupMapper.selectByPrimaryKey(id);
    }

    public BillingGroup findByGroupCode(String groupCode) {
        return billingGroupMapper.findByGroupCode(groupCode);
    }

    // Add pagination support
    public PageResponse<BillingGroup> findWithPagination(PageRequest pageRequest) {
        try {
            // Calculate offset
            int offset = (pageRequest.getPage() - 1) * pageRequest.getSize();
            
            // Get search parameters
            String search = pageRequest.getSearch();
            Integer status = null; // Can be extended for filtering
            String billingCycle = null; // Can be extended for filtering
            
            // Get data with pagination
            List<BillingGroup> billingGroups = billingGroupMapper.findWithPagination(
                search, status, billingCycle,
                pageRequest.getSortBy(), 
                pageRequest.getSortDir(), 
                offset, 
                pageRequest.getSize()
            );
            
            // Get total count
            long totalElements = billingGroupMapper.countWithFilter(search, status, billingCycle);
            
            // Calculate total pages
            int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
            
            // Create response
            PageResponse<BillingGroup> response = new PageResponse<>();
            response.setContent(billingGroups);
            response.setPage(pageRequest.getPage());
            response.setSize(pageRequest.getSize());
            response.setTotalElements(totalElements);
            response.setTotalPages(totalPages);
            response.setFirst(pageRequest.getPage() == 1);
            response.setLast(pageRequest.getPage() >= totalPages);
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching billing groups with pagination", e);
        }
    }

    public BillingGroup save(BillingGroup billingGroup) {
        try {
            if (billingGroup.getId() == null) {
                // Create new billing group
                billingGroup.setCreatedAt(LocalDateTime.now());
                billingGroup.setUpdatedAt(LocalDateTime.now());
                
                // Generate group code if not provided
                if (billingGroup.getGroupCode() == null || billingGroup.getGroupCode().trim().isEmpty()) {
                    billingGroup.setGroupCode(generateGroupCode(billingGroup.getGroupName()));
                }
                
            } else {
                // Update existing billing group
                billingGroup.setUpdatedAt(LocalDateTime.now());
            }
            billingGroup = super.save(billingGroup);
            
            return billingGroup;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving billing group", e);
        }
    }

    public void delete(Long id) {
        try {
            BillingGroup billingGroup = findById(id);
            if (billingGroup != null) {
                // Soft delete - set status to 0 (inactive)
                billingGroup.setStatus(0);
                billingGroup.setUpdatedAt(LocalDateTime.now());
                super.save(billingGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting billing group", e);
        }
    }

    // Validation methods
    public Boolean existsByGroupCode(String groupCode, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L; // Use -1 for new records
        }
        return billingGroupMapper.countByGroupCodeExcludeId(groupCode, excludeId) > 0;
    }

    public Boolean existsByGroupName(String groupName, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L; // Use -1 for new records
        }
        return billingGroupMapper.countByGroupNameExcludeId(groupName, excludeId) > 0;
    }

    // Statistics methods
    public Integer countActiveBillingGroups() {
        return billingGroupMapper.countActiveBillingGroups();
    }

    public Integer countAutoGenerateBillingGroups() {
        return billingGroupMapper.countAutoGenerateBillingGroups();
    }

    public List<Map<String, Object>> getBillingCycleStats() {
        return billingGroupMapper.getBillingCycleStats();
    }

    public List<Map<String, Object>> getTopRevenueGroups(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return billingGroupMapper.getTopRevenueGroups(limit);
    }

    // Customer assignment methods
    public List<Map<String, Object>> findCustomerAssignments(Long billingGroupId) {
        return billingGroupMapper.findCustomerAssignments(billingGroupId);
    }

    public List<Map<String, Object>> findByCustomerId(Long customerId) {
        return billingGroupMapper.findByCustomerId(customerId);
    }

    // Customer-Billing Group management
    public CustomerBillingGroup assignCustomerToBillingGroup(CustomerBillingGroup assignment) {
        try {
            // Check for existing active assignment
            CustomerBillingGroup existing = customerBillingGroupMapper.findByCustomerAndBillingGroup(
                assignment.getCustomerId(), assignment.getBillingGroupId());
            
            if (existing != null && existing.getStatus() == 1) {
                throw new RuntimeException("Customer is already assigned to this billing group");
            }

            assignment.setCreatedAt(LocalDateTime.now());
            assignment.setUpdatedAt(LocalDateTime.now());
            customerBillingGroupMapper.insertSelective(assignment);
            
            return assignment;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error assigning customer to billing group", e);
        }
    }

    public CustomerBillingGroup updateCustomerBillingGroupAssignment(CustomerBillingGroup assignment) {
        try {
            assignment.setUpdatedAt(LocalDateTime.now());
            customerBillingGroupMapper.updateByPrimaryKeySelective(assignment);
            return assignment;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating customer billing group assignment", e);
        }
    }

    public void removeCustomerFromBillingGroup(Long assignmentId) {
        try {
            CustomerBillingGroup assignment = customerBillingGroupMapper.selectByPrimaryKey(assignmentId);
            if (assignment != null) {
                // Soft delete - set status to 0
                assignment.setStatus(0);
                assignment.setUpdatedAt(LocalDateTime.now());
                customerBillingGroupMapper.updateByPrimaryKeySelective(assignment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error removing customer from billing group", e);
        }
    }

    public List<Map<String, Object>> findCustomerBillingGroups(Long customerId) {
        return customerBillingGroupMapper.findByCustomerId(customerId);
    }

    public List<Map<String, Object>> findBillingGroupCustomers(Long billingGroupId) {
        return customerBillingGroupMapper.findByBillingGroupId(billingGroupId);
    }

    public Map<String, Object> findCustomerBillingGroupById(Long assignmentId) {
        return customerBillingGroupMapper.findByIdWithDetails(assignmentId);
    }

    public CustomerBillingGroup findCustomerBillingGroupAssignment(Long customerId, Long billingGroupId) {
        return customerBillingGroupMapper.findByCustomerAndBillingGroup(customerId, billingGroupId);
    }

    // Expiring assignments
    public List<Map<String, Object>> findExpiringSoon(int days) {
        return customerBillingGroupMapper.findExpiringSoon(days);
    }

    // High value assignments
    public List<Map<String, Object>> findHighestValueAssignments(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return customerBillingGroupMapper.findHighestValueAssignments(limit);
    }

    // Assignment statistics
    public Integer countActiveAssignments() {
        return customerBillingGroupMapper.countActiveAssignments();
    }

    public Integer countCustomersWithAssignments() {
        return customerBillingGroupMapper.countCustomersWithAssignments();
    }

    public Integer countBillingGroupsWithAssignments() {
        return customerBillingGroupMapper.countBillingGroupsWithAssignments();
    }

    // Utility methods
    private String generateGroupCode(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return "GROUP" + System.currentTimeMillis() % 10000;
        }
        
        // Generate code from group name
        String code = groupName.toUpperCase()
                              .replaceAll("[^A-Z0-9\\s]", "")
                              .replaceAll("\\s+", "_");
        
        // Limit to 20 characters
        if (code.length() > 20) {
            code = code.substring(0, 20);
        }
        
        // Check if code already exists
        int counter = 1;
        String originalCode = code;
        while (billingGroupMapper.findByGroupCode(code) != null) {
            code = originalCode + "_" + counter;
            counter++;
        }
        
        return code;
    }

    // Billing group status management
    public void activateBillingGroup(Long billingGroupId) {
        BillingGroup billingGroup = findById(billingGroupId);
        if (billingGroup != null) {
            billingGroup.setStatus(1);
            billingGroup.setUpdatedAt(LocalDateTime.now());
            billingGroupMapper.updateByPrimaryKeySelective(billingGroup);
        }
    }

    public void deactivateBillingGroup(Long billingGroupId) {
        BillingGroup billingGroup = findById(billingGroupId);
        if (billingGroup != null) {
            billingGroup.setStatus(0);
            billingGroup.setUpdatedAt(LocalDateTime.now());
            billingGroupMapper.updateByPrimaryKeySelective(billingGroup);
        }
    }

	
}
