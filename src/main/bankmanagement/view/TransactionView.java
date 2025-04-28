// TransactionView.java
package main.bankmanagement.view;

import main.bankmanagement.controller.BankController;
import main.bankmanagement.model.Account;
import main.bankmanagement.model.Account.AccountStatus;
import main.bankmanagement.model.Customer;
import main.bankmanagement.model.Transaction;
import main.bankmanagement.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

public class TransactionView {
    private Scanner scanner;
    private BankController controller;
    
    public TransactionView(Scanner scanner, BankController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }
    
    public void displayTransactionMenu() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n===== TRANSACTION MANAGEMENT =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. View Account Transactions");
            System.out.println("5. View Account Statement by Date Range");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    makeDeposit();
                    break;
                case 2:
                    makeWithdrawal();
                    break;
                case 3:
                    makeTransfer();
                    break;
                case 4:
                    viewAccountTransactions();
                    break;
                case 5:
                    viewAccountStatementByDateRange();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
    
    private void makeDeposit() {
        System.out.println("\n----- DEPOSIT FUNDS -----");
        
        Account account = getAccountForTransaction();
        if (account == null) {
            return;
        }
        
        System.out.print("Enter Deposit Amount: ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Deposit amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }
        
        System.out.print("Enter Description (optional): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "Deposit";
        }
        
        boolean success = controller.deposit(account.getAccountId(), amount, description);
        
        if (success) {
            System.out.println("Deposit successful.");
            System.out.println("New Balance: $" + controller.getAccountById(account.getAccountId()).getBalance());
        } else {
            System.out.println("Deposit failed. Please try again.");
        }
    }
    
    private void makeWithdrawal() {
        System.out.println("\n----- WITHDRAW FUNDS -----");
        
        Account account = getAccountForTransaction();
        if (account == null) {
            return;
        }
        
        System.out.print("Enter Withdrawal Amount: ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Withdrawal amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }
        
        // Check if sufficient funds
        if (amount.compareTo(account.getBalance()) > 0) {
            System.out.println("Insufficient funds. Current balance: $" + account.getBalance());
            return;
        }
        
        System.out.print("Enter Description (optional): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "Withdrawal";
        }
        
        boolean success = controller.withdraw(account.getAccountId(), amount, description);
        
        if (success) {
            System.out.println("Withdrawal successful.");
            System.out.println("New Balance: $" + controller.getAccountById(account.getAccountId()).getBalance());
        } else {
            System.out.println("Withdrawal failed. Please try again.");
        }
    }
    
    private void makeTransfer() {
        System.out.println("\n----- TRANSFER FUNDS -----");
        
        System.out.println("Source Account:");
        Account sourceAccount = getAccountForTransaction();
        if (sourceAccount == null) {
            return;
        }
        
        System.out.println("\nDestination Account:");
        System.out.print("Enter Destination Account Number: ");
        String destAccountNumber = scanner.nextLine();
        
        Account destAccount = controller.getAccountByNumber(destAccountNumber);
        if (destAccount == null) {
            System.out.println("Destination account not found.");
            return;
        }
        
        if (destAccount.getStatus() != AccountStatus.ACTIVE) {
            System.out.println("Destination account is not active.");
            return;
        }
        
        if (sourceAccount.getAccountId() == destAccount.getAccountId()) {
            System.out.println("Cannot transfer to the same account.");
            return;
        }
        
        System.out.print("Enter Transfer Amount: ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Transfer amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }
        
        // Check if sufficient funds
        if (amount.compareTo(sourceAccount.getBalance()) > 0) {
            System.out.println("Insufficient funds. Current balance: $" + sourceAccount.getBalance());
            return;
        }
        
        System.out.print("Enter Description (optional): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "Transfer to " + destAccount.getAccountNumber();
        }
        
        boolean success = controller.transfer(sourceAccount.getAccountId(), destAccount.getAccountId(), amount, description);
        
        if (success) {
            System.out.println("Transfer successful.");
            System.out.println("New Balance: $" + controller.getAccountById(sourceAccount.getAccountId()).getBalance());
        } else {
            System.out.println("Transfer failed. Please try again.");
        }
    }
    
    private void viewAccountTransactions() {
        System.out.println("\n----- VIEW ACCOUNT TRANSACTIONS -----");
        
        Account account = getAccountForTransaction();
        if (account == null) {
            return;
        }
        
        List<Transaction> transactions = controller.getAccountTransactions(account.getAccountId());
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this account.");
        } else {
            displayTransactions(transactions);
        }
    }
    
    private void viewAccountStatementByDateRange() {
        System.out.println("\n----- VIEW ACCOUNT STATEMENT BY DATE RANGE -----");
        
        Account account = getAccountForTransaction();
        if (account == null) {
            return;
        }
        
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        Timestamp startDate = null;
        
        if (!startDateStr.isEmpty()) {
            startDate = DateUtil.parseTimestamp(startDateStr + " 00:00:00");
            if (startDate == null) {
                System.out.println("Invalid start date format.");
                return;
            }
        } else {
            // Default to 30 days ago
            startDate = new Timestamp(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
        }
        
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        Timestamp endDate = null;
        
        if (!endDateStr.isEmpty()) {
            endDate = DateUtil.parseTimestamp(endDateStr + " 23:59:59");
            if (endDate == null) {
                System.out.println("Invalid end date format.");
                return;
            }
        } else {
            // Default to current date
            endDate = new Timestamp(System.currentTimeMillis());
        }
        
        List<Transaction> transactions = controller.getAccountTransactionsByDateRange(
            account.getAccountId(), startDate, endDate);
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this account in the specified date range.");
        } else {
            System.out.println("\nAccount Statement from " + 
                DateUtil.formatDate(new Date(startDate.getTime())) + " to " + 
                DateUtil.formatDate(new Date(endDate.getTime())));
            
            displayTransactions(transactions);
        }
    }
    
    private Account getAccountForTransaction() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        
        Account account = controller.getAccountByNumber(accountNumber);
        
        if (account == null) {
            System.out.println("Account not found with number: " + accountNumber);
            return null;
        }
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            System.out.println("This account is not active.");
            return null;
        }
        
        // Display the account information
        Customer customer = controller.getCustomerById(account.getCustomerId());
        System.out.println("Account: " + account.getAccountNumber() + " | " + 
            account.getAccountType() + " | " + 
            "Balance: $" + account.getBalance() + " | " +
            "Customer: " + customer.getFirstName() + " " + customer.getLastName());
        
        return account;
    }
    
    private void displayTransactions(List<Transaction> transactions) {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-12s | %-10s | %-10s | %-20s | %s\n", 
            "ID", "Date", "Type", "Amount", "Description", "Destination");
        System.out.println("---------------------------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            String formattedDate = DateUtil.formatTimestamp(transaction.getTransactionDate());
            
            // Format the destination account info, if any
            String destination = "";
            if (transaction.getDestinationAccountId() != null) {
                Account destAccount = controller.getAccountById(transaction.getDestinationAccountId());
                if (destAccount != null) {
                    destination = destAccount.getAccountNumber();
                }
            }
            
            System.out.printf("%-5d | %-12s | %-10s | $%-9.2f | %-20s | %s\n", 
                transaction.getTransactionId(),
                formattedDate.substring(0, 10), // Show just the date part
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                destination
            );
        }
        System.out.println("---------------------------------------------------------------------------------");
    }
}