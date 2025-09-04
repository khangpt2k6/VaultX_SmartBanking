package main.bankmanagement.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private int accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Timestamp transactionDate;
    private String description;
    private Integer destinationAccountId;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST_CREDIT
    }

    // Default constructor
    public Transaction() {
    }

    // Constructor for new transactions
    public Transaction(int accountId, TransactionType transactionType, BigDecimal amount, 
                       String description, Integer destinationAccountId) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.destinationAccountId = destinationAccountId;
    }

    // Constructor with all fields
    public Transaction(int transactionId, int accountId, TransactionType transactionType, 
                      BigDecimal amount, Timestamp transactionDate, String description, 
                      Integer destinationAccountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.destinationAccountId = destinationAccountId;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
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

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Integer destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
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
                '}';
    }
}