/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.financetracker.gui;

import com.example.financetracker.dao.TransaksiDAO;
import com.example.financetracker.model.Transaksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {
    private TransaksiDAO transaksiDAO;
    private JLabel lblTotalPemasukan, lblTotalPengeluaran, lblSaldo;
    private JTable tabelTransaksi;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;
    
    public MainFrame() {
        this.transaksiDAO = new TransaksiDAO();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        initializeComponents();
        setupLayout();
        loadData();
        updateDashboard();
    }
    
    private void initializeComponents() {
        setTitle("Pencatat Keuangan Pribadi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Dashboard Labels
        lblTotalPemasukan = new JLabel("Rp 0");
        lblTotalPengeluaran = new JLabel("Rp 0");
        lblSaldo = new JLabel("Rp 0");
        
        // Table
        String[] columnNames = {"ID", "Tanggal", "Kategori", "Deskripsi", "Jenis", "Jumlah"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelTransaksi = new JTable(tableModel);
        tabelTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top Panel - Dashboard
        JPanel dashboardPanel = createDashboardPanel();
        add(dashboardPanel, BorderLayout.NORTH);
        
        // Center Panel - Table
        JScrollPane scrollPane = new JScrollPane(tabelTransaksi);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Riwayat Transaksi"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom Panel - Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Dashboard Keuangan"));
        panel.setPreferredSize(new Dimension(0, 80));
        
        // Pemasukan Panel
        JPanel pemasukanPanel = new JPanel(new BorderLayout());
        pemasukanPanel.setBorder(BorderFactory.createEtchedBorder());
        pemasukanPanel.setBackground(new Color(230, 255, 230));
        pemasukanPanel.add(new JLabel("Total Pemasukan", JLabel.CENTER), BorderLayout.NORTH);
        lblTotalPemasukan.setHorizontalAlignment(JLabel.CENTER);
        lblTotalPemasukan.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalPemasukan.setForeground(new Color(0, 150, 0));
        pemasukanPanel.add(lblTotalPemasukan, BorderLayout.CENTER);
        
        // Pengeluaran Panel
        JPanel pengeluaranPanel = new JPanel(new BorderLayout());
        pengeluaranPanel.setBorder(BorderFactory.createEtchedBorder());
        pengeluaranPanel.setBackground(new Color(255, 230, 230));
        pengeluaranPanel.add(new JLabel("Total Pengeluaran", JLabel.CENTER), BorderLayout.NORTH);
        lblTotalPengeluaran.setHorizontalAlignment(JLabel.CENTER);
        lblTotalPengeluaran.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalPengeluaran.setForeground(new Color(200, 0, 0));
        pengeluaranPanel.add(lblTotalPengeluaran, BorderLayout.CENTER);
        
        // Saldo Panel
        JPanel saldoPanel = new JPanel(new BorderLayout());
        saldoPanel.setBorder(BorderFactory.createEtchedBorder());
        saldoPanel.setBackground(new Color(230, 230, 255));
        saldoPanel.add(new JLabel("Saldo", JLabel.CENTER), BorderLayout.NORTH);
        lblSaldo.setHorizontalAlignment(JLabel.CENTER);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 18));
        lblSaldo.setForeground(new Color(0, 0, 150));
        saldoPanel.add(lblSaldo, BorderLayout.CENTER);
        
        panel.add(pemasukanPanel);
        panel.add(pengeluaranPanel);
        panel.add(saldoPanel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnTambah = new JButton("Tambah Transaksi");
        JButton btnEdit = new JButton("Edit Transaksi");
        JButton btnHapus = new JButton("Hapus Transaksi");
        JButton btnRefresh = new JButton("Refresh");
        
        btnTambah.addActionListener(e -> openTambahTransaksiDialog());
        btnEdit.addActionListener(e -> editTransaksi());
        btnHapus.addActionListener(e -> hapusTransaksi());
        btnRefresh.addActionListener(e -> {
            loadData();
            updateDashboard();
        });
        
        panel.add(btnTambah);
        panel.add(btnEdit);
        panel.add(btnHapus);
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void openTambahTransaksiDialog() {
        TambahTransaksiDialog dialog = new TambahTransaksiDialog(this, transaksiDAO);
        dialog.setVisible(true);
        
        if (dialog.isTransaksiAdded()) {
            loadData();
            updateDashboard();
        }
    }
    
    private void editTransaksi() {
        int selectedRow = tabelTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan diedit!", 
                                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int transaksiId = (Integer) tableModel.getValueAt(selectedRow, 0);
        EditTransaksiDialog dialog = new EditTransaksiDialog(this, transaksiDAO, transaksiId);
        dialog.setVisible(true);
        
        if (dialog.isTransaksiUpdated()) {
            loadData();
            updateDashboard();
        }
    }
    
    private void hapusTransaksi() {
        int selectedRow = tabelTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus!", 
                                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus transaksi ini?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int transaksiId = (Integer) tableModel.getValueAt(selectedRow, 0);
            if (transaksiDAO.hapusTransaksi(transaksiId)) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!");
                loadData();
                updateDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus transaksi!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Transaksi> transaksiList = transaksiDAO.getAllTransaksi();
        
        for (Transaksi transaksi : transaksiList) {
            Object[] rowData = {
                transaksi.getId(),
                transaksi.getTanggal(),
                transaksi.getKategori(),
                transaksi.getDeskripsi(),
                transaksi.getJenis(),
                currencyFormat.format(transaksi.getJumlah())
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void updateDashboard() {
        BigDecimal totalPemasukan = transaksiDAO.getTotalPemasukan();
        BigDecimal totalPengeluaran = transaksiDAO.getTotalPengeluaran();
        BigDecimal saldo = totalPemasukan.subtract(totalPengeluaran);
        
        lblTotalPemasukan.setText(currencyFormat.format(totalPemasukan));
        lblTotalPengeluaran.setText(currencyFormat.format(totalPengeluaran));
        lblSaldo.setText(currencyFormat.format(saldo));
        
        // Ubah warna saldo berdasarkan nilai
        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            lblSaldo.setForeground(new Color(0, 150, 0));
        } else if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            lblSaldo.setForeground(new Color(200, 0, 0));
        } else {
            lblSaldo.setForeground(Color.BLACK);
        }
    }
}