package com.one.financetracker.gui;

import com.one.financetracker.dao.TransaksiDAO;
import com.one.financetracker.model.Transaksi;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class EditTransaksiDialog extends JDialog {
    private TransaksiDAO transaksiDAO;
    private int transaksiId;
    private boolean transaksiUpdated = false;

    // Komponen GUI
    private JFormattedTextField txtTanggal; // Input tanggal + waktu
    private JComboBox<String> cmbKategori;
    private JTextField txtDeskripsi;
    private JComboBox<Transaksi.JenisTransaksi> cmbJenis;
    private JTextField txtJumlah;

    public EditTransaksiDialog(Frame parent, TransaksiDAO transaksiDAO, int transaksiId) {
        super(parent, "Edit Transaksi", true);
        this.transaksiDAO = transaksiDAO;
        this.transaksiId = transaksiId;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeComponents();
        setupLayout();
        loadTransaksiData();
    }

    private void initializeComponents() {
        try {
            MaskFormatter mask = new MaskFormatter("####-##-## ##:##:##");
            mask.setPlaceholderCharacter('_');
            txtTanggal = new JFormattedTextField(mask);
        } catch (Exception e) {
            txtTanggal = new JFormattedTextField();
        }

        cmbKategori = new JComboBox<>();
        txtDeskripsi = new JTextField();
        cmbJenis = new JComboBox<>(Transaksi.JenisTransaksi.values());
        txtJumlah = new JTextField();

        cmbJenis.addActionListener(e -> updateKategoriComboBox());

        updateKategoriComboBox();
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
            cmbKategori.addItem("Lain-lain");
        }

        // Restore pilihan sebelumnya jika ada
        if (selectedKategori != null) {
            cmbKategori.setSelectedItem(selectedKategori);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Tanggal (yyyy-MM-dd HH:mm:ss):"));
        formPanel.add(txtTanggal);

        formPanel.add(new JLabel("Jenis:"));
        formPanel.add(cmbJenis);

        formPanel.add(new JLabel("Kategori:"));
        formPanel.add(cmbKategori);

        formPanel.add(new JLabel("Deskripsi:"));
        formPanel.add(txtDeskripsi);

        formPanel.add(new JLabel("Jumlah (Rp):"));
        formPanel.add(txtJumlah);

        add(formPanel, BorderLayout.CENTER);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnUpdate = new JButton("Simpan Perubahan");
        JButton btnBatal = new JButton("Batal");

        btnUpdate.addActionListener(e -> updateTransaksi());
        btnBatal.addActionListener(e -> dispose());

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBatal);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadTransaksiData() {
        Transaksi transaksi = transaksiDAO.getTransaksiById(transaksiId);
        if (transaksi != null) {
            // Tampilkan tanggal dengan format yyyy-MM-dd HH:mm:ss
            txtTanggal.setText(transaksi.getTanggal().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            cmbJenis.setSelectedItem(transaksi.getJenis());
            updateKategoriComboBox();
            cmbKategori.setSelectedItem(transaksi.getKategori());
            txtDeskripsi.setText(transaksi.getDeskripsi());
            txtJumlah.setText(transaksi.getJumlah().toString());
        } else {
            JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void updateTransaksi() {
        try {
            // Validasi input
            String tanggalInput = txtTanggal.getText().trim();
            String jumlahInput = txtJumlah.getText().trim();
            String deskripsiInput = txtDeskripsi.getText().trim();

            if (tanggalInput.isEmpty() || jumlahInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tanggal dan Jumlah wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tanggal;
            try {
                tanggal = LocalDateTime.parse(tanggalInput, formatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan yyyy-MM-dd HH:mm:ss", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Transaksi.JenisTransaksi jenis = (Transaksi.JenisTransaksi) cmbJenis.getSelectedItem();

            String kategori = (String) cmbKategori.getSelectedItem();
            if (kategori == null || kategori.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih kategori terlebih dahulu.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (deskripsiInput.isEmpty()) {
                txtDeskripsi.setText("Tanpa deskripsi");
            }

            BigDecimal jumlah;
            try {
                jumlah = new BigDecimal(jumlahInput);
                if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih besar dari nol!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka valid!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Transaksi transaksi = new Transaksi();
            transaksi.setId(transaksiId);
            transaksi.setTanggal(tanggal);
            transaksi.setKategori(kategori);
            transaksi.setDeskripsi(deskripsiInput);
            transaksi.setJenis(jenis);
            transaksi.setJumlah(jumlah);

            // Simpan perubahan ke database
            if (transaksiDAO.updateTransaksi(transaksi)) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil diupdate!");
                transaksiUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isTransaksiUpdated() {
        return transaksiUpdated;
    }
}