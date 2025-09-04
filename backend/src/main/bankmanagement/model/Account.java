package main.bankmanagement.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    private int accountId;
    private int customerId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal interestRate;
    private Timestamp createdAt;
    private AccountStatus status;

    public enum AccountType {
        SAVINGS, CHECKING, FIXED_DEPOSIT
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, CLOSED
    }

    // Default constructor
    public Account() {
    }

    // Constructor with parameters
    public Account(int customerId, String accountNumber, AccountType accountType, 
                  BigDecimal balance, BigDecimal interestRate, AccountStatus status) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.interestRate = interestRate;
        this.status = status;
    }

    // Constructor with accountId
    public Account(int accountId, int customerId, String accountNumber, AccountType accountType, 
                  BigDecimal balance, BigDecimal interestRate, Timestamp createdAt, AccountStatus status) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.interestRate = interestRate;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", customerId=" + customerId +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", interestRate=" + interestRate +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
