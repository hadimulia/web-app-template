package app.spring.web.service.billinggroup;

import java.util.List;
import java.util.Map;

import app.spring.web.model.BillingGroup;
import app.spring.web.model.CustomerBillingGroup;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericService;

public interface BillingGroupService extends GenericService<BillingGroup,Long >{

	List<BillingGroup> findActiveBillingGroups();
	
	BillingGroup findByGroupCode(String groupCode);
	
	PageResponse<BillingGroup> findWithPagination(PageRequest pageRequest);
	
	Boolean existsByGroupCode(String groupCode, Long excludeId);
	Boolean existsByGroupName(String groupName, Long excludeId);
	
	Integer countActiveBillingGroups();
	Integer countAutoGenerateBillingGroups();
	Integer countActiveAssignments();
	Integer countCustomersWithAssignments();
	Integer countBillingGroupsWithAssignments();
	
	List<Map<String, Object>> getBillingCycleStats();
	List<Map<String, Object>> getTopRevenueGroups(Integer limit);
	
	List<Map<String, Object>> findCustomerAssignments(Long billingGroupId);
	List<Map<String, Object>> findByCustomerId(Long customerId);
	List<Map<String, Object>> findCustomerBillingGroups(Long customerId);
	List<Map<String, Object>> findBillingGroupCustomers(Long billingGroupId);
	List<Map<String, Object>> findExpiringSoon(int days);
	List<Map<String, Object>> findHighestValueAssignments(Integer limit);
	
	Map<String, Object> findCustomerBillingGroupById(Long assignmentId);
	
	CustomerBillingGroup assignCustomerToBillingGroup(CustomerBillingGroup assignment);
	CustomerBillingGroup updateCustomerBillingGroupAssignment(CustomerBillingGroup assignment);
	CustomerBillingGroup findCustomerBillingGroupAssignment(Long customerId, Long billingGroupId);
	
	void removeCustomerFromBillingGroup(Long assignmentId);
	void activateBillingGroup(Long billingGroupId);
	void deactivateBillingGroup(Long billingGroupId);
	void delete(Long id);
	
	
	
	
}
