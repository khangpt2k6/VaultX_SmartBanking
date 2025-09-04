package main.bankmanagement.view;

import main.bankmanagement.controller.BankController;
import main.bankmanagement.model.Account;
import main.bankmanagement.model.Account.AccountStatus;
import main.bankmanagement.model.Account.AccountType;
import main.bankmanagement.model.Customer;
import main.bankmanagement.util.DateUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class AccountView {
    private Scanner scanner;
    private BankController controller;
    
    public AccountView(Scanner scanner, BankController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }
    
    public void displayAccountMenu() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n===== ACCOUNT MANAGEMENT =====");
            System.out.println("1. Create New Account");
            System.out.println("2. View Account Details");
            System.out.println("3. List Customer Accounts");
            System.out.println("4. Update Account Details");
            System.out.println("5. Close Account");
            System.out.println("6. List All Accounts");
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
                    createNewAccount();
                    break;
                case 2:
                    viewAccountDetails();
                    break;
                case 3:
                    listCustomerAccounts();
                    break;
                case 4:
                    updateAccountDetails();
                    break;
                case 5:
                    closeAccount();
                    break;
                case 6:
                    listAllAccounts();
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
    
    private void createNewAccount() {
        System.out.println("\n----- CREATE NEW ACCOUNT -----");
        
        System.out.print("Enter Customer ID: ");
        int customerId;
        try {
            customerId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Customer ID format.");
            return;
        }
        
        Customer customer = controller.getCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }
        
        System.out.println("Creating account for: " + customer.getFirstName() + " " + customer.getLastName());
        
        System.out.println("Select Account Type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.println("3. Fixed Deposit");
        System.out.print("Enter choice: ");
        
        AccountType accountType;
        try {
            int typeChoice = Integer.parseInt(scanner.nextLine());
            switch (typeChoice) {
                case 1:
                    accountType = AccountType.SAVINGS;
                    break;
                case 2:
                accountType = AccountType.CHECKING;
                break;
            case 3:
                accountType = AccountType.FIXED_DEPOSIT;
                break;
            default:
                System.out.println("Invalid choice. Defaulting to Savings Account.");
                accountType = AccountType.SAVINGS;
                break;
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Defaulting to Savings Account.");
        accountType = AccountType.SAVINGS;
    }
    
    System.out.print("Enter Initial Deposit Amount: ");
    BigDecimal initialDeposit;
    try {
        initialDeposit = new BigDecimal(scanner.nextLine());
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Initial deposit cannot be negative. Using 0.00");
            initialDeposit = BigDecimal.ZERO;
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid amount format. Using 0.00");
        initialDeposit = BigDecimal.ZERO;
    }
    
    System.out.print("Enter Interest Rate (e.g., 2.5 for 2.5%): ");
    BigDecimal interestRate;
    try {
        interestRate = new BigDecimal(scanner.nextLine());
        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Interest rate cannot be negative. Using 0.00%");
            interestRate = BigDecimal.ZERO;
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid rate format. Using 0.00%");
        interestRate = BigDecimal.ZERO;
    }
    
    int accountId = controller.createAccount(customerId, accountType, initialDeposit, interestRate);
    
    if (accountId > 0) {
        Account newAccount = controller.getAccountById(accountId);
        System.out.println("Account created successfully!");
        System.out.println("Account Number: " + newAccount.getAccountNumber());
        System.out.println("Account ID: " + accountId);
    } else {
        System.out.println("Failed to create account. Please try again.");
    }
}

private void viewAccountDetails() {
    System.out.println("\n----- VIEW ACCOUNT DETAILS -----");
    
    System.out.print("Enter Account Number or ID: ");
    String input = scanner.nextLine();
    
    Account account = null;
    
    try {
        // Try to parse as account ID first
        int accountId = Integer.parseInt(input);
        account = controller.getAccountById(accountId);
    } catch (NumberFormatException e) {
        // If not a valid ID, try as account number
        account = controller.getAccountByNumber(input);
    }
    
    if (account != null) {
        displayAccountDetails(account);
    } else {
        System.out.println("Account not found with the given ID/Number.");
    }
}

private void listCustomerAccounts() {
    System.out.println("\n----- LIST CUSTOMER ACCOUNTS -----");
    
    System.out.print("Enter Customer ID: ");
    try {
        int customerId = Integer.parseInt(scanner.nextLine());
        Customer customer = controller.getCustomerById(customerId);
        
        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }
        
        List<Account> accounts = controller.getAccountsByCustomerId(customerId);
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for customer: " + customer.getFirstName() + " " + customer.getLastName());
        } else {
            System.out.println("Accounts for " + customer.getFirstName() + " " + customer.getLastName() + ":");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-5s | %-12s | %-10s | %-10s | %-8s\n", 
                "ID", "Account No.", "Type", "Balance", "Status");
            System.out.println("--------------------------------------------------");
            
            for (Account account : accounts) {
                System.out.printf("%-5d | %-12s | %-10s | %-10.2f | %-8s\n", 
                    account.getAccountId(), 
                    account.getAccountNumber(),
                    account.getAccountType(),
                    account.getBalance(),
                    account.getStatus()
                );
            }
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid Customer ID format.");
    }
}

private void updateAccountDetails() {
    System.out.println("\n----- UPDATE ACCOUNT DETAILS -----");
    
    System.out.print("Enter Account ID: ");
    try {
        int accountId = Integer.parseInt(scanner.nextLine());
        Account account = controller.getAccountById(accountId);
        
        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }
        
        if (account.getStatus() == AccountStatus.CLOSED) {
            System.out.println("Cannot update a closed account.");
            return;
        }
        
        System.out.println("Current account details:");
        displayAccountDetails(account);
        
        System.out.println("\nSelect new account type (current: " + account.getAccountType() + "):");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.println("3. Fixed Deposit");
        System.out.println("0. Keep current");
        System.out.print("Enter choice: ");
        
        AccountType accountType = account.getAccountType();
        try {
            int typeChoice = Integer.parseInt(scanner.nextLine());
            switch (typeChoice) {
                case 1:
                    accountType = AccountType.SAVINGS;
                    break;
                case 2:
                    accountType = AccountType.CHECKING;
                    break;
                case 3:
                    accountType = AccountType.FIXED_DEPOSIT;
                    break;
                case 0:
                    // Keep current
                    break;
                default:
                    System.out.println("Invalid choice. Keeping current account type.");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Keeping current account type.");
        }
        
        System.out.print("Enter new Interest Rate (current: " + account.getInterestRate() + "%): ");
        BigDecimal interestRate = account.getInterestRate();
        String interestRateInput = scanner.nextLine();
        if (!interestRateInput.isEmpty()) {
            try {
                interestRate = new BigDecimal(interestRateInput);
                if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Interest rate cannot be negative. Keeping current rate.");
                    interestRate = account.getInterestRate();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid rate format. Keeping current rate.");
            }
        }
        
        boolean updated = controller.updateAccountDetails(accountId, accountType, interestRate);
        
        if (updated) {
            System.out.println("Account updated successfully.");
        } else {
            System.out.println("Failed to update account. Please try again.");
        }
        
    } catch (NumberFormatException e) {
        System.out.println("Invalid Account ID format.");
    }
}

private void closeAccount() {
    System.out.println("\n----- CLOSE ACCOUNT -----");
    
    System.out.print("Enter Account ID: ");
    try {
        int accountId = Integer.parseInt(scanner.nextLine());
        Account account = controller.getAccountById(accountId);
        
        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }
        
        if (account.getStatus() == AccountStatus.CLOSED) {
            System.out.println("This account is already closed.");
            return;
        }
        
        System.out.println("Account details:");
        displayAccountDetails(account);
        
        System.out.print("Are you sure you want to close this account? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            boolean closed = controller.closeAccount(accountId);
            
            if (closed) {
                System.out.println("Account closed successfully.");
            } else {
                System.out.println("Failed to close account. Please try again.");
            }
        } else {
            System.out.println("Account closure cancelled.");
        }
        
    } catch (NumberFormatException e) {
        System.out.println("Invalid Account ID format.");
    }
}

private void listAllAccounts() {
    System.out.println("\n----- ALL ACCOUNTS -----");
    
    List<Account> accounts = controller.getAllAccounts();
    
    if (accounts.isEmpty()) {
        System.out.println("No accounts found in the system.");
    } else {
        System.out.println("--------------------------------------------------");
        System.out.printf("%-5s | %-12s | %-10s | %-10s | %-8s\n", 
            "ID", "Account No.", "Type", "Balance", "Status");
        System.out.println("--------------------------------------------------");
        
        for (Account account : accounts) {
            System.out.printf("%-5d | %-12s | %-10s | %-10.2f | %-8s\n", 
                account.getAccountId(), 
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus()
            );
        }
    }
}

private void displayAccountDetails(Account account) {
    Customer customer = controller.getCustomerById(account.getCustomerId());
    String customerName = customer != null ? customer.getFirstName() + " " + customer.getLastName() : "Unknown";
    
    System.out.println("------------------------------------------");
    System.out.println("Account ID: " + account.getAccountId());
    System.out.println("Account Number: " + account.getAccountNumber());
    System.out.println("Account Type: " + account.getAccountType());
    System.out.println("Customer: " + customerName + " (ID: " + account.getCustomerId() + ")");
    System.out.println("Balance: $" + account.getBalance());
    System.out.println("Interest Rate: " + account.getInterestRate() + "%");
    System.out.println("Status: " + account.getStatus());
    System.out.println("Created At: " + DateUtil.formatTimestamp(account.getCreatedAt()));
    System.out.println("------------------------------------------");
}
}