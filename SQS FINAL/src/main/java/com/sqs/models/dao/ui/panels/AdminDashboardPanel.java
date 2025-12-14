package main.java.com.sqs.models.dao.ui.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import main.java.com.sqs.models.Admin;
import main.java.com.sqs.models.ServiceQueue;
import main.java.com.sqs.models.Ticket;

import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private Admin currentAdmin;
    private ServiceQueue serviceQueue;
    private JTable adminTable;
    private DefaultTableModel adminTableModel;
    private JLabel servingLabel, nextLabel;
    
    public AdminDashboardPanel(Admin admin, ServiceQueue serviceQueue) {
        this.currentAdmin = admin;
        this.serviceQueue = serviceQueue;
        initComponents();
        updateDashboard();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Queue Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel staffLabel = new JLabel("Staff: " + currentAdmin.getFullName() + 
                                     " | Department: " + currentAdmin.getDepartment(), SwingConstants.CENTER);
        staffLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        staffLabel.setForeground(Color.WHITE);
        headerPanel.add(staffLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Active Queue Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2), 
            "Active Queue List",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        
        String[] columns = {"Ticket", "Name", "ID", "Purpose", "Status"};
        adminTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        adminTable = new JTable(adminTableModel);
        adminTable.setFont(new Font("Arial", Font.PLAIN, 12));
        adminTable.setRowHeight(25);
        adminTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        adminTable.getTableHeader().setBackground(new Color(52, 73, 94));
        adminTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(adminTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
        
        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        controlPanel.setBackground(new Color(236, 240, 241));
        
        // Currently Serving
        JPanel servingPanel = new JPanel(new BorderLayout());
        servingPanel.setBackground(Color.WHITE);
        servingPanel.setBorder(BorderFactory.createTitledBorder("Currently Serving"));
        
        servingLabel = new JLabel("-", SwingConstants.CENTER);
        servingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        servingLabel.setForeground(new Color(52, 152, 219));
        servingPanel.add(servingLabel, BorderLayout.CENTER);
        
        controlPanel.add(servingPanel);
        
        // Next in Line
        JPanel nextPanel = new JPanel(new BorderLayout());
        nextPanel.setBackground(Color.WHITE);
        nextPanel.setBorder(BorderFactory.createTitledBorder("Next Up"));
        
        nextLabel = new JLabel("-", SwingConstants.CENTER);
        nextLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nextLabel.setForeground(new Color(46, 204, 113));
        nextPanel.add(nextLabel, BorderLayout.CENTER);
        
        controlPanel.add(nextPanel);
        
        // Control Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        JButton serveNextButton = createControlButton("Call Next Ticket", new Color(41, 128, 185));
        serveNextButton.addActionListener(e -> {
            serviceQueue.serveNextTicket();
            updateDashboard();
        });
        
        JButton completeButton = createControlButton("Complete Current", new Color(0, 0, 255));
        completeButton.addActionListener(e -> {
            String current = serviceQueue.getCurrentlyServing();
            if (current != null) {
                serviceQueue.completeTicket(current);
                updateDashboard();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No ticket is currently being served.", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton refreshButton = createControlButton("Refresh", new Color(155, 89, 182));
        refreshButton.addActionListener(e -> updateDashboard());
        
        buttonPanel.add(serveNextButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(refreshButton);
        
        controlPanel.add(buttonPanel);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsPanel.setBackground(new Color(236, 240, 241));
        
        JLabel statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setForeground(new Color(52, 73, 94));
        
        // Update stats in updateDashboard method
        controlPanel.add(statsPanel);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JButton createControlButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.GREEN);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void updateDashboard() {
        // Update table
        adminTableModel.setRowCount(0);
        for (Ticket ticket : serviceQueue.getActiveTickets()) {
            adminTableModel.addRow(ticket.toTableRow());
        }
        
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
        
        // Update statistics
        int waiting = serviceQueue.getWaitingCount();
        int serving = serviceQueue.getCurrentlyServing() != null ? 1 : 0;
        
        // Find stats panel and update label
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel)comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getText().contains("Waiting:") || label.getText().isEmpty()) {
                            label.setText("Waiting: " + waiting + " | Serving: " + serving);
                            break;
                        }
                    }
                }
            }
        }
    }
}
