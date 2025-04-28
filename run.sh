#!/bin/bash

echo "Bank Management System"
echo "======================"

cd src
echo "Compiling the application..."
javac -cp ".:lib/mysql-connector-java-8.0.28.jar" main/bankmanagement/BankManagementApp.java

if [ $? -ne 0 ]; then
    echo "Compilation failed! Please check your setup."
    echo "Make sure you have:"
    echo "1. JDK installed and in your PATH"
    echo "2. The MySQL JDBC driver in the src/lib directory"
    echo "3. Correct package declarations in all files"
    exit 1
fi

echo "Running the application..."
java -cp ".:lib/mysql-connector-java-8.0.28.jar" main.bankmanagement.BankManagementApp

if [ $? -ne 0 ]; then
    echo "Application terminated with an error."
    echo "Please check your database connection and try again."
fi

echo "Press Enter to exit..."
read 