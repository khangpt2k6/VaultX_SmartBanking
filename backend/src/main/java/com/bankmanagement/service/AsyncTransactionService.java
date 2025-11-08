package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.repository.AccountRepository;
import com.bankmanagement.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AsyncTransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    // Thread-safe counters for metrics
    private final AtomicLong processedTransactions = new AtomicLong(0);
    private final AtomicLong failedTransactions = new AtomicLong(0);
    private final ConcurrentHashMap<String, Long> transactionMetrics = new ConcurrentHashMap<>();

    @Async("transactionExecutor")
    @Transactional
    public CompletableFuture<Transaction> processTransactionAsync(Transaction transaction) {
        try {
            System.out.println("🔄 Processing transaction asynchronously: " + 
                             Thread.currentThread().getName() + " - " + transaction.getTransactionType());
            
            // Validate and process transaction
            validateTransaction(transaction);
            updateAccountBalance(transaction);
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            processedTransactions.incrementAndGet();
            
            // Update metrics
            String type = transaction.getTransactionType().name();
            transactionMetrics.merge(type, 1L, Long::sum);
            
            System.out.println("✅ Transaction processed successfully: " + savedTransaction.getTransactionId());
            return CompletableFuture.completedFuture(savedTransaction);
            
        } catch (Exception e) {
            failedTransactions.incrementAndGet();
            System.err.println("❌ Async transaction failed: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Transaction>> processBatchTransactionsAsync(List<Transaction> transactions) {
        System.out.println("🔄 Processing batch of " + transactions.size() + " transactions asynchronously");
        
        try {
            // Process transactions in parallel using parallel streams
            List<CompletableFuture<Transaction>> futures = transactions.parallelStream()
                .map(this::processTransactionAsync)
                .collect(Collectors.toList());
            
            // Wait for all transactions to complete
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            return allOf.thenApply(v -> 
                futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
            );
            
        } catch (Exception e) {
            System.err.println("❌ Batch processing failed: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<Map<String, Object>> recalculateAllAccountBalancesAsync() {
        System.out.println("🔄 Starting async balance recalculation: " + Thread.currentThread().getName());

        try {
            // First, validate data integrity
            Map<String, Object> integrityCheck = validateDataIntegrity();
            if (!(Boolean) integrityCheck.get("isValid")) {
                System.err.println("❌ Data integrity issues found: " + integrityCheck.get("issues"));
                Map<String, Object> errorResult = new ConcurrentHashMap<>();
                errorResult.put("error", "Data integrity validation failed");
                errorResult.put("issues", integrityCheck.get("issues"));
                return CompletableFuture.completedFuture(errorResult);
            }

            List<Account> accounts = accountRepository.findAll();
            System.out.println("📊 Processing " + accounts.size() + " accounts sequentially");

            // Process accounts sequentially to avoid transaction conflicts
            Map<String, Object> result = new ConcurrentHashMap<>();
            List<Account> updatedAccounts = new ArrayList<>();

            for (Account account : accounts) {
                try {
                    Account updated = recalculateAccountBalanceAsync(account).get(); // Wait for each
                    updatedAccounts.add(updated);
                } catch (Exception e) {
                    System.err.println("❌ Failed to recalculate account " + account.getAccountNumber() + ": " + e.getMessage());
                    // Continue with other accounts instead of failing completely
                }
            }

            result.put("processedAccounts", updatedAccounts.size());
            result.put("timestamp", LocalDateTime.now());
            result.put("threadName", Thread.currentThread().getName());

            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            System.err.println("❌ Async balance recalculation failed: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<Account> recalculateAccountBalanceAsync(Account account) {
        try {
            System.out.println("🔍 Recalculating balance for account " + account.getAccountNumber() + 
                             " on thread: " + Thread.currentThread().getName());
            
            List<Transaction> transactions = transactionRepository
                .findByAccountIdOrderByTransactionDateAsc(account.getAccountId());
            
            BigDecimal calculatedBalance = transactions.parallelStream()
                .reduce(BigDecimal.ZERO, 
                    (balance, transaction) -> {
                        switch (transaction.getTransactionType()) {
                            case DEPOSIT:
                            case INTEREST_CREDIT:
                                return balance.add(transaction.getAmount());
                            case WITHDRAWAL:
                            case TRANSFER:
                                return balance.subtract(transaction.getAmount());
                            default:
                                return balance;
                        }
                    },
                    BigDecimal::add);
            
            if (!calculatedBalance.equals(account.getBalance())) {
                account.setBalance(calculatedBalance);
                accountRepository.save(account);
                System.out.println("✅ Updated balance for account " + account.getAccountNumber() + 
                                 " to $" + calculatedBalance);
            }
            
            return CompletableFuture.completedFuture(account);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to recalculate balance for account " + account.getAccountNumber() + 
                             ": " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("schedulerExecutor")
    public CompletableFuture<Map<String, Object>> generateTransactionReportAsync() {
        try {
            System.out.println("📊 Generating transaction report asynchronously: " + 
                             Thread.currentThread().getName());
            
            long totalTransactions = transactionRepository.count();
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            long monthlyTransactions = transactionRepository.countByTransactionDateAfter(startOfMonth);
            
            Map<String, Object> report = new ConcurrentHashMap<>();
            report.put("totalTransactions", totalTransactions);
            report.put("monthlyTransactions", monthlyTransactions);
            report.put("processedTransactions", processedTransactions.get());
            report.put("failedTransactions", failedTransactions.get());
            report.put("transactionMetrics", new ConcurrentHashMap<>(transactionMetrics));
            report.put("generatedAt", LocalDateTime.now());
            report.put("threadName", Thread.currentThread().getName());
            
            return CompletableFuture.completedFuture(report);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to generate transaction report: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    // Helper methods
    private void validateTransaction(Transaction transaction) {
        if (!accountRepository.existsById(transaction.getAccountId())) {
            throw new RuntimeException("Account not found with id: " + transaction.getAccountId());
        }
        
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transaction amount must be greater than zero");
        }
        
        if (transaction.getTransactionType() == Transaction.TransactionType.WITHDRAWAL || 
            transaction.getTransactionType() == Transaction.TransactionType.TRANSFER) {
            
            var accountOpt = accountRepository.findById(transaction.getAccountId());
            if (accountOpt.isPresent()) {
                var account = accountOpt.get();
                if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                    throw new RuntimeException("Insufficient funds. Available balance: $" + 
                        account.getBalance() + ", Required: $" + transaction.getAmount());
                }
            }
        }
    }

    private void updateAccountBalance(Transaction transaction) {
        var accountOpt = accountRepository.findById(transaction.getAccountId());
        if (accountOpt.isPresent()) {
            var account = accountOpt.get();
            var currentBalance = account.getBalance();
            var transactionAmount = transaction.getAmount();
            
            switch (transaction.getTransactionType()) {
                case DEPOSIT:
                case INTEREST_CREDIT:
                    account.setBalance(currentBalance.add(transactionAmount));
                    break;
                case WITHDRAWAL:
                    account.setBalance(currentBalance.subtract(transactionAmount));
                    break;
                case TRANSFER:
                    account.setBalance(currentBalance.subtract(transactionAmount));
                    if (transaction.getDestinationAccountId() != null) {
                        var destAccountOpt = accountRepository.findById(transaction.getDestinationAccountId());
                        if (destAccountOpt.isPresent()) {
                            var destAccount = destAccountOpt.get();
                            destAccount.setBalance(destAccount.getBalance().add(transactionAmount));
                            accountRepository.save(destAccount);
                        }
                    }
                    break;
            }
            
            accountRepository.save(account);
        }
    }

    // Data integrity validation
    private Map<String, Object> validateDataIntegrity() {
        Map<String, Object> result = new ConcurrentHashMap<>();
        List<String> issues = new ArrayList<>();
        boolean isValid = true;

        try {
            // Basic data count validation
            long accountCount = accountRepository.count();
            long transactionCount = transactionRepository.count();

            if (accountCount == 0) {
                issues.add("No accounts found in database");
                isValid = false;
            }

            if (transactionCount == 0) {
                issues.add("No transactions found in database");
                // This might be valid for a new system, so don't set isValid = false
            }

            // Check for transactions with invalid account references
            long invalidTransactionRefs = transactionRepository.findAll().stream()
                .filter(t -> !accountRepository.existsById(t.getAccountId()))
                .count();

            if (invalidTransactionRefs > 0) {
                issues.add("Found " + invalidTransactionRefs + " transactions referencing non-existent accounts");
                isValid = false;
            }

            // Check for transactions with invalid destination account references
            long invalidDestRefs = transactionRepository.findAll().stream()
                .filter(t -> t.getDestinationAccountId() != null && !accountRepository.existsById(t.getDestinationAccountId()))
                .count();

            if (invalidDestRefs > 0) {
                issues.add("Found " + invalidDestRefs + " transactions with invalid destination account references");
                isValid = false;
            }

            // Check for accounts with negative balances (potential data corruption)
            long negativeBalanceAccounts = accountRepository.findAll().stream()
                .filter(account -> account.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .count();

            if (negativeBalanceAccounts > 0) {
                issues.add("Found " + negativeBalanceAccounts + " accounts with negative balances");
                // This might be valid in some cases, so don't set isValid = false
            }

        } catch (Exception e) {
            issues.add("Error during data integrity check: " + e.getMessage());
            isValid = false;
        }

        result.put("isValid", isValid);
        result.put("issues", issues);
        return result;
    }

    // Getters for metrics
    public long getProcessedTransactionsCount() {
        return processedTransactions.get();
    }

    public long getFailedTransactionsCount() {
        return failedTransactions.get();
    }

    public Map<String, Long> getTransactionMetrics() {
        return new ConcurrentHashMap<>(transactionMetrics);
    }
}
