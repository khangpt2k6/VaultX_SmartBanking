package com.bankmanagement.controller;

import com.bankmanagement.util.DatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "*")
public class DatabaseController {

    @Autowired
    private DatabaseManager databaseManager;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            List<Map<String, Object>> tables = databaseManager.getAllTables();
            
            Map<String, Integer> tableCounts = new HashMap<>();
            for (Map<String, Object> table : tables) {
                String tableName = (String) table.get("table_name");
                int rowCount = databaseManager.getTableRowCount(tableName);
                tableCounts.put(tableName, rowCount);
            }
            
            response.put("success", true);
            response.put("tables", tableCounts);
            response.put("message", "Database status retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving database status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/tables/{tableName}/info")
    public ResponseEntity<Map<String, Object>> getTableInfo(@PathVariable String tableName) {
        try {
            Map<String, Object> response = new HashMap<>();
            List<Map<String, Object>> tableInfo = databaseManager.getTableInfo(tableName);
            
            response.put("success", true);
            response.put("tableName", tableName);
            response.put("columns", tableInfo);
            response.put("message", "Table info retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving table info: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetDatabase() {
        try {
            databaseManager.resetDatabase();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Database reset successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error resetting database: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/sample-data")
    public ResponseEntity<Map<String, Object>> addSampleData() {
        try {
            databaseManager.addSampleData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sample data added successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error adding sample data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/execute-sql")
    public ResponseEntity<Map<String, Object>> executeSQL(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "SQL statement is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            databaseManager.executeSQL(sql);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "SQL executed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error executing SQL: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
