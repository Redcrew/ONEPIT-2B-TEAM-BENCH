package main.java.com.sqs.models.dao.ui.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import main.java.com.sqs.models.ServiceQueue;
import main.java.com.sqs.models.Ticket;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportingPanel extends JPanel {
    private ServiceQueue serviceQueue;
    private JLabel reportTotalLabel, reportAvgLabel, reportPeakLabel;
    private JComboBox<String> reportTypeCombo;
    private JTextField dateFromField, dateToField;
    
    public ReportingPanel(ServiceQueue serviceQueue) {
        this.serviceQueue = serviceQueue;
        initComponents();
        updateReporting();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(149, 165, 166));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Reporting and Analytics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Report Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(236, 240, 241));
        
        JLabel reportTypeLabel = new JLabel("Report Type:");
        reportTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        reportTypeCombo = new JComboBox<>(new String[]{
            "Daily", "Weekly", "Monthly", "Custom"
        });
        reportTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel dateLabel = new JLabel("Date Range:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        dateFromField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 12);
        dateFromField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel toLabel = new JLabel("to");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        dateToField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 12);
        dateToField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        controlPanel.add(reportTypeLabel);
        controlPanel.add(reportTypeCombo);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(dateLabel);
        controlPanel.add(dateFromField);
        controlPanel.add(toLabel);
        controlPanel.add(dateToField);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Metrics Panel
        JPanel metricsPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        metricsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2), 
            "Performance Metrics",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        metricsPanel.setBackground(Color.WHITE);
        
        // Total Services
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(241, 196, 15, 100));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel totalTitle = new JLabel("Total Services Today:");
        totalTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        reportTotalLabel = new JLabel("0", SwingConstants.RIGHT);
        reportTotalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        reportTotalLabel.setForeground(new Color(231, 76, 60));
        
        totalPanel.add(totalTitle, BorderLayout.WEST);
        totalPanel.add(reportTotalLabel, BorderLayout.EAST);
        
        // Peak Hours
        JPanel peakPanel = new JPanel(new BorderLayout());
        peakPanel.setBackground(new Color(52, 152, 219, 100));
        peakPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel peakTitle = new JLabel("Peak Hours:");
        peakTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        reportPeakLabel = new JLabel("-", SwingConstants.RIGHT);
        reportPeakLabel.setFont(new Font("Arial", Font.BOLD, 24));
        reportPeakLabel.setForeground(new Color(52, 73, 94));
        
        peakPanel.add(peakTitle, BorderLayout.WEST);
        peakPanel.add(reportPeakLabel, BorderLayout.EAST);
        
        // Average Service Time
        JPanel avgPanel = new JPanel(new BorderLayout());
        avgPanel.setBackground(new Color(46, 204, 113, 100));
        avgPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel avgTitle = new JLabel("Average Service Time:");
        avgTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        reportAvgLabel = new JLabel("-", SwingConstants.RIGHT);
        reportAvgLabel.setFont(new Font("Arial", Font.BOLD, 24));
        reportAvgLabel.setForeground(new Color(39, 174, 96));
        
        avgPanel.add(avgTitle, BorderLayout.WEST);
        avgPanel.add(reportAvgLabel, BorderLayout.EAST);
        
        metricsPanel.add(totalPanel);
        metricsPanel.add(peakPanel);
        metricsPanel.add(avgPanel);
        
        add(metricsPanel, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        actionPanel.setBackground(new Color(236, 240, 241));
        
        JButton generateButton = new JButton("Generate Report");
        styleReportButton(generateButton, new Color(41, 128, 185));
        
        generateButton.addActionListener(e -> {
            updateReporting();
            JOptionPane.showMessageDialog(this, 
                "Report generated successfully!\n" +
                "Date: " + dateFromField.getText() + " to " + dateToField.getText() + "\n" +
                "Type: " + reportTypeCombo.getSelectedItem(),
                "Report Generated", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton viewDataButton = new JButton("View Stored Data");
        styleReportButton(viewDataButton, new Color(39, 174, 96));
        
        viewDataButton.addActionListener(e -> showStoredDataDialog());
        
        JButton exportButton = new JButton("Export to PDF");
        styleReportButton(exportButton, new Color(231, 76, 60));
        
        exportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "PDF export feature would be implemented here.\n" +
                "Report data exported successfully!",
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        });
        
        actionPanel.add(generateButton);
        actionPanel.add(viewDataButton);
        actionPanel.add(exportButton);
        
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private void styleReportButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void updateReporting() {
        int total = serviceQueue.getTotalServicesToday();
        int waiting = serviceQueue.getWaitingCount();
        int serving = serviceQueue.getCurrentlyServing() != null ? 1 : 0;
        int completed = total - waiting - serving;
        
        reportTotalLabel.setText(total + " (C:" + completed + " W:" + waiting + " S:" + serving + ")");
        
        // Calculate average service time
        List<Ticket> completedTickets = serviceQueue.getCompletedTickets();
        if (!completedTickets.isEmpty()) {
            long totalTime = 0;
            for (Ticket ticket : completedTickets) {
                totalTime += ticket.getServiceDuration();
            }
            double avgTime = totalTime / (double) completedTickets.size();
            reportAvgLabel.setText(String.format("%.1f min", avgTime));
        } else {
            reportAvgLabel.setText("0 min");
        }
        
        // Set peak hours (simplified for demo)
        reportPeakLabel.setText("10:00 - 11:00");
    }
    
    private void showStoredDataDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                    "Stored Queue Data", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columns = {"Ticket", "Name", "ID", "Purpose", "Service", "Status", "Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Show active tickets first
        for (Ticket ticket : serviceQueue.getActiveTickets()) {
            model.addRow(new Object[]{
                ticket.getTicketNumber(),
                ticket.getUserName(),
                ticket.getUserId(),
                ticket.getPurpose(),
                ticket.getServiceType(),
                ticket.getStatus(),
                ticket.getRequestTimeFormatted()
            });
        }
        
        // Show some completed tickets
        java.util.List<Ticket> completed = serviceQueue.getCompletedTickets();
        int limit = Math.min(15, completed.size());
        for (int i = 0; i < limit; i++) {
            Ticket ticket = completed.get(i);
            model.addRow(new Object[]{
                ticket.getTicketNumber(),
                ticket.getUserName(),
                ticket.getUserId(),
                ticket.getPurpose(),
                ticket.getServiceType(),
                ticket.getStatus(),
                ticket.getRequestTimeFormatted()
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel("Showing " + model.getRowCount() + " records");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}