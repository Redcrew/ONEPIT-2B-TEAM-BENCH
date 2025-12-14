package main.java.com.sqs.models.dao.ui.panels;

import javax.swing.*;
import main.java.com.sqs.models.ServiceQueue;
import main.java.com.sqs.models.Ticket;
import main.java.com.sqs.models.User;

import java.awt.*;

public class UserPortalPanel extends JPanel {
    private User currentUser;
    private ServiceQueue serviceQueue;
    private JTextField nameField, idField;
    private JTextArea detailsArea;
    private JComboBox<String> purposeCombo;
    private JRadioButton[] serviceButtons;
    
    public UserPortalPanel(User user, ServiceQueue serviceQueue) {
        this.currentUser = user;
        this.serviceQueue = serviceQueue;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("School Queue System: User Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // User Information
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Your Information:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(infoLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(currentUser.getFullName());
        nameField.setEditable(false);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel idLabel = new JLabel("ID Number:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        idField = new JTextField(currentUser.getUserId());
        idField.setEditable(false);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(idField, gbc);
        
        // Service Location
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel serviceLabel = new JLabel("Select Service Location:");
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(serviceLabel, gbc);
        
        String[] services = {"School Office", "Clinic", "Registrar"};
        serviceButtons = new JRadioButton[services.length];
        ButtonGroup serviceGroup = new ButtonGroup();
        
        for (int i = 0; i < services.length; i++) {
            serviceButtons[i] = new JRadioButton(services[i]);
            serviceButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            serviceButtons[i].setBackground(new Color(236, 240, 241));
            serviceButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            serviceGroup.add(serviceButtons[i]);
            gbc.gridx = 0; gbc.gridy = i + 4; gbc.gridwidth = 2;
            contentPanel.add(serviceButtons[i], gbc);
        }
        
        // Select first service by default
        if (serviceButtons.length > 0) {
            serviceButtons[0].setSelected(true);
        }
        
        // Purpose of Visit
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JLabel purposeLabel = new JLabel("Purpose of Visit:");
        purposeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(purposeLabel, gbc);
        
        gbc.gridy = 8;
        purposeCombo = new JComboBox<>(new String[]{
            "Select Purpose", "Document Request", "Payment", "ID Renewal", 
            "Transcript", "Consultation", "Registration", "Other"
        });
        purposeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        purposeCombo.setBackground(Color.GREEN);
        contentPanel.add(purposeCombo, gbc);
        
        gbc.gridy = 9;
        detailsArea = new JTextArea(3, 30);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsArea.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE), "Additional Details:"
        ));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        contentPanel.add(detailsScroll, gbc);
        
        // Generate Ticket Button
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton generateButton = new JButton("Get Queue Number");
        generateButton.setFont(new Font("Arial", Font.BOLD, 16));
        generateButton.setBackground(new Color(46, 204, 113));
        generateButton.setForeground(Color.GREEN);
        generateButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        generateButton.addActionListener(e -> generateTicket());
        
        contentPanel.add(generateButton, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void generateTicket() {
        String purpose = (String) purposeCombo.getSelectedItem();
        if ("Select Purpose".equals(purpose)) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purpose for your visit.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedService = "";
        for (JRadioButton rb : serviceButtons) {
            if (rb.isSelected()) {
                selectedService = rb.getText();
                break;
            }
        }
        
        if (selectedService.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select a service location.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Generate ticket
        Ticket newTicket = serviceQueue.generateTicket(
            currentUser.getFullName(), 
            purpose, 
            currentUser.getUserId()
        );
        
        if (newTicket != null) {
            // Show success message
            String message = String.format(
                "<html><div style='text-align: center;'>" +
                "<h3>Ticket Generated Successfully!</h3>" +
                "<p><b>Ticket Number:</b> %s</p>" +
                "<p><b>Name:</b> %s</p>" +
                "<p><b>Service:</b> %s</p>" +
                "<p><b>Purpose:</b> %s</p>" +
                "<p><b>Estimated Wait:</b> %d minutes</p>" +
                "<p><b>Your Position:</b> %d</p>" +
                "<p>Please proceed to check your queue status.</p>" +
                "</div></html>",
                newTicket.getTicketNumber(), 
                currentUser.getFullName(), 
                selectedService, 
                purpose, 
                serviceQueue.getWaitingCount() * 2, 
                serviceQueue.getPositionInQueue(newTicket.getTicketNumber())
            );
            
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            detailsArea.setText("");
            purposeCombo.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to generate ticket. Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}