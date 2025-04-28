package main.bankmanagement.view;

import main.bankmanagement.controller.BankController;
import main.bankmanagement.model.Customer;
import main.bankmanagement.util.DateUtil;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class CustomerView {
    private Scanner scanner;
    private BankController controller;
    
    public CustomerView(Scanner scanner, BankController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }
    
    public void displayCustomerMenu() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n===== CUSTOMER MANAGEMENT =====");
            System.out.println("1. Add New Customer");
            System.out.println("2. View Customer Details");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Search Customers");
            System.out.println("6. List All Customers");
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
                    addNewCustomer();
                    break;
                case 2:
                    viewCustomerDetails();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 5:
                    searchCustomers();
                    break;
                case 6:
                    listAllCustomers();
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
    
    private void addNewCustomer() {
        System.out.println("\n----- ADD NEW CUSTOMER -----");
        
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
        String dobStr = scanner.nextLine();
        
        Date dob = null;
        if (!dobStr.isEmpty()) {
            dob = DateUtil.parseDate(dobStr);
            if (dob == null) {
                System.out.println("Invalid date format. Customer creation failed.");
                return;
            }
        }
        
        int customerId = controller.createCustomer(firstName, lastName, address, phone, email, dob);
        
        if (customerId > 0) {
            System.out.println("Customer created successfully with ID: " + customerId);
        } else {
            System.out.println("Failed to create customer. Please try again.");
        }
    }
    
    private void viewCustomerDetails() {
        System.out.println("\n----- VIEW CUSTOMER DETAILS -----");
        
        System.out.print("Enter Customer ID: ");
        try {
            int customerId = Integer.parseInt(scanner.nextLine());
            Customer customer = controller.getCustomerById(customerId);
            
            if (customer != null) {
                displayCustomerDetails(customer);
            } else {
                System.out.println("Customer not found with ID: " + customerId);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Customer ID format.");
        }
    }
    
    private void updateCustomer() {
        System.out.println("\n----- UPDATE CUSTOMER -----");
        
        System.out.print("Enter Customer ID: ");
        try {
            int customerId = Integer.parseInt(scanner.nextLine());
            Customer customer = controller.getCustomerById(customerId);
            
            if (customer == null) {
                System.out.println("Customer not found with ID: " + customerId);
                return;
            }
            
            System.out.println("Current details:");
            displayCustomerDetails(customer);
            
            System.out.println("\nEnter new details (leave blank to keep current value):");
            
            System.out.print("First Name [" + customer.getFirstName() + "]: ");
            String firstName = scanner.nextLine();
            if (firstName.isEmpty()) {
                firstName = customer.getFirstName();
            }
            
            System.out.print("Last Name [" + customer.getLastName() + "]: ");
            String lastName = scanner.nextLine();
            if (lastName.isEmpty()) {
                lastName = customer.getLastName();
            }
            
            System.out.print("Address [" + customer.getAddress() + "]: ");
            String address = scanner.nextLine();
            if (address.isEmpty()) {
                address = customer.getAddress();
            }
            
            System.out.print("Phone [" + customer.getPhone() + "]: ");
            String phone = scanner.nextLine();
            if (phone.isEmpty()) {
                phone = customer.getPhone();
            }
            
            System.out.print("Email [" + customer.getEmail() + "]: ");
            String email = scanner.nextLine();
            if (email.isEmpty()) {
                email = customer.getEmail();
            }
            
            String currentDob = DateUtil.formatDate(customer.getDateOfBirth());
            System.out.print("Date of Birth [" + currentDob + "]: ");
            String dobStr = scanner.nextLine();
            Date dob = customer.getDateOfBirth();
            if (!dobStr.isEmpty()) {
                dob = DateUtil.parseDate(dobStr);
                if (dob == null) {
                    System.out.println("Invalid date format. Using current value.");
                    dob = customer.getDateOfBirth();
                }
            }
            
            boolean updated = controller.updateCustomer(customerId, firstName, lastName, address, phone, email, dob);
            
            if (updated) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("Failed to update customer. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid Customer ID format.");
        }
    }
    
    private void deleteCustomer() {
        System.out.println("\n----- DELETE CUSTOMER -----");
        
        System.out.print("Enter Customer ID: ");
        try {
            int customerId = Integer.parseInt(scanner.nextLine());
            Customer customer = controller.getCustomerById(customerId);
            
            if (customer == null) {
                System.out.println("Customer not found with ID: " + customerId);
                return;
            }
            
            System.out.println("Customer details:");
            displayCustomerDetails(customer);
            
            System.out.print("Are you sure you want to delete this customer? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                boolean deleted = controller.deleteCustomer(customerId);
                
                if (deleted) {
                    System.out.println("Customer deleted successfully.");
                } else {
                    System.out.println("Failed to delete customer. The customer may have active accounts.");
                }
            } else {
                System.out.println("Customer deletion cancelled.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid Customer ID format.");
        }
    }
    
    private void searchCustomers() {
        System.out.println("\n----- SEARCH CUSTOMERS -----");
        
        System.out.print("Enter name to search: ");
        String searchName = scanner.nextLine();
        
        List<Customer> customers = controller.searchCustomersByName(searchName);
        
        if (customers.isEmpty()) {
            System.out.println("No customers found matching: " + searchName);
        } else {
            System.out.println("\nSearch Results:");
            System.out.println("-------------------------------------------------------------");
            System.out.printf("%-5s | %-15s | %-15s | %-12s\n", "ID", "First Name", "Last Name", "Phone");
            System.out.println("-------------------------------------------------------------");
            
            for (Customer customer : customers) {
                System.out.printf("%-5d | %-15s | %-15s | %-12s\n", 
                    customer.getCustomerId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPhone()
                );
            }
        }
    }
    
    private void listAllCustomers() {
        System.out.println("\n----- ALL CUSTOMERS -----");
        
        List<Customer> customers = controller.getAllCustomers();
        
        if (customers.isEmpty()) {
            System.out.println("No customers found in the system.");
        } else {
            System.out.println("-------------------------------------------------------------");
            System.out.printf("%-5s | %-15s | %-15s | %-12s\n", "ID", "First Name", "Last Name", "Phone");
            System.out.println("-------------------------------------------------------------");
            
            for (Customer customer : customers) {
                System.out.printf("%-5d | %-15s | %-15s | %-12s\n", 
                    customer.getCustomerId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPhone()
                );
            }
        }
    }
    
    private void displayCustomerDetails(Customer customer) {
        System.out.println("------------------------------------------");
        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Name: " + customer.getFirstName() + " " + customer.getLastName());
        System.out.println("Address: " + customer.getAddress());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Date of Birth: " + DateUtil.formatDate(customer.getDateOfBirth()));
        System.out.println("Created At: " + DateUtil.formatTimestamp(customer.getCreatedAt()));
        System.out.println("------------------------------------------");
    }
}