// BankManagementApp.java
package main.bankmanagement;

import main.bankmanagement.dao.DatabaseConnection;
import main.bankmanagement.view.MainMenuView;

public class BankManagementApp {
    public static void main(String[] args) {
        try {
            // Test database connection
            if (DatabaseConnection.getConnection() == null) {
                System.err.println("Failed to connect to database. Please check your database settings.");
                return;
            }
            
            System.out.println("Bank Management System");
            System.out.println("======================");
            
            MainMenuView mainMenu = new MainMenuView();
            mainMenu.displayMainMenu();
            mainMenu.closeScanner();
            
            // Close the database connection when done
            DatabaseConnection.closeConnection();
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}