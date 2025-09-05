package com.bankmanagement.controller;

import com.bankmanagement.model.Transaction;
import com.bankmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        try {
            System.out.println("🔍 Getting all transactions...");
            System.out.println("🔍 TransactionService: " + (transactionService != null ? "OK" : "NULL"));
            
            // Test repository directly
            System.out.println("🔍 Testing repository...");
            long count = transactionService.getTotalTransactions();
            System.out.println("🔍 Total transactions count: " + count);
            
            // Use custom query to get transaction data as Map to avoid entity mapping issues
            List<Map<String, Object>> transactions = transactionService.getAllTransactionsAsMap();
            System.out.println("✅ Found " + transactions.size() + " transactions using custom query");
            
            // Log each transaction
            for (Map<String, Object> transaction : transactions) {
                System.out.println("📝 Transaction: " + transaction.get("transactionType") + " - $" + transaction.get("amount") + 
                                 " - Account: " + transaction.get("accountNumber") + 
                                 " - Customer: " + transaction.get("customerName") + 
                                 " - " + transaction.get("description"));
            }
            
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            System.err.println("❌ Error getting transactions: " + e.getMessage());
            System.err.println("❌ Error class: " + e.getClass().getSimpleName());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch transactions: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction savedTransaction = transactionService.createTransaction(transaction);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transaction created successfully");
            response.put("transaction", savedTransaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transaction deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/recalculate-balances")
    public ResponseEntity<?> recalculateBalances() {
        try {
            System.out.println("🔍 Recalculating all account balances...");
            transactionService.recalculateAllAccountBalances();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All account balances recalculated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error recalculating balances: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to recalculate balances: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
