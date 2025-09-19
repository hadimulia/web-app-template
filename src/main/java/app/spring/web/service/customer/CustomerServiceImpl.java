package app.spring.web.service.customer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.spring.web.mapper.CustomerMapper;
import app.spring.web.model.Customer;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import app.spring.web.service.generic.GenericServiceImpl;

@Service
@Transactional
public class CustomerServiceImpl extends GenericServiceImpl<Customer, Long> implements CustomerService{

    private CustomerMapper customerMapper;
    
    
    public CustomerServiceImpl(CustomerMapper mapper) {
		super(mapper);
		this.customerMapper=mapper;
	}


    public List<Customer> findActiveCustomers() {
        return customerMapper.findActiveCustomers();
    }

    public Customer findByCustomerCode(String customerCode) {
        return customerMapper.findByCustomerCode(customerCode);
    }

    public Customer findByEmail(String email) {
        return customerMapper.findByEmail(email);
    }

    // Add pagination support
    public PageResponse<Customer> findWithPagination(PageRequest pageRequest) {
        try {
            // Calculate offset
            Integer offset = (pageRequest.getPage() - 1) * pageRequest.getSize();
            
            // Get search parameters
            String search = pageRequest.getSearch();
            Integer status = null; // Can be extended for filtering
            String customerType = null; // Can be extended for filtering
            
            // Get data with pagination
            List<Customer> customers = customerMapper.findWithPagination(
                search, status, customerType,
                pageRequest.getSortBy(), 
                pageRequest.getSortDir(), 
                offset, 
                pageRequest.getSize()
            );
            
            // Get total count
            long totalElements = customerMapper.countWithFilter(search, status, customerType);
            
            // Calculate total pages
            Integer totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
            
            // Create response
            PageResponse<Customer> response = new PageResponse<>();
            response.setContent(customers);
            response.setPage(pageRequest.getPage());
            response.setSize(pageRequest.getSize());
            response.setTotalElements(totalElements);
            response.setTotalPages(totalPages);
            response.setFirst(pageRequest.getPage() == 1);
            response.setLast(pageRequest.getPage() >= totalPages);
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching customers with pagination", e);
        }
    }

    public Customer save(Customer customer) {
        try {
            if (customer.getId() == null) {
                // Create new customer
                customer.setCreatedAt(new Date());
                customer.setUpdatedAt(new Date());
                
                // Generate customer code if not provided
                if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
                    customer.setCustomerCode(generateCustomerCode());
                }
                
                customerMapper.insertSelective(customer);
            } else {
                // Update existing customer
                customer.setUpdatedAt(new Date());
                customerMapper.updateByPrimaryKeySelective(customer);
            }
            return customer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving customer", e);
        }
    }

    public void delete(Long id) {
        try {
            Customer customer = get(id);
            if (customer != null) {
                // Soft delete - set status to 0 (inactive)
                customer.setStatus(0);
                customer.setUpdatedAt(new Date());
                customerMapper.updateByPrimaryKeySelective(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting customer", e);
        }
    }


    // Validation methods
    public Boolean existsByCustomerCode(String customerCode, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L; // Use -1 for new records
        }
        return customerMapper.countByCustomerCodeExcludeId(customerCode, excludeId) > 0;
    }

    public Boolean existsByEmail(String email, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L; // Use -1 for new records
        }
        return customerMapper.countByEmailExcludeId(email, excludeId) > 0;
    }

    // Statistics methods
    public Integer countActiveCustomers() {
        return customerMapper.countActiveCustomers();
    }

    public Integer countSuspendedCustomers() {
        return customerMapper.countSuspendedCustomers();
    }

    public Integer countNewCustomersToday() {
        return customerMapper.countNewCustomersToday();
    }

    public Integer countNewCustomersThisMonth() {
        return customerMapper.countNewCustomersThisMonth();
    }

    public List<Customer> findCustomersWithOverdueInvoices(Integer limit) {
        return customerMapper.findCustomersWithOverdueInvoices(limit);
    }

    public List<Customer> findByBillingGroupId(Long billingGroupId) {
        return customerMapper.findByBillingGroupId(billingGroupId);
    }

    public Customer findWithPaymentSummary(Long customerId) {
        return customerMapper.findWithPaymentSummary(customerId);
    }

    // Utility methods
    private String generateCustomerCode() {
        // Generate customer code like CUST001, CUST002, etc.
        try {
            // Find the highest customer code number
            List<Customer> allCustomers = customerMapper.selectAll();
            Integer maxNumber = 0;
            
            for (Customer customer : allCustomers) {
                if (customer.getCustomerCode() != null && customer.getCustomerCode().startsWith("CUST")) {
                    try {
                        String numberPart = customer.getCustomerCode().substring(4);
                        Integer number = Integer.parseInt(numberPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid codes
                    }
                }
            }
            
            return String.format("CUST%03d", maxNumber + 1);
            
        } catch (Exception e) {
            // Fallback to timestamp-based code
            return "CUST" + System.currentTimeMillis() % 10000;
        }
    }

    public void updateCustomerStatistics(Long customerId) {
        customerMapper.updateCustomerStatistics(customerId);
    }

    // Customer status management
    public void activateCustomer(Long customerId) {
        Customer customer = get(customerId);
        if (customer != null) {
            customer.setStatus(1);
            customer.setUpdatedAt(new Date());
            customerMapper.updateByPrimaryKeySelective(customer);
        }
    }

    public void suspendCustomer(Long customerId) {
        Customer customer = get(customerId);
        if (customer != null) {
            customer.setStatus(2);
            customer.setUpdatedAt(new Date());
            customerMapper.updateByPrimaryKeySelective(customer);
        }
    }

    public void deactivateCustomer(Long customerId) {
        Customer customer = get(customerId);
        if (customer != null) {
            customer.setStatus(0);
            customer.setUpdatedAt(new Date());
            customerMapper.updateByPrimaryKeySelective(customer);
        }
    }

}
