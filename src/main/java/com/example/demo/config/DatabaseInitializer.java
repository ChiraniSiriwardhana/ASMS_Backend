package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Run FIRST, before TestDataInitializer
public class DatabaseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) {
        try {
            logger.info("=== Starting Database Schema Migration ===");
            
            // Check if both 'name' and 'full_name' columns exist
            String checkNameColumnSql = "SELECT column_name FROM information_schema.columns " +
                                       "WHERE table_name = 'customer_questions' AND column_name = 'name'";
            String checkFullNameColumnSql = "SELECT column_name FROM information_schema.columns " +
                                           "WHERE table_name = 'customer_questions' AND column_name = 'full_name'";
            
            boolean nameColumnExists = !jdbcTemplate.queryForList(checkNameColumnSql, String.class).isEmpty();
            boolean fullNameColumnExists = !jdbcTemplate.queryForList(checkFullNameColumnSql, String.class).isEmpty();
            
            logger.info("Column 'name' exists: " + nameColumnExists);
            logger.info("Column 'full_name' exists: " + fullNameColumnExists);
            
            if (nameColumnExists && fullNameColumnExists) {
                // Both columns exist - copy data from 'name' to 'full_name' if needed, then drop 'name'
                logger.info("Both 'name' and 'full_name' columns exist. Migrating data...");
                jdbcTemplate.execute("UPDATE customer_questions SET full_name = name WHERE full_name IS NULL");
                logger.info("Data migrated. Dropping 'name' column...");
                jdbcTemplate.execute("ALTER TABLE customer_questions DROP COLUMN name");
                logger.info("Successfully dropped 'name' column");
            } else if (nameColumnExists && !fullNameColumnExists) {
                // Only 'name' exists - rename it to 'full_name'
                logger.info("Only 'name' column exists. Renaming to 'full_name'...");
                jdbcTemplate.execute("ALTER TABLE customer_questions RENAME COLUMN name TO full_name");
                logger.info("Successfully renamed 'name' to 'full_name'");
            } else {
                logger.info("Schema is already up to date - only 'full_name' column exists");
            }
            
            logger.info("=== Database Schema Migration Complete ===");
        } catch (Exception e) {
            logger.error("Error during database initialization: " + e.getMessage(), e);
            // Don't throw exception to prevent application startup failure
        }
    }
}
