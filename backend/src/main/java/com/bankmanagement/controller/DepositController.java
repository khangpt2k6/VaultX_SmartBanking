package com.bankmanagement.controller;

import com.bankmanagement.model.Deposit;
import com.bankmanagement.service.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for deposit and funding operations
 */
@RestController
@RequestMapping("/api/deposits")
public class DepositController {
    private static final Logger log = LoggerFactory.getLogger(DepositController.class);
    
    @Autowired
    private DepositService depositService;
    
    /**
     * Process deposit (instant funding)
     * POST /api/deposits/process
     */
    @PostMapping("/process")
    public ResponseEntity<?> processDeposit(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "CARD") String paymentMethod) {
        
        try {
            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "Deposit amount must be greater than zero");
                return ResponseEntity.badRequest().body(error);
            }
            
            Deposit deposit = depositService.processDeposit(userId, amount, paymentMethod);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "✅ Deposit processed successfully! Your account has been credited instantly.");
            response.put("deposit", deposit);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid deposit request: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Invalid deposit request: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("❌ Error processing deposit: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to process deposit: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's deposit history
     * GET /api/deposits/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserDeposits(@PathVariable Long userId) {
        try {
            List<Deposit> deposits = depositService.getUserDeposits(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalDeposits", deposits.size());
            response.put("deposits", deposits);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching user deposits: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch deposits: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get deposit by ID
     * GET /api/deposits/{depositId}
     */
    @GetMapping("/{depositId}")
    public ResponseEntity<?> getDepositById(@PathVariable Long depositId) {
        try {
            Optional<Deposit> deposit = depositService.getDepositById(depositId);
            
            if (deposit.isEmpty()) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("status", "error");
                notFound.put("message", "Deposit not found");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("deposit", deposit.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching deposit: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to fetch deposit: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get total deposits amount for user
     * GET /api/deposits/total/{userId}
     */
    @GetMapping("/total/{userId}")
    public ResponseEntity<?> getTotalDeposits(@PathVariable Long userId) {
        try {
            BigDecimal totalAmount = depositService.getTotalDepositsAmount(userId);
            List<Deposit> deposits = depositService.getUserCompletedDeposits(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalAmount", totalAmount);
            response.put("totalDeposits", deposits.size());
            response.put("deposits", deposits);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error calculating total deposits: {}", e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to calculate total: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
}