package com.bankmanagement.controller;

import com.bankmanagement.model.Account;
import com.bankmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Specific endpoints first (before the generic /{id} pattern)
    @GetMapping("/basic")
    public ResponseEntity<?> getBasicTest() {
        try {
            System.out.println("🔍 Basic test endpoint...");
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Basic test working");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error in basic test: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getAccountCount() {
        try {
            System.out.println("🔍 Getting account count...");
            long count = accountService.getTotalAccounts();
            System.out.println("✅ Account count: " + count);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error getting account count: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get count: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/simple")
    public ResponseEntity<?> getSimpleAccounts() {
        try {
            System.out.println("🔍 Getting simple accounts...");
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Simple accounts endpoint working");
            response.put("accounts", new ArrayList<>());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error in simple accounts: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testAccountsEndpoint() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Accounts endpoint is working");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/basic-accounts")
    public ResponseEntity<?> getBasicAccounts() {
        try {
            System.out.println("🔍 Getting basic accounts...");
            // Return a simple response with account count
            long count = accountService.getTotalAccounts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("accounts", new ArrayList<>());
            response.put("message", "Basic accounts data (simplified)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error getting basic accounts: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        try {
            System.out.println("🔍 Getting all accounts...");
            System.out.println("🔍 AccountService: " + (accountService != null ? "OK" : "NULL"));
            
            // Test repository directly
            System.out.println("🔍 Testing repository...");
            long count = accountService.getTotalAccounts();
            System.out.println("🔍 Total accounts count: " + count);
            
            // Use custom query to get account data as Map to avoid entity mapping issues
            List<Map<String, Object>> accounts = accountService.getAllAccountsAsMap();
            System.out.println("✅ Found " + accounts.size() + " accounts using custom query");
            
            // Log each account
            for (Map<String, Object> account : accounts) {
                System.out.println("📝 Account: " + account.get("accountNumber") + " - " + account.get("accountType") + " - $" + account.get("balance"));
            }
            
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            System.err.println("❌ Error getting accounts: " + e.getMessage());
            System.err.println("❌ Error class: " + e.getClass().getSimpleName());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch accounts: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        try {
            System.out.println("🔍 Getting account by ID: " + id);
            
            // Use custom query to get account data as Map to avoid entity mapping issues
            List<Map<String, Object>> accounts = accountService.getAllAccountsAsMap();
            
            // Find the account with the matching ID
            Optional<Map<String, Object>> accountOpt = accounts.stream()
                .filter(account -> {
                    Object accountId = account.get("accountId");
                    if (accountId instanceof Number) {
                        return ((Number) accountId).longValue() == id;
                    }
                    return accountId != null && accountId.toString().equals(id.toString());
                })
                .findFirst();
            
            if (accountOpt.isPresent()) {
                Map<String, Object> account = accountOpt.get();
                System.out.println("✅ Found account: " + account.get("accountNumber"));
                return ResponseEntity.ok(account);
            } else {
                System.out.println("❌ Account not found with ID: " + id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting account by ID: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch account: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        try {
            System.out.println("🔍 Creating new account...");
            System.out.println("📝 Account data received: " + account);
            System.out.println("📝 Account number: " + account.getAccountNumber());
            System.out.println("📝 Account type: " + account.getAccountType());
            System.out.println("📝 Customer ID: " + account.getCustomerId());
            System.out.println("📝 Balance: " + account.getBalance());
            System.out.println("📝 Interest rate: " + account.getInterestRate());
            System.out.println("📝 Status: " + account.getStatus());
            
            // Validate required fields
            if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
                System.err.println("❌ Account number is required");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account number is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (account.getAccountType() == null) {
                System.err.println("❌ Account type is required");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account type is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (account.getCustomerId() == null) {
                System.err.println("❌ Customer ID is required");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Customer ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            Account savedAccount = accountService.createAccount(account);
            System.out.println("✅ Account created successfully: " + savedAccount.getAccountId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account created successfully");
            response.put("account", savedAccount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error creating account: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody Map<String, Object> accountData) {
        try {
            System.out.println("🔍 Updating account ID: " + id);
            System.out.println("📝 Account data received: " + accountData);
            
            // Create a new Account object with the updated data
            Account updatedAccount = new Account();
            updatedAccount.setAccountId(id);
            
            // Set fields from the request data
            if (accountData.containsKey("customerId")) {
                updatedAccount.setCustomerId(Long.valueOf(accountData.get("customerId").toString()));
            }
            if (accountData.containsKey("accountNumber")) {
                updatedAccount.setAccountNumber(accountData.get("accountNumber").toString());
            }
            if (accountData.containsKey("accountType")) {
                updatedAccount.setAccountType(Account.AccountType.valueOf(accountData.get("accountType").toString()));
            }
            if (accountData.containsKey("balance")) {
                updatedAccount.setBalance(new java.math.BigDecimal(accountData.get("balance").toString()));
            }
            if (accountData.containsKey("interestRate")) {
                updatedAccount.setInterestRate(new java.math.BigDecimal(accountData.get("interestRate").toString()));
            }
            if (accountData.containsKey("status")) {
                updatedAccount.setStatus(Account.AccountStatus.valueOf(accountData.get("status").toString()));
            }
            
            // Save the updated account
            Account savedAccount = accountService.createAccount(updatedAccount);
            System.out.println("✅ Account updated successfully: " + savedAccount.getAccountId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account updated successfully");
            response.put("account", savedAccount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error updating account: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update account: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
