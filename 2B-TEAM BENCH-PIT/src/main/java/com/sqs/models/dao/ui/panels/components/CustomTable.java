package main.java.com.sqs.models.dao.ui.panels.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CustomTable extends JTable {
    
    public CustomTable(DefaultTableModel model) {
        super(model);
        initCustomSettings();
    }
    
    private void initCustomSettings() {
        // Set font and colors
        setFont(new Font("Arial", Font.PLAIN, 12));
        setRowHeight(25);
        setSelectionBackground(new Color(52, 152, 219, 100));
        setSelectionForeground(Color.BLACK);
        
        // Customize header
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Set grid lines
        setShowGrid(true);
        setGridColor(new Color(220, 220, 220));
        
        // Make table non-editable by default
        setDefaultEditor(Object.class, null);
        
        // Set selection mode
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        
        // Alternate row colors
        if (!isRowSelected(row)) {
            component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        // Highlight specific statuses
        String status = getValueAt(row, 4).toString(); // Assuming status is in column 4
        if ("Serving".equals(status)) {
            component.setBackground(new Color(255, 255, 200)); // Light yellow
        } else if ("Completed".equals(status)) {
            component.setBackground(new Color(220, 255, 220)); // Light green
        }
        
        return component;
    }
}
