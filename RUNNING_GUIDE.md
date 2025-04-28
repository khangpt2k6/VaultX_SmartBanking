# Bank Management System - Running Guide

## Prerequisites

1. Java Development Kit (JDK) 8 or higher
2. MySQL Server 5.7 or higher
3. MySQL Connector/J (JDBC driver)

## Setup Instructions

### Step 1: Database Setup

1. Install MySQL if you haven't already
2. Log in to MySQL and create a database:
   ```sql
   CREATE DATABASE bank_management;
   ```
3. Run the SQL script to create tables and sample data:
   - Navigate to the project's root directory
   - Run the following command (adjust username and password as needed):
   ```
   mysql -u root -p bank_management < src/main/resources/bank_db.sql
   ```
   - Alternatively, you can copy and paste the script content from `src/main/resources/bank_db.sql` into a MySQL client

### Step 2: JDBC Driver Setup

1. Download MySQL Connector/J (JDBC driver):
   - Visit: https://dev.mysql.com/downloads/connector/j/
   - Download MySQL Connector/J 8.0.28 or later
   - Extract the downloaded archive
   - Locate the `mysql-connector-java-8.0.28.jar` file

2. Place the JAR file in the `src/lib` directory of the project

### Step 3: Configure Database Connection

1. Edit `src/main/bankmanagement/dao/DatabaseConnection.java`
2. Update the following fields with your MySQL credentials:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/bank_management";
   private static final String USER = "root"; // Change to your MySQL username
   private static final String PASSWORD = "password"; // Change to your MySQL password
   ```

### Step 4: Compile and Run

1. Open a command prompt/terminal
2. Navigate to the project's src directory:
   ```
   cd path/to/BankManagementSystem/src
   ```
3. Compile the application:
   ```
   javac -cp ".;lib/mysql-connector-java-8.0.28.jar" main/bankmanagement/BankManagementApp.java
   ```
   (On Linux/Mac, use `:` instead of `;` in the classpath: `-cp .:lib/mysql-connector-java-8.0.28.jar`)

4. Run the application:
   ```
   java -cp ".;lib/mysql-connector-java-8.0.28.jar" main.bankmanagement.BankManagementApp
   ```
   (On Linux/Mac, use `:` instead of `;` in the classpath: `-cp .:lib/mysql-connector-java-8.0.28.jar`)

## Using the Application

The application provides a console-based interface with the following menus:

1. **Customer Management**
   - Add new customers
   - View customer details
   - Update customer information
   - Delete customers
   - Search for customers
   - List all customers

2. **Account Management**
   - Create accounts (Savings, Checking, Fixed Deposit)
   - View account details
   - Update account information
   - Close accounts
   - List customer accounts

3. **Transaction Management**
   - Deposit funds
   - Withdraw funds
   - Transfer funds between accounts
   - View transaction history
   - View account statements by date range

Follow the on-screen prompts to navigate through the application.

## Sample Data

The database is pre-populated with:
- 2 customers
- 3 accounts
- Initial transactions

You can use these to test the functionality or create new entries. 