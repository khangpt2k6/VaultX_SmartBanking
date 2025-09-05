package com.bankmanagement.repository;

import com.bankmanagement.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.bankmanagement.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    long countByTransactionDateAfter(LocalDateTime dateTime);
    
    @Query("SELECT t.transactionId as transactionId, t.accountId as accountId, t.amount as amount, " +
           "t.transactionType as transactionType, t.description as description, " +
           "t.transactionDate as transactionDate, " +
           "a.accountNumber as accountNumber, " +
           "CONCAT(c.firstName, ' ', c.lastName) as customerName, " +
           "t.destinationAccountId as destinationAccountId, " +
           "da.accountNumber as destinationAccountNumber, " +
           "CONCAT(dc.firstName, ' ', dc.lastName) as destinationCustomerName " +
           "FROM Transaction t " +
           "LEFT JOIN Account a ON t.accountId = a.accountId " +
           "LEFT JOIN Customer c ON a.customerId = c.customerId " +
           "LEFT JOIN Account da ON t.destinationAccountId = da.accountId " +
           "LEFT JOIN Customer dc ON da.customerId = dc.customerId " +
           "ORDER BY t.transactionDate DESC")
    List<Map<String, Object>> findAllTransactionsAsMap();
    
    List<Transaction> findByAccountIdOrderByTransactionDateAsc(Long accountId);
}
