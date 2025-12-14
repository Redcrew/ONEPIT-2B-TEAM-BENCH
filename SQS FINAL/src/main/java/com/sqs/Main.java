package main.java.com.sqs;

import javax.swing.SwingUtilities;

import main.java.com.sqs.models.dao.ui.LoginGUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== School Queue Management System ===");
        System.out.println("Initializing database connection...");
        
        // Initialize database connection
        DatabaseConnection.init();
        
        // Test if database is available
        if (!DatabaseConnection.isConnected()) {
            System.out.println("⚠️ WARNING: Database not available.");
            System.out.println("The application will run in in-memory mode.");
            System.out.println("Data will not persist between sessions.");
        }
        
        // Show login screen on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            System.out.println("Starting login interface...");
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
            
            // Add shutdown hook to close database connection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down application...");
                DatabaseConnection.close();
            }));
        });
    }
}