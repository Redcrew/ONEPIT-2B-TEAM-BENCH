package main.java.com.sqs.models.dao.ui.panels.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CustomTextField extends JTextField {
    private String placeholder;
    private Color placeholderColor = Color.GRAY;
    private Color normalColor = Color.BLACK;
    private boolean showingPlaceholder;
    
    public CustomTextField(String placeholder) {
        this.placeholder = placeholder;
        this.showingPlaceholder = true;
        
        initTextField();
        updateText();
        
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setForeground(normalColor);
                    showingPlaceholder = false;
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    showingPlaceholder = true;
                    updateText();
                }
            }
        });
    }
    
    private void initTextField() {
        setFont(new Font("Arial", Font.PLAIN, 14));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setPreferredSize(new Dimension(200, 30));
    }
    
    private void updateText() {
        if (showingPlaceholder) {
            setText(placeholder);
            setForeground(placeholderColor);
        } else {
            setForeground(normalColor);
        }
    }
    
    @Override
    public String getText() {
        return showingPlaceholder ? "" : super.getText();
    }
    
    @Override
    public void setText(String t) {
        if (t == null || t.isEmpty()) {
            showingPlaceholder = true;
            updateText();
        } else {
            showingPlaceholder = false;
            super.setText(t);
            setForeground(normalColor);
        }
    }
    
    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        if (showingPlaceholder) {
            setForeground(placeholderColor);
        }
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        if (!showingPlaceholder) {
            setForeground(normalColor);
        }
    }
}