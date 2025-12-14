package main.java.com.sqs;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/school_queue_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    public static void init() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver loaded successfully!");
            
            // Try to connect to MySQL
            System.out.println("Attempting to connect to database: " + DB_URL);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Test the connection
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connected successfully!");
                
                // Create tables if not exist
                createTables();
                
                // Set connection properties
                connection.setAutoCommit(true);
                System.out.println("Database initialization complete!");
            } else {
                System.err.println("Database connection failed!");
                connection = null;
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
            System.out.println("Please add mysql-connector-java.jar to classpath");
            connection = null;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.out.println("Using in-memory mode. Data will not persist.");
            connection = null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            connection = null;
        }
    }
    
    private static void createTables() {
        if (connection == null) return;
        
        String[] createTables = {
            """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                user_id VARCHAR(20) UNIQUE NOT NULL,
                role VARCHAR(20) DEFAULT 'user',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS admins (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                admin_id VARCHAR(20) UNIQUE NOT NULL,
                department VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS tickets (
                id INT AUTO_INCREMENT PRIMARY KEY,
                ticket_number VARCHAR(20) UNIQUE NOT NULL,
                user_id VARCHAR(20),
                user_name VARCHAR(100) NOT NULL,
                purpose VARCHAR(100) NOT NULL,
                service_type VARCHAR(50) NOT NULL,
                status VARCHAR(20) DEFAULT 'Waiting',
                request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                start_time TIMESTAMP NULL,
                completion_time TIMESTAMP NULL
            )
            """
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String sql : createTables) {
                stmt.execute(sql);
            }
            System.out.println("Tables created/verified successfully!");
            
            // Insert sample data if tables are empty
            insertSampleData();
            
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            // Don't set connection to null - continue with in-memory fallback
        }
    }
    
    private static void insertSampleData() {
        try {
            // Check if users table is empty
            ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            if (rs.getInt(1) == 0) {
                String insertUsers = """
                    INSERT INTO users (username, password, full_name, user_id) VALUES
                    ('john.doe', 'password123', 'John Doe', 'S12345'),
                    ('jane.smith', 'password123', 'Jane Smith', 'S12346')
                """;
                connection.createStatement().executeUpdate(insertUsers);
                System.out.println("Sample users inserted.");
            }
            
            // Check if admins table is empty
            rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM admins");
            rs.next();
            if (rs.getInt(1) == 0) {
                String insertAdmins = """
                    INSERT INTO admins (username, password, full_name, admin_id, department) VALUES
                    ('admin', 'admin123', 'Ms. Reyes', 'A001', 'School Office'),
                    ('admin2', 'admin123', 'Mr. Cruz', 'A002', 'Registrar')
                """;
                connection.createStatement().executeUpdate(insertAdmins);
                System.out.println("Sample admins inserted.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Reconnecting to database...");
                init(); // Try to reconnect
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    // Test database connection
    public static boolean testConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Testing new connection...");
                Connection testConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                testConn.close();
                return true;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}