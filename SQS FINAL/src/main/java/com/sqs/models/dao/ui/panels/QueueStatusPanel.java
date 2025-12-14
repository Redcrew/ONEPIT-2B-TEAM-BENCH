package main.java.com.sqs.models.dao.ui.panels;

import javax.swing.*;
import main.java.com.sqs.models.ServiceQueue;
import main.java.com.sqs.models.Ticket;

import java.awt.*;

public class QueueStatusPanel extends JPanel {
    private ServiceQueue serviceQueue;
    private JLabel statusTicketLabel, statusPositionLabel, statusTimeLabel, statusNotificationLabel;
    private JLabel servingLabel, nextLabel;
    private JTextField checkTicketField;
    
    public QueueStatusPanel(ServiceQueue serviceQueue) {
        this.serviceQueue = serviceQueue;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Check Queue Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Ticket Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel ticketLabel = new JLabel("Enter Ticket Number:");
        ticketLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        checkTicketField = new JTextField(15);
        checkTicketField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        inputPanel.add(ticketLabel);
        inputPanel.add(checkTicketField);
        
        JButton checkButton = new JButton("Check Status");
        checkButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkButton.setBackground(new Color(41, 128, 185));
        checkButton.setForeground(Color.BLUE);
        checkButton.addActionListener(e -> checkTicketStatus());
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.GREEN);
        clearButton.addActionListener(e -> {
            checkTicketField.setText("");
            clearStatusDisplay();
        });
        
        inputPanel.add(checkButton);
        inputPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(inputPanel, gbc);
        
        // Status Display Panel
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        statusTicketLabel = new JLabel("Ticket: -");
        statusTicketLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        statusPositionLabel = new JLabel("Position: -");
        statusPositionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        statusTimeLabel = new JLabel("Estimated Wait: -");
        statusTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        statusNotificationLabel = new JLabel("");
        statusNotificationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        statusPanel.add(statusTicketLabel);
        statusPanel.add(statusPositionLabel);
        statusPanel.add(statusTimeLabel);
        statusPanel.add(statusNotificationLabel);
        
        gbc.gridy = 1;
        contentPanel.add(statusPanel, gbc);
        
        // Queue Progress Panel
        JPanel progressPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        progressPanel.setBackground(new Color(241, 196, 15));
        progressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Queue Progress"),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel servingTitle = new JLabel("Currently Serving:");
        servingTitle.setFont(new Font("Arial", Font.BOLD, 14));
        
        servingLabel = new JLabel("-");
        servingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel nextTitle = new JLabel("Next Up:");
        nextTitle.setFont(new Font("Arial", Font.BOLD, 14));
        
        nextLabel = new JLabel("-");
        nextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        progressPanel.add(servingTitle);
        progressPanel.add(servingLabel);
        progressPanel.add(nextTitle);
        progressPanel.add(nextLabel);
        
        gbc.gridy = 2;
        contentPanel.add(progressPanel, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Initialize display
        updateQueueInfo();
    }
    
    private void checkTicketStatus() {
        String ticketNumber = checkTicketField.getText().trim();
        
        if (ticketNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a ticket number.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Ticket ticket = serviceQueue.getTicket(ticketNumber);
        if (ticket != null) {
            int position = serviceQueue.getPositionInQueue(ticketNumber);
            int waitTime = position * 2;
            
            statusTicketLabel.setText("Ticket: " + ticketNumber + " (" + ticket.getUserName() + ")");
            statusPositionLabel.setText("Position: " + position);
            statusTimeLabel.setText("Estimated Wait: " + waitTime + " minutes");
            
            if (position == 1) {
                statusNotificationLabel.setText("Notification: Your turn is next!");
                statusNotificationLabel.setForeground(new Color(39, 174, 96));
            } else if (position == 0) {
                statusNotificationLabel.setText("Currently being served or completed");
                statusNotificationLabel.setForeground(new Color(231, 76, 60));
            } else {
                statusNotificationLabel.setText("");
            }
            
            updateQueueInfo();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Ticket number not found. Please check and try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            clearStatusDisplay();
        }
    }
    
    private void clearStatusDisplay() {
        statusTicketLabel.setText("Ticket: -");
        statusPositionLabel.setText("Position: -");
        statusTimeLabel.setText("Estimated Wait: -");
        statusNotificationLabel.setText("");
        servingLabel.setText("-");
        nextLabel.setText("-");
    }
    
    private void updateQueueInfo() {
        // Update serving and next
        servingLabel.setText(serviceQueue.getCurrentlyServing() != null ? 
            serviceQueue.getCurrentlyServing() : "None");
        
        // Find next waiting ticket
        String nextTicket = "";
        for (Ticket t : serviceQueue.getActiveTickets()) {
            if ("Waiting".equals(t.getStatus())) {
                nextTicket = t.getTicketNumber();
                break;
            }
        }
        nextLabel.setText(nextTicket.isEmpty() ? "None" : nextTicket);
    }
}
