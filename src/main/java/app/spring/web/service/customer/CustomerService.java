package app.spring.web.service.customer;

import java.util.List;

import app.spring.web.model.Customer;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericService;

public interface CustomerService extends GenericService<Customer, Long> {
	
	List<Customer> findActiveCustomers();
	
	Customer findByCustomerCode(String customerCode);
	
	Customer findByEmail(String email);
	
	PageResponse<Customer> findWithPagination(PageRequest pageRequest);
	
	Boolean existsByCustomerCode(String customerCode, Long excludeId);
	Boolean existsByEmail(String email, Long excludeId);
	
	Integer countActiveCustomers();
	Integer countSuspendedCustomers();
	Integer countNewCustomersToday();
	Integer countNewCustomersThisMonth();
	
	List<Customer> findCustomersWithOverdueInvoices(Integer limit);
	List<Customer> findByBillingGroupId(Long billingGroupId);
	
	Customer findWithPaymentSummary(Long customerId);
	
	void delete(Long id);
	void updateCustomerStatistics(Long customerId);
	void activateCustomer(Long customerId);
	void suspendCustomer(Long customerId);
	void deactivateCustomer(Long customerId);
}