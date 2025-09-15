package app.spring.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {
    
    @Test
    public void testPasswordMatching() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // The stored hash from database
        String storedHash = "$2a$10$rRjT3vL7vhvX7qQqY8oNKuE.lQ.FzKzMoF8xT9jK4ZJh7qV7Q9HzS";
        
        // The password to test
        String rawPassword = "admin123";
        
        // Test if they match
        boolean matches = passwordEncoder.matches(rawPassword, storedHash);
        
        System.out.println("Password: " + rawPassword);
        System.out.println("Stored Hash: " + storedHash);
        System.out.println("Matches: " + matches);
        
        // Also generate a new hash for comparison
        String newHash = passwordEncoder.encode(rawPassword);
        System.out.println("New Hash: " + newHash);
        System.out.println("New hash matches: " + passwordEncoder.matches(rawPassword, newHash));
    }
}
