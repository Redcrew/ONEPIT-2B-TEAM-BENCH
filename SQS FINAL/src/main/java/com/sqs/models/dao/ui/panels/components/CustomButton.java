package main.java.com.sqs.models.dao.ui.panels.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CustomButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    
    public CustomButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        
        initButton();
        addMouseListener(new ButtonMouseListener());
    }
    
    private void initButton() {
        setFont(new Font("Arial", Font.BOLD, 14));
        setBackground(normalColor);
        setForeground(Color.BLUE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(normalColor.darker(), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        
        // Remove default button styling
        setContentAreaFilled(false);
        setOpaque(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g.setColor(hoverColor);
        } else {
            g.setColor(normalColor);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
    
    private class ButtonMouseListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            setBackground(hoverColor);
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            setBackground(normalColor);
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            setBackground(pressedColor);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            setBackground(normalColor);
        }
    }
}
