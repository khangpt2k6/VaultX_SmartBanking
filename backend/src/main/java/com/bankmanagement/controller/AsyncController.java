package com.bankmanagement.controller;

import com.bankmanagement.model.Transaction;
import com.bankmanagement.service.AsyncTransactionService;
import com.bankmanagement.service.ScheduledTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for asynchronous transaction processing operations.
 * Provides endpoints for async transactions, batch processing, balance recalculation, and stress testing.
 */
@RestController
@RequestMapping("/api/async")
@CrossOrigin(origins = "*")
public class AsyncController {

    private final AsyncTransactionService asyncTransactionService;
    private final ScheduledTaskService scheduledTaskService;

    /**
     * Constructs AsyncController with required service dependencies.
     *
     * @param asyncTransactionService service for handling async transactions
     * @param scheduledTaskService service for scheduled task management
     */
    public AsyncController(AsyncTransactionService asyncTransactionService, ScheduledTaskService scheduledTaskService) {
        this.asyncTransactionService = asyncTransactionService;
        this.scheduledTaskService = scheduledTaskService;
    }

    /**
     * Asynchronously processes a single transaction.
     *
     * @param transaction the transaction to process
     * @return CompletableFuture with response containing transaction details and thread name
     */
    @PostMapping("/transaction")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> processTransactionAsync(@RequestBody Transaction transaction) {
        try {
            System.out.println("🔄 Processing transaction asynchronously via API");
            
            return asyncTransactionService.processTransactionAsync(transaction)
                .thenApply(savedTransaction -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Transaction processed asynchronously");
                    response.put("transaction", savedTransaction);
                    response.put("threadName", Thread.currentThread().getName());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Async transaction failed: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
                
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing async transaction: " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
    }

    /**
     * Asynchronously processes a batch of transactions.
     *
     * @param transactions list of transactions to process
     * @return CompletableFuture with response containing processed count and thread name
     */
    @PostMapping("/transactions/batch")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> processBatchTransactionsAsync(@RequestBody List<Transaction> transactions) {
        try {
            System.out.println("🔄 Processing batch of " + transactions.size() + " transactions asynchronously");
            
            return asyncTransactionService.processBatchTransactionsAsync(transactions)
                .thenApply(processedTransactions -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Batch transactions processed asynchronously");
                    response.put("processedCount", processedTransactions.size());
                    response.put("threadName", Thread.currentThread().getName());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Batch processing failed: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
                
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing batch transactions: " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
    }

    /**
     * Asynchronously recalculates all account balances.
     *
     * @return CompletableFuture with response containing recalculation result
     */
    @PostMapping("/balances/recalculate")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> recalculateBalancesAsync() {
        try {
            System.out.println("🔄 Starting async balance recalculation via API");
            
            return asyncTransactionService.recalculateAllAccountBalancesAsync()
                .thenApply(result -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Balance recalculation completed asynchronously");
                    response.put("result", result);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Balance recalculation failed: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
                
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error starting balance recalculation: " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
    }

    /**
     * Asynchronously generates a transaction report.
     *
     * @return CompletableFuture with response containing transaction report data
     */
    @GetMapping("/report")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> generateTransactionReportAsync() {
        try {
            System.out.println("📊 Generating async transaction report via API");
            
            return asyncTransactionService.generateTransactionReportAsync()
                .thenApply(report -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Transaction report generated asynchronously");
                    response.put("report", report);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Report generation failed: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
                
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error generating report: " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
    }

    /**
     * Retrieves async processing metrics and statistics.
     *
     * @return ResponseEntity with metrics data including processed/failed transactions
     */
    @GetMapping("/metrics")
    public ResponseEntity<?> getAsyncMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("processedTransactions", asyncTransactionService.getProcessedTransactionsCount());
            metrics.put("failedTransactions", asyncTransactionService.getFailedTransactionsCount());
            metrics.put("transactionMetrics", asyncTransactionService.getTransactionMetrics());
            metrics.put("scheduledTaskExecutions", scheduledTaskService.getScheduledTaskExecutions());
            metrics.put("lastExecutionTimes", scheduledTaskService.getLastExecutionTimes());
            metrics.put("timestamp", java.time.LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("metrics", metrics);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving metrics: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Checks the health status of async processing system.
     *
     * @return ResponseEntity with health status and success rate metrics
     */
    @GetMapping("/health")
    public ResponseEntity<?> getAsyncHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("threadName", Thread.currentThread().getName());
            health.put("timestamp", java.time.LocalDateTime.now());
            health.put("processedTransactions", asyncTransactionService.getProcessedTransactionsCount());
            health.put("failedTransactions", asyncTransactionService.getFailedTransactionsCount());
            
            // Calculate success rate
            long processed = asyncTransactionService.getProcessedTransactionsCount();
            long failed = asyncTransactionService.getFailedTransactionsCount();
            double successRate = processed > 0 ? (processed - failed) * 100.0 / processed : 100.0;
            health.put("successRate", String.format("%.2f%%", successRate));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("health", health);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Health check failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Runs a stress test with the specified number of transactions (max 100).
     *
     * @param count number of test transactions to process
     * @return CompletableFuture with response containing test results
     */
    @PostMapping("/stress-test/{count}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> stressTest(@PathVariable int count) {
        try {
            if (count > 100) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Stress test limited to 100 transactions maximum");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
            }
            
            System.out.println("🧪 Starting stress test with " + count + " transactions");
            
            // Create test transactions
            List<Transaction> testTransactions = java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    Transaction t = new Transaction();
                    t.setAccountId(1L); // Assuming account ID 1 exists
                    t.setTransactionType(Transaction.TransactionType.DEPOSIT);
                    t.setAmount(java.math.BigDecimal.valueOf(Math.random() * 1000));
                    t.setDescription("Stress test transaction " + i);
                    t.setTransactionDate(java.time.LocalDateTime.now());
                    return t;
                })
                .toList();
            
            return asyncTransactionService.processBatchTransactionsAsync(testTransactions)
                .thenApply(processedTransactions -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Stress test completed");
                    response.put("requestedCount", count);
                    response.put("processedCount", processedTransactions.size());
                    response.put("threadName", Thread.currentThread().getName());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Stress test failed: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
                
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error in stress test: " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
    }
}
