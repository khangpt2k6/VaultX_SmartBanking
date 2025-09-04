package com.bankmanagement.controller;

import com.bankmanagement.dto.CustomerDTO;
import com.bankmanagement.model.Customer;
import com.bankmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        try {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setFirstName(userData.get("firstName"));
            customerDTO.setLastName(userData.get("lastName"));
            customerDTO.setEmail(userData.get("email"));
            customerDTO.setPhone(userData.get("phone"));
            customerDTO.setAddress(userData.get("address"));
            // Parse date if provided
            String dateOfBirth = userData.get("dateOfBirth");
            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                customerDTO.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
            }
            
            CustomerDTO savedCustomer = customerService.createCustomer(customerDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("customer", savedCustomer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String phone = loginData.get("phone");
            
            // Simple authentication - just check if customer exists
            CustomerDTO customer = null;
            if (email != null && !email.isEmpty()) {
                customer = customerService.getCustomerByEmail(email).orElse(null);
            } else if (phone != null && !phone.isEmpty()) {
                customer = customerService.getCustomerByPhone(phone).orElse(null);
            }
            
            if (customer != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("customer", customer);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid credentials");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
