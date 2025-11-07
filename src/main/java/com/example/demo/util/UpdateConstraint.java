package com.example.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UpdateConstraint {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/demo";
        String user = "postgres";
        String password = "Sana2001";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to database successfully!");
            
            // Drop the old constraint
            String dropConstraint = "ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check";
            stmt.execute(dropConstraint);
            System.out.println("Old constraint dropped successfully!");
            
            // Add the new constraint with all status values
            String addConstraint = "ALTER TABLE appointments ADD CONSTRAINT appointments_status_check " +
                    "CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_SERVICE', 'READY', 'COMPLETED', 'CANCELLED'))";
            stmt.execute(addConstraint);
            System.out.println("New constraint added successfully!");
            
            // Update any existing IN_PROGRESS statuses to IN_SERVICE
            String updateStatuses = "UPDATE appointments SET status = 'IN_SERVICE' WHERE status = 'IN_PROGRESS'";
            int updated = stmt.executeUpdate(updateStatuses);
            System.out.println("Updated " + updated + " appointments from IN_PROGRESS to IN_SERVICE");
            
            System.out.println("\n✅ Database constraint updated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error updating constraint: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
