package main.bankmanagement.controller;

import main.bankmanagement.dao.AccountDAO;
import main.bankmanagement.dao.CustomerDAO;
import main.bankmanagement.dao.TransactionDAO;
import main.bankmanagement.model.Account;
import main.bankmanagement.model.Account.AccountStatus;
import main.bankmanagement.model.Account.AccountType;
import main.bankmanagement.model.Customer;
import main.bankmanagement.model.Transaction;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class BankController {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    
    public BankController() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    // CUSTOMER MANAGEMENT
    
    public int createCustomer(String firstName, String lastName, String address, 
                             String phone, String email, Date dateOfBirth) {
        Customer customer = new Customer(firstName, lastName, address, phone, email, dateOfBirth);
        return customerDAO.createCustomer(customer);
    }
    
    public Customer getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }
    
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public List<Customer> searchCustomersByName(String name) {
        return customerDAO.searchCustomersByName(name);
    }
    
    public boolean updateCustomer(int customerId, String firstName, String lastName, 
                                 String address, String phone, String email, Date dateOfBirth) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            return false;
        }
        
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setDateOfBirth(dateOfBirth);
        
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
    
    // ACCOUNT MANAGEMENT
    
    public int createAccount(int customerId, AccountType accountType, BigDecimal initialDeposit, BigDecimal interestRate) {
        String accountNumber = accountDAO.generateAccountNumber();
        Account account = new Account(customerId, accountNumber, accountType, 
                                     initialDeposit, interestRate, AccountStatus.ACTIVE);
        
        int accountId = accountDAO.createAccount(account);
        
        if (accountId > 0 && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            // Create initial deposit transaction
            transactionDAO.processDeposit(accountId, initialDeposit, "Initial deposit");
        }
        
        return accountId;
    }
    
    public Account getAccountById(int accountId) {
        return accountDAO.getAccountById(accountId);
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }
    
    public List<Account> getAccountsByCustomerId(int customerId) {
        return accountDAO.getAccountsByCustomerId(customerId);
    }
    
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }
    
    public boolean updateAccountDetails(int accountId, AccountType accountType, BigDecimal interestRate) {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            return false;
        }
        
        account.setAccountType(accountType);
        account.setInterestRate(interestRate);
        
        return accountDAO.updateAccount(account);
    }
    
    public boolean closeAccount(int accountId) {
        return accountDAO.closeAccount(accountId);
    }
    
    // TRANSACTION MANAGEMENT
    
    public boolean deposit(int accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false; // Amount must be positive
        }
        
        return transactionDAO.processDeposit(accountId, amount, description);
    }
    
    public boolean withdraw(int accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false; // Amount must be positive
        }
        
        return transactionDAO.processWithdrawal(accountId, amount, description);
    }
    
    public boolean transfer(int sourceAccountId, int destinationAccountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false; // Amount must be positive
        }
        
        return transactionDAO.processTransfer(sourceAccountId, destinationAccountId, amount, description);
    }
    
    public List<Transaction> getAccountTransactions(int accountId) {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }
    
    public List<Transaction> getAccountTransactionsByDateRange(int accountId, Timestamp startDate, Timestamp endDate) {
        return transactionDAO.getTransactionsByDateRange(accountId, startDate, endDate);
    }
}