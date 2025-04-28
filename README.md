# Bank Management System

A simple Java application for managing bank operations such as customers, accounts, and transactions.

## Features

- Customer Management:
  - Create, view, update, and delete customers
  - Search customers by name
  - List all customers

- Account Management:
  - Create different types of accounts (Savings, Checking, Fixed Deposit)
  - View account details
  - Update account details
  - Close accounts
  - List accounts by customer

- Transaction Management:
  - Deposit funds
  - Withdraw funds
  - Transfer funds between accounts
  - View transaction history
  - View account statements by date range

## System Requirements

- Java 8 or higher
- MySQL 5.7 or higher

## Installation and Setup

1. Clone the repository or download the source code
2. Import the project into your IDE
3. Set up the MySQL database:
   - Create a database named `bank_management`
   - Run the SQL script `src/main/resources/database/bank_db.sql` to create tables and sample data
4. Edit the database connection settings in `src/main/java/com/bankmanagement/dao/DatabaseConnection.java` with your MySQL credentials
5. Add MySQL JDBC driver jar to your classpath (from the lib folder)
6. Compile and run the application

## Project Structure

- `src/main/java/com/bankmanagement/model/`: Contains the model classes
- `src/main/java/com/bankmanagement/dao/`: Contains the data access objects
- `src/main/java/com/bankmanagement/controller/`: Contains the controller classes
- `src/main/java/com/bankmanagement/view/`: Contains the view classes
- `src/main/java/com/bankmanagement/util/`: Contains utility classes
- `src/main/resources/database/`: Contains database scripts

## Running the Application

Run the `BankManagementApp.java` class to start the application.

## Usage

The application provides a console-based interface with menus for managing bank operations:

1. Customer Management
2. Account Management
3. Transaction Management

Follow the on-screen prompts to navigate through the application.

## Sample Accounts

The database script creates the following sample accounts:

- Savings Account: 1000001
- Checking Account: 1000002
- Savings Account: 1000003