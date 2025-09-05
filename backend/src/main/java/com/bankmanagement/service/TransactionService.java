package com.bankmanagement.service;

import com.bankmanagement.model.Transaction;
import com.bankmanagement.model.Account;
import com.bankmanagement.repository.TransactionRepository;
import com.bankmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public Transaction createTransaction(Transaction transaction) {
        // Set transaction date if not set
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        
        // Set default status to PENDING if not set
        // if (transaction.getStatus() == null) {
        //     transaction.setStatus(Transaction.TransactionStatus.PENDING);
        // }
        
        // Debug logging
        System.out.println("🔍 Creating transaction: " + transaction.getTransactionType() + 
                         " - Amount: $" + transaction.getAmount() + 
                         " - Account ID: " + transaction.getAccountId() + 
                         " - Destination Account ID: " + transaction.getDestinationAccountId());
        
        try {
            // Validate transaction before processing
            validateTransaction(transaction);
            
            // Update account balance based on transaction type
            updateAccountBalance(transaction);
            
            // If balance update was successful, mark as COMPLETED
            // transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            
        } catch (Exception e) {
            // If any error occurs, mark as FAILED
            // transaction.setStatus(Transaction.TransactionStatus.FAILED);
            System.err.println("❌ Transaction failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Save the transaction with determined status
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        System.out.println("✅ Transaction saved with ID: " + savedTransaction.getTransactionId() + 
                         " - Destination Account ID: " + savedTransaction.getDestinationAccountId());
        
        return savedTransaction;
    }
    
    private void validateTransaction(Transaction transaction) {
        // Validate account exists
        if (!accountRepository.existsById(transaction.getAccountId())) {
            throw new RuntimeException("Account not found with id: " + transaction.getAccountId());
        }
        
        // Validate amount is positive
        if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transaction amount must be greater than zero");
        }
        
        // For withdrawals and transfers, check if account has sufficient balance
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
        
        // For transfers, validate destination account exists
        if (transaction.getTransactionType() == Transaction.TransactionType.TRANSFER) {
            if (transaction.getDestinationAccountId() == null) {
                throw new RuntimeException("Destination account is required for transfers");
            }
            if (!accountRepository.existsById(transaction.getDestinationAccountId())) {
                throw new RuntimeException("Destination account not found with id: " + 
                    transaction.getDestinationAccountId());
            }
        }
    }
    
    private void updateAccountBalance(Transaction transaction) {
        try {
            // Get the account
            var accountOpt = accountRepository.findById(transaction.getAccountId());
            if (accountOpt.isPresent()) {
                var account = accountOpt.get();
                var currentBalance = account.getBalance();
                var transactionAmount = transaction.getAmount();
                
                // Update balance based on transaction type
                switch (transaction.getTransactionType()) {
                    case DEPOSIT:
                    case INTEREST_CREDIT:
                        account.setBalance(currentBalance.add(transactionAmount));
                        break;
                    case WITHDRAWAL:
                        account.setBalance(currentBalance.subtract(transactionAmount));
                        break;
                    case TRANSFER:
                        // For transfers, subtract from source account
                        account.setBalance(currentBalance.subtract(transactionAmount));
                        System.out.println("🔄 Transfer: Subtracted $" + transactionAmount + " from account " + account.getAccountNumber());
                        
                        // Add to destination account
                        if (transaction.getDestinationAccountId() != null) {
                            System.out.println("🔄 Transfer: Looking for destination account ID: " + transaction.getDestinationAccountId());
                            var destAccountOpt = accountRepository.findById(transaction.getDestinationAccountId());
                            if (destAccountOpt.isPresent()) {
                                var destAccount = destAccountOpt.get();
                                var oldDestBalance = destAccount.getBalance();
                                destAccount.setBalance(destAccount.getBalance().add(transactionAmount));
                                accountRepository.save(destAccount);
                                System.out.println("✅ Transfer: Updated destination account " + destAccount.getAccountNumber() + 
                                    " balance from $" + oldDestBalance + " to $" + destAccount.getBalance());
                            } else {
                                System.err.println("❌ Transfer: Destination account not found with ID: " + transaction.getDestinationAccountId());
                            }
                        } else {
                            System.err.println("❌ Transfer: Destination account ID is null!");
                        }
                        break;
                }
                
                accountRepository.save(account);
                System.out.println("✅ Updated account balance for account " + account.getAccountNumber() + 
                                 " to $" + account.getBalance());
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating account balance: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
    
    public long getTotalTransactions() {
        return transactionRepository.count();
    }
    
    public long getMonthlyTransactions() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return transactionRepository.countByTransactionDateAfter(startOfMonth);
    }
    
    public List<Map<String, Object>> getAllTransactionsAsMap() {
        return transactionRepository.findAllTransactionsAsMap();
    }
    
    public void recalculateAllAccountBalances() {
        try {
            System.out.println("🔍 Recalculating all account balances...");
            
            // Get all accounts
            List<Account> accounts = accountRepository.findAll();
            
            for (Account account : accounts) {
                System.out.println("🔍 Processing account: " + account.getAccountNumber() + " (current balance: $" + account.getBalance() + ")");
                
                // Get all transactions for this account
                List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateAsc(account.getAccountId());
                System.out.println("📝 Found " + transactions.size() + " transactions for this account");
                
                // Start with the current balance (don't reset to zero)
                var currentBalance = account.getBalance();
                
                // Apply each transaction
                for (Transaction transaction : transactions) {
                    var transactionAmount = transaction.getAmount();
                    var oldBalance = currentBalance;
                    
                    switch (transaction.getTransactionType()) {
                        case DEPOSIT:
                        case INTEREST_CREDIT:
                            currentBalance = currentBalance.add(transactionAmount);
                            System.out.println("  💰 " + transaction.getTransactionType() + " +$" + transactionAmount + " (balance: $" + oldBalance + " → $" + currentBalance + ")");
                            break;
                        case WITHDRAWAL:
                            currentBalance = currentBalance.subtract(transactionAmount);
                            System.out.println("  💸 " + transaction.getTransactionType() + " -$" + transactionAmount + " (balance: $" + oldBalance + " → $" + currentBalance + ")");
                            break;
                        case TRANSFER:
                            // For transfers, subtract from source account
                            currentBalance = currentBalance.subtract(transactionAmount);
                            System.out.println("  🔄 " + transaction.getTransactionType() + " -$" + transactionAmount + " (balance: $" + oldBalance + " → $" + currentBalance + ")");
                            break;
                    }
                }
                
                // Only update if the balance actually changed
                if (!currentBalance.equals(account.getBalance())) {
                    account.setBalance(currentBalance);
                    accountRepository.save(account);
                    System.out.println("✅ Updated balance for account " + account.getAccountNumber() + 
                                     " to $" + account.getBalance());
                } else {
                    System.out.println("ℹ️ No balance change needed for account " + account.getAccountNumber());
                }
            }
            
            System.out.println("✅ All account balances recalculated successfully");
        } catch (Exception e) {
            System.err.println("❌ Error recalculating account balances: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
