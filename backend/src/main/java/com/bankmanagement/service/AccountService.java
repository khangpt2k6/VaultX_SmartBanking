package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    public Account createAccount(Account account) {
        // Ensure account is created as ACTIVE by default
        if (account.getStatus() == null) {
            account.setStatus(Account.AccountStatus.ACTIVE);
        }
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        account.setCustomerId(accountDetails.getCustomerId());
        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setAccountType(accountDetails.getAccountType());
        account.setBalance(accountDetails.getBalance());
        account.setInterestRate(accountDetails.getInterestRate());
        account.setStatus(accountDetails.getStatus());
        
        return accountRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }
    
    public long getTotalAccounts() {
        return accountRepository.count();
    }
    
    public long getActiveAccounts() {
        return accountRepository.countByStatus(Account.AccountStatus.ACTIVE);
    }
    
    public double getTotalBalance() {
        java.math.BigDecimal totalBalance = accountRepository.getTotalBalance();
        return totalBalance != null ? totalBalance.doubleValue() : 0.0;
    }
    
    public List<Map<String, Object>> getAllAccountsAsMap() {
        return accountRepository.findAllAccountsAsMap();
    }
}
