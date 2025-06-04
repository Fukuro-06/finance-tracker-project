/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.financetracker.gui;

import com.example.financetracker.dao.TransaksiDAO;
import com.example.financetracker.model.Transaksi;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditTransaksiDialog extends JDialog {
    private TransaksiDAO transaksiDAO;
    private int transaksiId;
    private boolean transaksiUpdated = false;
    
    private JTextField txtTanggal;
    private JComboBox<String> cmbKategori;
    private JTextField txtDeskripsi;
    private JComboBox<Transaksi.JenisTransaksi> cmbJenis;
    private JTextField txtJumlah;
    
    public EditTransaksiDialog(Frame parent, TransaksiDAO transaksiDAO, int transaksiId) {
        super(parent, "Edit Transaksi", true);
        this.transaksiDAO = transaksiDAO;
        this.transaksiId = transaksiId;
        
        initializeComponents();
        setupLayout();
        loadTransaksiData();
    }
    
    private void initializeComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        txtTanggal = new JTextField();
        cmbKategori = new JComboBox<>();
        txtDeskripsi = new JTextField();
        cmbJenis = new JComboBox<>(Transaksi.JenisTransaksi.values());
        txtJumlah = new JTextField();
        
        // Setup jenis change listener
        cmbJenis.addActionListener(e -> updateKategoriComboBox());
    }
    
    private void updateKategoriComboBox() {
        String selectedKategori = (String) cmbKategori.getSelectedItem();
        cmbKategori.removeAllItems();
        Transaksi.JenisTransaksi selectedJenis = (Transaksi.JenisTransaksi) cmbJenis.getSelectedItem();
        
        if (selectedJenis == Transaksi.JenisTransaksi.PEMASUKAN) {
            cmbKategori.addItem("Gaji");
            cmbKategori.addItem("Bonus");
            cmbKategori.addItem("Investasi");
            cmbKategori.addItem("Lain-lain");
        } else {
            cmbKategori.addItem("Makanan");
            cmbKategori.addItem("Transport");
            cmbKategori.addItem("Belanja");
            cmbKategori.addItem("Tagihan");
            cmbKategori.addItem("Hiburan");
        }
        
        // Try to restore selected kategori
        if (selectedKategori != null) {
            cmbKategori.setSelectedItem(selectedKategori);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Form Panel (sama seperti TambahTransaksiDialog)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Layout fields (sama seperti TambahTransaksiDialog)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tanggal (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtTanggal, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jenis:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbJenis, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbKategori, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtDeskripsi, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jumlah (Rp):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtJumlah, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnUpdate = new JButton("Update");
        JButton btnBatal = new JButton("Batal");
        
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBatal);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Button Actions
        btnUpdate.addActionListener(e -> updateTransaksi());
        btnBatal.addActionListener(e -> dispose());
    }
    
    private void loadTransaksiData() {
        Transaksi transaksi = transaksiDAO.getTransaksiById(transaksiId);
        if (transaksi != null) {
            txtTanggal.setText(transaksi.getTanggal().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            cmbJenis.setSelectedItem(transaksi.getJenis());
            updateKategoriComboBox();
            cmbKategori.setSelectedItem(transaksi.getKategori());
            txtDeskripsi.setText(transaksi.getDeskripsi());
            txtJumlah.setText(transaksi.getJumlah().toString());
        }
    }
    
    private void updateTransaksi() {
        try {
            // Validasi input (sama seperti TambahTransaksiDialog)
            if (txtTanggal.getText().trim().isEmpty() || 
                txtJumlah.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap isi semua field yang wajib!", 
                                            "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse data
            LocalDate tanggal = LocalDate.parse(txtTanggal.getText().trim());
            String kategori = (String) cmbKategori.getSelectedItem();
            String deskripsi = txtDeskripsi.getText().trim();
            Transaksi.JenisTransaksi jenis = (Transaksi.JenisTransaksi) cmbJenis.getSelectedItem();
            BigDecimal jumlah = new BigDecimal(txtJumlah.getText().trim().replace(",", "."));
            
            if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih besar dari 0!", 
                                            "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update transaksi
            Transaksi transaksi = new Transaksi(tanggal, kategori, deskripsi, jenis, jumlah);
            transaksi.setId(transaksiId);
            
            if (transaksiDAO.updateTransaksi(transaksi)) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil diupdate!");
                transaksiUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate transaksi!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isTransaksiUpdated() {
        return transaksiUpdated;
    }
}