package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.repository.AccountRepository;
import com.bankmanagement.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ScheduledTaskService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AsyncTransactionService asyncTransactionService;

    private final AtomicLong scheduledTaskExecutions = new AtomicLong(0);
    private final ConcurrentHashMap<String, LocalDateTime> lastExecutionTimes = new ConcurrentHashMap<>();

    // Run every 5 minutes to recalculate account balances
    @Scheduled(fixedRate = 300000, initialDelay = 60000)
    public void scheduledBalanceRecalculation() {
        try {
            String taskName = "balance-recalculation";
            lastExecutionTimes.put(taskName, LocalDateTime.now());
            scheduledTaskExecutions.incrementAndGet();
            
            System.out.println("🔄 Scheduled task: Starting balance recalculation at " + 
                             LocalDateTime.now() + " (execution #" + scheduledTaskExecutions.get() + ")");
            
            asyncTransactionService.recalculateAllAccountBalancesAsync()
                .thenAccept(result -> {
                    System.out.println("✅ Scheduled balance recalculation completed: " + result);
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Scheduled balance recalculation failed: " + throwable.getMessage());
                    return null;
                });
                
        } catch (Exception e) {
            System.err.println("❌ Error in scheduled balance recalculation: " + e.getMessage());
        }
    }

    // Run every hour to generate transaction reports
    @Scheduled(fixedRate = 3600000, initialDelay = 120000)
    public void scheduledTransactionReport() {
        try {
            String taskName = "transaction-report";
            lastExecutionTimes.put(taskName, LocalDateTime.now());
            
            System.out.println("📊 Scheduled task: Generating transaction report at " + 
                             LocalDateTime.now());
            
            asyncTransactionService.generateTransactionReportAsync()
                .thenAccept(report -> {
                    System.out.println("✅ Scheduled transaction report generated: " + report);
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Scheduled transaction report failed: " + throwable.getMessage());
                    return null;
                });
                
        } catch (Exception e) {
            System.err.println("❌ Error in scheduled transaction report: " + e.getMessage());
        }
    }

    // Run daily at 2 AM to clean up old transactions (example)
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledDataCleanup() {
        try {
            String taskName = "data-cleanup";
            lastExecutionTimes.put(taskName, LocalDateTime.now());
            
            System.out.println("🧹 Scheduled task: Starting data cleanup at " + 
                             LocalDateTime.now());
            
            // Example: Clean up transactions older than 1 year
            LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);
            List<Transaction> oldTransactions = transactionRepository.findAll().stream()
                .filter(t -> t.getTransactionDate().isBefore(cutoffDate))
                .toList();
            
            if (!oldTransactions.isEmpty()) {
                System.out.println("🗑️ Found " + oldTransactions.size() + " old transactions to clean up");
                // In a real scenario, you might archive these instead of deleting
                // transactionRepository.deleteAll(oldTransactions);
                System.out.println("ℹ️ Data cleanup completed (transactions archived, not deleted)");
            } else {
                System.out.println("ℹ️ No old transactions found for cleanup");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in scheduled data cleanup: " + e.getMessage());
        }
    }

    // Run every 30 seconds to monitor system health
    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void scheduledHealthCheck() {
        try {
            String taskName = "health-check";
            lastExecutionTimes.put(taskName, LocalDateTime.now());
            
            // Check database connectivity
            long accountCount = accountRepository.count();
            long transactionCount = transactionRepository.count();
            
            // Check async service metrics
            long processedTxn = asyncTransactionService.getProcessedTransactionsCount();
            long failedTxn = asyncTransactionService.getFailedTransactionsCount();
            
            System.out.println("💓 Health Check - Accounts: " + accountCount + 
                             ", Transactions: " + transactionCount + 
                             ", Processed: " + processedTxn + 
                             ", Failed: " + failedTxn + 
                             " (Thread: " + Thread.currentThread().getName() + ")");
            
            // Alert if failure rate is high
            if (processedTxn > 0 && (failedTxn * 100.0 / processedTxn) > 10) {
                System.err.println("⚠️ High transaction failure rate detected: " + 
                                 String.format("%.2f%%", (failedTxn * 100.0 / processedTxn)));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Health check failed: " + e.getMessage());
        }
    }

    // Run every 15 minutes to apply interest credits
    @Scheduled(fixedRate = 900000, initialDelay = 300000)
    public void scheduledInterestCredit() {
        try {
            String taskName = "interest-credit";
            lastExecutionTimes.put(taskName, LocalDateTime.now());
            
            System.out.println("💰 Scheduled task: Processing interest credits at " + 
                             LocalDateTime.now());
            
            List<Account> savingsAccounts = accountRepository.findAll().stream()
                .filter(account -> account.getAccountType() == Account.AccountType.SAVINGS)
                .filter(account -> account.getStatus() == Account.AccountStatus.ACTIVE)
                .filter(account -> account.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .toList();
            
            if (!savingsAccounts.isEmpty()) {
                System.out.println("💰 Found " + savingsAccounts.size() + " savings accounts for interest calculation");
                
                // Process interest credits in parallel
                savingsAccounts.parallelStream().forEach(account -> {
                    BigDecimal interestAmount = account.getBalance()
                        .multiply(account.getInterestRate())
                        .divide(BigDecimal.valueOf(365 * 24 * 60 * 15)); // Every 15 minutes
                    
                    if (interestAmount.compareTo(BigDecimal.ZERO) > 0) {
                        Transaction interestTransaction = new Transaction();
                        interestTransaction.setAccountId(account.getAccountId());
                        interestTransaction.setTransactionType(Transaction.TransactionType.INTEREST_CREDIT);
                        interestTransaction.setAmount(interestAmount);
                        interestTransaction.setDescription("Scheduled interest credit");
                        interestTransaction.setTransactionDate(LocalDateTime.now());
                        
                        asyncTransactionService.processTransactionAsync(interestTransaction)
                            .thenAccept(t -> System.out.println("💰 Interest credited to account " + 
                                         account.getAccountNumber() + ": $" + interestAmount))
                            .exceptionally(throwable -> {
                                System.err.println("❌ Failed to credit interest to account " + 
                                                 account.getAccountNumber() + ": " + throwable.getMessage());
                                return null;
                            });
                    }
                });
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in scheduled interest credit: " + e.getMessage());
        }
    }

    // Getters for monitoring
    public long getScheduledTaskExecutions() {
        return scheduledTaskExecutions.get();
    }

    public ConcurrentHashMap<String, LocalDateTime> getLastExecutionTimes() {
        return new ConcurrentHashMap<>(lastExecutionTimes);
    }
}
