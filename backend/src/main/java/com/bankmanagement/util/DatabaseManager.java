package com.bankmanagement.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Execute custom SQL commands
     */
    public void executeSQL(String sql) {
        try {
            jdbcTemplate.execute(sql);
            System.out.println("✅ SQL executed successfully");
        } catch (Exception e) {
            System.err.println("❌ Error executing SQL: " + e.getMessage());
        }
    }

    /**
     * Execute SQL from a file
     */
    public void executeSQLFromFile(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            executeSQL(sql);
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        }
    }

    /**
     * Get table information
     */
    public List<Map<String, Object>> getTableInfo(String tableName) {
        String sql = "SELECT column_name, data_type, is_nullable, column_default " +
                    "FROM information_schema.columns " +
                    "WHERE table_name = ? AND table_schema = 'public' " +
                    "ORDER BY ordinal_position";
        return jdbcTemplate.queryForList(sql, tableName);
    }

    /**
     * Get all tables
     */
    public List<Map<String, Object>> getAllTables() {
        String sql = "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = 'public' " +
                    "ORDER BY table_name";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Get table row count
     */
    public int getTableRowCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * Reset database (drop and recreate all tables)
     */
    public void resetDatabase() {
        System.out.println("🔄 Resetting database...");
        
        // Drop tables in reverse order
        executeSQL("DROP TABLE IF EXISTS transactions CASCADE");
        executeSQL("DROP TABLE IF EXISTS accounts CASCADE");
        executeSQL("DROP TABLE IF EXISTS customers CASCADE");
        
        // Recreate tables
        executeSQLFromFile("database-schema.sql");
        
        System.out.println("✅ Database reset completed");
    }

    /**
     * Add sample data - DISABLED (no mock data)
     */
    public void addSampleData() {
        System.out.println("ℹ️ Sample data feature is disabled - no mock data will be added");
    }

    /**
     * Show database status
     */
    public void showDatabaseStatus() {
        System.out.println("\n📊 Database Status:");
        System.out.println("==================");
        
        List<Map<String, Object>> tables = getAllTables();
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            int rowCount = getTableRowCount(tableName);
            System.out.println(tableName + ": " + rowCount + " rows");
        }
    }
}
