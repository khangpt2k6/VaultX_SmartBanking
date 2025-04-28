-- bank_db.sql
CREATE DATABASE IF NOT EXISTS bank_management;
USE bank_management;

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(50),
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type ENUM('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT') NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    interest_rate DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'INTEREST_CREDIT') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(100),
    destination_account_id INT,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id)
);

-- Insert some test data
INSERT INTO customers (first_name, last_name, address, phone, email, date_of_birth)
VALUES 
('John', 'Doe', '123 Main St, Anytown', '555-123-4567', 'john.doe@email.com', '1985-07-15'),
('Jane', 'Smith', '456 Oak Ave, Somecity', '555-987-6543', 'jane.smith@email.com', '1990-03-22');

-- Create accounts for the customers
INSERT INTO accounts (customer_id, account_number, account_type, balance, interest_rate)
VALUES 
(1, '1000001', 'SAVINGS', 5000.00, 2.50),
(1, '1000002', 'CHECKING', 2500.00, 0.50),
(2, '1000003', 'SAVINGS', 7500.00, 2.50);

-- Add some transactions
INSERT INTO transactions (account_id, transaction_type, amount, description)
VALUES 
(1, 'DEPOSIT', 1000.00, 'Initial deposit'),
(2, 'DEPOSIT', 500.00, 'Initial deposit'),
(3, 'DEPOSIT', 2000.00, 'Initial deposit');