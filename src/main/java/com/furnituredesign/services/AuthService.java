package com.furnituredesign.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private static final String DB_URL = "jdbc:sqlite:furniture_designer.db";

    public AuthService() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            security_question TEXT,
                            security_answer TEXT
                        )
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            }

            // Add a default admin account if none exists
            sql = "INSERT OR IGNORE INTO users (username, password, security_question, security_answer) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "admin");
                stmt.setString(2, "admin"); // In production, use proper password hashing
                stmt.setString(3, "What is your favorite furniture piece?");
                stmt.setString(4, "chair");
                stmt.execute();
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a user exists in the database
     * @param username The username to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("User check error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get the security question for a user
     * @param username The username to get the security question for
     * @return The security question or null if not found
     */
    public String getSecurityQuestion(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT security_question FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("security_question");
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting security question: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verify the security answer for a user
     * @param username The username to verify the answer for
     * @param answer The answer to verify
     * @return true if the answer is correct, false otherwise
     */
    public boolean verifySecurityAnswer(String username, String answer) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT security_answer FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedAnswer = rs.getString("security_answer");
                        return storedAnswer != null && storedAnswer.equalsIgnoreCase(answer);
                    }
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying security answer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reset a user's password
     * @param username The username to reset the password for
     * @param newPassword The new password
     * @return true if the password was reset, false otherwise
     */
    public boolean resetPassword(String username, String newPassword) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPassword);
                stmt.setString(2, username);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
