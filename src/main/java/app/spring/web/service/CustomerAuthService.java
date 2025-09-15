package app.spring.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.spring.web.mapper.CustomerAuthMapper;
import app.spring.web.mapper.CustomerMapper;
import app.spring.web.model.Customer;
import app.spring.web.model.CustomerAuth;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class CustomerAuthService {
    
    @Autowired
    private CustomerAuthMapper customerAuthMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Authentication
    @Transactional(readOnly = true)
    public CustomerAuth authenticateCustomer(String username, String password) {
        CustomerAuth auth = customerAuthMapper.findByUsername(username);
        
        if (auth == null || auth.getStatus() != 1) {
            return null; // User not found or inactive
        }
        
        if (!passwordEncoder.matches(password, auth.getPassword())) {
            return null; // Invalid password
        }
        
        // Update last login
        updateLastLogin(auth.getId());
        
        // Load customer details
        Customer customer = customerMapper.selectByPrimaryKey(auth.getCustomerId());
        auth.setCustomer(customer);
        
        return auth;
    }
    
    @Transactional(readOnly = true)
    public CustomerAuth findByUsername(String username) {
        CustomerAuth auth = customerAuthMapper.findByUsername(username);
        if (auth != null) {
            Customer customer = customerMapper.selectByPrimaryKey(auth.getCustomerId());
            auth.setCustomer(customer);
        }
        return auth;
    }
    
    @Transactional(readOnly = true)
    public CustomerAuth findByCustomerId(Long customerId) {
        CustomerAuth auth = customerAuthMapper.findByCustomerId(customerId);
        if (auth != null) {
            Customer customer = customerMapper.selectByPrimaryKey(auth.getCustomerId());
            auth.setCustomer(customer);
        }
        return auth;
    }
    
    // Registration and account management
    public CustomerAuth createCustomerAuth(Long customerId, String username, String password) {
        // Check if username already exists
        if (customerAuthMapper.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if customer already has auth
        if (customerAuthMapper.findByCustomerId(customerId) != null) {
            throw new RuntimeException("Customer already has authentication credentials");
        }
        
        CustomerAuth auth = new CustomerAuth();
        auth.setCustomerId(customerId);
        auth.setUsername(username);
        auth.setPassword(passwordEncoder.encode(password));
        auth.setStatus(1);
        auth.setEmailVerified(false);
        auth.setEmailVerificationToken(UUID.randomUUID().toString());
        auth.setCreatedAt(LocalDateTime.now());
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.insert(auth);
        
        return auth;
    }
    
    public void updatePassword(Long authId, String newPassword) {
        CustomerAuth auth = customerAuthMapper.selectByPrimaryKey(authId);
        if (auth == null) {
            throw new RuntimeException("Customer authentication not found");
        }
        
        auth.setPassword(passwordEncoder.encode(newPassword));
        auth.setPasswordResetToken(null);
        auth.setPasswordResetExpires(null);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
    }
    
    public void updateLastLogin(Long authId) {
        CustomerAuth auth = new CustomerAuth();
        auth.setId(authId);
        auth.setLastLogin(LocalDateTime.now());
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKeySelective(auth);
    }
    
    // Password reset functionality
    public String generatePasswordResetToken(String username) {
        CustomerAuth auth = customerAuthMapper.findByUsername(username);
        if (auth == null) {
            throw new RuntimeException("Customer not found");
        }
        
        String token = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(24); // 24 hours expiry
        
        auth.setPasswordResetToken(token);
        auth.setPasswordResetExpires(expires);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
        
        return token;
    }
    
    public boolean validatePasswordResetToken(String token) {
        CustomerAuth auth = customerAuthMapper.findByPasswordResetToken(token);
        
        if (auth == null) {
            return false;
        }
        
        if (auth.getPasswordResetExpires() == null || 
            auth.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }
    
    public void resetPasswordWithToken(String token, String newPassword) {
        CustomerAuth auth = customerAuthMapper.findByPasswordResetToken(token);
        
        if (auth == null || auth.getPasswordResetExpires() == null || 
            auth.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired reset token");
        }
        
        auth.setPassword(passwordEncoder.encode(newPassword));
        auth.setPasswordResetToken(null);
        auth.setPasswordResetExpires(null);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
    }
    
    // Email verification
    public void verifyEmail(String token) {
        CustomerAuth auth = customerAuthMapper.findByEmailVerificationToken(token);
        
        if (auth == null) {
            throw new RuntimeException("Invalid verification token");
        }
        
        auth.setEmailVerified(true);
        auth.setEmailVerificationToken(null);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
    }
    
    public String generateEmailVerificationToken(Long authId) {
        CustomerAuth auth = customerAuthMapper.selectByPrimaryKey(authId);
        if (auth == null) {
            throw new RuntimeException("Customer authentication not found");
        }
        
        String token = UUID.randomUUID().toString();
        auth.setEmailVerificationToken(token);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
        
        return token;
    }
    
    // Account status management
    public void activateAccount(Long authId) {
        CustomerAuth auth = customerAuthMapper.selectByPrimaryKey(authId);
        if (auth == null) {
            throw new RuntimeException("Customer authentication not found");
        }
        
        auth.setStatus(1);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
    }
    
    public void deactivateAccount(Long authId) {
        CustomerAuth auth = customerAuthMapper.selectByPrimaryKey(authId);
        if (auth == null) {
            throw new RuntimeException("Customer authentication not found");
        }
        
        auth.setStatus(0);
        auth.setUpdatedAt(LocalDateTime.now());
        
        customerAuthMapper.updateByPrimaryKey(auth);
    }
    
    // Validation methods
    public boolean isUsernameAvailable(String username) {
        return customerAuthMapper.findByUsername(username) == null;
    }
    
    public boolean isUsernameAvailable(String username, Long excludeAuthId) {
        CustomerAuth auth = customerAuthMapper.findByUsername(username);
        return auth == null || auth.getId().equals(excludeAuthId);
    }
    
    // Security helpers
    public boolean isAccountLocked(String username) {
        // TODO: Implement account locking logic based on failed login attempts
        return false;
    }
    
    public void recordFailedLogin(String username) {
        // TODO: Implement failed login tracking
    }
    
    public void clearFailedLogins(String username) {
        // TODO: Clear failed login attempts after successful login
    }
}
