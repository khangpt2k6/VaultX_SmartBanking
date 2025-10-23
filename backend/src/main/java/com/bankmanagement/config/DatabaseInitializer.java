package com.bankmanagement.config;

import com.bankmanagement.model.Role;
import com.bankmanagement.model.User;
import com.bankmanagement.repository.RoleRepository;
import com.bankmanagement.repository.UserRepository;
import com.bankmanagement.service.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private MarketDataService marketDataService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        
        // Initialize roles and admin user
        try {
            System.out.println("🔄 Initializing roles and admin user...");
            initializeRolesAndAdmin();
            System.out.println("✅ Roles and admin user initialized!");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to initialize roles and admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize trading assets
        try {
            System.out.println("🔄 Initializing trading assets...");
            marketDataService.initializeDefaultAssets();
            System.out.println("✅ Trading assets initialized!");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to initialize trading assets: " + e.getMessage());
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
    
    private void initializeRolesAndAdmin() {
        try {
            // Create roles if they don't exist
            createRoleIfNotExists(Role.RoleName.ROLE_ADMIN, "Administrator - Full access to all features");
            createRoleIfNotExists(Role.RoleName.ROLE_MANAGER, "Manager - Can manage accounts and transactions");
            createRoleIfNotExists(Role.RoleName.ROLE_TELLER, "Teller - Can process transactions");
            createRoleIfNotExists(Role.RoleName.ROLE_CUSTOMER, "Customer - Can view their own data");
            
            // Create admin user if it doesn't exist
            Optional<User> adminUser = userRepository.findByUsername("admin");
            if (adminUser.isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@vaultx.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Password: admin123
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setIsEnabled(true);
                admin.setIsAccountNonExpired(true);
                admin.setIsAccountNonLocked(true);
                admin.setIsCredentialsNonExpired(true);
                
                // Assign ROLE_ADMIN to admin user
                Optional<Role> adminRole = roleRepository.findByRoleName(Role.RoleName.ROLE_ADMIN);
                if (adminRole.isPresent()) {
                    admin.addRole(adminRole.get());
                }
                
                userRepository.save(admin);
                System.out.println("✅ Admin user created: username='admin', password='admin123'");
            } else {
                System.out.println("✅ Admin user already exists");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error initializing roles and admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createRoleIfNotExists(Role.RoleName roleName, String description) {
        try {
            Optional<Role> role = roleRepository.findByRoleName(roleName);
            if (role.isEmpty()) {
                Role newRole = new Role(roleName, description);
                roleRepository.save(newRole);
                System.out.println("✅ Created role: " + roleName);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error creating role: " + e.getMessage());
        }
    }
}
