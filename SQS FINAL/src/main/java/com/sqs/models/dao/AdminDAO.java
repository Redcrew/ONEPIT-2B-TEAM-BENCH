package main.java.com.sqs.models.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.java.com.sqs.DatabaseConnection;
import main.java.com.sqs.models.Admin;

public class AdminDAO {
    // In-memory storage as fallback
    private static List<Admin> inMemoryAdmins = new ArrayList<>();
    private static int nextId = 1;
    
    static {
        // Add some sample admins for in-memory mode
        inMemoryAdmins.add(new Admin("admin", "admin123", "Ms. Reyes", "A001", "School Office"));
        inMemoryAdmins.add(new Admin("admin2", "admin123", "Mr. Cruz", "A002", "Registrar"));
        inMemoryAdmins.get(0).setId(1);
        inMemoryAdmins.get(1).setId(2);
        nextId = 3;
    }
    
    public boolean registerAdmin(Admin admin) {
        // First check if username already exists
        if (isUsernameTaken(admin.getUsername())) {
            return false;
        }
        
        // Check if admin ID already exists
        if (isAdminIdTaken(admin.getAdminId())) {
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
                
                String sql = "INSERT INTO admins (username, password, full_name, admin_id, department) VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                
                pstmt.setString(1, admin.getUsername());
                pstmt.setString(2, admin.getPassword());
                pstmt.setString(3, admin.getFullName());
                pstmt.setString(4, admin.getAdminId());
                pstmt.setString(5, admin.getDepartment() != null ? admin.getDepartment() : "General");
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        admin.setId(rs.getInt(1));
                    }
                    System.out.println("Admin registered in database: " + admin.getUsername());
                    return true;
                }
                
            } catch (SQLException e) {
                System.err.println("Error registering admin in database: " + e.getMessage());
                // Fall through to in-memory storage
            } finally {
                closeResources(rs, pstmt, null);
            }
        }
        
        // Fallback to in-memory storage
        admin.setId(nextId++);
        if (admin.getDepartment() == null || admin.getDepartment().isEmpty()) {
            admin.setDepartment("General");
        }
        inMemoryAdmins.add(admin);
        System.out.println("Admin registered in memory: " + admin.getUsername());
        return true;
    }
    
    public Admin login(String username, String password) {
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    throw new SQLException("Database connection is null");
                }
                
                String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
                pstmt = conn.prepareStatement(sql);
                
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setFullName(rs.getString("full_name"));
                    admin.setAdminId(rs.getString("admin_id"));
                    admin.setDepartment(rs.getString("department"));
                    return admin;
                }
                
            } catch (SQLException e) {
                System.err.println("Error during admin login: " + e.getMessage());
                // Fall through to in-memory storage
            } finally {
                closeResources(rs, pstmt, conn);
            }
        }
        
        // Fallback to in-memory storage
        System.out.println("Using in-memory storage for admin login...");
        for (Admin admin : inMemoryAdmins) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                System.out.println("Admin found in memory: " + username);
                return admin;
            }
        }
        
        System.out.println("Admin not found: " + username);
        return null;
    }
    
    private boolean isUsernameTaken(String username) {
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT COUNT(*) FROM admins WHERE username = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
            } catch (SQLException e) {
                System.err.println("Error checking admin username: " + e.getMessage());
            } finally {
                closeResources(rs, pstmt, conn);
            }
        }
        
        // Check in-memory storage
        for (Admin admin : inMemoryAdmins) {
            if (admin.getUsername().equals(username)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isAdminIdTaken(String adminId) {
        if (DatabaseConnection.isConnected()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT COUNT(*) FROM admins WHERE admin_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, adminId);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
            } catch (SQLException e) {
                System.err.println("Error checking admin ID: " + e.getMessage());
            } finally {
                closeResources(rs, pstmt, conn);
            }
        }
        
        // Check in-memory storage
        for (Admin admin : inMemoryAdmins) {
            if (admin.getAdminId().equals(adminId)) {
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
        
        // Note: We don't close the shared connection
    }
}