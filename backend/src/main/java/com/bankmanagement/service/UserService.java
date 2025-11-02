package com.bankmanagement.service;

import com.bankmanagement.model.User;
import com.bankmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user with email and password
     */
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken: " + username);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        
        // Encode password and log for debugging
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("=== REGISTRATION DEBUG ===");
        System.out.println("Email: " + email);
        System.out.println("Password length: " + (password != null ? password.length() : "NULL"));
        System.out.println("Encoded password hash: " + (encodedPassword != null ? encodedPassword.substring(0, Math.min(20, encodedPassword.length())) + "..." : "NULL"));
        System.out.println("==========================");
        
        user.setPassword(encodedPassword);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsEnabled(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user by email and password
     */
    public Optional<User> authenticateByEmail(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User foundUser = user.get();
            String storedPassword = foundUser.getPassword();
            boolean matches = passwordEncoder.matches(password, storedPassword);
            
            // Debug logging
            System.out.println("=== AUTHENTICATION DEBUG ===");
            System.out.println("Email: " + email);
            System.out.println("User found: " + (user.isPresent() ? "YES" : "NO"));
            System.out.println("Stored password hash: " + (storedPassword != null ? storedPassword.substring(0, Math.min(20, storedPassword.length())) + "..." : "NULL"));
            System.out.println("Password matches: " + matches);
            System.out.println("Password length provided: " + (password != null ? password.length() : "NULL"));
            System.out.println("==========================");
            
            if (matches) {
                // Update last login timestamp
                foundUser.setLastLogin(LocalDateTime.now());
                userRepository.save(foundUser);
                return user;
            }
        } else {
            System.out.println("=== AUTHENTICATION DEBUG ===");
            System.out.println("Email: " + email);
            System.out.println("User found: NO");
            System.out.println("==========================");
        }
        return Optional.empty();
    }
    
    /**
     * Authenticate user by username and password
     */
    public Optional<User> authenticateByUsername(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            // Update last login timestamp
            user.get().setLastLogin(LocalDateTime.now());
            userRepository.save(user.get());
            return user;
        }
        return Optional.empty();
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}