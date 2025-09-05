package com.bankmanagement.controller;

import com.bankmanagement.service.CustomerService;
import com.bankmanagement.service.AccountService;
import com.bankmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        System.out.println("🔍 Dashboard stats endpoint called");
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Initialize with default values
            stats.put("totalCustomers", 0);
            stats.put("activeCustomers", 0);
            stats.put("totalAccounts", 0);
            stats.put("activeAccounts", 0);
            stats.put("totalBalance", 0.0);
            stats.put("totalTransactions", 0);
            stats.put("monthlyTransactions", 0);
            
            System.out.println("📊 Testing customer service...");
            try {
                // Try to get customer statistics
                long totalCustomers = customerService.getTotalCustomers();
                long activeCustomers = customerService.getActiveCustomersCount();
                stats.put("totalCustomers", totalCustomers);
                stats.put("activeCustomers", activeCustomers);
                System.out.println("✅ Customer service OK: " + totalCustomers + " customers");
            } catch (Exception e) {
                System.out.println("❌ Customer service error: " + e.getMessage());
                e.printStackTrace();
                // Keep default values
            }
            
            System.out.println("📊 Testing account service...");
            try {
                // Try to get account statistics
                long totalAccounts = accountService.getTotalAccounts();
                long activeAccounts = accountService.getActiveAccounts();
                double totalBalance = accountService.getTotalBalance();
                stats.put("totalAccounts", totalAccounts);
                stats.put("activeAccounts", activeAccounts);
                stats.put("totalBalance", totalBalance);
                System.out.println("✅ Account service OK: " + totalAccounts + " accounts");
            } catch (Exception e) {
                System.out.println("❌ Account service error: " + e.getMessage());
                e.printStackTrace();
                // Keep default values
            }
            
            System.out.println("📊 Testing transaction service...");
            try {
                // Try to get transaction statistics
                long totalTransactions = transactionService.getTotalTransactions();
                long monthlyTransactions = transactionService.getMonthlyTransactions();
                stats.put("totalTransactions", totalTransactions);
                stats.put("monthlyTransactions", monthlyTransactions);
                System.out.println("✅ Transaction service OK: " + totalTransactions + " transactions");
            } catch (Exception e) {
                System.out.println("❌ Transaction service error: " + e.getMessage());
                e.printStackTrace();
                // Keep default values
            }
            
            System.out.println("✅ Returning stats: " + stats);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.out.println("❌ Fatal error in dashboard stats: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testConnection() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Backend is running!");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/dbstatus")
    public ResponseEntity<?> getDatabaseStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Test each service individually
            Map<String, String> serviceStatus = new HashMap<>();
            
            try {
                customerService.getTotalCustomers();
                serviceStatus.put("customers", "OK");
            } catch (Exception e) {
                serviceStatus.put("customers", "ERROR: " + e.getMessage());
            }
            
            try {
                accountService.getTotalAccounts();
                serviceStatus.put("accounts", "OK");
            } catch (Exception e) {
                serviceStatus.put("accounts", "ERROR: " + e.getMessage());
            }
            
            try {
                transactionService.getTotalTransactions();
                serviceStatus.put("transactions", "OK");
            } catch (Exception e) {
                serviceStatus.put("transactions", "ERROR: " + e.getMessage());
            }
            
            response.put("success", true);
            response.put("services", serviceStatus);
            response.put("message", "Database status checked");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
