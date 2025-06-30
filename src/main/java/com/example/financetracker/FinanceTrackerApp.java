package com.one.financetracker;

import com.one.financetracker.gui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FinanceTrackerApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Jalankan Main file sebagai Aplikasi Utama
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}