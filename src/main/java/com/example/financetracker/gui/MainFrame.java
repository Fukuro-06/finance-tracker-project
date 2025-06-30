/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.one.financetracker.gui;

import com.one.financetracker.dao.TransaksiDAO;
import com.one.financetracker.model.Transaksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.ChartPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

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
        setTitle("Pencatat Keuangan Pribadi beta 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Dashboard Labels
        lblTotalPemasukan = new JLabel("Rp 0");
        lblTotalPengeluaran = new JLabel("Rp 0");
        lblSaldo = new JLabel("Rp 0");

        // Table
        String[] columnNames = {"Nomor", "ID", "Tanggal/Waktu", "Kategori", "Deskripsi", "Jenis", "Jumlah"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelTransaksi = new JTable(tableModel);
        tabelTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
    }

    // Layout Tampilan
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top Panel - Dashboard
        JPanel dashboardPanel = createDashboardPanel();
        add(dashboardPanel, BorderLayout.NORTH);

        // Center Panel - Table
        JScrollPane scrollPane = new JScrollPane(tabelTransaksi);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Riwayat Transaksi"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Buttons (Gunakan JScrollPane)
        JScrollPane buttonScrollPane = createButtonPanel();
        add(buttonScrollPane, BorderLayout.SOUTH);
        
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
        lblTotalPemasukan.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalPemasukan.setForeground(new Color(0, 150, 0));
        pemasukanPanel.add(lblTotalPemasukan, BorderLayout.CENTER);

        // Pengeluaran Panel
        JPanel pengeluaranPanel = new JPanel(new BorderLayout());
        pengeluaranPanel.setBorder(BorderFactory.createEtchedBorder());
        pengeluaranPanel.setBackground(new Color(255, 230, 230));
        pengeluaranPanel.add(new JLabel("Total Pengeluaran", JLabel.CENTER), BorderLayout.NORTH);
        lblTotalPengeluaran.setHorizontalAlignment(JLabel.CENTER);
        lblTotalPengeluaran.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalPengeluaran.setForeground(new Color(200, 0, 0));
        pengeluaranPanel.add(lblTotalPengeluaran, BorderLayout.CENTER);

        // Saldo Panel
        JPanel saldoPanel = new JPanel(new BorderLayout());
        saldoPanel.setBorder(BorderFactory.createEtchedBorder());
        saldoPanel.setBackground(new Color(230, 230, 255));
        saldoPanel.add(new JLabel("Saldo", JLabel.CENTER), BorderLayout.NORTH);
        lblSaldo.setHorizontalAlignment(JLabel.CENTER);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 20));
        lblSaldo.setForeground(new Color(0, 0, 150));
        saldoPanel.add(lblSaldo, BorderLayout.CENTER);

        panel.add(pemasukanPanel);
        panel.add(pengeluaranPanel);
        panel.add(saldoPanel);

        return panel;
    }

    private JScrollPane createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        // Method untuk Mengatur Pilihan di Combo Box
        String[] filterOptions = {"Tampilkan Semua", "Hanya Pemasukan", "Hanya Pengeluaran"};
        JComboBox<String> cmbFilter = new JComboBox<>(filterOptions);
        String[] exportOptions = {"Export to Excel", "Export to PDF"};
        JComboBox<String> cmbExport = new JComboBox<>(exportOptions);
        String[] chartOptions = {"Grafik Pemasukan", "Grafik Pengeluaran"};
        JComboBox<String> cmbChart = new JComboBox<>(chartOptions);

        JButton btnTambah = new JButton("Tambah Transaksi");
        JButton btnEdit = new JButton("Edit Transaksi");
        JButton btnHapus = new JButton("Hapus Transaksi");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExit = new JButton("Exit");

        // Listener untuk Button
        btnTambah.addActionListener(e -> openTambahTransaksiDialog());
        btnEdit.addActionListener(e -> editTransaksi());
        btnHapus.addActionListener(e -> hapusTransaksi());
        btnExit.addActionListener(e -> exitApplication());
        btnRefresh.addActionListener(e -> {
            loadData();
            updateDashboard();
        });
        cmbFilter.addActionListener(e -> applyFilter(cmbFilter));
        cmbExport.addActionListener(e -> {
        String selectedOption = (String) cmbExport.getSelectedItem();
            if (selectedOption != null) {
            exportData(selectedOption);
            }
        });
        cmbChart.addActionListener(e -> {
        String selectedChart = (String) cmbChart.getSelectedItem();
            if ("Grafik Pemasukan".equals(selectedChart)) {
                showPieChartPemasukan();
            } else if ("Grafik Pengeluaran".equals(selectedChart)) {
            showPieChartPengeluaran();
            }
        });

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(cmbExport); 
        buttonPanel.add(cmbChart);
        buttonPanel.add(cmbFilter); 
        buttonPanel.add(btnExit);
        
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Menu Aksi"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
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

        int transaksiId = (int) tableModel.getValueAt(selectedRow, 0);
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
        int no = 1;

        for (Transaksi transaksi : transaksiList) {
            Object[] rowData = {
                no++,
                formatId(transaksi.getId()),
                formatDateWithTime(transaksi.getTanggal()),
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
    
    // Update Table
    private void updateTable(List<Transaksi> transaksiList) {
    tableModel.setRowCount(0);
    int rowNumber = 1;

        for (Transaksi transaksi : transaksiList) {
            Object[] rowData = {
                rowNumber++,
                transaksi.getId(),
                formatDateWithTime(transaksi.getTanggal()),
                transaksi.getKategori(),
                transaksi.getDeskripsi(),
                transaksi.getJenis(),
                currencyFormat.format(transaksi.getJumlah())
            };
            tableModel.addRow(rowData);
        }
    } 
    
    // Menampilkan format tampilan tanggal dan format yang mencakup waktu.
    private String formatDateWithTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        return dateTime != null ? dateTime.format(formatter) : "";
    }
    
    // Method Export Data ke PDF/Excel
    private void exportData(String option) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan File Export");
    fileChooser.setApproveButtonText("Simpan");

    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        if (!filePath.endsWith(".xlsx") && !filePath.endsWith(".pdf")) {
            if ("Excel".equals(option)) {
                filePath += ".xlsx";
            } else if ("PDF".equals(option)) {
                filePath += ".pdf";
            }
        }

        try {
            List<Transaksi> transaksiList = transaksiDAO.getAllTransaksi();

            if ("Excel".equals(option)) {
                transaksiDAO.exportToExcel(filePath, transaksiList);
                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke Excel!");
            } else if ("PDF".equals(option)) {
                transaksiDAO.exportToPDF(filePath, transaksiList);
                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke PDF!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengekspor data: " + ex.getMessage(), 
                                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
    }
    
    // Method menampilkan Grafik Pie Pemasukan
    private void showPieChartPemasukan() {
    try {
        // Buat panel grafik untuk pemasukan
        ChartPanel chartPanel = transaksiDAO.createPieChartPemasukan();

        // Tampilkan dalam jendela baru
        JFrame frame = new JFrame("Grafik Statistik Pemasukan");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(chartPanel);
        frame.setVisible(true);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan grafik pemasukan: " + ex.getMessage(),
                                  "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    // Method menampilkan Grafik Pie Pengeluaran
    private void showPieChartPengeluaran() {
    try {
        // Buat panel grafik untuk pengeluaran
        ChartPanel chartPanel = transaksiDAO.createPieChartPengeluaran();

        // Tampilkan dalam jendela baru
        JFrame frame = new JFrame("Grafik Statistik Pengeluaran");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(chartPanel);
        frame.setVisible(true);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan grafik pengeluaran: " + ex.getMessage(),
                                  "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void applyFilter(JComboBox<String> cmbFilter) {
    String selectedFilter = (String) cmbFilter.getSelectedItem();

    List<Transaksi> filteredTransaksiList;

    // Method filter Transaksi
    switch (selectedFilter) {
        case "Hanya Pemasukan":
            filteredTransaksiList = transaksiDAO.getTransaksiByJenis("PEMASUKAN");
            break;
        case "Hanya Pengeluaran":
            filteredTransaksiList = transaksiDAO.getTransaksiByJenis("PENGELUARAN");
            break;
        default:
            filteredTransaksiList = transaksiDAO.getAllTransaksi();
            break;
    }
    
    updateTable(filteredTransaksiList);
    }
    
    // Method agar keluaran ID menghasilkan 4 angka yang membuat nampak profesional
    private String formatId(int id) {
        return String.format("%04d", id);
    }   
    
    // Method menutup Program
    private void exitApplication() {
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Mau keluar dari aplikasi?", 
        "Konfirmasi Keluar", 
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        System.exit(0); // Keluar dari aplikasi
    }
    }
}