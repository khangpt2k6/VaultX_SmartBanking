package com.bankmanagement.controller;

import com.bankmanagement.dto.CustomerDTO;
import com.bankmanagement.model.User;
import com.bankmanagement.service.CustomerService;
import com.bankmanagement.service.UserService;
import com.bankmanagement.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for authentication and registration operations.
 * Handles user login and registration with proper password validation and JWT token generation.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    /**
     * Constructs AuthController with required service dependencies.
     *
     * @param userService service for user authentication and management
     * @param customerService service for customer data management
     * @param jwtUtil JWT utility for token generation
     */
    public AuthController(UserService userService, CustomerService customerService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user account with customer data.
     * Creates both User (for authentication) and Customer (for business data) records.
     * 
     * @param userData map containing firstName, lastName, email, phone, address, dateOfBirth, password
     * @return ResponseEntity with success status and user token/data
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        try {
            // Validate required fields
            String firstName = userData.get("firstName");
            String lastName = userData.get("lastName");
            String email = userData.get("email");
            String password = userData.get("password");
            String phone = userData.get("phone");
            String address = userData.get("address");
            String dateOfBirth = userData.get("dateOfBirth");

            if (firstName == null || firstName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "First name is required"
                ));
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Last name is required"
                ));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email is required"
                ));
            }
            if (password == null || password.trim().isEmpty() || password.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Password must be at least 6 characters"
                ));
            }
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Phone is required"
                ));
            }

            // Register user with authentication credentials
            String username = email.split("@")[0]; // Use email prefix as username
            User savedUser = userService.registerUser(username, email, password, firstName, lastName);

            // Create customer record
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setFirstName(firstName);
            customerDTO.setLastName(lastName);
            customerDTO.setEmail(email);
            customerDTO.setPhone(phone);
            customerDTO.setAddress(address);
            
            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                customerDTO.setDateOfBirth(LocalDate.parse(dateOfBirth));
            }
            
            CustomerDTO savedCustomer = customerService.createCustomer(customerDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", savedUser.getUserId());
            response.put("email", savedUser.getEmail());
            response.put("firstName", savedUser.getFirstName());
            response.put("lastName", savedUser.getLastName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Authenticates a user using email and password.
     * Returns user data if authentication is successful.
     * 
     * @param loginData map containing email and password
     * @return ResponseEntity with success status and user/customer data if authenticated
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email is required"
                ));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Password is required"
                ));
            }

            // Authenticate user by email and password
            Optional<User> authenticatedUser = userService.authenticateByEmail(email, password);

            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();
                
                // Get customer data if available
                Optional<CustomerDTO> customer = customerService.getCustomerByEmail(email);
                
                // Extract roles
                List<String> roles = user.getRoles().stream()
                        .map(role -> role.getRoleName().toString())
                        .collect(Collectors.toList());
                
                // If no roles assigned, default to ROLE_CUSTOMER
                if (roles.isEmpty()) {
                    roles = List.of("ROLE_CUSTOMER");
                }
                
                // Generate JWT token with roles
                String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), roles);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("userId", user.getUserId());
                response.put("email", user.getEmail());
                response.put("firstName", user.getFirstName());
                response.put("lastName", user.getLastName());
                response.put("roles", roles);
                
                if (customer.isPresent()) {
                    response.put("customer", customer.get());
                }
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
