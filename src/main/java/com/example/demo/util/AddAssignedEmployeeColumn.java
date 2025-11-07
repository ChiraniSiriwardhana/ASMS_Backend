package com.example.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AddAssignedEmployeeColumn {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/demo";
        String user = "postgres";
        String password = "Sana2001";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("Connected to PostgreSQL database!");

            // Add assigned_employee_id column if it doesn't exist
            String addColumnSQL = "ALTER TABLE appointments ADD COLUMN IF NOT EXISTS assigned_employee_id BIGINT";
            stmt.execute(addColumnSQL);
            System.out.println("✅ Column 'assigned_employee_id' added successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
