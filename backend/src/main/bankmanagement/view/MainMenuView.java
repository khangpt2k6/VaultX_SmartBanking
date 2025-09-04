package main.bankmanagement.view;

import main.bankmanagement.controller.BankController;
import java.util.Scanner;

public class MainMenuView {
    private Scanner scanner;
    private BankController controller;
    private CustomerView customerView;
    private AccountView accountView;
    private TransactionView transactionView;
    
    public MainMenuView() {
        this.scanner = new Scanner(System.in);
        this.controller = new BankController();
        this.customerView = new CustomerView(scanner, controller);
        this.accountView = new AccountView(scanner, controller);
        this.transactionView = new TransactionView(scanner, controller);
    }
    
    public void displayMainMenu() {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
            System.out.println("1. Customer Management");
            System.out.println("2. Account Management");
            System.out.println("3. Transaction Management");
            System.out.println("0. Exit");
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
                    customerView.displayCustomerMenu();
                    break;
                case 2:
                    accountView.displayAccountMenu();
                    break;
                case 3:
                    transactionView.displayTransactionMenu();
                    break;
                case 0:
                    exit = true;
                    System.out.println("Thank you for using Bank Management System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
    
    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}