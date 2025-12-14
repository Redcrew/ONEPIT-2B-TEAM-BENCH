package main.java.com.sqs.models.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.java.com.sqs.DatabaseConnection;
import main.java.com.sqs.models.User;

public class UserDAO {
    // In-memory storage as fallback
    private static List<User> inMemoryUsers = new ArrayList<>();
    private static int nextId = 1;
    
    static {
        // Add some sample users for in-memory mode
        inMemoryUsers.add(new User("john.doe", "password123", "John Doe", "S12345"));
        inMemoryUsers.add(new User("jane.smith", "password123", "Jane Smith", "S12346"));
        inMemoryUsers.get(0).setId(1);
        inMemoryUsers.get(0).setRole("user");
        inMemoryUsers.get(1).setId(2);
        inMemoryUsers.get(1).setRole("user");
        nextId = 3;
    }
    
    public boolean registerUser(User user) {
        // First check if username already exists
        if (isUsernameTaken(user.getUsername())) {
            return false;
        }
        
        // Check if user ID already exists
        if (isUserIdTaken(user.getUserId())) {
            return false;
        }
        
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    throw new SQLException("Database connection is null");
                }
                
                String sql = "INSERT INTO users (username, password, full_name, user_id, role) VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getFullName());
                pstmt.setString(4, user.getUserId());
                pstmt.setString(5, "user");
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                    System.out.println("User registered in database: " + user.getUsername());
                    return true;
                }
                
            } catch (SQLException e) {
                System.err.println("Error registering user in database: " + e.getMessage());
                // Fall through to in-memory storage
            } finally {
                // Close resources properly
                closeResources(rs, pstmt, null);
            }
        }
        
        // Fallback to in-memory storage
        user.setId(nextId++);
        user.setRole("user");
        inMemoryUsers.add(user);
        System.out.println("User registered in memory: " + user.getUsername());
        return true;
    }
    
    public User login(String username, String password) {
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    throw new SQLException("Database connection is null");
                }
                
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                pstmt = conn.prepareStatement(sql);
                
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setUserId(rs.getString("user_id"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
                
            } catch (SQLException e) {
                System.err.println("Error during login: " + e.getMessage());
                // Fall through to in-memory storage
            } finally {
                // Close resources properly
                closeResources(rs, pstmt, conn);
            }
        }
        
        // Fallback to in-memory storage
        System.out.println("Using in-memory storage for login...");
        for (User user : inMemoryUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("User found in memory: " + username);
                return user;
            }
        }
        
        System.out.println("User not found: " + username);
        return null;
    }
    
    private boolean isUsernameTaken(String username) {
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
            } catch (SQLException e) {
                System.err.println("Error checking username: " + e.getMessage());
            } finally {
                closeResources(rs, pstmt, conn);
            }
        }
        
        // Check in-memory storage
        for (User user : inMemoryUsers) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isUserIdTaken(String userId) {
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, userId);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
            } catch (SQLException e) {
                System.err.println("Error checking user ID: " + e.getMessage());
            } finally {
                closeResources(rs, pstmt, conn);
            }
        }
        
        // Check in-memory storage
        for (User user : inMemoryUsers) {
            if (user.getUserId().equals(userId)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.err.println("Error closing ResultSet: " + e.getMessage());
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Error closing Statement: " + e.getMessage());
        }
        
        // Note: We don't close the connection here because we're using a shared connection
        // Connection is managed by DatabaseConnection class
    }
}