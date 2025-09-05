package com.bankmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Check if tables exist, if not create them
        if (!tablesExist()) {
            System.out.println("🔄 Initializing database tables...");
            initializeDatabase();
            System.out.println("✅ Database initialization completed!");
        } else {
            System.out.println("✅ Database tables already exist, skipping initialization.");
        }
    }

    private boolean tablesExist() {
        try {
            String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name IN ('customers', 'accounts', 'transactions')";
            Integer count = jdbcTemplate.queryForObject(query, Integer.class);
            return count != null && count >= 3;
        } catch (Exception e) {
            return false;
        }
    }

    private void initializeDatabase() {
        try {
            // Read the SQL file from resources
            ClassPathResource resource = new ClassPathResource("database-schema.sql");
            String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            // Split by semicolon and execute each statement
            String[] statements = sql.split(";");
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(statement);
                    } catch (Exception e) {
                        System.err.println("Warning: Failed to execute statement: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading database schema file: " + e.getMessage());
        }
    }
}
