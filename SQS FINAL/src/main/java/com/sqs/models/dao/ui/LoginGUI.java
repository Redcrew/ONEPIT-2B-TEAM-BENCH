package main.java.com.sqs.models.dao.ui;

import javax.swing.*;

import main.java.com.sqs.models.Admin;
import main.java.com.sqs.models.User;
import main.java.com.sqs.models.dao.AdminDAO;
import main.java.com.sqs.models.dao.UserDAO;

import java.awt.*;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton loginButton, registerButton;
    
    public LoginGUI() {
        setTitle("School Queue System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(52, 73, 94));
        
        // Header
        JLabel titleLabel = new JLabel("School Queue System Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Role Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel roleLabel = new JLabel("Login As:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(roleLabel, gbc);
        
        gbc.gridy = 1;
        roleCombo = new JComboBox<>(new String[]{"User", "Administrator"});
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(roleCombo, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(85, 239, 196));
        loginButton.addActionListener(e -> performLogin());
        
        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(41, 128, 185));
        registerButton.addActionListener(e -> showRegistration());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.GREEN);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if ("User".equals(role)) {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.login(username, password);
            
            if (user != null) {
                dispose();
                new SQSGUI(user, null).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password.", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.login(username, password);
            
            if (admin != null) {
                dispose();
                new SQSGUI(null, admin).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid administrator credentials.", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showRegistration() {
        String role = (String) roleCombo.getSelectedItem();
        
        if ("User".equals(role)) {
            showUserRegistration();
        } else {
            showAdminRegistration();
        }
    }
    
    private void showUserRegistration() {
        JDialog dialog = new JDialog(this, "User Registration", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Create New User Account");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, gbc);
        
        // Fields
        String[] labels = {"Full Name:", "User ID:", "Username:", "Password:"};
        JTextField[] fields = new JTextField[labels.length - 1];
        JPasswordField passwordField = new JPasswordField();
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.gridwidth = 1;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            if (i < labels.length - 1) {
                fields[i] = new JTextField(15);
                panel.add(fields[i], gbc);
            } else {
                panel.add(passwordField, gbc);
            }
        }
        
        // Register Button
        gbc.gridx = 0; gbc.gridy = labels.length + 1; gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn, new Color(46, 204, 113));
        
        registerBtn.addActionListener(e -> {
            String fullName = fields[0].getText().trim();
            String userId = fields[1].getText().trim();
            String username = fields[2].getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (fullName.isEmpty() || userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please fill in all fields.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UserDAO userDAO = new UserDAO();
            User newUser = new User(username, password, fullName, userId);
            
            if (userDAO.registerUser(newUser)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Registration successful! You can now login.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Registration failed. Username or User ID might already exist.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(registerBtn, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAdminRegistration() {
        JDialog dialog = new JDialog(this, "Administrator Registration", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Create New Administrator Account");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, gbc);
        
        // Fields
        String[] labels = {"Full Name:", "Admin ID:", "Department:", "Username:", "Password:"};
        JTextField[] fields = new JTextField[labels.length - 1];
        JPasswordField passwordField = new JPasswordField();
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.gridwidth = 1;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            if (i < labels.length - 1) {
                fields[i] = new JTextField(15);
                panel.add(fields[i], gbc);
            } else {
                panel.add(passwordField, gbc);
            }
        }
        
        // Register Button
        gbc.gridx = 0; gbc.gridy = labels.length + 1; gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn, new Color(46, 204, 113));
        
        registerBtn.addActionListener(e -> {
            String fullName = fields[0].getText().trim();
            String adminId = fields[1].getText().trim();
            String department = fields[2].getText().trim();
            String username = fields[3].getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (fullName.isEmpty() || adminId.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please fill in all required fields.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            Admin newAdmin = new Admin(username, password, fullName, adminId, department);
            
            if (adminDAO.registerAdmin(newAdmin)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Administrator registration successful!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Registration failed. Username or Admin ID might already exist.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(registerBtn, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}