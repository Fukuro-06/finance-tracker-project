/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.one.financetracker.gui;

import com.one.financetracker.dao.TransaksiDAO;
import com.one.financetracker.model.Transaksi;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TambahTransaksiDialog extends JDialog {
    private TransaksiDAO transaksiDAO;
    private boolean transaksiAdded = false;
    
    private JTextField txtTanggal;
    private JComboBox<String> cmbKategori;
    private JTextField txtDeskripsi;
    private JComboBox<Transaksi.JenisTransaksi> cmbJenis;
    private JTextField txtJumlah;
    
    public TambahTransaksiDialog(Frame parent, TransaksiDAO transaksiDAO) {
        super(parent, "Tambah Transaksi", true);
        this.transaksiDAO = transaksiDAO;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        txtTanggal = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        cmbKategori = new JComboBox<>();
        txtDeskripsi = new JTextField();
        cmbJenis = new JComboBox<>(Transaksi.JenisTransaksi.values());
        txtJumlah = new JTextField();
        
        // Populate kategori
        updateKategoriComboBox();
        
        // Setup jenis change listener
        cmbJenis.addActionListener(e -> updateKategoriComboBox());
    }
    
    private void updateKategoriComboBox() {
        cmbKategori.removeAllItems();
        Transaksi.JenisTransaksi selectedJenis = (Transaksi.JenisTransaksi) cmbJenis.getSelectedItem();
        
        if (selectedJenis == Transaksi.JenisTransaksi.PEMASUKAN) {
            cmbKategori.addItem("Gaji");
            cmbKategori.addItem("Bonus");
            cmbKategori.addItem("Investasi");
            cmbKategori.addItem("Tabungan");
            cmbKategori.addItem("Lain-lain");
        } else {
            cmbKategori.addItem("Makanan");
            cmbKategori.addItem("Transport");
            cmbKategori.addItem("Belanja");
            cmbKategori.addItem("Tagihan");
            cmbKategori.addItem("Hiburan");
            cmbKategori.addItem("Lain-lain");
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 2, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0 - Tanggal
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tanggal (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtTanggal, gbc);
        
        // Row 1 - Jenis
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jenis:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbJenis, gbc);
        
        // Row 2 - Kategori
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbKategori, gbc);
        
        // Row 3 - Deskripsi
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Deskripsi (Opsional):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtDeskripsi, gbc);
        
        // Row 4 - Jumlah
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jumlah (Rp):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtJumlah, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSimpan = new JButton("Simpan");
        JButton btnBatal = new JButton("Batal");
        
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Button Actions
        btnSimpan.addActionListener(e -> simpanTransaksi());
        btnBatal.addActionListener(e -> dispose());
    }
    
    private void setupEventHandlers() {
        // Enter key untuk simpan
        getRootPane().setDefaultButton((JButton) ((JPanel) getContentPane().getComponent(1)).getComponent(0));
    }
    
    private void simpanTransaksi() {
        try {
            // Validasi input
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
            
            // Buat transaksi baru
            Transaksi transaksi = new Transaksi(
            LocalDateTime.now(), 
            (String) cmbKategori.getSelectedItem(),
            txtDeskripsi.getText(), 
            Transaksi.JenisTransaksi.valueOf(cmbJenis.getSelectedItem().toString()),
            new BigDecimal(txtJumlah.getText())
            );
            
            // Simpan ke database
            if (transaksiDAO.tambahTransaksi(transaksi)) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!");
                transaksiAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isTransaksiAdded() {
        return transaksiAdded;
    }
}