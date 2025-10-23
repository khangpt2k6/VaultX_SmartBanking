package com.bankmanagement.repository;

import com.bankmanagement.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    long countByStatus(Account.AccountStatus status);
    
    Optional<Account> findByCustomerId(Long customerId);
    
    List<Account> findByCustomerIdAndStatus(Long customerId, Account.AccountStatus status);
    
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.status = 'ACTIVE'")
    BigDecimal getTotalBalance();
    
    @Query("SELECT a.accountId as accountId, a.customerId as customerId, a.accountNumber as accountNumber, " +
           "a.accountType as accountType, a.balance as balance, a.interestRate as interestRate, " +
           "a.status as status, a.createdAt as createdAt " +
           "FROM Account a")
    List<Map<String, Object>> findAllAccountsAsMap();
}
