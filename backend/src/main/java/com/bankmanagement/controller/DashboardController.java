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
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get customer statistics
            long totalCustomers = customerService.getTotalCustomers();
            long activeCustomers = customerService.getActiveCustomersCount();
            
            // Get account statistics
            long totalAccounts = accountService.getTotalAccounts();
            long activeAccounts = accountService.getActiveAccounts();
            double totalBalance = accountService.getTotalBalance();
            
            // Get transaction statistics
            long totalTransactions = transactionService.getTotalTransactions();
            long monthlyTransactions = transactionService.getMonthlyTransactions();
            
            stats.put("totalCustomers", totalCustomers);
            stats.put("activeCustomers", activeCustomers);
            stats.put("totalAccounts", totalAccounts);
            stats.put("activeAccounts", activeAccounts);
            stats.put("totalBalance", totalBalance);
            stats.put("totalTransactions", totalTransactions);
            stats.put("monthlyTransactions", monthlyTransactions);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
