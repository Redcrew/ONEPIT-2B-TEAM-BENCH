package main.java.com.sqs.models.dao.ui;

import javax.swing.*;

import main.java.com.sqs.models.Admin;
import main.java.com.sqs.models.ServiceQueue;
import main.java.com.sqs.models.User;
import main.java.com.sqs.models.dao.ui.panels.AdminDashboardPanel;
import main.java.com.sqs.models.dao.ui.panels.QueueStatusPanel;
import main.java.com.sqs.models.dao.ui.panels.ReportingPanel;
import main.java.com.sqs.models.dao.ui.panels.UserPortalPanel;

import java.awt.*;

public class SQSGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ServiceQueue schoolOffice;
    private User currentUser;
    private Admin currentAdmin;
    
    public SQSGUI(User user, Admin admin) {
        this.currentUser = user;
        this.currentAdmin = admin;
        this.schoolOffice = new ServiceQueue("School Office");
        
        setTitle("School Queue Management System - " + 
                (user != null ? "User Portal" : "Admin Dashboard"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        initGUI();
    }
    
    private void initGUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create panels based on user role
        if (currentUser != null) {
            // User panels
            mainPanel.add(new UserPortalPanel(currentUser, schoolOffice), "USER_PORTAL");
            mainPanel.add(new QueueStatusPanel(schoolOffice), "QUEUE_STATUS");
        } else if (currentAdmin != null) {
            // Admin panels
            mainPanel.add(new AdminDashboardPanel(currentAdmin, schoolOffice), "ADMIN_DASHBOARD");
            mainPanel.add(new ReportingPanel(schoolOffice), "REPORTING");
        }
        
        // Create navigation panel
        JPanel navPanel = createNavigationPanel();
        
        // Add to frame
        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(new Color(52, 73, 94));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(52, 73, 94));
        
        if (currentUser != null) {
            String[] userButtons = {"User Portal", "Queue Status"};
            String[] userCards = {"USER_PORTAL", "QUEUE_STATUS"};
            
            for (int i = 0; i < userButtons.length; i++) {
                JButton button = createNavButton(userButtons[i]);
                final String cardName = userCards[i];
                button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
                buttonPanel.add(button);
            }
        } else if (currentAdmin != null) {
            String[] adminButtons = {"Admin Dashboard", "Reporting"};
            String[] adminCards = {"ADMIN_DASHBOARD", "REPORTING"};
            
            for (int i = 0; i < adminButtons.length; i++) {
                JButton button = createNavButton(adminButtons[i]);
                final String cardName = adminCards[i];
                button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
                buttonPanel.add(button);
            }
        }
        
        navPanel.add(buttonPanel, BorderLayout.WEST);
        
        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(52, 73, 94));
        
        String userInfo;
        if (currentUser != null) {
            userInfo = currentUser.getFullName() + " (" + currentUser.getUserId() + ")";
        } else {
            userInfo = currentAdmin.getFullName() + " (" + currentAdmin.getAdminId() + ")";
        }
        
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.RED);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });
        
        userPanel.add(logoutButton);
        navPanel.add(userPanel, BorderLayout.EAST);
        
        return navPanel;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.BLUE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(31, 97, 141), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
