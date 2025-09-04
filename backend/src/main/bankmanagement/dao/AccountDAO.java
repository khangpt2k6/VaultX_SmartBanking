package main.bankmanagement.dao;

import main.bankmanagement.model.Account;
import main.bankmanagement.model.Account.AccountStatus;
import main.bankmanagement.model.Account.AccountType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection connection;
    
    public AccountDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // Create a new account
    public int createAccount(Account account) {
        String sql = "INSERT INTO accounts (customer_id, account_number, account_type, balance, interest_rate, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, account.getCustomerId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setString(3, account.getAccountType().toString());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.setBigDecimal(5, account.getInterestRate());
            stmt.setString(6, account.getStatus().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    // Get account by ID
    public Account getAccountById(int accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get account by account number
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all accounts for a customer
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    // Get all accounts
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    // Update account details
    public boolean updateAccount(Account account) {
        String sql = "UPDATE accounts SET account_type = ?, interest_rate = ?, status = ? " +
                     "WHERE account_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountType().toString());
            stmt.setBigDecimal(2, account.getInterestRate());
            stmt.setString(3, account.getStatus().toString());
            stmt.setInt(4, account.getAccountId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update account balance
    public boolean updateBalance(int accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Close an account (set status to CLOSED)
    public boolean closeAccount(int accountId) {
        String sql = "UPDATE accounts SET status = 'CLOSED' WHERE account_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Generate a unique account number
    public String generateAccountNumber() {
        String sql = "SELECT MAX(CAST(account_number AS UNSIGNED)) as max_num FROM accounts";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String maxNum = rs.getString("max_num");
                if (maxNum == null) {
                    return "1000001"; // Starting account number
                } else {
                    long nextNum = Long.parseLong(maxNum) + 1;
                    return String.valueOf(nextNum);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "1000001"; // Default starting account number
    }
    
    // Helper method to extract Account from ResultSet
    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("account_id"),
            rs.getInt("customer_id"),
            rs.getString("account_number"),
            AccountType.valueOf(rs.getString("account_type")),
            rs.getBigDecimal("balance"),
            rs.getBigDecimal("interest_rate"),
            rs.getTimestamp("created_at"),
            AccountStatus.valueOf(rs.getString("status"))
        );
    }
}