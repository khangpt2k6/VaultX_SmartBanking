package com.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    
    @NotNull(message = "Account ID is required")
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @CreatedDate
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "destination_account_id")
    private Long destinationAccountId;
    
    // @Enumerated(EnumType.STRING)
    // @Column(name = "status")
    // private TransactionStatus status;
    
    // Removed reference_number column to match database schema
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id", insertable = false, updatable = false)
    private Account destinationAccount;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST_CREDIT
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED
    }

    // Default constructor
    public Transaction() {
    }

    // Constructor for new transactions
    public Transaction(Long accountId, TransactionType transactionType, BigDecimal amount, 
                       String description, Long destinationAccountId) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.destinationAccountId = destinationAccountId;
        // this.status = TransactionStatus.PENDING; // Default to pending
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    // public TransactionStatus getStatus() {
    //     return status;
    // }

    // public void setStatus(TransactionStatus status) {
    //     this.status = status;
    // }

    // Removed getter/setter methods for reference_number

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                ", destinationAccountId=" + destinationAccountId +
                // ", status=" + status +
                '}';
    }
}
