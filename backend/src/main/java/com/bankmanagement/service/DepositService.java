package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.Deposit;
import com.bankmanagement.repository.AccountRepository;
import com.bankmanagement.repository.DepositRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing user deposits and instant funding
 */
@Service
public class DepositService {
    private static final Logger log = LoggerFactory.getLogger(DepositService.class);
    
    @Autowired
    private DepositRepository depositRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * Process deposit and update account balance instantly
     */
    @Transactional
    public Deposit processDeposit(Long userId, BigDecimal amount, String paymentMethod) {
        
        // Verify user has trading account
        Optional<Account> accountOpt = accountRepository.findByCustomerId(userId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("No trading account found for user");
        }
        
        Account account = accountOpt.get();
        
        // Create deposit record
        Deposit deposit = new Deposit(userId, amount, paymentMethod);
        deposit.setStatus(Deposit.DepositStatus.COMPLETED);
        deposit.setCompletedAt(LocalDateTime.now());
        deposit.setTransactionReference(generateTransactionReference());
        
        // Update account balance instantly
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        
        // Save deposit record
        Deposit savedDeposit = depositRepository.save(deposit);
        
        log.info("✅ Deposit processed - User: {}, Amount: {}, Reference: {}", 
                userId, amount, deposit.getTransactionReference());
        
        return savedDeposit;
    }
    
    /**
     * Get all deposits for user
     */
    public List<Deposit> getUserDeposits(Long userId) {
        return depositRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get deposit by ID
     */
    public Optional<Deposit> getDepositById(Long depositId) {
        return depositRepository.findById(depositId);
    }
    
    /**
     * Get completed deposits for user
     */
    public List<Deposit> getUserCompletedDeposits(Long userId) {
        return depositRepository.findByUserIdAndStatus(userId, Deposit.DepositStatus.COMPLETED);
    }
    
    /**
     * Get total deposits amount for user
     */
    public BigDecimal getTotalDepositsAmount(Long userId) {
        List<Deposit> deposits = getUserCompletedDeposits(userId);
        return deposits.stream()
                .map(Deposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Generate unique transaction reference
     */
    private String generateTransactionReference() {
        return "DEP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}