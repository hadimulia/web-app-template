package app.spring.web.service;

import app.spring.web.mapper.CustomerMapper;
import app.spring.web.model.Customer;
import app.spring.web.model.PageRequest;
import app.spring.web.model.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> findAll() {
        return customerMapper.selectAll();
    }

    public List<Customer> findActiveCustomers() {
        return customerMapper.findActiveCustomers();
    }

    public Customer findById(Long id) {
        return customerMapper.selectByPrimaryKey(id);
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
            int offset = (pageRequest.getPage() - 1) * pageRequest.getSize();
            
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
            int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
            
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
                customer.setCreatedAt(LocalDateTime.now());
                customer.setUpdatedAt(LocalDateTime.now());
                
                // Generate customer code if not provided
                if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
                    customer.setCustomerCode(generateCustomerCode());
                }
                
                customerMapper.insertSelective(customer);
            } else {
                // Update existing customer
                customer.setUpdatedAt(LocalDateTime.now());
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
            Customer customer = findById(id);
            if (customer != null) {
                // Soft delete - set status to 0 (inactive)
                customer.setStatus(0);
                customer.setUpdatedAt(LocalDateTime.now());
                customerMapper.updateByPrimaryKeySelective(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    public void hardDelete(Long id) {
        try {
            customerMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error permanently deleting customer", e);
        }
    }

    // Validation methods
    public boolean existsByCustomerCode(String customerCode, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L; // Use -1 for new records
        }
        return customerMapper.countByCustomerCodeExcludeId(customerCode, excludeId) > 0;
    }

    public boolean existsByEmail(String email, Long excludeId) {
        if (excludeId == null) {
            excludeId = -1L; // Use -1 for new records
        }
        return customerMapper.countByEmailExcludeId(email, excludeId) > 0;
    }

    // Statistics methods
    public int countActiveCustomers() {
        return customerMapper.countActiveCustomers();
    }

    public int countSuspendedCustomers() {
        return customerMapper.countSuspendedCustomers();
    }

    public int countNewCustomersToday() {
        return customerMapper.countNewCustomersToday();
    }

    public int countNewCustomersThisMonth() {
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
            int maxNumber = 0;
            
            for (Customer customer : allCustomers) {
                if (customer.getCustomerCode() != null && customer.getCustomerCode().startsWith("CUST")) {
                    try {
                        String numberPart = customer.getCustomerCode().substring(4);
                        int number = Integer.parseInt(numberPart);
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
        Customer customer = findById(customerId);
        if (customer != null) {
            customer.setStatus(1);
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.updateByPrimaryKeySelective(customer);
        }
    }

    public void suspendCustomer(Long customerId) {
        Customer customer = findById(customerId);
        if (customer != null) {
            customer.setStatus(2);
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.updateByPrimaryKeySelective(customer);
        }
    }

    public void deactivateCustomer(Long customerId) {
        Customer customer = findById(customerId);
        if (customer != null) {
            customer.setStatus(0);
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.updateByPrimaryKeySelective(customer);
        }
    }
}
