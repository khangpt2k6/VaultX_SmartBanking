package main.bankmanagement.dao;

import main.bankmanagement.model.Transaction;
import main.bankmanagement.model.Transaction.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection connection;
    private AccountDAO accountDAO;
    
    public TransactionDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.accountDAO = new AccountDAO();
    }
    
    // Create a new transaction
    public int createTransaction(Transaction transaction) {
        connection = DatabaseConnection.getConnection();
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, description, destination_account_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTransactionType().toString());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setString(4, transaction.getDescription());
            
            if (transaction.getDestinationAccountId() != null) {
                stmt.setInt(5, transaction.getDestinationAccountId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    // Process a deposit transaction
    public boolean processDeposit(int accountId, BigDecimal amount, String description) {
        connection = DatabaseConnection.getConnection();
        try {
            connection.setAutoCommit(false);
            
            // Get current balance
            BigDecimal currentBalance = accountDAO.getAccountById(accountId).getBalance();
            BigDecimal newBalance = currentBalance.add(amount);
            
            // Update account balance
            boolean balanceUpdated = accountDAO.updateBalance(accountId, newBalance);
            if (!balanceUpdated) {
                connection.rollback();
                return false;
            }
            
            // Create transaction record
            Transaction transaction = new Transaction(accountId, TransactionType.DEPOSIT, amount, description, null);
            int transactionId = createTransaction(transaction);
            
            if (transactionId > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Process a withdrawal transaction
    public boolean processWithdrawal(int accountId, BigDecimal amount, String description) {
        connection = DatabaseConnection.getConnection();
        try {
            connection.setAutoCommit(false);
            
            // Get current balance
            BigDecimal currentBalance = accountDAO.getAccountById(accountId).getBalance();
            
            // Check if sufficient funds
            if (currentBalance.compareTo(amount) < 0) {
                connection.rollback();
                return false; // Insufficient funds
            }
            
            BigDecimal newBalance = currentBalance.subtract(amount);
            
            // Update account balance
            boolean balanceUpdated = accountDAO.updateBalance(accountId, newBalance);
            if (!balanceUpdated) {
                connection.rollback();
                return false;
            }
            
            // Create transaction record
            Transaction transaction = new Transaction(accountId, TransactionType.WITHDRAWAL, amount, description, null);
            int transactionId = createTransaction(transaction);
            
            if (transactionId > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Process a transfer transaction
    public boolean processTransfer(int sourceAccountId, int destinationAccountId, BigDecimal amount, String description) {
        connection = DatabaseConnection.getConnection();
        try {
            connection.setAutoCommit(false);
            
            // Get source account balance
            BigDecimal sourceBalance = accountDAO.getAccountById(sourceAccountId).getBalance();
            
            // Check if sufficient funds
            if (sourceBalance.compareTo(amount) < 0) {
                connection.rollback();
                return false; // Insufficient funds
            }
            
            // Update source account balance
            BigDecimal newSourceBalance = sourceBalance.subtract(amount);
            boolean sourceUpdated = accountDAO.updateBalance(sourceAccountId, newSourceBalance);
            
            if (!sourceUpdated) {
                connection.rollback();
                return false;
            }
            
            // Get destination account balance
            BigDecimal destBalance = accountDAO.getAccountById(destinationAccountId).getBalance();
            BigDecimal newDestBalance = destBalance.add(amount);
            
            // Update destination account balance
            boolean destUpdated = accountDAO.updateBalance(destinationAccountId, newDestBalance);
            
            if (!destUpdated) {
                connection.rollback();
                return false;
            }
            
            // Create transaction record for the source account
            Transaction sourceTransaction = new Transaction(sourceAccountId, TransactionType.TRANSFER, amount, description, destinationAccountId);
            int sourceTransId = createTransaction(sourceTransaction);
            
            if (sourceTransId > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get transaction by ID
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all transactions for an account
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(int accountId, Timestamp startDate, Timestamp endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? AND transaction_date BETWEEN ? AND ? " +
                     "ORDER BY transaction_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setTimestamp(2, startDate);
            stmt.setTimestamp(3, endDate);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Helper method to extract Transaction from ResultSet
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Integer destAccountId = rs.getInt("destination_account_id");
        if (rs.wasNull()) {
            destAccountId = null;
        }
        
        return new Transaction(
            rs.getInt("transaction_id"),
            rs.getInt("account_id"),
            TransactionType.valueOf(rs.getString("transaction_type")),
            rs.getBigDecimal("amount"),
            rs.getTimestamp("transaction_date"),
            rs.getString("description"),
            destAccountId
        );
    }
}